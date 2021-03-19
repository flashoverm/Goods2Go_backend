package com.goods2go.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shipmentsize")
@Data
//@JsonFilter("ShipmentSizeFilter")
public class ShipmentSize {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	
	private String name;
	
	/**
	 * A quantifier for sorting the sizes: 0 (smallest) - 100 (biggest)
	 */
	private int quantifier;
	
	//inch, m, ...
	private String measure;
	
	private float maxlength;
	
	private float maxheight;
	
	private float maxwidth;
	
	private float maxweight;
	
	private float pricefactor;
	
	public ShipmentSize() {
		super();
	}

	public ShipmentSize(String name, int quantifier, String measure, float maxlength, float maxheight, float maxwidth, float maxweight,
			float pricefactor) {
		super();
		this.name = name;
		this.quantifier = quantifier;
		this.measure = measure;
		this.maxlength = maxlength;
		this.maxheight = maxheight;
		this.maxwidth = maxwidth;
		this.maxweight = maxweight;
		this.pricefactor = pricefactor;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShipmentSize other = (ShipmentSize) obj;
		if (Float.floatToIntBits(maxheight) != Float.floatToIntBits(other.maxheight))
			return false;
		if (Float.floatToIntBits(maxlength) != Float.floatToIntBits(other.maxlength))
			return false;
		if (Float.floatToIntBits(maxweight) != Float.floatToIntBits(other.maxweight))
			return false;
		if (Float.floatToIntBits(maxwidth) != Float.floatToIntBits(other.maxwidth))
			return false;
		if (measure == null) {
			if (other.measure != null)
				return false;
		} else if (!measure.equals(other.measure))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Float.floatToIntBits(pricefactor) != Float.floatToIntBits(other.pricefactor))
			return false;
		if (quantifier != other.quantifier)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(maxheight);
		result = prime * result + Float.floatToIntBits(maxlength);
		result = prime * result + Float.floatToIntBits(maxweight);
		result = prime * result + Float.floatToIntBits(maxwidth);
		result = prime * result + ((measure == null) ? 0 : measure.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(pricefactor);
		result = prime * result + quantifier;
		return result;
	}

}
