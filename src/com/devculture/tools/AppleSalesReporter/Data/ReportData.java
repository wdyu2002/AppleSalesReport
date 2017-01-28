package com.devculture.tools.AppleSalesReporter.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.devculture.swing.PopupMessage;
import com.devculture.tools.AppleSalesReporter.Data.ReportFilter.FilterType;
import com.devculture.util.FileManager;
import com.devculture.util.StaticDefines;

public class ReportData {

	/** variables **/
	
	private Vector<ReportPlot> plots = new Vector<ReportPlot>();
	private Vector<ReportDataListener> listeners = new Vector<ReportDataListener>();
	private Vector<ReportFilter> infixFilters = new Vector<ReportFilter>();
	
	// data post-filtering
	private Vector<String[]> filteredTableData = new Vector<String[]>();
	
	private String[] columnTitles = null;	// titles of the data columns
	private String[][] columnDatas = null;	// original data, not filtered
	private int rowCount = 0;
	private int columnCount = 0;
	private int columnForUnits = 0;
	private int columnForDates = 0;
	
	private enum ReportDataRowComparison {
		RESULT_IS_SAME,
		RESULT_IS_DIFFERENT,
		RESULT_IS_SRC_LARGER,
		RESULT_IS_SRC_SMALLER,
	}
	
	/** constructor **/
	
	public ReportData(String dataDir) throws IOException {
		File directory = new File(dataDir);
		
		if(FileManager.isDirectory(directory) && FileManager.hasChildren(directory)) {
			File[] children = FileManager.getChildrenFiltered(directory);
			
			if(children.length > 0) {
				Vector<String[]> data = new Vector<String[]>();
				BufferedReader reader = null;
				String line = null;
				
				for(int i=0; i<children.length; i++) {
					int lineNumber = 0;

					// create reader
					reader = new BufferedReader(new FileReader(children[i]));
					
					// read line by line
					while((line = reader.readLine()) != null) {
						lineNumber++;
						
						// grab columns
						String[] columns = line.split("\t");
						
						// set up the titles
						if(columnTitles == null) {
							columnTitles = columns;
							columnCount = columnTitles.length;
						} else {
							ReportDataRowComparison result = compareRows(columnTitles, columns);
							switch(result) {
							case RESULT_IS_SRC_LARGER:
							case RESULT_IS_SRC_SMALLER:
								System.err.println("Line " + lineNumber + " in file '" + children[i].getName() + "' has an unexpected number of columns = " + columns.length);
								break;
							case RESULT_IS_DIFFERENT:
								data.add(columns);
								break;
							case RESULT_IS_SAME:
								// ignore, we're at another column titles row
								break;
							}
						}
					}
				}
				
				// set the row count
				rowCount = data.size();
				
				// convert this data into 2D array
				if(columnCount > 0 && rowCount > 0) {
					// the final output data array
					columnDatas = new String[rowCount][columnCount];
					for(int i=0; i<rowCount; i++) {
						String[] row = data.elementAt(i);
						for(int j=0; j<columnCount; j++) {
							columnDatas[i][j] = row[j];
						}
					}
				} else {
					throw new IOException("No rows were recorded from the data directory");
				}
				
				// figure out the key columns necessary for graphing
				for(int i=0; i<columnCount; i++) {
					if(columnTitles[i].equals("Units")) {
						columnForUnits = i;
					} else if(columnTitles[i].equals("Begin Date")) {
						columnForDates = i;
					}
				}
				
				// initialize the filtered Datas vector
				for(int i=0; i<rowCount; i++) {
					// add every row into filtered data
					filteredTableData.add(columnDatas[i]);
				}
				
				// debug
				if(StaticDefines.GLOBAL_DEBUG_DATA_MODE) {
					// print the column titles
					for(int j=0; j<columnCount; j++) {
						System.out.print(columnTitles[j] + ", ");
					}
					System.out.print("\n");
					
					// print each of the columns of data
					for(int i=0; i<rowCount; i++) {
						String[] row = data.elementAt(i);
						for(int j=0; j<row.length; j++) {
							System.out.print(row[j] + ", ");
						}
						System.out.print("\n");
					}
				}
			} else {
				throw new IOException("No files found in the data directory");
			}
		} else {
			throw new IOException("Wrong directory or directory is empty");
		}
	}
	
