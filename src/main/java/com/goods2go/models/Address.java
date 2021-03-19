package com.goods2go.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "address")
@Data
//@JsonFilter("AddressFilter")
public class Address implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String companyname;
	
	private String lastname;
	
	private String firstname;
	
	private String streetno;
		
	private String street;
	
	private String postcode;
	
	private String city;
	
	private String country;
	
	public Address() {
		
	}
	
	/*
	 * Constructor for filter/subscription
	 */
	public Address(String postcode, String city) {
		super();
		this.postcode = postcode;
		this.city = city;
	}
	
	/*
	 * Constructor only address
	 */
	public Address(String streetno, String street, String postcode, String city, String country) {
		super();
		this.streetno = streetno;
		this.street = street;
		this.postcode = postcode;
		this.city = city;
		this.country = country;
	}

	/*
	 * Constructor for default address, source, destination
	 */
	public Address(String lastname, String firstname, String streetno, String street, String postcode, String city, String country) {
		super();
		this.lastname = lastname;
		this.firstname = firstname;
		this.streetno = streetno;
		this.street = street;
		this.postcode = postcode;
		this.city = city;
		this.country = country;
	}
	
	/*
	 * Constructor for default address, source, destination with company name
	 */
	public Address(String companyname, String lastname, String firstname, String streetno, String street, String postcode, String city, String country) {
		super();
		this.companyname = companyname;
		this.lastname = lastname;
		this.firstname = firstname;
		this.streetno = streetno;
		this.street = street;
		this.postcode = postcode;
		this.city = city;
		this.country = country;
	}
	
	/*
	 * Constructor for filter/subscription with country
	 */
	public Address(String postcode, String city, String country) {
		super();
		this.postcode = postcode;
		this.city = city;
		this.country = country;
	}	
    
    private String getNameAsString(){
    	if(firstname != null && !firstname.equals("") 
        		&& lastname != null && !lastname.equals("")) {
            return firstname + " " + lastname;
    	}
    	return "";
    }

    public String getAddressAsString(){
    	String address = getNameAsString();
    	if(!address.equals("")) {
    		address += "\n";
    	}
    	
    	if(street != null && !street.equals("")) {
    		if(streetno != null && !streetno.equals("")) {
                return address + street + " " + streetno + "\n" + postcode + " " + city;
    		}
            return address + street + "\n" + postcode + " " + city;
    	}
        return address + postcode + " " + city;
    }
}
