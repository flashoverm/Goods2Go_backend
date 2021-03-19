package com.goods2go.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.ShipmentRequest;
import com.goods2go.models.User;

@Transactional
public interface ShipmentRequestDao extends CrudRepository<ShipmentRequest, Long>  {

	public List<ShipmentRequest> findAllByDeliverer(User user);

}
