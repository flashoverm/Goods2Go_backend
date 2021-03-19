package com.goods2go.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.config.MessageBrokerConfig;
import com.goods2go.controllers.errors.ForbiddenException;
import com.goods2go.controllers.errors.NotFoundException;
import com.goods2go.geo.repositories.ShipmentAnnouncementSpotDao;
import com.goods2go.models.Address;
import com.goods2go.models.NotificationMessage;
import com.goods2go.models.Shipment;
import com.goods2go.models.ShipmentAnnouncement;
import com.goods2go.models.ShipmentRequest;
import com.goods2go.models.User;
import com.goods2go.models.enums.NotificationMessageType;
import com.goods2go.models.util.ModelFilters;
import com.goods2go.repositories.NotificationMessageDao;
import com.goods2go.repositories.ShipmentAnnouncementDao;
import com.goods2go.repositories.ShipmentDao;
import com.goods2go.repositories.ShipmentRequestDao;
import com.goods2go.repositories.UserDao;

@RestController
@RequestMapping("/shipmentrequest")
public class ShipmentRequestController {
	
	@Autowired
	private ShipmentRequestDao shipmentRequestDao;
	
	@Autowired
	ShipmentAnnouncementDao shipmentAnnouncementDao;
	
	@Autowired
	ShipmentAnnouncementSpotDao shipmentAnnouncementSpotDao;
	
	@Autowired
	ShipmentDao shipmentDao;
		
	@Autowired
	UserDao userDao;
	
	@Autowired
	SimpMessageSendingOperations template;
	
	@Autowired
	NotificationMessageDao notificationMessageDao;
	
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public MappingJacksonValue save(@RequestBody ShipmentRequest shipmentRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ShipmentRequest result;
		
	    ShipmentAnnouncement annoucement = shipmentAnnouncementDao.findOne(
	    		shipmentRequest.getShipmentannouncement().getId());
	    if(annoucement == null) {
			throw new NotFoundException();
	    }
	    	    
	    User user = userDao.findOneByEmail(auth.getName());
	    shipmentRequest.setDeliverer(user);
	    shipmentRequest.setShipmentannouncement(annoucement);
	    result = shipmentRequestDao.save(shipmentRequest);
	    
	    sendRequestNotification(
	    		annoucement.getSender().getEmail(), NotificationMessageType.NewRequest, 
	    		annoucement.getId(), annoucement.getDescription());
	    
	    return ModelFilters.filterBeforeSend(result);
	}

	@RequestMapping(value="/getall", method=RequestMethod.GET)
	public MappingJacksonValue getAll() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userDao.findOneByEmail(auth.getName());
	    List<ShipmentRequest> requests = shipmentRequestDao.findAllByDeliverer(user);
		return ModelFilters.filterBeforeSend(prepareRequests(requests));
	}
	
	@RequestMapping(value="/accept", method=RequestMethod.POST)
	public MappingJacksonValue accept(@RequestBody ShipmentRequest shipmentRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ShipmentRequest request = shipmentRequestDao.findOne(shipmentRequest.getId());
		if(request == null) {
			throw new NotFoundException();
		}
		ShipmentAnnouncement announcement = request.getShipmentannouncement();
		
		if(!announcement.getSender().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
		}
		Shipment shipment = shipmentDao.save(new Shipment(announcement, request));
		shipmentAnnouncementDao.delete(announcement.getId());
		shipmentAnnouncementSpotDao.delete(announcement.getId());
		
		sendRequestNotification(
				shipment.getDeliverer().getEmail(), NotificationMessageType.RequestAccepted, 
				shipment.getId(), shipment.getDescription());
		
		for(ShipmentRequest otherRequest : announcement.getShipmentrequests()) {
			if(otherRequest.getId() != request.getId()) {
				sendRequestNotification(
						otherRequest.getDeliverer().getEmail(), NotificationMessageType.RequestDeclined, 
						otherRequest.getId(), otherRequest.getShipmentannouncement().getDescription());
			}
		}
		return ModelFilters.filterBeforeSend(shipment);
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public Boolean delete(@RequestBody ShipmentRequest shipmentRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ShipmentRequest request = shipmentRequestDao.findOne(shipmentRequest.getId());
		if(request == null) {
			throw new NotFoundException();
		}
		if(!request.getDeliverer().getEmail().equals(auth.getName())) {
    		throw new ForbiddenException();
		}
		shipmentRequestDao.delete(shipmentRequest.getId());
		return true;
	}
	
	private List<ShipmentRequest> prepareRequests(List<ShipmentRequest> requests){
		List<ShipmentRequest> newList = new ArrayList<>();
		for(ShipmentRequest request : requests) {
			Address destination = request.getShipmentannouncement().getDestination();
			destination.setFirstname("");
			destination.setLastname("");
			destination.setStreetno("");

			Address source = request.getShipmentannouncement().getSource();
			source.setFirstname("");
			source.setLastname("");
			source.setStreetno("");
			newList.add(request);
		}
		return newList;
	}
	
	//TODO redundant
	private void sendRequestNotification(String recipient, NotificationMessageType type, long subjectId, String subjectDescription) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage(recipient, type, subjectId, subjectDescription));
		
		template.convertAndSendToUser(recipient, MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + recipient);
	}
}
