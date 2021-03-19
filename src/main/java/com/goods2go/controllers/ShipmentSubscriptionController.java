package com.goods2go.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.controllers.errors.ForbiddenException;
import com.goods2go.controllers.errors.InternalServerErrorException;
import com.goods2go.controllers.errors.NotFoundException;
import com.goods2go.geo.models.ShipmentSubscriptionGeofence;
import com.goods2go.geo.repositories.ShipmentSubscriptionGeofenceDao;
import com.goods2go.models.Address;
import com.goods2go.models.ShipmentSize;
import com.goods2go.models.ShipmentSubscription;
import com.goods2go.models.User;
import com.goods2go.models.util.DateTime;
import com.goods2go.models.util.GeoCoordinates;
import com.goods2go.models.util.ModelFilters;
import com.goods2go.repositories.AddressDao;
import com.goods2go.repositories.ShipmentSizeDao;
import com.goods2go.repositories.ShipmentSubscriptionDao;
import com.goods2go.repositories.UserDao;

@RestController
@RequestMapping("/shipmentsubscription")
public class ShipmentSubscriptionController {
	
	@Autowired
	ShipmentSubscriptionDao shipmentSubscriptionDao;
	
	@Autowired
	ShipmentSubscriptionGeofenceDao shipmentSubscriptionGeofenceDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	AddressDao addressDao;
		
	@Autowired
	ShipmentSizeDao shipmentSizeDao;
		
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public MappingJacksonValue save(@RequestBody ShipmentSubscription shipmentSubscription) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(shipmentSubscription.getRadius() > 100) {
			shipmentSubscription.setRadius(100);
		}
		if(shipmentSubscription.getDestinationradius() > 100) {
			shipmentSubscription.setDestinationradius(100);
		}
		
		// Check if dates are valid
    	if(shipmentSubscription.getPickupfrom() != null && shipmentSubscription.getDeliveruntil() != null) {
    		if(!DateTime.checkIfDatesAreValidForSearch(shipmentSubscription.getPickupfrom(), shipmentSubscription.getDeliveruntil())) {
    			throw new InternalServerErrorException();
    		}	
    	} else if(shipmentSubscription.getPickupfrom() != null && shipmentSubscription.getDeliveruntil() == null) {
    		if(DateTime.removeTime(shipmentSubscription.getPickupfrom()).compareTo(DateTime.removeTime(new Date())) < 0) {
    			throw new InternalServerErrorException();
    		}
    	} else if(shipmentSubscription.getPickupfrom() == null && shipmentSubscription.getDeliveruntil() != null) {
    		if(DateTime.removeTime(shipmentSubscription.getDeliveruntil()).compareTo(DateTime.removeTime(new Date())) < 0) {
    			throw new InternalServerErrorException();
    		}
    	}
		
		if(shipmentSubscription.getMaxsize() != null) {
		    ShipmentSize size = shipmentSizeDao.findByName(shipmentSubscription.getMaxsize().getName());
		    if(size == null) {
				throw new NotFoundException();
			} else {
				shipmentSubscription.setMaxsize(size);
			}
		}
		
		User user = userDao.findOneByEmail(auth.getName());
	    shipmentSubscription.setDeliverer(user);
	    shipmentSubscription.setSource(queryAddress(shipmentSubscription.getSource()));
	    shipmentSubscription.setDestination(queryAddress(shipmentSubscription.getDestination()));
	    
	    if(shipmentSubscription.getPickupfrom() != null) {
	    	shipmentSubscription.setPickupfrom(DateTime.removeTime(shipmentSubscription.getPickupfrom()));
	    } 
	    if(shipmentSubscription.getDeliveruntil() != null) {
	    	shipmentSubscription.setDeliveruntil(DateTime.removeTime(shipmentSubscription.getDeliveruntil()));
	    }
	    
	    shipmentSubscription = shipmentSubscriptionDao.save(shipmentSubscription);
	    
