package com.goods2go.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.Shipment;
import com.goods2go.models.User;

@Transactional
public interface ShipmentDao extends CrudRepository<Shipment, Long>  {
	
	//Pending shipments
	public List<Shipment> findByPickupdatetimeIsNullAndSender(User user);
	
	//Active shipments
	public List<Shipment> findByPickupdatetimeNotNullAndDeliverydatetimeIsNullAndSender(User user);
	
	//Closed shipments
	public List<Shipment> findByDeliverydatetimeNotNullAndSender(User user);

	//Pending shipments
	public List<Shipment> findByPickupdatetimeIsNullAndDeliverer(User user);

	//Active shipments
	public List<Shipment> findByPickupdatetimeNotNullAndDeliverydatetimeIsNullAndDeliverer(User user);
	
	//Closed shipments
	public List<Shipment> findByDeliverydatetimeNotNullAndDeliverer(User user);

	public Shipment findByQrstring(String qrstring);


}
