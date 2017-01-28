package com.devculture.tools.AppleSalesReporter.UI.Linked;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LinkedJButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	
	private Object linkedObject = null;
	
	public LinkedJButton(ImageIcon icon) {
		super(icon);
	}
	
	public void linkToObject(Object obj) {
		linkedObject = obj;
	}
	
	public Object getLinkedObject() {
		return linkedObject;
	}

}
