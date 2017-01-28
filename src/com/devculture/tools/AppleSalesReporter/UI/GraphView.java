package com.devculture.tools.AppleSalesReporter.UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JPanel;
import com.devculture.tools.AppleSalesReporter.Data.ReportData;
import com.devculture.tools.AppleSalesReporter.Data.ReportDataListener;
import com.devculture.tools.AppleSalesReporter.Data.ReportPlot;

public class GraphView extends JPanel implements MouseMotionListener, ReportDataListener {
	
	/** variables **/
	
	private static final long serialVersionUID = 1L;

	private Vector<ReportPlot> plotsRef = null;
	private ReportData dataRef = null;
	private Point mouse = new Point(0, 0);

	private final Dimension DEFAULT_WINDOW_SIZE = new Dimension(740, 530);
	private final BasicStroke DOTTED_STROKE = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {2f}, 0f);
	private final Color GRID_LINE_COLOR = new Color(0xcccccc);

	private final int LEFT_MARGIN_FOR_LABEL = 75;
	private final int BOTTOM_MARGIN_FOR_LABEL = 75;
	private final int TEXT_LABEL_SEPARATION = 10;
	private final int MINIMUM_PIXELS_MAJOR_GRID_DELTA = 44;
	private final int MAXIMUM_PIXELS_MAJOR_GRID_DELTA = 80;
	private final int MINIMUM_DX_BETWEEN_VERTICAL_GRID_LINES = 60;
	private final int MINIMUM_NUMBER_OF_GRID_LINES = (DEFAULT_WINDOW_SIZE.height-BOTTOM_MARGIN_FOR_LABEL)/MAXIMUM_PIXELS_MAJOR_GRID_DELTA; // 6
	private final int MAXIMUM_NUMBER_OF_GRID_LINES = (DEFAULT_WINDOW_SIZE.height-BOTTOM_MARGIN_FOR_LABEL)/MINIMUM_PIXELS_MAJOR_GRID_DELTA; // 12
	private final int[] baseMajorGridDeltaValues = {10, 25, 50};

	/** constructor **/
	
	public GraphView(ReportData ref) {
		dataRef = ref;
		addMouseMotionListener(this);
	}
	
	/** window property **/
	
	private int getCurrentWindowWidth() {
		return getBounds().width;
	}
	
	private int getCurrentWindowHeight() {
		return getBounds().height;
	}
	
	/** grid row label calculation **/
	
	private int getMaximumRevenueValueFromDataSource(Vector<ReportPlot> plots) {
		int max = 0;
		for(ReportPlot plot : plots) {
			if(((int)plot.getValue()) > max) {
				max = (int) plot.getValue();
			}
		}
		return max;
	}
	
	private int getVerticalMajorGridMax(int maximumValue) {
		int multiplier = 1;
		int low, high, result;
		
		while(true) {
			low = 2000 * multiplier;
			high = 10000 * multiplier;
			if(maximumValue <= low) {
				result = 100 * multiplier;
				break;
			} else if(maximumValue <= high) {
				result = 500 * multiplier;
				break;
			}
			multiplier *= 10;
		}
		
		return (maximumValue / result +1) * result;
	}
	
	private int getVerticalMajorGridDelta(int maximumValue) {
		int multiplier = 1;
		int delta = 1;
		int count = 1;
		int largest = 1;
		
		while(delta < maximumValue / MINIMUM_NUMBER_OF_GRID_LINES) {
			for(int suggested : baseMajorGridDeltaValues) {
				delta = suggested * multiplier;
				count = maximumValue / delta;
				if(count >= MINIMUM_NUMBER_OF_GRID_LINES && count <= MAXIMUM_NUMBER_OF_GRID_LINES) {
					largest = delta;
				}
			}
			multiplier *= 10;	
		} 
		
		return largest;
	}
	
	private String[] getVerticalMajorGridLabels(int maximumValue) {
		int delta = getVerticalMajorGridDelta(maximumValue);
		int count = (maximumValue/delta + 1);
		String[] labels = new String[count];
		for(int i=0; i<count; i++) {
			labels[i] = "" + delta * i;
		}		
		return labels;
	}
	
	/** painting **/
	
	private void paintGridWithLabels(Graphics g, String[] horizontalMajorGridLabels, String[] verticalMajorGridLabels) {
		Graphics2D gfx = (Graphics2D) g.create();
		FontMetrics metrics = gfx.getFontMetrics();
		
		int windowWidth = getCurrentWindowWidth();
		int windowHeight = getCurrentWindowHeight();
		int dx = Math.max((windowWidth-LEFT_MARGIN_FOR_LABEL) / (horizontalMajorGridLabels.length), 1);
		int dy = Math.max((windowHeight-BOTTOM_MARGIN_FOR_LABEL) / (verticalMajorGridLabels.length), 1);

		// draw horizontal grid
		for(int i=0, y=windowHeight-BOTTOM_MARGIN_FOR_LABEL; i<verticalMajorGridLabels.length; i++, y-=dy) {
			g.setColor(Color.black);
			g.drawString(verticalMajorGridLabels[i], LEFT_MARGIN_FOR_LABEL-metrics.stringWidth(verticalMajorGridLabels[i])-TEXT_LABEL_SEPARATION, y+5);
			g.setColor((i == 0)? Color.black : GRID_LINE_COLOR);
			g.drawLine(LEFT_MARGIN_FOR_LABEL, y, windowWidth, y);
		}
		
		// draw vertical grid
		for(int i=0, x=LEFT_MARGIN_FOR_LABEL, last=0; i<horizontalMajorGridLabels.length; i++, x+=dx) {
			if(x - last > MINIMUM_DX_BETWEEN_VERTICAL_GRID_LINES) {
				gfx.translate(x, windowHeight-BOTTOM_MARGIN_FOR_LABEL+TEXT_LABEL_SEPARATION+5);
				gfx.rotate(Math.toRadians(45));
			
				gfx.setColor(Color.black);
				gfx.drawString(horizontalMajorGridLabels[i], 0, 0);
				
				gfx.rotate(Math.toRadians(-45));
				gfx.translate(-x, -(windowHeight-BOTTOM_MARGIN_FOR_LABEL+TEXT_LABEL_SEPARATION+5));
				
				gfx.setColor((i == 0)? Color.black : GRID_LINE_COLOR);
			
				gfx.drawLine(x, 0, x, windowHeight-BOTTOM_MARGIN_FOR_LABEL-1);
				last = x;
			}
		}
	
		gfx.dispose();
	}
	
	private void paintGraph(Graphics g, String[] horizontalMajorGridLabels, String[] verticalMajorGridLabels, int verticalMajorGridDelta) {
		g.setColor(Color.black);

		if(horizontalMajorGridLabels.length == plotsRef.size()) {
			int windowWidth = getCurrentWindowWidth();
			int windowHeight = getCurrentWindowHeight();
			int dx = Math.max((windowWidth-LEFT_MARGIN_FOR_LABEL) / (horizontalMajorGridLabels.length), 1);
			int dy = Math.max((windowHeight-BOTTOM_MARGIN_FOR_LABEL) / (verticalMajorGridLabels.length), 1);
			int x = LEFT_MARGIN_FOR_LABEL;
			int y = 0;
			int lastX = LEFT_MARGIN_FOR_LABEL;
			int lastY = windowHeight-BOTTOM_MARGIN_FOR_LABEL;
			
			for(ReportPlot plot : plotsRef) {
				// convert value into x/y coordinate
				y = (windowHeight-BOTTOM_MARGIN_FOR_LABEL) - (int)((plot.getValue() * dy) / verticalMajorGridDelta);
				g.drawString("x", x-3, y+3);
				g.drawLine(lastX, lastY, x, y);
				lastX = x;
				lastY = y;
				x += dx;
			}
		}
	}

	private void paintMousePoint(Graphics g, String[] horizontalMajorGridLabels, String[] verticalMajorGridLabels, int verticalMajorGridDelta) {
		if(horizontalMajorGridLabels.length == plotsRef.size()) {
			int windowWidth = getCurrentWindowWidth();
			int windowHeight = getCurrentWindowHeight();
			int dx = Math.max((windowWidth-LEFT_MARGIN_FOR_LABEL) / (horizontalMajorGridLabels.length), 1);
			int dy = Math.max((windowHeight-BOTTOM_MARGIN_FOR_LABEL) / (verticalMajorGridLabels.length), 1);
			int x = LEFT_MARGIN_FOR_LABEL;
			int y = 0;
			int mx = mouse.x;
			int my = mouse.y;
			
			for(ReportPlot plot : plotsRef) {
				if(mx >= x-dx/2 && mx <= x+dx/2) {
					y = (windowHeight-BOTTOM_MARGIN_FOR_LABEL) - (int)((plot.getValue() * dy) / verticalMajorGridDelta);
					if(mx >= LEFT_MARGIN_FOR_LABEL && my <= windowHeight-BOTTOM_MARGIN_FOR_LABEL) {
						Graphics2D gfx = (Graphics2D) g.create();
						gfx.setStroke(DOTTED_STROKE);
						gfx.setColor(Color.darkGray);
						gfx.drawLine(x, 0, x, windowHeight-BOTTOM_MARGIN_FOR_LABEL);
						gfx.drawLine(LEFT_MARGIN_FOR_LABEL, y, windowWidth, y);
						gfx.dispose();
						
						g.setColor(Color.black);
						FontMetrics metrics = gfx.getFontMetrics();
						String text = plot.getDateStr() + " = " + ((int)plot.getValue()) + " Units";
						g.drawString(text, windowWidth-metrics.stringWidth(text) - 10, 17);
					}
					break;
				}
				x += dx;
			}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if(plotsRef != null) {
			int maximumRevenueValue = getVerticalMajorGridMax(getMaximumRevenueValueFromDataSource(plotsRef));
			String[] verticalMajorGridLabels = getVerticalMajorGridLabels(maximumRevenueValue);
			String[] horizontalMajorGridLabels = new String[plotsRef.size()];
			
			if(plotsRef.size() > 0) {
				// get the date strings as axis labels
				for(int i=0; i<plotsRef.size(); i++) {
					horizontalMajorGridLabels[i] = plotsRef.elementAt(i).getDateStr();
				}
				
				// paint labels
				paintGridWithLabels(g, horizontalMajorGridLabels, verticalMajorGridLabels);
				
				// paint graph, based on the given data
				paintGraph(g, horizontalMajorGridLabels, verticalMajorGridLabels, getVerticalMajorGridDelta(maximumRevenueValue));
				
				// paint the mouse line so that we could have an accurate reading of our current position
				paintMousePoint(g, horizontalMajorGridLabels, verticalMajorGridLabels, getVerticalMajorGridDelta(maximumRevenueValue));
			}
		}
	}
	
	/** mouse events **/

	public void mouseDragged(MouseEvent e) {
		mouse = e.getPoint();
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();		
		repaint();
	}

	public void reportDataChanged() {
		// update graph
		plotsRef = dataRef.getPlots();
		updateUI();
	}
}
