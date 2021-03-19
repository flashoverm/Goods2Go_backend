package com.goods2go.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.goods2go.controllers.errors.ConflictException;
import com.goods2go.controllers.errors.ForbiddenException;
import com.goods2go.controllers.errors.NotFoundException;
import com.goods2go.models.DelivererRating;
import com.goods2go.models.NotificationMessage;
import com.goods2go.models.SenderRating;
import com.goods2go.models.Shipment;
import com.goods2go.models.User;
import com.goods2go.models.enums.NotificationMessageType;
import com.goods2go.models.util.ModelFilters;
import com.goods2go.repositories.DelivererRatingDao;
import com.goods2go.repositories.NotificationMessageDao;
import com.goods2go.repositories.SenderRatingDao;
import com.goods2go.repositories.ShipmentDao;
import com.goods2go.repositories.UserDao;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {
	
	
	@Autowired
	ShipmentDao shipmentDao;
	
	@Autowired
	UserDao userDao;
	
    @Autowired
	SimpMessageSendingOperations template;
	
	@Autowired
	NotificationMessageDao notificationMessageDao;
	
	@Autowired
	SenderRatingDao senderRatingDao;
	
	@Autowired
	DelivererRatingDao delivererRatingDao;
	
	//Not used
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public MappingJacksonValue save(@RequestBody Shipment shipment) {
		return ModelFilters.filterBeforeSend(shipmentDao.save(shipment));
	}
	
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public MappingJacksonValue getAllShipments() {
		return ModelFilters.filterButKeepAllFields(shipmentDao.findAll());
	}
	
	@RequestMapping(value="/get/{shipmentid}", method=RequestMethod.GET)
	public MappingJacksonValue get(@PathVariable long shipmentid) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
	    Shipment requested = shipmentDao.findOne(shipmentid);
	    if(requested == null) {
			throw new NotFoundException();
	    }
	    
	    if(requested.getDeliverer().getEmail().equals(auth.getName())) {
	    	//TODO prepare shipment for deliverer
	    	return ModelFilters.filterBeforeSend(requested);
	    } else if(requested.getSender().getEmail().equals(auth.getName())) {
	    	//TODO prepare shipment for sender
	    	return ModelFilters.filterBeforeSend(requested);
	    } else {
    		throw new ForbiddenException();
	    }
	}
	
	@RequestMapping(value="/sender/getpending", method=RequestMethod.GET)
	public MappingJacksonValue getSendersPending(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    
	    return ModelFilters.filterBeforeSend(shipmentDao.findByPickupdatetimeIsNullAndSender(user));
	}
	
	@RequestMapping(value="/sender/getactive", method=RequestMethod.GET)
	public MappingJacksonValue getSendersActive(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    
	    return ModelFilters.filterBeforeSend(shipmentDao.findByPickupdatetimeNotNullAndDeliverydatetimeIsNullAndSender(user));
	}
	
	@RequestMapping(value="/sender/getclosed", method=RequestMethod.GET)
	public MappingJacksonValue getSendersClosed(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    
	    return ModelFilters.filterButKeepAllFields(shipmentDao.findByDeliverydatetimeNotNullAndSender(user));
	}
	
	@RequestMapping(value="/deliverer/getpending", method=RequestMethod.GET)
	public MappingJacksonValue getDeliverersPending(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    
	    return ModelFilters.filterBeforeSend(shipmentDao.findByPickupdatetimeIsNullAndDeliverer(user));
	}
	
	@RequestMapping(value="/deliverer/getactive", method=RequestMethod.GET)
	public MappingJacksonValue getDeliverersActive(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    
	    return ModelFilters.filterBeforeSend(shipmentDao.findByPickupdatetimeNotNullAndDeliverydatetimeIsNullAndDeliverer(user));
	}
	
	@RequestMapping(value="/deliverer/getclosed", method=RequestMethod.GET)
	public MappingJacksonValue getDeliverersClosed(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    
	    return ModelFilters.filterBeforeSend(shipmentDao.findByDeliverydatetimeNotNullAndDeliverer(user));
	}
	
	@RequestMapping(value="/pickup", method=RequestMethod.POST)
	public Boolean pickupShipment(@RequestBody Shipment shipment) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    Shipment requested = shipmentDao.findOne(shipment.getId());
	    if(requested == null) {
			throw new NotFoundException();
	    }
	    if(!requested.getDeliverer().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
	    }
	    if(requested.pickupShipment(shipment.getQrstring())){
	    	shipmentDao.save(requested);
		    sendStatusNotification(
		    		requested.getSender().getEmail(), NotificationMessageType.ShipmentPickedUp, 
		    		requested.getId(), requested.getDescription());	    
			return true;
	    }
	    return false;
	}
	
	@RequestMapping(value="/deliver", method=RequestMethod.POST)
	public Boolean deliverShipment(@RequestBody Shipment shipment) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    Shipment requested = shipmentDao.findOne(shipment.getId());
	    if(requested == null) {
			throw new NotFoundException();
	    }
	    if(!requested.getDeliverer().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
	    }
	    requested.deliverShipment(shipment.getDeliverycoordinates());
	    shipmentDao.save(requested);
	    sendStatusNotification(
	    		requested.getSender().getEmail(), NotificationMessageType.ShipmentDelivered, 
	    		requested.getId(), requested.getDescription());	    
	    return true;
	}
	
	@RequestMapping(value="/deliverer/rate", method=RequestMethod.POST)
	public MappingJacksonValue rateAsDeliverer(@RequestBody Shipment shipment) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    Shipment requested = shipmentDao.findOne(shipment.getId());
	    if(requested == null) {
			throw new NotFoundException();
	    }
	    if(!requested.getDeliverer().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
	    }
	    if(requested.getSenderRating() != null) {
	    	throw new ConflictException();
	    }
	    
	    shipment.getSenderRating().setIduser(requested.getSender().getId());
	    SenderRating rating = senderRatingDao.save(shipment.getSenderRating());
	    requested.setSenderRating(rating);
	    Shipment updated = shipmentDao.save(requested);
	    
	    User sender = userDao.findOne(shipment.getSender().getId());
	    sender.updateSenderRating(rating.getRating());
	    userDao.save(sender);
	    
	    sendStatusNotification(
	    		requested.getSender().getEmail(), NotificationMessageType.ShipmentDelivererRated, 
	    		requested.getId(), requested.getDescription());
	    
	    return ModelFilters.filterBeforeSend(updated);
	}
	
	@RequestMapping(value="/sender/rate", method=RequestMethod.POST)
	public MappingJacksonValue rateAsSender(@RequestBody Shipment shipment) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    Shipment requested = shipmentDao.findOne(shipment.getId());
	    if(requested == null) {
			throw new NotFoundException();
	    }
	    if(!requested.getSender().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
	    }
	    if(requested.getDelivererRating() != null) {
	    	throw new ConflictException();
	    }
	    
	    shipment.getDelivererRating().setIduser(requested.getDeliverer().getId());
	    DelivererRating rating = delivererRatingDao.save(shipment.getDelivererRating());
	    requested.setDelivererRating(rating);
	    Shipment updated = shipmentDao.save(requested);
	    
	    User deliverer = userDao.findOne(shipment.getDeliverer().getId());
	    deliverer.updateDelivererRating(rating.getRating());
	    userDao.save(deliverer);
	    
	    sendStatusNotification(
	    		requested.getDeliverer().getEmail(), NotificationMessageType.ShipmentSenderRated, 
	    		requested.getId(), requested.getDescription());
	    
		return ModelFilters.filterBeforeSend(updated);
	}
	
	//TODO redundant: sendRequestNotification
	private void sendStatusNotification(String recipient, NotificationMessageType type, long subjectId, String subjectDescription) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage(recipient, type, subjectId, subjectDescription));
		
		template.convertAndSendToUser(recipient, MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + recipient);
	}

}
