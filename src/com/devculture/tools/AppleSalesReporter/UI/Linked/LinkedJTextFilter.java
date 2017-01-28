package com.devculture.tools.AppleSalesReporter.UI.Linked;

import javax.swing.JTextField;

public class LinkedJTextFilter extends JTextField {
	
	private static final long serialVersionUID = 1L;
	
	private Object linkedObject;
	
	public void linkToObject(Object obj) {
		linkedObject = obj;
	}
	
	public Object getLinkedObject() {
		return linkedObject;
	}
	
}
