package com.goods2go.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.goods2go.models.enums.Status;
import com.goods2go.models.util.DateTime;

import lombok.Data;

@Entity
@Table(name = "shipment")
@Data
//@JsonFilter("ShipmentFilter")
public class Shipment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String description;

	@ManyToOne
	private User deliverer;
	
	@ManyToOne
	private User sender;
	
	@ManyToOne
	private Address source;
	
	@ManyToOne
	private Address destination;
	
	private String qrstring;
	
	private Date pickupdatetime;
	
	private Date deliverydatetime;
	
	private String deliverycoordinates;
	
	private Date negpickupdatetime;
	
	private Date negdeliverydatetime;
	
	@ManyToOne
	private ShipmentSize size;
	
	private float price;
	
	/**
	 * Rating of the sender from the deliverer
	 */
	@ManyToOne
	private SenderRating senderRating;
	
	/**
	 * Rating of the deliverer from the sender
	 */
	@ManyToOne
	private DelivererRating delivererRating;
	
	public Shipment() {
		super();
	}
		
	public Shipment(String description, ShipmentSize size, float price, User sender, Address source, Date negpickupdatetime, User deliverer, Address destination, Date negdeliverydatetime) {
		super();
		this.description = description;
		this.size = size;
		this.price = price;
		this.sender = sender;
		this.source = source;
		this.negpickupdatetime = negpickupdatetime;
		this.deliverer = deliverer;
		this.destination = destination;
		this.negdeliverydatetime = negdeliverydatetime;
		this.qrstring = generateQrString();
	}
	
	public Shipment(ShipmentAnnouncement announcement, ShipmentRequest acceptedRequest) {
		super();
		this.description = announcement.getDescription();
		this.size = announcement.getSize();
		this.price = announcement.getPrice();
		this.sender = announcement.getSender();
		this.source = announcement.getSource();
		this.negpickupdatetime = acceptedRequest.getPickupdatetime();
		this.deliverer = acceptedRequest.getDeliverer();
		this.destination = announcement.getDestination();
		this.negdeliverydatetime = acceptedRequest.getDeliverydatetime();
		this.qrstring = generateQrString();
	}
	
	private String generateQrString() {
		return UUID.randomUUID().toString();
	}
	
	
	public Status getStatus() {
		if(pickupdatetime == null) {
			return Status.PENDING;
		}
		if(deliverydatetime == null || deliverycoordinates == null) {
			return Status.ACTIVE;
		}
		if(delivererRating == null) {
			return Status.DELIVERED;
		}
		if(senderRating == null) {
			return Status.PAIDANDSENDERRATED;
		}
		return Status.DELIVERERRATED;
	}
	
	public boolean pickupShipment(String qrstring) {
		if(qrstring.equals(this.qrstring)) {
		    pickupdatetime = DateTime.getCurrentDateTime();
		    return true;
		}
		return false;
	}
	
	public void deliverShipment(String deliveryCoordinates) {
		this.deliverycoordinates = deliveryCoordinates;
		deliverydatetime = DateTime.getCurrentDateTime();
	}

}
