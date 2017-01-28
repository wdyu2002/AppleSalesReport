package com.devculture.tools.AppleSalesReporter.Data;

import java.util.Date;

import com.devculture.util.DateConverter;

public class ReportPlot implements Comparable<ReportPlot> {
	
	/** variables **/
	
	private String dateStr = null;
	private Date date = null;
	private float value = 0;
	
	/** constructor **/
	
	public ReportPlot(String date, String initial) {
		this.dateStr = date;
		this.date = DateConverter.getDate(dateStr);
		adjustValue(initial);
	}

	/** getters **/
	
	public Date getDate() {
		return date;
	}
	
	public String getDateStr() {
		return dateStr;
	}
	
	public float getValue() {
		return value;
	}
	
	/** alteration **/
	
	public void adjustValue(String adjustment) {
		value += Float.parseFloat(adjustment);
	}

	/** debug **/
	
	public String toString() {
		return date.toString() + " = " + value;
	}
	
	/** compare (apparently does nothing) **/

	public int compareTo(Date other) {
		return date.compareTo(other);
	}

	public boolean equals(Date other) {
		return date.equals(other);
	}
	
	public int compareTo(ReportPlot other) {
		return date.compareTo(other.getDate());
	}
	
	public boolean equals(ReportPlot plot) {
		return date.equals(plot.getDate());
	}
}
