package com.goods2go.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.ShipmentSubscription;
import com.goods2go.models.User;

@Transactional
public interface ShipmentSubscriptionDao extends CrudRepository<ShipmentSubscription, Long>, JpaSpecificationExecutor<ShipmentSubscription>{

	public List<ShipmentSubscription> findAllByDeliverer(User user);
	
	public List<ShipmentSubscription> findAll(Specification<ShipmentSubscription> spec);	

}
