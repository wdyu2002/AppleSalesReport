package com.devculture.tools.AppleSalesReporter;

import javax.swing.JTabbedPane;
import com.devculture.swing.SplitPane;
import com.devculture.tools.AppleSalesReporter.Data.ReportData;
import com.devculture.tools.AppleSalesReporter.Save.SaveManager;
import com.devculture.tools.AppleSalesReporter.UI.GraphView;
import com.devculture.tools.AppleSalesReporter.UI.QueryBar;
import com.devculture.tools.AppleSalesReporter.UI.TableView;
import com.devculture.util.PathFinder;

public class AppleSalesReporter extends SplitPane {
	
	private static final long serialVersionUID = 1L;

	/** variables **/
	
	private ReportData reportData = null;
	private QueryBar queryBar = null;
	private GraphView graphView = null;
	private TableView tableView = null;
	
	/** constructor **/
	
	public AppleSalesReporter() throws Exception {
		// make a saved directory if one doesn't already exist
		SaveManager.getInstance().initSaveDirectory();
		
		// one set of map data per game
		reportData = new ReportData(PathFinder.getInstance().getSubdirCanonicalPath(PathFinder.SUBDIR_DATA));
		
		queryBar = new QueryBar(reportData);
		graphView = new GraphView(reportData);
		tableView = new TableView(reportData);

		// initialize the table data
		reportData.addReportDataListener(graphView);
		reportData.addReportDataListener(tableView);
		reportData.filterData();
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Graph", graphView);
		tabPane.addTab("Table", tableView);
		
		setLeftPane(queryBar);
		setRightComponent(tabPane);
	}
	
}
