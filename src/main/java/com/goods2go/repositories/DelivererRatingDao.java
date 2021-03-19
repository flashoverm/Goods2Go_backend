package com.goods2go.repositories;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.DelivererRating;

@Transactional
public interface DelivererRatingDao extends CrudRepository<DelivererRating, Long>  {

}
