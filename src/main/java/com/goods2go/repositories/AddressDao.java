package com.goods2go.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.Address;

@Transactional
public interface AddressDao extends CrudRepository<Address, Long> {
	
	public List<Address> findAll();
	
	public Address findOneByFirstnameAndLastnameAndStreetAndStreetnoAndPostcodeAndCityAndCountryAndCompanyname(
			String firstname, String lastname, String street, String streetno, String postcode, String city, String country, String companyname);

}
