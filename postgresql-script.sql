-- Following SQL-Cmds must be executed after init of g2ggeo-PostgreSQL-Schema


ALTER TABLE shipmentannouncementspot 
add COLUMN destinationgeog geography(Point, 4326),
add COLUMN sourcegeog geography(Point, 4326);

CREATE INDEX sa_sourcegeocoords ON shipmentannouncementspot USING BRIN ( sourcegeog );

CREATE INDEX sa_destinationgeocoords ON shipmentannouncementspot USING BRIN ( destinationgeog );

CREATE FUNCTION create_geo() RETURNS trigger AS $create_geo$
    BEGIN
        NEW.sourcegeog := ST_GeogFromText('SRID=4326;POINT(' || NEW.sourcelng || ' ' || New.sourcelat ||')');
		NEW.destinationgeog := ST_GeogFromText('SRID=4326;POINT(' || NEW.destinationlng || ' ' || New.destinationlat ||')');
        RETURN NEW;
    END;
$create_geo$ LANGUAGE plpgsql;

CREATE TRIGGER create_geo BEFORE INSERT OR UPDATE ON shipmentannouncementspot
    FOR EACH ROW EXECUTE PROCEDURE create_geo();


ALTER TABLE shipmentsubscriptiongeofence 
add COLUMN destinationgeog geography(Polygon, 4326),
add COLUMN sourcegeog geography(Polygon, 4326);

CREATE INDEX ssg_sourcegeocoords ON shipmentsubscriptiongeofence USING BRIN ( sourcegeog );

CREATE INDEX ssg_destinationgeocoords ON shipmentsubscriptiongeofence USING BRIN ( destinationgeog );

CREATE FUNCTION create_geo4geofence() RETURNS trigger AS $create_geo4geofence$
    BEGIN
		IF NEW.sourcelng IS NOT NULL AND NEW.sourcelat IS NOT NULL THEN
        	NEW.sourcegeog := St_Buffer(ST_GeogFromText('SRID=4326;POINT(' || NEW.sourcelng || ' ' || New.sourcelat ||')'), New.sourceradius);
		END IF;
		IF NEW.destinationlng IS NOT NULL AND NEW.destinationlat IS NOT NULL THEN
			NEW.destinationgeog := St_Buffer(ST_GeogFromText('SRID=4326;POINT(' || NEW.destinationlng || ' ' || New.destinationlat ||')'), New.destinationradius);
		END IF;
        RETURN NEW;
    END;
$create_geo4geofence$ LANGUAGE plpgsql;

CREATE TRIGGER create_geo4geofence BEFORE INSERT OR UPDATE ON shipmentsubscriptiongeofence
    FOR EACH ROW EXECUTE PROCEDURE create_geo4geofence();

ALTER TABLE shipmentsubscriptiongeofence 
alter COLUMN sourceradius DROP NOT NULL

ALTER TABLE shipmentsubscriptiongeofence 
alter COLUMN destinationradius DROP NOT NULL