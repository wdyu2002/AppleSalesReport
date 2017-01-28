package com.devculture.tools.AppleSalesReporter.UI.Linked;

import javax.swing.JPanel;

public class LinkedJPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Object linkedObject = null;
	
	public void linkToObject(Object obj) {
		linkedObject = obj;
	}
	
	public Object getLinkedObject() {
		return linkedObject;
	}

}
