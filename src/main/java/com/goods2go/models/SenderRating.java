package com.goods2go.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "senderrating")
@IdClass(com.goods2go.models.SenderRatingId.class)
@Data
//@JsonFilter("SenderRatingFilter")
public class SenderRating {
	
	@Id
	private Long iduser;
	
	@Id
	private Long idshipment;
	
	private float rating;
	
	public SenderRating() {
		super();
	}
}
