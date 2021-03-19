package com.goods2go.geo.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goods2go.geo.models.ShipmentAnnouncementSpot;

@Transactional
public interface ShipmentAnnouncementSpotDao extends JpaRepository<ShipmentAnnouncementSpot, Long> {
	
	@Query(value =  "Select * From shipmentannouncementspot Where ST_DWithin(ST_GeogFromText('SRID=4326;POINT(' || :lng || ' ' || :lat || ')'), sourcegeog, :radius)", nativeQuery = true)
	public List<ShipmentAnnouncementSpot> findBySource(
			@Param("lng") String lng,
			@Param("lat") String lat,
			@Param("radius") double radius);
	
	@Query(value =  "Select * From shipmentannouncementspot Where ST_DWithin(ST_GeogFromText('SRID=4326;POINT(' || :lng || ' ' || :lat || ')'), destinationgeog, :radius)", nativeQuery = true)
	public List<ShipmentAnnouncementSpot> findByDestination(
			@Param("lng") String lng,
			@Param("lat") String lat,
			@Param("radius") double radius);
	
	@Query(value =  "Select * From shipmentannouncementspot Where ST_DWithin(ST_GeogFromText('SRID=4326;POINT(' || :sourcelng || ' ' || :sourcelat || ')'), sourcegeog, :sourceradius) And ST_DWithin(ST_GeogFromText('SRID=4326;POINT(' || :destinationlng || ' ' || :destinationlat || ')'), destinationgeog, :destinationradius)", nativeQuery = true)
	public List<ShipmentAnnouncementSpot> findBySourceAndDestination(
			@Param("sourcelng") String sourcelng,
			@Param("sourcelat") String sourcelat,
			@Param("sourceradius") double sourceradius,
			@Param("destinationlng") String destinationlng,
			@Param("destinationlat") String destinationlat,
			@Param("destinationradius") double destinationradius);
	
	
}
