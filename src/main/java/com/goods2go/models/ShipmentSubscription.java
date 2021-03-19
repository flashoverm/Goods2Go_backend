package com.goods2go.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shipmentsubscription")
@Data
//@JsonFilter("ShipmentSubscriptionFilter")
public class ShipmentSubscription implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private Address source;
	private String sourcecoordinates;

	@ManyToOne
	private Address destination;
	private String destinationcoordinates;

	@ManyToOne
	private User deliverer;
	
	@ManyToOne
	private ShipmentSize maxsize;
	
    private Date pickupfrom;

	private Date deliveruntil;
	
	private int radius;
	
	private int destinationradius;
	
	public ShipmentSubscription() {
		super();
	}
}
