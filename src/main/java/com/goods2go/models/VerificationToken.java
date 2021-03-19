package com.goods2go.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.goods2go.config.MailConfig;
import com.goods2go.models.util.DateTime;

import lombok.Data;

@Entity
@Table(name = "verificationtoken")
@Data
public class VerificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String token;
    
    @OneToOne(cascade=CascadeType.REFRESH)
    private User user;
    
    private Date expiryDate;
    
    public VerificationToken() {
    	super();
    }
    
    public VerificationToken(User user) {
    	super();
    	this.user = user;
    	this.expiryDate = DateTime.getExpiryDate(MailConfig.MAIL_VERIFICATION_VALIDITY_MIN);
    	this.token = generateToken();
    }
    
    private String generateToken() {
    	return UUID.randomUUID().toString();
    }
    
    public boolean isValid() {
    	return (DateTime.getCurrentDateTime().compareTo(expiryDate) <=0);
    }
}
