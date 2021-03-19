package com.goods2go.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.goods2go.models.enums.PaymentType;

import lombok.Data;

@Entity
@Table(name = "paymentinformation")
@Data
//@JsonFilter("PaymentInformationFilter")
public class PaymentInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private PaymentType paymentType;
	
	/**
	 * i.e. paypal address or IBAN
	 */
	private String identifier;
	
	/**
	 * Optional (i.e. "firstname lastname" for banktransfer)
	 */
	private String name;
	
	public PaymentInformation() {
		super();
	}
	
	public PaymentInformation(PaymentType paymentType, String identifier) {
		super();
		this.paymentType = paymentType;
		this.identifier = identifier;
	}
	
	public PaymentInformation(PaymentType paymentType, String name, String identifier) {
		super();
		this.paymentType = paymentType;
		this.name = name;
		this.identifier = identifier;
	}
}
