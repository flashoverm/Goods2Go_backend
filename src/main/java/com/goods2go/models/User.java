package com.goods2go.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.goods2go.models.enums.IdentType;
import com.goods2go.models.enums.Role;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
@JsonFilter("UserFilter")
public class User {
	
  private static final int ADDRESS_HISTORY_LENGTH = 15;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  
  @NotNull
  private String email;
  
  private String displayname;
  
  private String password;
  
  private Role role;
  
  private boolean active;
  
  @ManyToOne
  private Address defaultaddress;
  
  private boolean mailvalidated;
  
  /**
   * Rating of the user as sender
   */
  private float senderrating;
  
  private int senderratingcount;
  
  /**
   * Rating of the user as deliverer
   */
  private float delivererrating;
  
  private int delivererratingcount;
  
  private IdentType identtype;
  
  private String identno;
  
  private boolean identconfirmed;
  
  private boolean delivererstatuspending;
  
  @OneToOne(cascade=CascadeType.ALL)
  private PaymentInformation paymentInformation;
  
  @ManyToMany(fetch = FetchType.EAGER)
  private List<Address> addresshistory;

  
  	public User() {		
  		super();
  	}

	public User(long id) { 
		super();
		this.id = id;
	}
	
	public User(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
		
	public User(String email, String password, IdentType identtype, String identno) {
		super();
		this.email = email;
		this.password = password;
		this.identtype = identtype;
		this.identno = identno;
	}
	
	public String getDisplayNameOrMail() {
		if(displayname != null && !displayname.equals("")) {
			return displayname;
		}
		return email;
	}
	
	public void updateAddressHistory(Address address) {
		if(addresshistory.contains(address)) {
			addresshistory.remove(address);
		} else if(addresshistory.size() >= ADDRESS_HISTORY_LENGTH){
			addresshistory.remove(0);
		}
		addresshistory.add(address);
	}
	
	public void updateSenderRating(float rating) {
		senderrating = calcRunningAverage(senderratingcount, senderrating, rating);
		senderratingcount += 1;	
	}
	
	public void updateDelivererRating(float rating) {
		delivererrating = calcRunningAverage(delivererratingcount, delivererrating, rating);
		delivererratingcount += 1;
	}
	
	public void revokeSenderRating(float rating) {
		senderrating = revokeRating(senderratingcount, senderrating, rating);
		senderratingcount -= 1;
	}
	
	public void revokeDelivererRating(float rating) {
		delivererrating = revokeRating(delivererratingcount, delivererrating, rating);
		delivererratingcount -= 1;
	}
	
	private float calcRunningAverage (int count, float oldAvg, float rating){
		return ((oldAvg * count) + rating) / (count + 1);
	}
	
	private float revokeRating(int count, float average, float rating) {
		return ((average*count)-rating)/(count - 1);
	}
}
