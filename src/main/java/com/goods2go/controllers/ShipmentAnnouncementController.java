package com.goods2go.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.config.MessageBrokerConfig;
import com.goods2go.controllers.errors.ForbiddenException;
import com.goods2go.controllers.errors.InternalServerErrorException;
import com.goods2go.controllers.errors.NotFoundException;
import com.goods2go.geo.models.ShipmentAnnouncementSpot;
import com.goods2go.geo.models.ShipmentSubscriptionGeofence;
import com.goods2go.geo.repositories.ShipmentAnnouncementSpotDao;
import com.goods2go.geo.repositories.ShipmentSubscriptionGeofenceDao;
import com.goods2go.models.Address;
import com.goods2go.models.NotificationMessage;
import com.goods2go.models.ShipmentAnnouncement;
import com.goods2go.models.ShipmentRequest;
import com.goods2go.models.ShipmentSize;
import com.goods2go.models.ShipmentSubscription;
import com.goods2go.models.User;
import com.goods2go.models.enums.NotificationMessageType;
import com.goods2go.models.util.DateTime;
import com.goods2go.models.util.GeoCoordinates;
import com.goods2go.models.util.ModelFilters;
import com.goods2go.models.util.TaskQueue;
import com.goods2go.repositories.AddressDao;
import com.goods2go.repositories.NotificationMessageDao;
import com.goods2go.repositories.ShipmentAnnouncementDao;
import com.goods2go.repositories.ShipmentAnnouncementSpecs;
import com.goods2go.repositories.ShipmentSizeDao;
import com.goods2go.repositories.ShipmentSubscriptionDao;
import com.goods2go.repositories.ShipmentSubscriptionSpecs;
import com.goods2go.repositories.UserDao;

@RestController
@RequestMapping("/shipmentannouncement")
public class ShipmentAnnouncementController {
	
	@Autowired
	ShipmentAnnouncementSpotDao shipmentAnnouncementSpotDao;
	
	@Autowired
	ShipmentAnnouncementDao shipmentAnnouncementDao;
	
	@Autowired
	ShipmentSubscriptionDao shipmentSubscriptionDao;
	
	@Autowired
	ShipmentSubscriptionGeofenceDao shipmentSubscriptionGeofenceDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	ShipmentSizeDao shipmentSizeDao;
	
	@Autowired
	AddressDao addressDao;
	
	@Autowired
	SimpMessageSendingOperations template;
	
	@Autowired
	NotificationMessageDao notificationMessageDao;
	

	@RequestMapping(value="/save", method=RequestMethod.POST)
	public MappingJacksonValue save(@RequestBody ShipmentAnnouncement shipmentAnnouncement) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		// Check if ShipmentAnnouncement already exists and if so, delete it before creation
		if(shipmentAnnouncement.getId() > 0) {
			this.revoke(shipmentAnnouncement);
			shipmentAnnouncement.setId(0l);
		}
		
		ShipmentSize size = shipmentSizeDao.findByName(shipmentAnnouncement.getSize().getName());
		if(size == null) {
			throw new NotFoundException();
		}

		// Check if dates are valid
		if(!DateTime.checkIfDatesAreValid(shipmentAnnouncement.getEarliestpickupdate(),
				shipmentAnnouncement.getLatestpickupdate(), shipmentAnnouncement.getLatestdeliverydate())) {
			throw new InternalServerErrorException();
		}
		
		
		User sender = userDao.findOneByEmail(auth.getName());

	    shipmentAnnouncement.setSender(sender);
	    shipmentAnnouncement.setSize(size);
	    shipmentAnnouncement.setSource(queryAddress(shipmentAnnouncement.getSource()));
	    shipmentAnnouncement.setDestination(queryAddress(shipmentAnnouncement.getDestination()));
	    shipmentAnnouncement.setShipmentrequests(null);
	    shipmentAnnouncement.setEarliestpickupdate(DateTime.removeTime(shipmentAnnouncement.getEarliestpickupdate()));
	    shipmentAnnouncement.setLatestpickupdate(DateTime.removeTime(shipmentAnnouncement.getLatestpickupdate()));
	    shipmentAnnouncement.setLatestdeliverydate(DateTime.removeTime(shipmentAnnouncement.getLatestdeliverydate()));

