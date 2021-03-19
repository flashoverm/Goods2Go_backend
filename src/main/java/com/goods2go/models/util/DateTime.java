package com.goods2go.models.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime {
	

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    //public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final SimpleDateFormat JSON_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
	
	public static Date getCurrentDateTime() {
		Calendar cal = Calendar.getInstance();

	    return cal.getTime();
	}
	
	public static Date removeTime(Date date) {	
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    		
	    return new Date(cal.getTime().getTime());
	}
	
	public static Date getExpiryDate(int expirationMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        cal.add(Calendar.MINUTE, expirationMinutes);
        return cal.getTime();
	}
	
	public static Date getExpiryDateFrom(Date date, int expirationMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, expirationMinutes);
        return cal.getTime();
	}
	
	public static boolean checkIfDatesAreValid(Date earliestPickupDate, Date latestPickupDate, Date latestDeliveryDate) {
		Date today = getCurrentDate();
		System.out.println("today " + today + " - millis " + today.getTime());
		System.out.println("epd " + earliestPickupDate + " - millis " + earliestPickupDate.getTime());
		System.out.println("lpd " + latestPickupDate + " - millis " + latestPickupDate.getTime());
		System.out.println("ldd " + latestDeliveryDate + " - millis " + latestDeliveryDate.getTime());

		if(removeTime(earliestPickupDate).compareTo(today) < 0 || 
				removeTime(latestPickupDate).compareTo(today) < 0 ||
				removeTime(latestDeliveryDate).compareTo(today) < 0 ||
				removeTime(latestPickupDate).compareTo(removeTime(earliestPickupDate)) < 0 ||
				removeTime(latestDeliveryDate).compareTo(removeTime(latestPickupDate)) < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean checkIfDatesAreValidForSearch(Date earliestPickupDate, Date latestDeliveryDate) {
		Date today = getCurrentDate();
		if(removeTime(earliestPickupDate).compareTo(today) < 0 || 
				removeTime(latestDeliveryDate).compareTo(today) < 0 ||
				removeTime(latestDeliveryDate).compareTo(removeTime(earliestPickupDate)) < 0) {
			return false;
		} else {
			return true;
		}
	}
}
