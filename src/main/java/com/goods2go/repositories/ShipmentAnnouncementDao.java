package com.goods2go.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.ShipmentAnnouncement;
import com.goods2go.models.User;

@Transactional
public interface ShipmentAnnouncementDao extends CrudRepository<ShipmentAnnouncement, Long>, JpaSpecificationExecutor<ShipmentAnnouncement> {
	
	public List<ShipmentAnnouncement> findAllBySender(User user);
	
	public List<ShipmentAnnouncement> findAll();
	
	public List<ShipmentAnnouncement> findAll(Specification<ShipmentAnnouncement> spec);	
	

}
