package com.goods2go.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.config.MessageBrokerConfig;
import com.goods2go.controllers.errors.ForbiddenException;
import com.goods2go.controllers.errors.NotFoundException;
import com.goods2go.models.NotificationMessage;
import com.goods2go.models.enums.NotificationMessageType;
import com.goods2go.repositories.NotificationMessageDao;

@RestController
@RequestMapping("/notificationmessage")
public class NotificationMessageController {

	@Autowired
	NotificationMessageDao notificationMessageDao;
		
    @Autowired
	SimpMessageSendingOperations template;

	@SubscribeMapping(MessageBrokerConfig.MSG_TARGET_USER)
	public void handleSubscribe(Principal p) { 
		System.out.println(p.getName() + " subscribed channel: " + MessageBrokerConfig.MSG_TARGET_USER);	
		
		List<NotificationMessage> queue = notificationMessageDao.findByRecipient(p.getName());
		for(NotificationMessage message : queue) {
			if(message.isValid()) {
				template.convertAndSendToUser(message.getRecipient(), MessageBrokerConfig.MSG_TARGET_USER , message);
				System.out.println("Sent message to " + message.getRecipient());
			} else {
				notificationMessageDao.delete(message.getId());
			}
		}
	}
	
	@RequestMapping(value="/confirm", method=RequestMethod.POST)
	public Boolean confirm(@RequestBody NotificationMessage notificationMessage) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(!notificationMessage.getRecipient().equals(auth.getName())) {
    		throw new ForbiddenException();
		}
		if(notificationMessageDao.findOne(notificationMessage.getId()) == null) {
			throw new NotFoundException();
		}
		notificationMessageDao.delete(notificationMessage.getId());
		return true;	    
	}

	/*
	//TODO only for testing - remove
	@RequestMapping(value="/send/{destination}", method=RequestMethod.GET)
	public Boolean notification(@PathVariable String destination) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage(destination+".com", NotificationMessageType.Test, -1));
		template.convertAndSendToUser(destination+".com", MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + destination+".com");
		return true;
	}
	
	//TODO only for testing - remove
	@RequestMapping(value="/send/{destination}", method=RequestMethod.GET)
	public Boolean notification2(@PathVariable long destination) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage("a@b.com", NotificationMessageType.ShipmentDelivered, destination, "mitID"+destination));
		template.convertAndSendToUser("a@b.com", MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + "a@b.com");
		return true;
	}
	
	//TODO only for testing - remove
	@RequestMapping(value="/send/{destination}", method=RequestMethod.GET)
	public Boolean notification3(@PathVariable long destination) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage("a@b.com", NotificationMessageType.NewRequest, destination, "mitID"+destination));
		template.convertAndSendToUser("a@b.com", MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + "a@b.com");
		return true;
	}
	*/
	
	//TODO only for testing - remove
	@RequestMapping(value="/send/{destination}", method=RequestMethod.GET)
	public Boolean notification3(@PathVariable long destination) {
		NotificationMessage message = notificationMessageDao.save(new NotificationMessage("a@b.com", NotificationMessageType.FittingAnnouncement, destination, "mitID"+destination));
		template.convertAndSendToUser("a@b.com", MessageBrokerConfig.MSG_TARGET_USER, message);
		System.out.println("Sent message to " + "a@b.com");
		return true;
	}

	
}
