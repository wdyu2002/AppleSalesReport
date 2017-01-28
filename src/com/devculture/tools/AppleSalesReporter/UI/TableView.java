package com.devculture.tools.AppleSalesReporter.UI;

import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.devculture.tools.AppleSalesReporter.Data.ReportData;
import com.devculture.tools.AppleSalesReporter.Data.ReportDataListener;

public class TableView extends JScrollPane implements ReportDataListener, TablePopupMenuListener {
	
	/** variables **/
	
	private static final long serialVersionUID = 1L;

	private ReportData dataRef = null;
	
	private JTable table = null;
	private Vector<TableColumn> columns = new Vector<TableColumn>();
	
	public TableView(ReportData ref) {
		// data reference
		dataRef = ref;
		
		// table
		String[] titles = dataRef.getColumnTitles();
		table = new JTable(dataRef.getColumnDatasFiltered(), titles);
		initialize(table, titles);
	}
	
	private void initialize(JTable table, String[] titles) {
		TablePopupMenu popup = new TablePopupMenu(titles);
		popup.setListener(this);

		table.setComponentPopupMenu(popup);
		setViewportView(table);
		
		// clear all columns before continuing
		columns.removeAllElements();
		
		// grab all columns to make it easier to hide/show them
		for(int i=0; i<table.getColumnCount(); i++) {
			String title = table.getColumnName(i);
			columns.add(table.getColumn(title));
		}

		// initialize table columns (hide unnecessary)
		boolean[] selections = popup.getCheckboxSelections();
		for(int i=0; i<selections.length; i++) {
			toggleViewColumnNamed(titles[i], selections[i]);
		}
	}
	
	private boolean isColumnDisplayed(String column) {
		try {
			table.getColumn(column);
		} catch(IllegalArgumentException ex) {
			return false;
		}
		return true;
	}

	private TableColumn getColumn(String column) {
		for(TableColumn col : columns) {
			if(column.equals((String)col.getIdentifier())) {
				return col;
			}
		}
		// should not happen
		return null;
	}
	
	private void toggleViewColumnNamed(String column, boolean showColumn) {
		if(table != null) {
			if(!isColumnDisplayed(column)) {
				// column doesn't exist
				if(showColumn) {
					// ok to add
					table.addColumn(getColumn(column));
				} else {
					// hide column on a non-existing column, not cool
				}
			} else {
				if(showColumn) {
					// duplicate if shown again, ignore
				} else {
					// ok to remove
					table.removeColumn(getColumn(column));
				}
			}
		}
	}
	
	public void reportDataChanged() {
		// update table
		String[] titles = dataRef.getColumnTitles();
		table = new JTable(dataRef.getColumnDatasFiltered(), titles);
		initialize(table, titles);
		updateUI();
	}

	public void checkboxChanged(String checkboxIdentifier, boolean showColumn) {
		toggleViewColumnNamed(checkboxIdentifier, showColumn);
	}

}
