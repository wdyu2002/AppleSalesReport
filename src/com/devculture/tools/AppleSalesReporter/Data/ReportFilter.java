package com.devculture.tools.AppleSalesReporter.Data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import com.devculture.util.DateConverter;

public class ReportFilter implements Serializable {

	/** variables **/
	
	private static final long serialVersionUID = 1L;
	public static final String FILTER_COMMAND_INVALID = "-";
	public static final String FILTER_COMMAND_AND = "AND";
	public static final String FILTER_COMMAND_OR = "OR";
	public static final String FILTER_COMMAND_LPAREN = "(";
	public static final String FILTER_COMMAND_RPAREN = ")";
	
	private String text;
	private String filter;
	private FilterType type;
	private FilterComparison compare;
	
	public static final String[] DEFAULT_FILTER_COMMANDS = {
		FILTER_COMMAND_INVALID, 
		FILTER_COMMAND_AND, 
		FILTER_COMMAND_OR,
		FILTER_COMMAND_LPAREN,
		FILTER_COMMAND_RPAREN,
	};
	
	public static enum FilterType {
		DATE,			/* >, <, >=, <= */
		TEXT,			/* ==, != */
		OPERATOR, 		/* AND, OR */
		PARENTHESIS, 	/* (, ) */
	}
	
	public static enum FilterComparison {
		NONE,
		LESS_THAN,
		LESS_THAN_AND_EQUAL,
		GREATER_THAN,
		GREATER_THAN_AND_EQUAL,
		EQUAL,
		NOT_EQUAL,
	}
	
	/** constructor **/
	
	public ReportFilter(String text, FilterType type, FilterComparison compare) {
		this.text = text;
		this.type = type;
		this.compare = compare;
	}
	
	/** methods **/
	
	public void setFilter(String filter) {
		// we can only set the filter value if TEXT/DATE type
		if(type == FilterType.DATE || type == FilterType.TEXT) {
			this.filter = filter;
		}
	}
	
	public String getInequality() {
		switch(compare) {
		case LESS_THAN:
			return "<";
		case LESS_THAN_AND_EQUAL:
			return "<=";
		case GREATER_THAN:
			return ">";
		case GREATER_THAN_AND_EQUAL:
			return ">=";
		case EQUAL:
			return "==";
		case NOT_EQUAL:
			return "!=";
		}
		return "";
	}
	
	public String getText() {
		return text;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public FilterType getType() {
		return type;
	}
	
	/** filtering **/
	
	public boolean isFiltered(String string) {
		if(filter == null) {
			// if filter is null, filter nothing
			return false;
		} else if(type == FilterType.TEXT) {
			switch(compare) {
			case LESS_THAN:
				return !(filter.compareTo(string) > 0);
			case LESS_THAN_AND_EQUAL:
				return !(filter.compareTo(string) >= 0);
			case GREATER_THAN:
				return !(filter.compareTo(string) < 0);
			case GREATER_THAN_AND_EQUAL:
				return !(filter.compareTo(string) <= 0);
			case EQUAL:
				return filter.compareTo(string) != 0;
			case NOT_EQUAL:
				return filter.compareTo(string) == 0;
			}
		} else if(type == FilterType.DATE) {
			// apple date format
			Date stringDate = DateConverter.getDate(string);
			Date filterDate = DateConverter.getDate(filter);
			
			// if string or filter are NOT valid dates, filter them out
			if(stringDate == null || filterDate == null) {
				return true;
			}
			
			switch(compare) {
			case LESS_THAN:
				return !(filterDate.compareTo(stringDate) > 0);
			case LESS_THAN_AND_EQUAL:
				return !(filterDate.compareTo(stringDate) >= 0);
			case GREATER_THAN:
				return !(filterDate.compareTo(stringDate) < 0);
			case GREATER_THAN_AND_EQUAL:
				return !(filterDate.compareTo(stringDate) <= 0);
			case EQUAL:
				return filterDate.compareTo(stringDate) != 0;
			case NOT_EQUAL:
				return filterDate.compareTo(stringDate) == 0;
			}
		}
		
		// by default, filter nothing
		return false;
	}

	/** debug **/
	
	public String toString() {
		String typeStr = null;
		switch(type) {
		case DATE:
			typeStr = "Type:DATE";
			break;
		case TEXT:
			typeStr = "Type:TEXT";
			break;
		case OPERATOR:
			typeStr = "Type:OPER";
			break;
		case PARENTHESIS:
			typeStr = "Type:PARE";
			break;
		}		
		return "[" + text + " " + getInequality() + " " + filter+ " :: " + typeStr + "]";
	}
	
	/** serializable **/
	
	public void readObject(ObjectInputStream stream) throws Exception {
		stream.defaultReadObject();
	}
	
	public void writeObject(ObjectOutputStream stream) throws Exception {
		stream.defaultWriteObject();
	}
	
}