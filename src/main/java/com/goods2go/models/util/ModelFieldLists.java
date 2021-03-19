package com.goods2go.models.util;

public class ModelFieldLists {
	
public static class ShipmentAnnouncement {
		
		public static final String[] allFields = {
				"id",
				"sender",
				"description",
				"source",
				"aproxsourcecoordinates",
				"destination",
				"aproxdestinationcoordinates",
				"size",
				"earliestpickupdate",
				"latestpickupdate",
				"latestdeliverydate",
				"price",
				"shipmentrequests"
				};
		
		public static final String[] censoredDefault = {
				"id",
				"sender",
				"description",
				"source",
				"aproxsourcecoordinates",
				"destination",
				"aproxdestinationcoordinates",
				"size",
				"earliestpickupdate",
				"latestpickupdate",
				"latestdeliverydate",
				"price",
				"shipmentrequests"
				};
		
	}
	
	public static class ShipmentRequest {
		
		public static final String[] allFields = {
				"id",
				"shipmentannouncement",
				"deliverydatetime",
				"deliverer",
				"pickupdatetime"
				};
		
		public static final String[] censoredDefault = {
				"id",
				"shipmentannouncement",
				"deliverydatetime",
				//"deliverer",
				"pickupdatetime"
				};
		
	}
	
	public static class Address {
		
		public static final String[] allFields = {
				"id",
				"companyname",
				"lastname",
				"firstname",
				"streetno",
				"street",
				"postcode",
				"city",
				"country",
				"homeaddress",
				"user"
				};		
	}
	
	public static class DelivererRating {
		
		public static final String[] allFields = {
				"iduser",
				"idshipment",
				"rating"
				};
		
	}
	
	public static class NotificationMessage {
		
		public static final String[] allFields = {
				"id",
				"recipient",
				"type",
				"subjectId",
				"subjectDescription",
				"timestamp"
				};
		
	}
	
	public static class PaymentInformation {
		
		public static final String[] allFields = {
				"name",
				"identifier",
				"paymentType"
				};
		
	}
	
	public static class SenderRating {
		
		public static final String[] allFields = {
				"iduser",
				"idshipment",
				"rating"
				};
		
	}
	
	public static class Shipment {
		
		public static final String[] allFields = {
				"id",
				"sender",
				"deliverer",
				"description",
				"source",
				"destination",
				"deliverycoordinates",
				"size",
				"pickupdatetime",
				"deliverydatetime",
				"negpickupdatetime",
				"negdeliverydatetime",
				"price",
				"qrstring",
				"delivererRating",
				"senderRating"
				};
		
	}
	
	public static class ShipmentSize {
		
		public static final String[] allFields = {
				"id",
				"name",
				"quantifier",
				"measure",
				"maxlength",
				"maxheight",
				"maxwidth",
				"maxweight",
				"pricefactor"
				};
		
	}
	
	public static class ShipmentSubscription {
		
		public static final String[] allFields = {
				"id",
				"source",
				"sourcecoordinates",
				"destination",
				"destinationcoordinates",
				"deliverer",
				"maxsize",
				"pickupfrom",
				"deliveruntil",
				"radius",
				"destinationradius"
				};
		
	}
	
	public static class User {
		
		public static final String[] allFields = {
				"active",
				"delivererrating",
				"email",
				"id",
				"identconfirmed",
				"identno",
				"identtype",
				"mailvalidated",
				"password",
				"role",
				"senderrating",
				"displayname",
				"displayNameOrMail",
				"defaultaddress",
				"paymentInformation",
				"addresshistory",
				"delivererstatuspending"
				};
		
		public static final String[] censoredDefault = {
				//"active",
				"delivererrating",
				//"email",
				"id",
				//"identconfirmed",
				//"identno",
				//"identtype",
				//"mailvalidated",
				//"password",
				//"role",
				"senderrating",
				"displayname",
				"displayNameOrMail",
				//"defaultaddress",
				"paymentInformation",
				//"addresshistory",
				"delivererstatuspending"
				};
		
	}

}
