package com.goods2go.repositories;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.VerificationToken;

@Transactional
public interface VerificationTokenDao extends CrudRepository<VerificationToken, Long> {
	
	  public VerificationToken findOneByToken(String token);
	  
}
