package com.devculture.swing;

import java.awt.Container;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/** singleton **/
	
	private static MainFrame instance = null;

	private MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static MainFrame getInstance() {
		if(instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}
	
	/** main app setting functions **/
	
	public void setAppName(String appName) {
		setTitle("Dev.Culture | " + appName);
	}
	
	public void setAppSize(int width, int height) {
		setSize(width, height);
	}
	
	public void setAppPane(Container component) {
		setContentPane(component);
	}

}
