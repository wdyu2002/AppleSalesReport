package com.devculture.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
	
	public static String DEFAULT_APPLE_DATE_FORMAT = "MM/dd/yyyy";
	
	public static Date getDate(String date) {
		Date result = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat(DEFAULT_APPLE_DATE_FORMAT);
			result = format.parse(date);
        } catch(Exception ex) {
        	System.err.println("Failed to parse a date string " + ex.getMessage());
        }
		return result;
	}
	
}
