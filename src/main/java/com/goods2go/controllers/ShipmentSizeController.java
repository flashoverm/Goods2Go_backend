package com.goods2go.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.models.ShipmentSize;
import com.goods2go.repositories.ShipmentSizeDao;

@RestController
@RequestMapping("/shipmentsize")
public class ShipmentSizeController {
	
    public static final String SIZES_URL = "/shipmentsize/all";
	
	@Autowired
	ShipmentSizeDao shipmentSizeDao;
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public ShipmentSize save(@RequestBody ShipmentSize shipmentSize) {
		return shipmentSizeDao.save(shipmentSize);
	}
	
	//@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public List<ShipmentSize> getAllShipmentSize() {
		return shipmentSizeDao.findAll();
	}

}