package com.devculture.tools.AppleSalesReporter.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.devculture.swing.PopupMessage;
import com.devculture.swing.ThinBorder;
import com.devculture.tools.AppleSalesReporter.Data.Favorite;
import com.devculture.tools.AppleSalesReporter.Data.ReportData;
import com.devculture.tools.AppleSalesReporter.Data.ReportFilter;
import com.devculture.tools.AppleSalesReporter.Data.ReportFilter.FilterComparison;
import com.devculture.tools.AppleSalesReporter.Data.ReportFilter.FilterType;
import com.devculture.tools.AppleSalesReporter.Save.SaveManager;
import com.devculture.tools.AppleSalesReporter.UI.Linked.LinkedJButton;
import com.devculture.tools.AppleSalesReporter.UI.Linked.LinkedJPanel;
import com.devculture.tools.AppleSalesReporter.UI.Linked.LinkedJTextFilter;
import com.devculture.util.StaticDefines;
import com.devculture.util.Utilities;

public class QueryBar extends JPanel implements ActionListener, MouseListener, MouseMotionListener, FocusListener, KeyListener {
	
	/** variables **/
	
	private static final long serialVersionUID = 1L;

	private final int FAVORITES_LABEL_HEIGHT_OFFSET = 23;
	private final int HEIGHT_OF_EXEC_BUTTONS_BAR = 46;
	private final int TOGGLE_COM_WINDOW_MAXIMUM_HEIGHT = 100;
	private final int TOGGLE_FAV_WINDOW_MAXIMUM_HEIGHT = 100;
	private final int TOGGLE_WINDOW_MINIMUM_HEIGHT = 1;
	private final int TOGGLE_WINDOW_DELTA_HEIGHT = 20;
	private final int COMMAND_PANEL_WIDTH = 200;
	private final int COMMAND_PANEL_HEIGHT = 30;
	private final int COMMAND_PANEL_INITIAL_OFFSET = 5;
	private final int ARROW_LABEL_LEFT = 25;
	private final int ARROW_LABEL_Y_OFFSET = 5;
	private final int ARROW_LABEL_WIDTH = 15;
	private final int ARROW_LABEL_HEIGHT = 12;
	private final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN, 10);
	private final Font DEFAULT_FONT_BOLD = new Font("Verdana", Font.BOLD, 10);
	private final Color SELECTED_PANEL_COLOR = new Color(0xcccccc);
		
	private ReportData dataRef;
	private ImageIcon iconAdd;
	private ImageIcon iconExpandUp;
	private ImageIcon iconExpandDown;
	private ImageIcon iconTrash;
	private ImageIcon iconFavorite;
	private ImageIcon iconFavoritePlus;
	private ImageIcon iconDelete;
	private ImageIcon iconAction;
	private ImageIcon iconArrow;

	private ToggleAnimation animationComWindow;
	private ToggleAnimation animationFavWindow;
	
	private Vector<JPanel> commandPanels;
	private Vector<LinkedJPanel> favoritePanels;
	
	private JButton toggleButton;
	private JPanel toggleCommandWindow;
	private JPanel toggleFavoritesWindow;
	private JLabel arrowLabel;
	private JComboBox comboBox;
	private ButtonGroup radioGroup;
	private FilterComparison filterInequality;
	
	private int srcIndex = -1;
	private int destIndex = -1;
	private int last = -1;
	
	/** subclass in charge of expansion animation **/
	
	private class ToggleAnimation extends Thread {
		public int maxHeight = 0;

		private JPanel window = null;
		private boolean isToggling = false;
		private boolean isOpen = false;
		
		public ToggleAnimation(JPanel window, int maxHeight) {
			this.window = window;
			this.maxHeight = maxHeight;
		}
		
		public boolean isOpen() {
			return isOpen;
		}
		
		public void close() {
			Rectangle rect = window.getBounds();
			rect.height = TOGGLE_WINDOW_MINIMUM_HEIGHT;
			isToggling = false;
			isOpen = false;
			updateWindowBoundsPushCommandPanels(rect);
		}
		
		public void open() {
			Rectangle rect = window.getBounds();
			rect.height = maxHeight;
			isToggling = false;
			isOpen = true;
			updateWindowBoundsPushCommandPanels(rect);
		}
		
		public void toggle() {
			if(isToggling) {
				isOpen = !isOpen;
			}
			isToggling = true;
		}
		
		public void setWindowBounds(Rectangle rect) {
			window.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
		
		public Rectangle getWindowBounds() {
			return window.getBounds();
		}
		
		private void updateWindowBoundsPushCommandPanels(Rectangle rect) {
			window.setBounds(rect.x, rect.y, rect.width, rect.height);

			// move the command panels
			for(int i=0, offset=rect.y+rect.height+COMMAND_PANEL_INITIAL_OFFSET; i<commandPanels.size(); i++, offset+=COMMAND_PANEL_HEIGHT) {
				(commandPanels.get(i)).setBounds(0, offset, COMMAND_PANEL_WIDTH, COMMAND_PANEL_HEIGHT);
			}
			
			// make sure window height is correct
			updateWindowHeight();
		}
		
		public void run() {
			try {
				while(true) {
					if(isToggling) {
						Rectangle rect = window.getBounds();
						if(isOpen) {
							if(rect.height > TOGGLE_WINDOW_MINIMUM_HEIGHT) {
								rect.height -= TOGGLE_WINDOW_DELTA_HEIGHT;
							}
							if(rect.height <= TOGGLE_WINDOW_MINIMUM_HEIGHT) {
								rect.height = TOGGLE_WINDOW_MINIMUM_HEIGHT;
								isToggling = false;
								isOpen = false;
							}
						} else {
							if(rect.height < maxHeight) {
								rect.height += TOGGLE_WINDOW_DELTA_HEIGHT;
							}
							if(rect.height >= maxHeight) {
								rect.height = maxHeight;
								isToggling = false;
								isOpen = true;
							}
						}

						updateWindowBoundsPushCommandPanels(rect);
						
						// thread sleep
						sleep(25);
					}
				}
			} catch(Exception ex) {
				
			}
		}
	}
	
	/** constructor **/
	
	public QueryBar(ReportData ref) {
		dataRef = ref;
		
		iconAdd = Utilities.createImageIcon("/images/add.png", "add icon");
		iconExpandUp = Utilities.createImageIcon("/images/up.png", "expand up icon");
		iconExpandDown = Utilities.createImageIcon("/images/down.png", "expand down icon");
		iconTrash = Utilities.createImageIcon("/images/trash.png", "trash icon");
		iconFavorite = Utilities.createImageIcon("/images/favorite.png", "fav icon");
		iconFavoritePlus = Utilities.createImageIcon("/images/favorite+.png", "fav plus icon");
		iconDelete = Utilities.createImageIcon("/images/remove.png", "delete icon");
		iconAction = Utilities.createImageIcon("/images/refresh.png", "view data icon");
		iconArrow = Utilities.createImageIcon("/images/arrow.png", "arrow icon");
	
		commandPanels = new Vector<JPanel>();
		favoritePanels = new Vector<LinkedJPanel>();
		
		setLayout(null);
		
		toggleButton = addButton(this, new Rectangle(5+1, 7, 30, 30), iconExpandDown, "toggle");
		addButton(this, new Rectangle(40+1, 7, 30, 30), iconFavorite, "favorites");
		addButton(this, new Rectangle(75+2, 7, 30, 30), iconFavoritePlus, "favorites plus");
		addButton(this, new Rectangle(110+2, 7, 30, 30), iconTrash, "trash");
		addButton(this, new Rectangle(145+2, 7, 30, 30), iconAction, "execute");

		// insert commands & favorites toggle-able window
		toggleCommandWindow = addToggleWindow();
		animationComWindow = new ToggleAnimation(toggleCommandWindow, TOGGLE_COM_WINDOW_MAXIMUM_HEIGHT);
		animationComWindow.start();

		toggleFavoritesWindow = addToggleWindow();
		animationFavWindow = new ToggleAnimation(toggleFavoritesWindow, TOGGLE_FAV_WINDOW_MAXIMUM_HEIGHT);
		animationFavWindow.start();
		
		// arrow label
		arrowLabel = addLabel(this, new Rectangle(ARROW_LABEL_LEFT, 100, ARROW_LABEL_WIDTH, ARROW_LABEL_HEIGHT), iconArrow);
		arrowLabel.setVisible(false);

		// populate the commands & favorites window
		populateToggleFavWindow();
		
		// populate 
		populateToggleComWindow();

		// toggle the command window. default is open
		toggleButton.setIcon(toggleButton.getIcon() == iconExpandDown ? iconExpandUp : iconExpandDown);
		manageToggleWindows(animationComWindow, animationFavWindow);
		
		// add listeners
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	/** reset window height **/
	
	private int getMinimumBarHeight() {
		int height = 	HEIGHT_OF_EXEC_BUTTONS_BAR + // height of the buttons bar
						((animationFavWindow.isOpen())? animationFavWindow.maxHeight : 0) + // expandable window height
						((animationComWindow.isOpen())? animationComWindow.maxHeight : 0); // expandable window height
		return height;
	}
	
	private void updateWindowHeight() {
		int height = 	getMinimumBarHeight() + // top height
						COMMAND_PANEL_INITIAL_OFFSET + // added offset for rows
						(dataRef.getFilterCommandCount() * COMMAND_PANEL_HEIGHT); // the total rows height
		setPreferredSize(new Dimension(0, height));
		updateUI(); // tell the ui to update the scroll-bar if needed
	}
	
	/** combo-box **/
	
	private String[] getComboBoxContent() {
		// append default filter commands to the beginning of the combo box
		String[] commands = ReportFilter.DEFAULT_FILTER_COMMANDS;
		String[] append = dataRef.getColumnTitles();
		String[] content = new String[commands.length + append.length];
		for(int i=0; i<commands.length; i++) {
			content[i] = commands[i];
		}
		for(int i=0; i<append.length; i++) {
			content[i + commands.length] = append[i];
		}
		return content;
	}
	
	/** favorites **/

	private void populateToggleFavWindow() {
		int index = 0;
		int offset = 0;
		
		// load favorites
		try {
			SaveManager.getInstance().loadFavorites();
		} catch(Exception ex) {
			PopupMessage.showErrorMessage("Failed to load favorites");
		}
		
		// clear out whatever is inside favorites window
		toggleFavoritesWindow.removeAll();
		favoritePanels.removeAllElements();
		
		Vector<Favorite> favorites = SaveManager.getInstance().getFavorites();
		
		// add the favorites label, only if there are more than 1 favorites
		if(favorites.size() > 0) {
			addLabel(toggleFavoritesWindow, new Rectangle(8, 5, 200, 20), "Favorites:", DEFAULT_FONT_BOLD);
			offset = FAVORITES_LABEL_HEIGHT_OFFSET;
		}
		
		for(Favorite favorite : favorites) {
			// create panel
			LinkedJPanel panel = addLinkedPanel(toggleFavoritesWindow, new Rectangle(0, offset + index*COMMAND_PANEL_HEIGHT, COMMAND_PANEL_WIDTH, COMMAND_PANEL_HEIGHT), favorite);
			panel.setLayout(null);
			panel.setBackground(null);
			addLinkedButton(panel, new Rectangle(5, 5, 20, 20), iconDelete, "delete favorite", favorite);
			addLabel(panel, new Rectangle(30, 5, 170, 20), favorite.getName(), DEFAULT_FONT_BOLD);
			favoritePanels.add(panel);
			index++;
		}
		
		animationFavWindow.maxHeight = favorites.size() * COMMAND_PANEL_HEIGHT + offset + 1;
	}
	
	/** add toggle able window **/
	
	private JPanel addToggleWindow() {
		JPanel toggleWindow = addPanel(this, new Rectangle(0, 44, 200, TOGGLE_WINDOW_MINIMUM_HEIGHT));
		toggleWindow.setLayout(null);
		toggleWindow.setBorder(new ThinBorder(new Color(StaticDefines.GLOBAL_BORDER_COLOR), ThinBorder.THIN_BORDER_DIRECTION_TOP | ThinBorder.THIN_BORDER_DIRECTION_BOTTOM));
		toggleWindow.setBackground(SELECTED_PANEL_COLOR);
		return toggleWindow;
	}
	
	/** commands **/
	
	private void populateToggleComWindow() {
		// clear out whatever is inside command window
		toggleCommandWindow.removeAll();
		
		comboBox = addComboBox(toggleCommandWindow, new Rectangle(2, 15+1, 165-15, 40), getComboBoxContent());
		addLabel(toggleCommandWindow, new Rectangle(8, 5, 200, 20), "Insert Command:", DEFAULT_FONT_BOLD);
		addButton(toggleCommandWindow, new Rectangle(160-15, 15, 40, 40), iconAdd, "add row");
		
		// add radio buttons
		radioGroup = new ButtonGroup();
		JRadioButton button = addRadioButton(toggleCommandWindow, new Rectangle(1, 54, 55, 20), "==", "set inequality ==");
		radioGroup.add(button);
		radioGroup.add(addRadioButton(toggleCommandWindow, new Rectangle(1, 74, 55, 20), "!=", "set inequality !="));
		radioGroup.add(addRadioButton(toggleCommandWindow, new Rectangle(51+12, 54, 55, 20), "<", "set inequality <"));
		radioGroup.add(addRadioButton(toggleCommandWindow, new Rectangle(51+12, 74, 55, 20), ">", "set inequality >"));
		radioGroup.add(addRadioButton(toggleCommandWindow, new Rectangle(101+24, 54, 55, 20), "<=", "set inequality <="));
		radioGroup.add(addRadioButton(toggleCommandWindow, new Rectangle(101+24, 74, 55, 20), ">=", "set inequality >="));
		
		// set radio selection to ==
		radioGroup.setSelected(button.getModel(), true);
		filterInequality = FilterComparison.EQUAL;
	}
	
	private void addCommand(String command) {
		if(command.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_INVALID)) {
			// ignore this one
		} else if(command.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_AND) || command.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_OR)) {
			dataRef.addFilterCommand(new ReportFilter(command, FilterType.OPERATOR, FilterComparison.NONE));
		} else if(command.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_LPAREN) || command.equalsIgnoreCase(ReportFilter.FILTER_COMMAND_RPAREN)) {
			dataRef.addFilterCommand(new ReportFilter(command, FilterType.PARENTHESIS, FilterComparison.NONE));
		} else {
			dataRef.addFilterCommand(new ReportFilter(command, FilterType.TEXT, filterInequality));
		}
		updateCommands();
	}
	
	private void removeAllCommands() {
		dataRef.removeAllFilterCommands();
		updateCommands();
	}
	
	private void removeCommand(ReportFilter filter) {
		dataRef.removeFilterCommand(filter);
		updateCommands();
	}
	
	private void reorderCommand(int srcIndex, int destIndex) {
		// src index must always be within range of commandPanels
		if(srcIndex >= 0 && srcIndex < commandPanels.size()) {
			// dst index can be up to commandPanel size
			if(destIndex >= 0 && destIndex <= commandPanels.size()) {
				if(destIndex > srcIndex) {
					destIndex--;
				}
				if(destIndex == srcIndex) {
					// dst = src, does nothing
					return;
				} else {
					// remove src index, insert at dst index
					ReportFilter removed = dataRef.removeFilterCommandAtIndex(srcIndex);
					dataRef.insertFilterCommandAt(removed, destIndex);
					updateCommands();
				}
			}
		}
	}
	
	private void updateCommands() {
		Rectangle rect;
		
		if(animationFavWindow.isOpen()) {
			rect = toggleFavoritesWindow.getBounds();
			// if window is open
			rect.height = animationFavWindow.maxHeight;
			toggleFavoritesWindow.setBounds(rect);
		} else {
			rect = toggleCommandWindow.getBounds();
		}

		// remove all panels temporarily
		while(commandPanels.size() > 0) {
			remove(commandPanels.get(0));
			commandPanels.remove(0);
		}
		
		// create new panels, and add to window
		for(int i=0, offset=rect.y+rect.height+COMMAND_PANEL_INITIAL_OFFSET; i<dataRef.getFilterCommandCount(); i++, offset+=COMMAND_PANEL_HEIGHT) {
			ReportFilter filter = dataRef.getFilterCommandAtIndex(i);
			JPanel panel = addPanel(this, new Rectangle(0, offset, COMMAND_PANEL_WIDTH, COMMAND_PANEL_HEIGHT));
			panel.setLayout(null);
			addLinkedButton(panel, new Rectangle(5, 5, 20, 20), iconDelete, "delete command", filter);
			
			switch(filter.getType()) {
			case OPERATOR:
			case PARENTHESIS:
				addLabel(panel, new Rectangle(30, 5, 170, 20), filter.getText(), DEFAULT_FONT_BOLD);
				break;
			case DATE:
			case TEXT:
				addLabel(panel, new Rectangle(30, 5, 49, 20), filter.getText(), DEFAULT_FONT);
				addLabel(panel, new Rectangle(84, 5, 16, 20), filter.getInequality(), DEFAULT_FONT);
				addLinkedTextField(panel, new Rectangle(105, 5, 75, 20), filter.getFilter(), DEFAULT_FONT, filter);
				break;
			}
			commandPanels.add(panel);
		}
		
		// after we update the commands, we need to reset the window height
		updateWindowHeight();
	}
	
	/** components **/
	
	private void setRect(Component component, Rectangle rect) {
		Insets insets = getInsets();
		component.setBounds(rect.x + insets.left, rect.y + insets.top, rect.width, rect.height);
	}

	private JComboBox addComboBox(JPanel target, Rectangle rect, String[] selectables) {
		JComboBox box = new JComboBox(selectables);
		box.addActionListener(this);
		box.setActionCommand("combo box");
		setRect(box, rect);
		target.add(box);
		return box;
	}
	
	private JPanel addPanel(JPanel target, Rectangle rect) {
		JPanel panel = new JPanel();
		setRect(panel, rect);
		target.add(panel);
		return panel;
	}
	
	private JLabel addLabel(JPanel target, Rectangle rect, String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		setRect(label, rect);
		target.add(label);
		return label;
	}
	
	private JLabel addLabel(JPanel target, Rectangle rect, ImageIcon icon) {
		JLabel label = new JLabel(icon);
		setRect(label, rect);
		target.add(label);
		return label;
	}
	
	private JRadioButton addRadioButton(JPanel target, Rectangle rect, String text, String command) {
		JRadioButton button = new JRadioButton(text);
		button.setActionCommand(command);
		button.addActionListener(this);
		setRect(button, rect);
		target.add(button);
		return button;
	}
	
	private JButton addButton(JPanel target, Rectangle rect, ImageIcon icon, String command) {
		JButton button = new JButton(icon);
		button.setBorderPainted(false);
		button.setActionCommand(command);
		button.addActionListener(this);
		setRect(button, rect);
		target.add(button);
		return button;
	}
	
	/** linked components **/
	
	private LinkedJPanel addLinkedPanel(JPanel target, Rectangle rect, Object linkedObject) {
		LinkedJPanel panel = new LinkedJPanel();
		panel.linkToObject(linkedObject);
		setRect(panel, rect);
		target.add(panel);
		return panel;
	}
	
	private LinkedJButton addLinkedButton(JPanel target, Rectangle rect, ImageIcon icon, String command, Object linkedObject) {
		LinkedJButton button = new LinkedJButton(icon);
		button.setBorderPainted(false);
		button.setActionCommand(command);
		button.addActionListener(this);
		button.linkToObject(linkedObject);
		setRect(button, rect);
		target.add(button);
		return button;
	}

	private LinkedJTextFilter addLinkedTextField(JPanel target, Rectangle rect, String text, Font font, Object linkedObject) {
		LinkedJTextFilter textfield = new LinkedJTextFilter();
		textfield.setFont(font);
		textfield.setText(text);
		textfield.addKeyListener(this);
		textfield.addFocusListener(this);
		textfield.linkToObject(linkedObject);
		setRect(textfield, rect);
		target.add(textfield);
		return textfield;
	}

	/** events **/

	private void manageToggleWindows(ToggleAnimation open, ToggleAnimation close) {
		Rectangle rectClose = close.getWindowBounds();
		Rectangle rectOpen= open.getWindowBounds();
		if(rectClose.height > open.maxHeight) {
			close.close();
			open.open();
		} else {
			rectOpen.height = rectClose.height;
			open.setWindowBounds(rectOpen);
			close.close();
			open.toggle();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("toggle")) {
			toggleButton.setIcon(toggleButton.getIcon() == iconExpandDown ? iconExpandUp : iconExpandDown);
			manageToggleWindows(animationComWindow, animationFavWindow);
		} else if(command.equals("favorites")) {
			toggleButton.setIcon(iconExpandDown);
			manageToggleWindows(animationFavWindow, animationComWindow);
		} else if(command.equals("favorites plus")) {
			if(dataRef.getFilterCommandCount() > 0) {
				// save as favorites
				String result = null;
				try {
					while(true) {
						result = PopupMessage.askForUserInput("Save to Favorites", "Save current query as...");
						if(result != null) {
							if(SaveManager.getInstance().isFavoritesFileExist(result)) {
								int overwrite = PopupMessage.askForUserYesNo("Overwrite Existing", "Favorite named '" + result + "' exists, overwrite it?");
								if(overwrite == JOptionPane.NO_OPTION) {
									// ask for new favorites name
									continue;
								}
							}
							SaveManager.getInstance().saveQueryAs(result, dataRef);
						}
						break;
					}
				} catch(Exception ex) {
					System.err.println(ex.getMessage());
					PopupMessage.showErrorMessage("Failed to save new favorite query");
				}
				
				// re-populate the favorites panel
				populateToggleFavWindow();
				
				// update the commands window
				updateCommands();
			}
		} else if(command.equals("delete favorite")) {
			try {
				Object obj = e.getSource();
				if(obj instanceof LinkedJButton) {
					SaveManager.getInstance().deleteFavorite(((Favorite)((LinkedJButton)obj).getLinkedObject()));
					
					// re-populate the favorites panel
					populateToggleFavWindow();
					
					// update the commands window
					updateCommands();
				}			
			} catch(Exception ex) {
				PopupMessage.showErrorMessage("Failed to delete favorite query");
			}
		} else if(command.equals("trash")) {
			// button does nothing if there are no commands on the screen
			if(dataRef.getFilterCommandCount() > 0) {
				// clear all commands
				int result = PopupMessage.askForUserYesNo("Trash", "Are you sure you want to trash the current query?");
				if(result == JOptionPane.YES_OPTION) {
					removeAllCommands();
				}
			}
		} else if(command.equals("execute")) {
			dataRef.filterData();
		} else if(command.equals("combo box")) {
			// combo content has changed. ignore
		} else if(command.equals("add row")) {
			addCommand((String)comboBox.getSelectedItem());
		} else if(command.equalsIgnoreCase("delete command")) {
			Object obj = e.getSource();
			if(obj instanceof LinkedJButton) {
				removeCommand((ReportFilter)((LinkedJButton)obj).getLinkedObject());
			}
		} else if(command.startsWith("set inequality ")) {
			// inequality buttons
			if(command.equalsIgnoreCase("set inequality ==")) {
				filterInequality = FilterComparison.EQUAL;
			} else if(command.equalsIgnoreCase("set inequality !=")) {
				filterInequality = FilterComparison.NOT_EQUAL;
			} else if(command.equalsIgnoreCase("set inequality <")) {
				filterInequality = FilterComparison.LESS_THAN;
			} else if(command.equalsIgnoreCase("set inequality >")) {
				filterInequality = FilterComparison.GREATER_THAN;
			} else if(command.equalsIgnoreCase("set inequality <=")) {
				filterInequality = FilterComparison.LESS_THAN_AND_EQUAL;
			} else if(command.equalsIgnoreCase("set inequality >=")) {
				filterInequality = FilterComparison.GREATER_THAN_AND_EQUAL;
			}
		} else {
			System.err.println("Unknown command: " + command);
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if(animationFavWindow.isOpen()) {
			for(LinkedJPanel panel : favoritePanels) {
				Rectangle rect = panel.getBounds();
				
				// adjust the y because coordinates are not in reference to the top left
				rect.y += HEIGHT_OF_EXEC_BUTTONS_BAR;
				
				if(rect.contains(e.getPoint())) {
					Favorite favorite = (Favorite)panel.getLinkedObject();
					
					try {
						SaveManager.getInstance().loadQuery(favorite.getName(), dataRef);
					} catch(Exception ex) {
						PopupMessage.showErrorMessage("Failed to load query from " + favorite.getName());
					}
					
					// re-populate the favorites panel
					populateToggleFavWindow();
					
					// update the commands window
					updateCommands();
					break;
				}
			}
		}
		
		last = srcIndex = destIndex = -1;
		for(int i=0; i<commandPanels.size(); i++) {
			JPanel panel = commandPanels.get(i);
			Rectangle rect = panel.getBounds();
			if(rect.contains(e.getPoint())) {
				// set the selected panel bg color
				panel.setBackground(SELECTED_PANEL_COLOR);
				srcIndex = i;
				last = e.getY();
				break;
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if(srcIndex != -1) {
			int curr = e.getY();
			
			// threshold for drag event
			if(Math.abs(curr - last) > 1) {
				boolean dragDownDirection = curr > last;
				
				for(int i=0; i<commandPanels.size(); i++) {
					Rectangle rect = (commandPanels.get(i)).getBounds();
					if(rect.contains(e.getPoint())) {
						destIndex = i + ((dragDownDirection) ? 1 : 0);
						arrowLabel.setBounds(ARROW_LABEL_LEFT, rect.y + ((dragDownDirection) ? COMMAND_PANEL_HEIGHT /*mouse moving down*/ : 0 /*mouse moving up*/) - ARROW_LABEL_Y_OFFSET, ARROW_LABEL_WIDTH, ARROW_LABEL_HEIGHT);
						arrowLabel.setVisible(true);
						break;
					}
				}
				last = curr;
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		// unselect, reset selected panel bg color
		if(srcIndex >= 0 && srcIndex < commandPanels.size()) {
			JPanel panel = commandPanels.get(srcIndex);
			panel.setBackground(null);
		}
		
		// reorder the commands as expected
		reorderCommand(srcIndex, destIndex);
		
		// on mouse-up, always make the arrow disappear
		arrowLabel.setVisible(false);
		
		// reset indexes
		srcIndex = destIndex = -1;
	}
	
	public void mouseMoved(MouseEvent e) {
		// skip
	}

	public void mouseClicked(MouseEvent e) {
		// skip
	}

	public void mouseEntered(MouseEvent e) {
		// skip
	}

	public void mouseExited(MouseEvent e) {
		// skip
	}
	
	/** text field event **/

	public void updateTextFieldFilter(LinkedJTextFilter textfield) {
		ReportFilter filter = (ReportFilter) textfield.getLinkedObject();
		filter.setFilter(textfield.getText());
	}
	
	public void focusLost(FocusEvent e) {
		Component c = e.getComponent();
		if(c instanceof LinkedJTextFilter) {
			updateTextFieldFilter((LinkedJTextFilter)c);
		}
	}

	public void keyTyped(KeyEvent e) {
		Component c = e.getComponent();
		if(c instanceof LinkedJTextFilter) {
			updateTextFieldFilter((LinkedJTextFilter)c);
		}
	}

	public void focusGained(FocusEvent e) {
		// skip
	}

	public void keyPressed(KeyEvent e) {
		// skip
	}

	public void keyReleased(KeyEvent e) {
		// skip
	}
}
