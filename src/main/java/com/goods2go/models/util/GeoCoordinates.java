package com.goods2go.models.util;

public class GeoCoordinates {
	
	private static final String devider = ",";

	public static String toString(double latitude, double longitude) {
		return latitude + "," + longitude;
	}
	
	public static double[] fromString(String coordinatesString) {
		if(coordinatesString == null || coordinatesString.equals("")) {
			return null;
		}
		String[] strings = coordinatesString.split(devider);
		double[] coordinates = new double[2];
		coordinates[0] = Double.parseDouble(strings[0]);
		coordinates[1] = Double.parseDouble(strings[1]);
		return coordinates;
	}
}
