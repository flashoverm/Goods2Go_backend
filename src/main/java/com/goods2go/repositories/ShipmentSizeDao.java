package com.goods2go.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.ShipmentSize;

@Transactional
public interface ShipmentSizeDao extends CrudRepository<ShipmentSize, Long> {
	
	  public ShipmentSize findByName(String name);
	  
	  public List<ShipmentSize> findAll();


}
