package com.goods2go.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "shipmentrequest")
@Data
//@JsonFilter("ShipmentRequestFilter")
public class ShipmentRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JsonIgnoreProperties("shipmentrequests")
	@JoinColumn (name = "shipmentannouncement", nullable = false)
	private ShipmentAnnouncement shipmentannouncement;
	
	private Date deliverydatetime;
	
	@ManyToOne
	private User deliverer;
	
	private Date pickupdatetime;
	
	public ShipmentRequest() {
		super();
	}

	public ShipmentRequest(ShipmentAnnouncement shipmentannouncement, User deliverer, Date pickupdatetime, Date deliverydatetime) {
		super();
		this.shipmentannouncement = shipmentannouncement;
		this.deliverer = deliverer;
		this.pickupdatetime = pickupdatetime;
		this.deliverydatetime = deliverydatetime;
	}
	

}
