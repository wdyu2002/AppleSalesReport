package com.devculture.tools.AppleSalesReporter.Data;

import java.util.Vector;

import com.devculture.swing.PopupMessage;

public class FilterPostfixConverter {
	
	public static Vector<ReportFilter> getTransformedPostfixFilters(Vector<ReportFilter> infixFilters) {
		Vector<ReportFilter> postfixFilters = null;
		Vector<ReportFilter> operatorFilters = null;
		
		if(isValidForTransform(infixFilters)) {
			// this vector is the operator stack
			postfixFilters = new Vector<ReportFilter>();
			operatorFilters = new Vector<ReportFilter>();

			// convert into postfix format
			for(ReportFilter filter : infixFilters) {
				switch(filter.getType()) {
				case OPERATOR:
					operatorFilters.add(filter);
					break;
				case PARENTHESIS:
					if(filter.getText().equals("(")) {
						// ignore left parenthesis
					} else {
						// pop top element from operator filter & place into final postfix filter
						postfixFilters.add(operatorFilters.remove(operatorFilters.size()-1));
					}
					break;
				case DATE:
				case TEXT:
					postfixFilters.add(filter);
					break;					
				}
			}
			
			// pop the rest of operators onto the postfix filter
			while(operatorFilters.size() > 0) {
				postfixFilters.add(operatorFilters.remove(operatorFilters.size()-1));
			}
			
			// done
			return postfixFilters;
		}
		
		// failed
		return null;
	}
	
	private static boolean isValidForTransform(Vector<ReportFilter> infixFilters) {
		int balance = 0;
		
		// count the number of parenthesis
		for(ReportFilter filter : infixFilters) {
			String text = filter.getText();
			if(text.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_LPAREN)) {
				balance++;
			} else if(text.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_RPAREN)) {
				balance--;
			}
		}
		
		if(balance != 0) {
			PopupMessage.showErrorMessage("Parenthesis do not match up");
			return false;
		}
		
		return true;
	}

}
