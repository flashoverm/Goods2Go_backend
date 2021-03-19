package com.goods2go.repositories;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.SenderRating;

@Transactional
public interface SenderRatingDao extends CrudRepository<SenderRating, Long>  {

}
