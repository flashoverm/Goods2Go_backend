package com.goods2go.models.util;

import org.springframework.http.converter.json.MappingJacksonValue;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class ModelFilters {
	
	public static MappingJacksonValue filterBeforeSend(Object container) {
    	//SimpleBeanPropertyFilter shipmentAnnouncementFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.ShipmentAnnouncement.allFields);
    	//SimpleBeanPropertyFilter addressFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.Address.censoredDefault);
    	//SimpleBeanPropertyFilter shipmentSizeFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.ShipmentSize.allFields);
    	//SimpleBeanPropertyFilter shipmentRequestFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.ShipmentRequest.allFields);
		SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.User.censoredDefault);
        FilterProvider filters = new SimpleFilterProvider()
        		//.addFilter("AddressFilter", addressFilter)
        		//.addFilter("ShipmentSizeFilter", shipmentSizeFilter)
        		//.addFilter("ShipmentAnnouncementFilter", shipmentAnnouncementFilter)
        		//.addFilter("ShipmentRequestFilter", shipmentRequestFilter)
        		.addFilter("UserFilter", userFilter);
        MappingJacksonValue mapping = new MappingJacksonValue(container);
        mapping.setFilters(filters);

        return mapping;
		
	}
	
	public static MappingJacksonValue filterButKeepAllFields(Object container) {
    	//SimpleBeanPropertyFilter shipmentAnnouncementFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.ShipmentAnnouncement.allFields);
    	//SimpleBeanPropertyFilter addressFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.Address.allFields);
    	//SimpleBeanPropertyFilter shipmentSizeFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.ShipmentSize.allFields);
    	//SimpleBeanPropertyFilter shipmentRequestFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.ShipmentRequest.allFields);
		SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept(ModelFieldLists.User.allFields);
        FilterProvider filters = new SimpleFilterProvider()
        		//.addFilter("AddressFilter", addressFilter)
        		//.addFilter("ShipmentSizeFilter", shipmentSizeFilter)
        		//.addFilter("ShipmentAnnouncementFilter", shipmentAnnouncementFilter)
        		//.addFilter("ShipmentRequestFilter", shipmentRequestFilter)
        		.addFilter("UserFilter", userFilter);
        MappingJacksonValue mapping = new MappingJacksonValue(container);
        mapping.setFilters(filters);

        return mapping;
		
	}

}