	/** filter machine **/
	
	public void addFilterCommand(ReportFilter filter) {
		infixFilters.add(filter);
	}
	
	public void insertFilterCommandAt(ReportFilter filter, int index) {
		infixFilters.insertElementAt(filter, index);
	}
	
	public void removeAllFilterCommands() {
		infixFilters.removeAllElements();
	}
	
	public void removeFilterCommand(ReportFilter filter) {
		infixFilters.remove(filter);
	}
	
	public ReportFilter removeFilterCommandAtIndex(int index) {
		return infixFilters.remove(index);
	}

	public ReportFilter getFilterCommandAtIndex(int index) {
		return infixFilters.get(index);
	}
	
	public int getFilterCommandCount() {
		return infixFilters.size();
	}
	
	/** event listening delegates **/
	
	public void addReportDataListener(ReportDataListener listener) {
		listeners.add(listener);
	}
	
	public void removeReportDataListener(ReportDataListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners() {
		for(ReportDataListener listener : listeners) {
			listener.reportDataChanged();
		}
	}
	
	/** filtering **/
		
	private boolean[] filterOperationAND(boolean[] set1, boolean[] set2) {
		if(set1.length == rowCount && set2.length == rowCount) {
			boolean[] filteredResults = new boolean[rowCount];
			for(int i=0; i<rowCount; i++) {
				filteredResults[i] = (set1[i] || set2[i]);
			}
			return filteredResults;
		} else {
			System.err.println("Error set1/set2 lengths are unexpected");
		}
		return null;
	}
	
	private boolean[] filterOperationOR(boolean[] set1, boolean[] set2) {
		if(set1.length == rowCount && set2.length == rowCount) {
			boolean[] filteredResults = new boolean[rowCount];
			for(int i=0; i<rowCount; i++) {
				filteredResults[i] = (set1[i] && set2[i]);
			}
			return filteredResults;
		} else {
			System.err.println("Error set1/set2 lengths are unexpected");
		}
		return null;
	}
	
	private boolean[] getFilteredResult(ReportFilter filter) {
		// by default, none are filtered = false
		boolean[] filteredResults = new boolean[rowCount];
		
		// type will never be a parenthesis or operator
		if(filter.getType() == FilterType.DATE || filter.getType() == FilterType.TEXT) {
			String filterColumn = filter.getText();
			for(int col=0; col<columnCount; col++) {
				if(columnTitles[col].equalsIgnoreCase(filterColumn)) {
					for(int row=0; row<rowCount; row++) {
						// if item is to be hidden, then result[i] = true
						// if item is to be displayed, then result[i] = false
						filteredResults[row] = filter.isFiltered(columnDatas[row][col]);
					}
					break;
				}
			}
		}

		return filteredResults;
	}

	public void filterData() {
		Vector<ReportFilter> postfixFilters = FilterPostfixConverter.getTransformedPostfixFilters(infixFilters);
		
		if(postfixFilters == null) {
			PopupMessage.showErrorMessage("Commands failed to convert into postfix notation");
		} else {
			// must use the FilterMachine object's postfix format in order to filter the data
			Vector<boolean[]> workstack = new Vector<boolean[]>();
			
			for(ReportFilter filter : postfixFilters) {
				if(filter.getType() == FilterType.TEXT || filter.getType() == FilterType.DATE) {
					workstack.add(getFilteredResult(filter));
				} else if(filter.getType() == FilterType.OPERATOR) {
					// workstack size must be >= 2
					if(workstack.size() < 2) {
						PopupMessage.showErrorMessage("Found operator but without data sources");
						return;
					}
					
					boolean[] set1 = workstack.remove(workstack.size()-1); // pop
					boolean[] set2 = workstack.remove(workstack.size()-1); // pop

					// do operation calculations
					if(filter.getText().equalsIgnoreCase(ReportFilter.FILTER_COMMAND_AND)) {
						workstack.add(filterOperationAND(set1, set2));
					} else if(filter.getText().equalsIgnoreCase(ReportFilter.FILTER_COMMAND_OR)) {
						workstack.add(filterOperationOR(set1, set2));
					} else {
						// invalid operator, should never happen
						PopupMessage.showErrorMessage("Found unexpected operator filter inside postfix stack");
						return;
					}
				} else if(filter.getType() == FilterType.PARENTHESIS) {
					// invalid, should never happen
					PopupMessage.showErrorMessage("Found parenthesis filter inside postfix stack");
					return;
				}
			}
			
			// workstack should only be a single element by this point
			boolean[] result = null;
			
			if(workstack.size() == 0) {
				// no filters at all...
				result = new boolean[rowCount];
			} else if(workstack.size() == 1) {
				result = workstack.firstElement();
			} else {
				PopupMessage.showErrorMessage("Unexpected workstack size post-filtering");
				return;
			}
			
			// clean previous plots
			plots.removeAllElements();
			filteredTableData.removeAllElements();
			
			// set up plots
			for(int row=0; row<rowCount; row++) {
				// if row is not filtered, add it to the display
				if(!result[row]) {
					String unitSold = columnDatas[row][columnForUnits];
					String beginDate = columnDatas[row][columnForDates];
					
					// add this row as a valid (non-filtered) row
					filteredTableData.add(columnDatas[row]);
					
					// find previously existing plot, if found alter its value
					boolean found = false;
					for(ReportPlot p : plots) {
						if(p.getDateStr().equals(beginDate)) {
							p.adjustValue(unitSold);
							found = true;
							break;
						}
					}
					
					if(!found) {
						plots.add(new ReportPlot(beginDate, unitSold));	
					}
				}
			}
			
			notifyListeners();
		}
	}
	
	public Vector<ReportPlot> getPlots() {
		return plots;
	}
	
	/** getters & setters **/

	public String[][] getColumnDatas() {
		return columnDatas;
	}
	
	public String[] getColumnTitles() {
		return columnTitles;
	}

	public String[][] getColumnDatasFiltered() {
		Object[] array = filteredTableData.toArray();
		String[][] filtered = new String[array.length][];
		for(int i=0; i<array.length; i++) {
			filtered[i] = (String[]) array[i];
		}
		return filtered;
	}
	
	/** compare columns in different rows **/
	
	private ReportDataRowComparison compareRows(String[] src, String[] dest) {
		if(src.length < dest.length) {
			return ReportDataRowComparison.RESULT_IS_SRC_SMALLER;
		} else if(src.length > dest.length) {
			return ReportDataRowComparison.RESULT_IS_SRC_LARGER;
		} else {
			// either they are the SAME (return 2), or they are NOT (return 0)
			for(int i=0; i<src.length; i++) {
				if(!src[i].equalsIgnoreCase(dest[i])) {
					return ReportDataRowComparison.RESULT_IS_DIFFERENT;
				}
			}
		}
		return ReportDataRowComparison.RESULT_IS_SAME;
	}
	
	/** saving the filters **/
	
	public void saveInfixFilters(ObjectOutputStream stream) throws Exception {
		// write the size of the vector
		stream.writeInt(infixFilters.size());
		
		// write the contents of the vector
		for(ReportFilter filter : infixFilters) {
			stream.writeObject(filter);
		}
	}
	
	public void loadInfixFilters(ObjectInputStream stream) throws Exception {
		// clear any currently existing infix filters
		infixFilters.removeAllElements();
		
		ReportFilter filter = null;
		int size = stream.readInt();
		for(int i=0; i<size; i++) {
			filter = (ReportFilter)stream.readObject();
			if(filter != null) {
				infixFilters.add(filter);
			}
		}
	}
		
}
