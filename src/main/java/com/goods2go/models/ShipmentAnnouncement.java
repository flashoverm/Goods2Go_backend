package com.goods2go.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goods2go.models.enums.Status;

import lombok.Data;

@Entity
@Table(name = "shipmentannouncement")
@Data
//@JsonFilter("ShipmentAnnouncementFilter")
public class ShipmentAnnouncement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	private User sender;
	
	private String description;
	
	@ManyToOne
	private Address source;
	private String aproxsourcecoordinates;
	
	@ManyToOne
	private Address destination;
	private String aproxdestinationcoordinates;

	@ManyToOne
	private ShipmentSize size;
	
	private Date earliestpickupdate;
	
	private Date latestpickupdate;
	
	private Date latestdeliverydate;
	
	private float price;
	
	@OneToMany(mappedBy = "shipmentannouncement", cascade=CascadeType.ALL)
	@JsonIgnoreProperties("shipmentannouncement")
	private List<ShipmentRequest> shipmentrequests;

	public ShipmentAnnouncement() {
		super();
	}
	
    public ShipmentAnnouncement(String description, User sender, Address source, Address destination,
            ShipmentSize size, float price,
            Date earliestpickupdate, Date latestpickupdate,
            Date latestdeliverydate) {
		super();

		this.description = description;
		this.sender = sender;
		this.source = source;
		this.destination = destination;
		this.size = size;
		this.price = price;
		this.earliestpickupdate = earliestpickupdate;
		this.latestpickupdate = latestpickupdate;
		this.latestdeliverydate = latestdeliverydate;
    }
    
	public Status getStatus() {
		return Status.ANNOUNCED;
	}
}
