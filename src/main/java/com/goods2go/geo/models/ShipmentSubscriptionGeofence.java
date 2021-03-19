package com.goods2go.geo.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shipmentsubscriptiongeofence")
@Data
public class ShipmentSubscriptionGeofence {
	
	@Id
	private long id;
	
	private String sourcelat;
	
	private String sourcelng;
	
	private int sourceradius;
	
	private String destinationlat;
	
	private String destinationlng;
	
	private int destinationradius;
	
	public ShipmentSubscriptionGeofence() {
		
	}

}
