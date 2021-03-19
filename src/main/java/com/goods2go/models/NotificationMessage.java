package com.goods2go.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.goods2go.config.MessageBrokerConfig;
import com.goods2go.models.enums.NotificationMessageType;
import com.goods2go.models.util.DateTime;

import lombok.Data;

@Entity
@Table(name = "notificationmessage")
@Data
//@JsonFilter("NotificationMessageFilter")
public class NotificationMessage {
		  
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;	//id of the message instance
	
	private String recipient;	//mail address of the recipient
	
	private NotificationMessageType type;
	
    private long subjectId;	//of announcement, request, shipment or subscription
    private String subjectDescription; //of announcement or shipment, or null
    
    private Date timestamp;
    
	public NotificationMessage() {
		
	}
	
	public NotificationMessage(String recipient, NotificationMessageType type, long subjectId, String subjectDescription) {
		this.recipient = recipient;
		this.type = type;
		this.subjectId = subjectId;
		this.subjectDescription = subjectDescription;
		this.timestamp = DateTime.getCurrentDateTime();
	}
	
	public NotificationMessage(String recipient, NotificationMessageType type, long subjectId) {
		this.recipient = recipient;
		this.type = type;
		this.subjectId = subjectId;
		this.subjectDescription = null;
		this.timestamp = DateTime.getCurrentDateTime();
	}
	
	public boolean isValid(){
		Date expiryDate = DateTime.getExpiryDateFrom(timestamp, MessageBrokerConfig.NOTIFICATION_TIMEOUT_MIN);
    	return (DateTime.getCurrentDateTime().compareTo(expiryDate) <=0);
	}	

	@Override
	public String toString() {
		return "NotificationMessage [id=" + id + ", type=" + type + ", subjectId=" + subjectId + ", timestamp="
				+ timestamp + "]";
	}

}