	    ShipmentAnnouncement tmpShipmentAnnouncement = shipmentAnnouncementDao.save(shipmentAnnouncement);
		
		sender.updateAddressHistory(tmpShipmentAnnouncement.getDestination());
		userDao.save(sender);
		
	    //Save Geo Information in Postgis
	    ShipmentAnnouncementSpot tmpShipmentAnnouncementSpot = new ShipmentAnnouncementSpot();
	    
	    tmpShipmentAnnouncementSpot.setId(tmpShipmentAnnouncement.getId());
	    
	    double[] coordsString = GeoCoordinates.fromString(tmpShipmentAnnouncement.getAproxsourcecoordinates());
	    tmpShipmentAnnouncementSpot.setSourcelat(String.valueOf(coordsString[0]));
	    tmpShipmentAnnouncementSpot.setSourcelng(String.valueOf(coordsString[1]));
	    
	    coordsString = GeoCoordinates.fromString(tmpShipmentAnnouncement.getAproxdestinationcoordinates());
	    tmpShipmentAnnouncementSpot.setDestinationlat(String.valueOf(coordsString[0]));
	    tmpShipmentAnnouncementSpot.setDestinationlng(String.valueOf(coordsString[1]));
	    shipmentAnnouncementSpotDao.save(tmpShipmentAnnouncementSpot);
	    
