package com.goods2go.geo.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shipmentannouncementspot")
@Data
public class ShipmentAnnouncementSpot {
	
	@Id
	private long id;
	
	private String sourcelat;
	
	private String sourcelng;
	
	private String destinationlat;
	
	private String destinationlng;
	
	
	public ShipmentAnnouncementSpot() {
	}


	

}
