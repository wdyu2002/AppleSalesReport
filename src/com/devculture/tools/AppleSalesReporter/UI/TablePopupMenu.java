package com.devculture.tools.AppleSalesReporter.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

public class TablePopupMenu extends JPopupMenu implements ActionListener {
	
	/** variables **/
	
	private static final long serialVersionUID = 1L;
	private TablePopupMenuListener listener = null;
	private String[] unnecessaryColumnTitles = {"Provider", "Provider Country", "Vendor Identifier", "UPC", "ISRC", "Artist / Show", "Label/Studio/Network", "Preorder", "Season Pass", "ISAN", "CMA", "Asset/Content Flavor", "Vendor Offer Code", "Grid", "Promo Code", "Parent Identifier"};
	private Vector<JCheckBoxMenuItem> checkboxes = new Vector<JCheckBoxMenuItem>();
	
	/** constructor **/
	
	public TablePopupMenu(String[] columnTitles) {
		// set up the check boxes
		for(int i=0; i<columnTitles.length; i++) {
			addCheckBox(columnTitles[i], !isUnnecessaryColumn(columnTitles[i]));
		}
	}
	
	/** methods **/
	
	public boolean[] getCheckboxSelections() {
		int index = 0;
		boolean[] result = new boolean[checkboxes.size()];
		for(JCheckBoxMenuItem item : checkboxes) {
			result[index++]  = item.isSelected();
		}
		return result;
	}
	
	private void addCheckBox(String text, boolean selected) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
		item.setActionCommand(text);
		item.addActionListener(this);
		item.setSelected(selected);
		add(item);
		checkboxes.add(item);
	}
	
	private boolean isUnnecessaryColumn(String columnTitle) {
		for(int i=0; i<unnecessaryColumnTitles.length; i++) {
			if(columnTitle.equalsIgnoreCase(unnecessaryColumnTitles[i])) {
				return true;
			}
		}
		return false;
	}

	public void setListener(TablePopupMenuListener listener) {
		this.listener = listener;
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		// supports check boxes only
		if(listener != null) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
			listener.checkboxChanged(command, item.isSelected());
		}
	}
	
}
