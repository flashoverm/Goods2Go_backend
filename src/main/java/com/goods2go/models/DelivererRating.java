package com.goods2go.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "delivererrating")
@IdClass(com.goods2go.models.DelivererRatingId.class)
@Data
//@JsonFilter("DelivererRatingFilter")
public class DelivererRating {
	
	@Id
	private Long iduser;
	
	@Id
	private Long idshipment;
	
	private float rating;
	
	public DelivererRating() {
		super();
	}

}
