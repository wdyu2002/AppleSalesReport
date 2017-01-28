package com.devculture.swing;

import javax.swing.JOptionPane;

public class PopupMessage {
	
	/** static methods **/
	
	public static String askForUserInput(String title, String text) {
		return JOptionPane.showInputDialog(MainFrame.getInstance(), text, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int askForUserYesNo(String title, String text) {
		return JOptionPane.showConfirmDialog(MainFrame.getInstance(), text, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showErrorMessage(String error) {
		JOptionPane.showMessageDialog(MainFrame.getInstance(), error, "Error", JOptionPane.WARNING_MESSAGE);
	}
	
}
