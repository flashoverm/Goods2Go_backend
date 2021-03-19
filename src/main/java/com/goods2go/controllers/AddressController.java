package com.goods2go.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goods2go.models.Address;
import com.goods2go.repositories.AddressDao;

@RestController
@RequestMapping("/address")
public class AddressController {
	
	@Autowired
	private AddressDao addressDao;
	
	//Not used
	@RequestMapping(value="/load", method=RequestMethod.POST)
	public Address load(@RequestBody Address address) {
		return addressDao.findOne(address.getId());
	}
	
}
