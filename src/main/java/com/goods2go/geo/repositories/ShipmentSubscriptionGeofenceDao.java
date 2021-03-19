package com.goods2go.geo.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goods2go.geo.models.ShipmentSubscriptionGeofence;

@Transactional
public interface ShipmentSubscriptionGeofenceDao extends JpaRepository<ShipmentSubscriptionGeofence, Long> {
	
	@Query(value =  "Select * From shipmentsubscriptiongeofence Where ST_DWithin(sourcegeog, ST_GeogFromText('SRID=4326;POINT(' || :lng || ' ' || :lat || ')'), 0)", nativeQuery = true)
	public List<ShipmentSubscriptionGeofence> findBySource(
			@Param("lng") String lng,
			@Param("lat") String lat);
	
	@Query(value =  "Select * From shipmentsubscriptiongeofence Where ST_DWithin(destinationgeog, ST_GeogFromText('SRID=4326;POINT(' || :lng || ' ' || :lat || ')'), 0)", nativeQuery = true)
	public List<ShipmentSubscriptionGeofence> findByDestination(
			@Param("lng") String lng,
			@Param("lat") String lat);
	
	@Query(value =  "Select * From shipmentsubscriptiongeofence Where "
			+ "sourcegeog IS NOT NULL "
			+ "And destinationgeog IS NOT NULL "
			+ "And ST_DWithin(sourcegeog, ST_GeogFromText('SRID=4326;POINT(' || :sourcelng || ' ' || :sourcelat || ')'), 0) And ST_DWithin(destinationgeog, ST_GeogFromText('SRID=4326;POINT(' || :destinationlng || ' ' || :destinationlat || ')'), 0) "
			
			+ "Or sourcegeog IS NULL "
			+ "And destinationgeog IS NOT NULL "
			+ "And ST_DWithin(destinationgeog, ST_GeogFromText('SRID=4326;POINT(' || :destinationlng || ' ' || :destinationlat || ')'), 0) "
			
			+ "Or destinationgeog IS NULL "
			+ "And sourcegeog IS NOT NULL "
			+ "And ST_DWithin(sourcegeog, ST_GeogFromText('SRID=4326;POINT(' || :sourcelng || ' ' || :sourcelat || ')'), 0)", nativeQuery = true)
	public List<ShipmentSubscriptionGeofence> findBySourceAndOrDestination(
			@Param("sourcelng") String sourcelng,
			@Param("sourcelat") String sourcelat,
			@Param("destinationlng") String destinationlng,
			@Param("destinationlat") String destinationlat);

}