	  	double[] sourceCoordsString = GeoCoordinates.fromString(shipmentSubscription.getSourcecoordinates());
	  	double[] destinationCoordsString = GeoCoordinates.fromString(shipmentSubscription.getDestinationcoordinates());
	  	
	  	ShipmentSubscriptionGeofence shipmenSubscriptionGeofence = new ShipmentSubscriptionGeofence();
	  	shipmenSubscriptionGeofence.setId(shipmentSubscription.getId());
	  	
	  	if(sourceCoordsString != null) {
	  		shipmenSubscriptionGeofence.setSourcelat(String.valueOf(sourceCoordsString[0]));
	  		shipmenSubscriptionGeofence.setSourcelng(String.valueOf(sourceCoordsString[1]));
	  		shipmenSubscriptionGeofence.setSourceradius(shipmentSubscription.getRadius() * 1000);
	  	}
	  	
	  	if(destinationCoordsString != null) {
	  		shipmenSubscriptionGeofence.setDestinationlat(String.valueOf(destinationCoordsString[0]));
	  		shipmenSubscriptionGeofence.setDestinationlng(String.valueOf(destinationCoordsString[1]));
	  		shipmenSubscriptionGeofence.setDestinationradius(shipmentSubscription.getDestinationradius() * 1000);	  		
	  	}
	  	 	
	  	try {
	  		shipmentSubscriptionGeofenceDao.save(shipmenSubscriptionGeofence);
	  	} catch(Exception ex) {
	  		shipmentSubscriptionDao.delete(shipmentSubscription);
	  		throw new InternalServerErrorException();
	  	}
	  	
  		return ModelFilters.filterBeforeSend(shipmentSubscription);
	    	    
		
	}
	
	@RequestMapping(value="/get/{subscriptionid}", method=RequestMethod.GET)
	public MappingJacksonValue get(@PathVariable long subscriptionid) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ShipmentSubscription requested = shipmentSubscriptionDao.findOne(subscriptionid);
		if(requested == null) {
			throw new NotFoundException();
		}
		if(!requested.getDeliverer().getEmail().equals(auth.getName())) {
			throw new ForbiddenException();
		}
		return ModelFilters.filterBeforeSend(requested);
	}
	
	@RequestMapping(value="/getAll", method=RequestMethod.GET)
	public MappingJacksonValue getAll() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    List<ShipmentSubscription> subscriptions = shipmentSubscriptionDao.findAllByDeliverer(user);
	    return ModelFilters.filterBeforeSend(subscriptions);
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public Boolean delete(@RequestBody ShipmentSubscription shipmentSubscription) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ShipmentSubscription subscription = shipmentSubscriptionDao.findOne(shipmentSubscription.getId());
		if(subscription == null) {
			throw new NotFoundException();
		}
		if(!subscription.getDeliverer().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
		}
		try {
			shipmentSubscriptionDao.delete(shipmentSubscription.getId());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		shipmentSubscriptionGeofenceDao.delete(shipmentSubscription.getId());
		return true;
		}
	
	@RequestMapping(value="/deleteAll", method=RequestMethod.POST)
	public Boolean deleteAll(@RequestBody List<ShipmentSubscription> shipmentSubscriptions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		for (ShipmentSubscription ss : shipmentSubscriptions) {
			this.delete(ss);
		}
	
		return true;
	}

	//TODO redundant
	private Address queryAddress(Address address) {
		Address updatedAddress = addressDao.findOneByFirstnameAndLastnameAndStreetAndStreetnoAndPostcodeAndCityAndCountryAndCompanyname(
				address.getFirstname(), address.getLastname(), address.getStreet(), address.getStreetno(), 
				address.getPostcode(), address.getCity(), address.getCountry(), address.getCompanyname());
		if(updatedAddress == null) {
			updatedAddress = addressDao.save(address);
		}
		return updatedAddress;
	}
}
