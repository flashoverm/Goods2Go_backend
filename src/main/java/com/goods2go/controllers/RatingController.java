package com.goods2go.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.models.DelivererRating;
import com.goods2go.models.SenderRating;
import com.goods2go.models.Shipment;
import com.goods2go.models.User;
import com.goods2go.repositories.DelivererRatingDao;
import com.goods2go.repositories.SenderRatingDao;
import com.goods2go.repositories.ShipmentDao;
import com.goods2go.repositories.UserDao;

@RestController
@RequestMapping("/rating")
public class RatingController {
	
	@Autowired
	SenderRatingDao senderRatingDao;
	
	@Autowired
	ShipmentDao shipmentDao;
	
	@Autowired
	DelivererRatingDao delivererRatingDao;
	
	@Autowired
	UserDao userDao;
	
	
	@RequestMapping(value="/sender/save", method=RequestMethod.POST)
	public SenderRating save(@RequestBody SenderRating senderRating) {
		//TODO endpoint only for administration
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    auth.getName();
		
		return senderRatingDao.save(senderRating);
	}
	
	@RequestMapping(value="/deliverer/save", method=RequestMethod.POST)
	public DelivererRating save(@RequestBody DelivererRating delivererRating) {
		//TODO endpoint only for administration
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    auth.getName();
		
		return delivererRatingDao.save(delivererRating);
	}
	
	@RequestMapping(value="/deliverer/delete", method=RequestMethod.POST)
	public ResponseEntity delete(@RequestBody DelivererRating delivererRating) {
		//TODO endpoint only for administration
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    auth.getName();
	    
	    Shipment shipment = shipmentDao.findOne(delivererRating.getIdshipment());
	    User rated = userDao.findOne(delivererRating.getIduser());
	    
	    if(shipment != null && rated != null) {
	    	shipment.setDelivererRating(null);
	    	shipmentDao.save(shipment);
	    	rated.revokeDelivererRating(delivererRating.getRating());
	    	userDao.save(rated);
	    	delivererRatingDao.delete(delivererRating);
	    }
	    	    
		return new ResponseEntity<>(HttpStatus.OK); // Only return HTTP Status 200, without response body
	}
	
	@RequestMapping(value="/sender/delete", method=RequestMethod.POST)
	public ResponseEntity delete(@RequestBody SenderRating senderRating) {
		//TODO endpoint only for administration
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    auth.getName();
	    
	    Shipment shipment = shipmentDao.findOne(senderRating.getIdshipment());
	    User rated = userDao.findOne(senderRating.getIduser());
	    
	    if(shipment != null && rated != null) {
	    	shipment.setSenderRating(null);
	    	shipmentDao.save(shipment);
	    	rated.revokeSenderRating(senderRating.getRating());
	    	userDao.save(rated);
	    	senderRatingDao.delete(senderRating);
	    }	    
		return new ResponseEntity<>(HttpStatus.OK); // Only return HTTP Status 200, without response body
	}
}