	    //check subscriptions for match
	    TaskQueue.enqueueTask(new Runnable() {
            @Override
            public void run() {
            	List<Long> shipmentSubscriptiontIds = getRelevantShipmentSubscriptiontIds(tmpShipmentAnnouncement);
            	
            	if(shipmentSubscriptiontIds.size() > 0) {
                	
            		List<ShipmentSubscription> matchedShipmentSubscription = searchMatchingShipmentSubscription(shipmentSubscriptiontIds, tmpShipmentAnnouncement);
                	
                	if(matchedShipmentSubscription != null && matchedShipmentSubscription.size() > 0) {
                		notifyAllSubscriber(matchedShipmentSubscription, tmpShipmentAnnouncement.getId());
                	}
            	}
            }
        });

	    	    		
	    return ModelFilters.filterBeforeSend(tmpShipmentAnnouncement);
	}
	
	
	private void notifyAllSubscriber(List<ShipmentSubscription> shipmentSubscriptions, Long id) {
		
		List<Long> deliveresAlreadySent = new ArrayList<Long>();
		
		for(ShipmentSubscription ss : shipmentSubscriptions) {
			
			if (!deliveresAlreadySent.contains(ss.getDeliverer().getId())) {
				sendRequestNotification(
		    		ss.getDeliverer().getEmail(), NotificationMessageType.FittingAnnouncement, 
		    		id, "");
				deliveresAlreadySent.add(ss.getDeliverer().getId());
			}
		}
	}
	
	private List<ShipmentSubscription> searchMatchingShipmentSubscription(List<Long> ids, ShipmentAnnouncement shipmentAnnouncement) {
		Specifications<ShipmentSubscription> specs = null;
		
		Iterable<ShipmentSize> allSizes = this.shipmentSizeDao.findAll();
		
		List<ShipmentSize> greaterOrEqualSizes = new ArrayList<ShipmentSize>();
		
		for(ShipmentSize size : allSizes) {
			if(size.getQuantifier() >= shipmentAnnouncement.getSize().getQuantifier()) {
				greaterOrEqualSizes.add(size);
			}
		}	


		Date from = DateTime.removeTime(shipmentAnnouncement.getEarliestpickupdate());
		Date toPick = DateTime.removeTime(shipmentAnnouncement.getLatestpickupdate());
		Date to = DateTime.removeTime(shipmentAnnouncement.getLatestdeliverydate());
		
		specs = specs
				.where(ShipmentSubscriptionSpecs.allGiven(ids, from, toPick, to, greaterOrEqualSizes))
				
				.or(ShipmentSubscriptionSpecs.allGivenButSizes(ids, from, toPick, to))
		
				.or(ShipmentSubscriptionSpecs.allGivenButUntilDate(ids, from, toPick, greaterOrEqualSizes))
				
				.or(ShipmentSubscriptionSpecs.allGivenButUntilDateAndSizes(ids, from, toPick))
				
				.or(ShipmentSubscriptionSpecs.allGivenButPickupFromDate(ids, to, greaterOrEqualSizes))
				
				.or(ShipmentSubscriptionSpecs.allGivenButPickupFromToDate(ids, greaterOrEqualSizes))
				
				.or(ShipmentSubscriptionSpecs.allGivenButPickupFromDateAndSizes(ids, to))
		
				.or(ShipmentSubscriptionSpecs.onlyIdsGiven(ids));
		
		return shipmentSubscriptionDao.findAll(specs);
	}
	
	private List<Long> getRelevantShipmentSubscriptiontIds(ShipmentAnnouncement shipmentAnnouncement) {
		double[] sourceCoordsString = GeoCoordinates.fromString(shipmentAnnouncement.getAproxsourcecoordinates());
		double[] destinationCoordsString = GeoCoordinates.fromString(shipmentAnnouncement.getAproxdestinationcoordinates());
		List<ShipmentSubscriptionGeofence> tmpShipmentSubscriptionGeofenceList = null;
		
		tmpShipmentSubscriptionGeofenceList = shipmentSubscriptionGeofenceDao.findBySourceAndOrDestination(
				String.valueOf(sourceCoordsString[1]),
				String.valueOf(sourceCoordsString[0]),
				String.valueOf(destinationCoordsString[1]),
				String.valueOf(destinationCoordsString[0]));
	
	    List<Long> geoResultIds = tmpShipmentSubscriptionGeofenceList.stream().map(ShipmentSubscriptionGeofence::getId).collect(Collectors.toList());
	    return geoResultIds;
	}
	
	@RequestMapping(value="/get/{announcementid}", method=RequestMethod.GET)
	public MappingJacksonValue get(@PathVariable long announcementid) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ShipmentAnnouncement requested = shipmentAnnouncementDao.findOne(announcementid);
		if(requested == null) {
			throw new NotFoundException();
		}
		if(!requested.getSender().getEmail().equals(auth.getName())) {
			return ModelFilters.filterBeforeSend(requested);
		}
		return ModelFilters.filterButKeepAllFields(requested);
	}
		
	@RequestMapping(value="/getall", method=RequestMethod.GET)
	public MappingJacksonValue getAll() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
    	User user = userDao.findOneByEmail(auth.getName());

        return ModelFilters.filterBeforeSend(shipmentAnnouncementDao.findAllBySender(user));
	}
	
	@RequestMapping(value="/admingetall", method=RequestMethod.GET)
	public MappingJacksonValue adminGetAll() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
        return ModelFilters.filterBeforeSend(shipmentAnnouncementDao.findAll());
	}
	
	@RequestMapping(value="/getfiltered", method=RequestMethod.POST)
	public MappingJacksonValue getFiltered(@RequestBody ShipmentSubscription filter) {
		
		ShipmentSize maxSize = shipmentSizeDao.findByName(filter.getMaxsize().getName());
		if(maxSize == null) {
			throw new NotFoundException();
		}
		
		return ModelFilters.filterBeforeSend(shipmentAnnouncementDao.findAll());
	}
	
	@RequestMapping(value="/find", method=RequestMethod.POST)
	public MappingJacksonValue find(@RequestBody List<ShipmentSubscription> shipmentSubscriptions) {
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Specifications<ShipmentAnnouncement> specs = null;
			    
	    Iterable<ShipmentSize> allSizes = this.shipmentSizeDao.findAll();
	    
	    boolean specsNotStarted = true;
	    for(ShipmentSubscription ss : shipmentSubscriptions) {
	    	
	    	
	    	// Check if dates are valid
	    	if(ss.getPickupfrom() != null && ss.getDeliveruntil() != null) {
	    		if(!DateTime.checkIfDatesAreValidForSearch(ss.getPickupfrom(), ss.getDeliveruntil())) {
	    			throw new InternalServerErrorException();
	    		}	
	    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() == null) {
	    		if(DateTime.removeTime(ss.getPickupfrom()).compareTo(DateTime.getCurrentDate()) < 0) {
	    			throw new InternalServerErrorException();
	    		}
	    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() != null) {
	    		if(DateTime.removeTime(ss.getDeliveruntil()).compareTo(DateTime.getCurrentDate()) < 0) {
	    			throw new InternalServerErrorException();
	    		}
	    	}
	    	
	    	List<Long> relevantShipmentAnnouncementIds = getRelevantShipmentAnnouncementIds(ss);
	    	
	    	if(relevantShipmentAnnouncementIds == null || relevantShipmentAnnouncementIds.size() < 1) {
	    		continue;
	    	}
	    	
	    	List<ShipmentSize> lowerOrEqualSizes = null;
	    	if(ss.getMaxsize() != null) {
	    		lowerOrEqualSizes = new ArrayList<ShipmentSize>();
	    		
	    		for(ShipmentSize size : allSizes) {
	    			if(size.getQuantifier() <= ss.getMaxsize().getQuantifier()) {
	    				lowerOrEqualSizes.add(size);
	    			}
	    		}  		
	    	}

	    	Date from = null;
	    	Date to = null;
	    	if(ss.getPickupfrom() != null) {
	    		from = DateTime.removeTime(ss.getPickupfrom());
	    	}
	    	if(ss.getDeliveruntil() != null) {
	    		to = DateTime.removeTime(ss.getDeliveruntil());
	    	}
	    	
	    	if(specsNotStarted) {
	    		if(ss.getPickupfrom() != null && ss.getDeliveruntil() != null && ss.getMaxsize() != null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.allGiven(relevantShipmentAnnouncementIds, from, to, lowerOrEqualSizes));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() != null && ss.getMaxsize() == null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.allGivenButSizes(relevantShipmentAnnouncementIds, from, to));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() != null && ss.getMaxsize() != null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.allGivenButPickupFromDate(relevantShipmentAnnouncementIds, to, lowerOrEqualSizes));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() != null && ss.getMaxsize() == null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.allGivenButPickupFromDateAndSizes(relevantShipmentAnnouncementIds, to));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() == null && ss.getMaxsize() != null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.allGivenButDeliverUntilDate(relevantShipmentAnnouncementIds, from, lowerOrEqualSizes));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() == null && ss.getMaxsize() == null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.allGivenButDeliverUntilDateAndSizes(relevantShipmentAnnouncementIds, from));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() == null && ss.getMaxsize() == null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.idIn(relevantShipmentAnnouncementIds));
		    		specsNotStarted = false;
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() == null && ss.getMaxsize() != null) {
		    		specs = specs.where(ShipmentAnnouncementSpecs.onlyIdsAndSizesGiven(relevantShipmentAnnouncementIds, lowerOrEqualSizes));
		    		specsNotStarted = false;
		    	}
	    	} else {
	    		if(ss.getPickupfrom() != null && ss.getDeliveruntil() != null && ss.getMaxsize() != null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.allGiven(relevantShipmentAnnouncementIds, from, to, lowerOrEqualSizes));
		    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() != null && ss.getMaxsize() == null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.allGivenButSizes(relevantShipmentAnnouncementIds, from, to));
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() != null && ss.getMaxsize() != null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.allGivenButPickupFromDate(relevantShipmentAnnouncementIds, to, lowerOrEqualSizes));
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() != null && ss.getMaxsize() == null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.allGivenButPickupFromDateAndSizes(relevantShipmentAnnouncementIds, to));
		    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() == null && ss.getMaxsize() != null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.allGivenButDeliverUntilDate(relevantShipmentAnnouncementIds, from, lowerOrEqualSizes));
		    	} else if(ss.getPickupfrom() != null && ss.getDeliveruntil() == null && ss.getMaxsize() == null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.allGivenButDeliverUntilDateAndSizes(relevantShipmentAnnouncementIds, from));
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() == null && ss.getMaxsize() == null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.idIn(relevantShipmentAnnouncementIds));
		    	} else if(ss.getPickupfrom() == null && ss.getDeliveruntil() == null && ss.getMaxsize() != null) {
		    		specs = specs.or(ShipmentAnnouncementSpecs.onlyIdsAndSizesGiven(relevantShipmentAnnouncementIds, lowerOrEqualSizes));
		    	}
	    	}
	    }
	    
	    if(specsNotStarted) {
	    	//throw new NotFoundException();
	    	return  ModelFilters.filterBeforeSend(new ArrayList<ShipmentAnnouncement>());
	    }
	    
    	List<ShipmentAnnouncement> list = shipmentAnnouncementDao.findAll(specs);
	    return ModelFilters.filterBeforeSend(list);
	}
	
	private List<Long> getRelevantShipmentAnnouncementIds(ShipmentSubscription shipmentSubscription) {
		double[] sourceCoordsString;
		double[] destinationCoordsString;
		List<ShipmentAnnouncementSpot> tmpShipmentAnnouncementSpotList = null;
		
		if(shipmentSubscription.getDestinationcoordinates() != null && shipmentSubscription.getSourcecoordinates() != null) {
			sourceCoordsString = GeoCoordinates.fromString(shipmentSubscription.getSourcecoordinates());
			destinationCoordsString = GeoCoordinates.fromString(shipmentSubscription.getDestinationcoordinates());

			tmpShipmentAnnouncementSpotList = shipmentAnnouncementSpotDao.findBySourceAndDestination(
					String.valueOf(sourceCoordsString[1]),
					String.valueOf(sourceCoordsString[0]), shipmentSubscription.getRadius() * 1000,
					String.valueOf(destinationCoordsString[1]),
					String.valueOf(destinationCoordsString[0]), shipmentSubscription.getDestinationradius() * 1000);
		} else if (shipmentSubscription.getDestinationcoordinates() == null && shipmentSubscription.getSourcecoordinates() != null) {
			sourceCoordsString = GeoCoordinates.fromString(shipmentSubscription.getSourcecoordinates());
			
			tmpShipmentAnnouncementSpotList = shipmentAnnouncementSpotDao.findBySource(
					String.valueOf(sourceCoordsString[1]),
					String.valueOf(sourceCoordsString[0]), shipmentSubscription.getRadius() * 1000);
		} else if (shipmentSubscription.getDestinationcoordinates() != null && shipmentSubscription.getSourcecoordinates() == null) {
			destinationCoordsString = GeoCoordinates.fromString(shipmentSubscription.getDestinationcoordinates());
			
			tmpShipmentAnnouncementSpotList = shipmentAnnouncementSpotDao.findByDestination(
					String.valueOf(destinationCoordsString[1]),
					String.valueOf(destinationCoordsString[0]), shipmentSubscription.getDestinationradius() * 1000);
		}
	    List<Long> geoResultIds = tmpShipmentAnnouncementSpotList.stream().map(ShipmentAnnouncementSpot::getId).collect(Collectors.toList());
	    return geoResultIds;
	    
	}
	
	@RequestMapping(value="/revoke", method=RequestMethod.POST)
	public Boolean revoke(@RequestBody ShipmentAnnouncement shipmentAnnouncement) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    	ShipmentAnnouncement announcement = shipmentAnnouncementDao.findOne(shipmentAnnouncement.getId());
    	if(announcement == null) {
			throw new NotFoundException();
		}
    	if(!announcement.getSender().getEmail().equals(auth.getName())){
    		throw new ForbiddenException();
    	}
    	
		for(ShipmentRequest request : announcement.getShipmentrequests()) {
			sendRequestNotification(
					request.getDeliverer().getEmail(), NotificationMessageType.RequestDeclined, 
					request.getId(), request.getShipmentannouncement().getDescription());
		}
		try {
			shipmentAnnouncementSpotDao.delete(announcement.getId());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
    	shipmentAnnouncementDao.delete(shipmentAnnouncement.getId());
		return true;
	}
	
	private Address queryAddress(Address address) {
		Address updatedAddress = addressDao.findOneByFirstnameAndLastnameAndStreetAndStreetnoAndPostcodeAndCityAndCountryAndCompanyname(
				address.getFirstname(), address.getLastname(), address.getStreet(), address.getStreetno(), 
				address.getPostcode(), address.getCity(), address.getCountry(), address.getCompanyname());
		if(updatedAddress == null) {
			updatedAddress = addressDao.save(address);
		}
		return updatedAddress;
	}
	
	//TODO redundant
	private void sendRequestNotification(String recipient, NotificationMessageType type, long subjectId, String subjectDescription) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage(recipient, type, subjectId, subjectDescription));
		
		template.convertAndSendToUser(recipient, MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + recipient);
	}
	
	
}
