import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This class represents a JPanel panel
 * It will be the drawing area
 */
public class Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// Number points to be used when drawing a curve
	private static final int N_POINTS = 20;
	// Radius of a point (it allows to click in a neighborhood)
	private static final int RADIUS = 4;
	// Width of the cross (mark of the point)
	private static final int CROSS_WIDTH = 4;
	
	// Polyline object
	private Polyline polyline;
	
	// Current selected point
	private Point selectedPoint;
	private int numPoints;
	private int currentPoints;
	
	// Used when creating the polyline
	private Point lastP;
	private Point lastP2;
	private Point tmpPoint;
	
	// Curves, polyline and convex hull visible state
	private boolean showBezierCurve;
	private boolean showBSplineCurve;
	private boolean showCatmullRomCurve;
	private boolean showConvexHull;
	private boolean showPolyline;
	
	// Printing rectangle state
	private boolean printBox;
	private boolean needsFirstPoint;
	
	// Printing rectangle coordinates
	private int boxXStartPos;
	private int boxYStartPos;
	private int boxXPreviousPos;
	private int boxYPreviousPos;
	
	private int rectanglePointsDrawn = 0;
	
	// Drawing line state and coordinates
	private boolean drawingLine = false;
	private int xStartPos, yStartPos;
	private int xPreviousPos, yPreviousPos;
	
	// Frame to be used to activate/disable menus
	private final Frame frame;

	/**
	 * Constructs a JPanel panel
	 * @param frame frame to be used
	 */
	public Panel(final Frame frame) {
		numPoints = 4;
		currentPoints = 0;
		polyline = new Polyline(4);
		selectedPoint = null;
		lastP = null;
		lastP2 = null;
		tmpPoint = null;
		showBezierCurve = false;
		showBSplineCurve = false;
		showCatmullRomCurve = false;
		showConvexHull = false;
		showPolyline = true;
		needsFirstPoint = false;
		this.frame = frame;
		
		// White background color
		setBackground(Color.WHITE);
		
		// Change cursor to be a crosshair
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		MouseListener ml = new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e) {
				selectedPoint = null;
				int xPos = e.getX();
				int yPos = e.getY();
				
				// Will get a point iff the polyline is finished
				if (e.getButton() == MouseEvent.BUTTON1 && polyline.isFinished()) {
					boolean existingPoint = false;
					Line l;
					existingPoint = (l = polyline.getLineWithPoint(xPos, yPos, RADIUS)) != null;

					if (existingPoint)
						selectedPoint = polyline.getExactPoint(l, xPos, yPos, RADIUS);
				}

			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				// Get current mouse coordinates
				int xPos = e.getX();
				int yPos = e.getY();
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (!polyline.isFinished()) {
						Graphics2D g = (Graphics2D) getGraphics();

						if (drawingLine) { // Finishing a line...
							currentPoints++;
							
							// Set XOR mode
							g.setXORMode(getBackground());
							g.setColor(new Color(255, 255, 0));
							// Clean the temporary line
							g.drawLine(xStartPos, yStartPos,
									xPreviousPos, yPreviousPos);
							// Draw the new line
							g.setPaintMode();
							g.drawLine(xStartPos, yStartPos, xPos, yPos);
							
							// Creates a new point with the current mouse coordinates
							lastP2 = new Point(xPos, yPos);
							
							// Creates a line from the initial point lastP to the final point lastP2
							Line l = new Line(lastP, lastP2);
							
							polyline.addLine(l);
							
							drawCross(g, xPos, yPos);
							
							// It isn't the last line's point yet
							if (currentPoints < numPoints) {
								
								// The first point of the polyline is not needed anymore
								tmpPoint = null;
								
								// The initial point of the next line will be the last point from the current line
								lastP = lastP2;
								
								xStartPos = xPreviousPos = xPos;
								yStartPos = yPreviousPos = yPos;
							} else { // It is the last point of the last line
								
								polyline.finish();
								frame.activateItems();
								frame.activatePrintItem();
								paintComponent(g);

								drawingLine = false;								
							}
						} else { // Starting a new line
							// Creates the first point of a polyline
							lastP = new Point(xPos, yPos);
							
							// This is needed because of repaint (the point isn't at polyline yet)
							tmpPoint = lastP;
							currentPoints = 1;
							polyline = new Polyline(numPoints);
							drawingLine = true;
							xStartPos = xPreviousPos = xPos;
							yStartPos = yPreviousPos = yPos;
							drawCross(g, xStartPos, yStartPos);
							frame.disablePrintItem();
						}
					} else { 
						// If the polyline is already finished and the mouse left button is clicked, perhaps it is trying to draw the print rectangle area
						
						if (needsFirstPoint) { // If the user is trying to draw the rectangle
							if (rectanglePointsDrawn == 0) {
								// The rectangle isn't drawn yet. Sets the rectangle's first point coordinates
								boxXPreviousPos = boxXStartPos = xPos;
								boxYPreviousPos = boxYStartPos = yPos;
							}

							printBox = true;
							needsFirstPoint = false;
							rectanglePointsDrawn++;
							
						}
						else if (rectanglePointsDrawn == 1) {
							// The rectangle's second point is drawn. Prints it's content to PostScript
							PostScriptConverter ps = new PostScriptConverter(polyline, boxXStartPos, boxYStartPos, xPos, yPos, showPolyline, showBezierCurve);
							try {
								ps.printToFile();
								frame.showInformationMessage(true);
							} catch (IOException e1) {
								frame.showInformationMessage(false);
							}
							printBox = false;
							frame.activateItems();
							frame.activatePrintItem();
							Graphics2D g = (Graphics2D)getGraphics();
							paintComponent(g);
							rectanglePointsDrawn = 0;
						}
					}

					return;
				}
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e) {
				int xPos = e.getX();
				int yPos = e.getY();

				if (selectedPoint != null) {
					selectedPoint.setX(xPos);
					selectedPoint.setY(yPos);
					Point pt;
					// If there's a neighbor point in the moved point's new position
					if ((pt = polyline.getNeighbor(selectedPoint, RADIUS)) != null) {
						pt.setX(xPos);
						pt.setY(yPos);
						selectedPoint.putInBox();
					}
					
					// Change the old neighbor point to be the moved point
					polyline.changePointReference(xPos, yPos, RADIUS, selectedPoint);

					Graphics2D g = (Graphics2D) getGraphics();
					paintComponent(g);
					selectedPoint = null;
				}	
			}

		};

		MouseMotionListener mml = new MouseMotionAdapter() {

			/* (non-Javadoc)
			 * @see java.awt.event.MouseMotionAdapter#mouseDragged(java.awt.event.MouseEvent)
			 */
			public void mouseDragged(MouseEvent e) {
				int xPos = e.getX();
				int yPos = e.getY();
				if (selectedPoint != null) {
					selectedPoint.setX(xPos);
					selectedPoint.setY(yPos);
					Graphics2D g = (Graphics2D) getGraphics();
					paintComponent(g);
				}
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseMotionAdapter#mouseMoved(java.awt.event.MouseEvent)
			 */
			public void mouseMoved(MouseEvent e) {
				int xPos = e.getX();
				int yPos = e.getY();
				// If there's a rectangle been drawn
				if (printBox) {
					Graphics2D g = (Graphics2D) getGraphics();
					g.setXORMode(getBackground());
					g.setColor(new Color(255, 255, 0));
					
					int xDiffAnterior = Math.abs(boxXStartPos-boxXPreviousPos);
					int yDiffAnterior = Math.abs(boxYStartPos-boxYPreviousPos);
					g.setColor(new Color(0, 0, 0));

					if (yPos >= boxYStartPos && xPos >= boxXStartPos) {	
						g.drawRect(boxXStartPos, boxYStartPos, xDiffAnterior, yDiffAnterior);
						g.drawRect(boxXStartPos, boxYStartPos, Math.abs(xPos-boxXStartPos), Math.abs(yPos-boxYStartPos));
						// Store actual position
						boxXPreviousPos = xPos;
						boxYPreviousPos = yPos;
					}
				}
				
				if (!drawingLine)
					return; // Not drawing a line

				drawingLine = true;

				// XOR mode
				Graphics2D g = (Graphics2D) getGraphics();
				g.setXORMode(getBackground());
				g.setColor(new Color(255, 255, 0));
				// Clean the old line
				g.drawLine(xStartPos, yStartPos, xPreviousPos, yPreviousPos);
				// Draw the new line
				g.drawLine(xStartPos, yStartPos, xPos, yPos);
				// Store actual position
				xPreviousPos = xPos;
				yPreviousPos = yPos;

			}
		};

		addMouseListener(ml);
		addMouseMotionListener(mml);
	}
	
	/**
	 * Activates the printing rectangle draw mode
	 */
	public void activatePrintingBox() {
		needsFirstPoint = true;
	}

	/**
	 * Draws the Bezier Curves
	 * @param g2 graphic in which the curves will be drawn
	 */
	private void drawBezierCurves(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		g.setColor(new Color(255, 0, 0));
		List<Point> controlPoints = new ArrayList<Point>();
		Color[] colors = new Color[2];
		colors[0] = new Color(255, 0, 0);
		colors[1] = new Color(120, 0, 0);		
		Line l2 = null;
		int i = 0, j = 0, color = 0;
		int nBezierCurves = polyline.getLines().size()+1 == 4 ? 1 : ((polyline.getLines().size() + 1) / 4) + 1;
		Iterator<Line> it = polyline.getLines().iterator();
		
		for (i=0;i<nBezierCurves;i++) {
			// Iterates over polyline's lines and gets the first three points
			while (it.hasNext() && j < 3) {
				l2 = it.next();
				controlPoints.add(l2.getInitialPoint());
				j++;
			}
			g.setColor(colors[color % 2]);
			color++;
			// The first three points were added to controlPoints in the iteration. 
			// This will be the last controlPoint of this curve
			controlPoints.add(l2.getFinalPoint());
			// Restart j to do a new iteration on the next curve
			j = 0;
			
			Curve c = new BezierCurve(controlPoints, N_POINTS);
			Point p, p2 = null;
			int length = c.getPoints().size();
			// Iterates over the points to draw a curve
			for (int k = 0;k<length-1;k++) {
				p = c.getPoints().get(k);
				p2 = c.getPoints().get(k+1);
				g.drawLine(p.getX(), p.getY(), p2.getX(), p2.getY());
			}
			System.out.print("Bézier curve " + (i+1) + ": [");
			for (Point pt : controlPoints) {
				System.out.print("(" + pt.getX() + ", " + pt.getY() + ")");
			}
			System.out.println("]");
			controlPoints.clear();
		}
		System.out.println("");
	}
	
	/**
	 * Draws the B-Spline Curves
	 * @param g2 graphic in which the curves will be drawn
	 */
	private void drawBSplineCurves(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		g.setColor(new Color(255, 0, 0));
		List<Point> controlPoints = new ArrayList<Point>();
		Color[] colors = new Color[2];
		colors[0] = new Color(0, 255, 0);
		colors[1] = new Color(0, 120, 0);
		
		Line l2 = null;
		int i = 0, j = 0, color = 0;
		int nBSplineCurves = polyline.getLines().size()+1 == 4 ? 1 : (polyline.getLines().size() + 1) - 3;
				
		List<Line> linesTmp = polyline.getLines();
		Iterator<Line> it = polyline.getLines().iterator();
		
		for (i=0;i<nBSplineCurves;i++) {
			// Iterates over polyline's lines and gets the first three points
			while (it.hasNext() && j < 3) {
				// When creating second B-Spline curve
				if (i >= 1) {
					// If getting the first point
					if (j == 0) {
						// Get the initial point of the current line
						l2 = linesTmp.get(i);
						controlPoints.add(l2.getInitialPoint());
					}
					else {
						// This allows get the next two lines in current and next i iteration so it is possible 
						// to get the initial point to construct a new curve in that lines' segment
						l2 = linesTmp.get(i+j);
						controlPoints.add(l2.getInitialPoint());
					}
				}
				// If j = 0 then get the first point of first line
				else {
					l2 = it.next();
					controlPoints.add(l2.getInitialPoint());
				}
				j++;
			}
			g.setColor(colors[color % 2]);
			color++;
			controlPoints.add(l2.getFinalPoint());
			j = 0;
			
			Curve c = new BSplineCurve(controlPoints, N_POINTS);
			Point p, p2 = null;
			int length = c.getPoints().size();
			// Iterates over the points to draw a curve
			for (int k = 0;k<length-1;k++) {
				p = c.getPoints().get(k);
				p2 = c.getPoints().get(k+1);
				g.drawLine(p.getX(), p.getY(), p2.getX(), p2.getY());
			}
			System.out.print("B-Spline curve " + (i+1) + ": [");
			for (Point pt : controlPoints) {
				System.out.print("(" + pt.getX() + ", " + pt.getY() + ")");
			}
			System.out.println("]");
			controlPoints.clear();
		}
		System.out.println("");
	}
	
	/**
	 * Draws the Catmull-Rom Curves
	 * @param g2 graphic in which the curves will be drawn
	 */
	private void drawCatmullRomCurves(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		g.setColor(new Color(255, 0, 0));
		List<Point> controlPoints = new ArrayList<Point>();
		Color[] colors = new Color[2];
		colors[0] = new Color(0, 0, 255);
		colors[1] = new Color(0, 182, 220);
		
		Line l2 = null;
		int i = 0, j = 0, color = 0;
		int nCatmullRomCurves = polyline.getLines().size()+1 == 4 ? 1 : polyline.getLines().size() - 2;
		
		List<Line> linesTmp = polyline.getLines();
		Iterator<Line> it = polyline.getLines().iterator();
		
		for (i=0;i<nCatmullRomCurves;i++) {
			// Iterates over polyline's lines and gets the first three points
			while (it.hasNext() && j < 3) {
				// When creating second B-Spline curve
				if (i >= 1) {
					// If getting the first point
					if (j == 0) {
						// Get the initial point of the current line
						l2 = linesTmp.get(i);
						controlPoints.add(l2.getInitialPoint());
					}
					else {
						// Get the initial point of the next line
						l2 = linesTmp.get(i+j);
						controlPoints.add(l2.getInitialPoint());
					}
				}
				// If j = 0 then get the first point of first line
				else {
					l2 = it.next();
					controlPoints.add(l2.getInitialPoint());
				}
				j++;
			}
			g.setColor(colors[color % 2]);
			color++;
			controlPoints.add(l2.getFinalPoint());
			j = 0;
			
			Curve c = new CatmullRomCurve(controlPoints, N_POINTS);
			Point p, p2 = null;
			int length = c.getPoints().size();
			// Iterates over the points to draw a curve
			for (int k = 0;k<length-1;k++) {
				p = c.getPoints().get(k);
				p2 = c.getPoints().get(k+1);
				g.drawLine(p.getX(), p.getY(), p2.getX(), p2.getY());
			}
			System.out.print("Catmull-Rom curve " + (i+1) + ": [");
			for (Point pt : controlPoints) {
				System.out.print("(" + pt.getX() + ", " + pt.getY() + ")");
			}
			System.out.println("]");
			controlPoints.clear();
		}
		System.out.println("");
	}
	
	
	/**
	 * Draws a cross in screen
	 * @param g graphic in which the cross will be drawn
	 * @param xPos x coordinate of the cross
	 * @param yPos y coordinate of the cross
	 */
	private void drawCross(Graphics2D g, int xPos, int yPos) {
		// Change color to black
		g.setColor(new Color(0, 0, 0));
		g.drawLine(xPos - CROSS_WIDTH, yPos - CROSS_WIDTH, xPos + CROSS_WIDTH, yPos + CROSS_WIDTH);
		g.drawLine(xPos - CROSS_WIDTH, yPos + CROSS_WIDTH, xPos + CROSS_WIDTH, yPos - CROSS_WIDTH);
		g.setColor(new Color(255, 255, 0));
	}
	
	/**
	 * Draws a cross inside a box in screen
	 * @param g graphic in which the cross will be drawn
	 * @param xPos x coordinate of the cross
	 * @param yPos y coordinate of the cross
	 */
	private void drawCrossInsideBox(Graphics2D g, int xPos, int yPos) {
		// Change color to black
		g.setColor(new Color(0, 0, 0));
		g.drawLine(xPos - CROSS_WIDTH, yPos - CROSS_WIDTH, xPos + CROSS_WIDTH, yPos + CROSS_WIDTH);
		g.drawLine(xPos - CROSS_WIDTH, yPos + CROSS_WIDTH, xPos + CROSS_WIDTH, yPos - CROSS_WIDTH);
		g.drawRect(xPos - (CROSS_WIDTH+2), yPos - (CROSS_WIDTH+2), (CROSS_WIDTH+2)*2, (CROSS_WIDTH+2)*2);
		g.setColor(new Color(255, 255, 0));
	}

	/**
	 * Sets the number of points that the polyline will have
	 * @param num number of points
	 */
	public void setNumPoints(int num) {
		numPoints = num;
		polyline = new Polyline(num);
	}

	/**
	 * Draws a Convex Hull with the first 4 points of the polyline
	 * @param g2 graphic in which the convex hull will be drawn
	 */
	private void drawConvexHull(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		g.setColor(new Color(0, 255, 255));
		int xPoints[] = new int[3];
		int yPoints[] = new int[3];
		int i = 0;
		Line l = null;
		// Get the first three points of the polyline (0, 1, 2)
		for (i = 0;i<3;i++) {
			l = polyline.getLineAt(i);
			if (l != null) {
				xPoints[i] = l.getInitialPoint().getX();
				yPoints[i] = l.getInitialPoint().getY();
			}
		}
		g.fillPolygon(xPoints, yPoints, 3);		
		
		// Get another three points
		for (i = 0;i<2;i++) {
			// Get lines 0, 2, 3
			l = polyline.getLineAt(i > 0 ? i+1 : i);
			if (l != null) {
				xPoints[i] = l.getInitialPoint().getX();
				yPoints[i] = l.getInitialPoint().getY();
				if (i == 1) {
					// Get the final point of the third line
					xPoints[i+1] = l.getFinalPoint().getX();
					yPoints[i+1] = l.getFinalPoint().getY();
				}
			}
		}
		g.fillPolygon(xPoints, yPoints, 3);
		
		// Get the second line's initial point to draw another triangle
		l = polyline.getLineAt(1);
		if (l != null) {
			xPoints[1] = l.getInitialPoint().getX();
			yPoints[1] = l.getInitialPoint().getY();
		}
		g.fillPolygon(xPoints, yPoints, 3);
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Will redraw the convex hull if exists at least 3 lines (4 points)
		if (polyline.getLines().size() >= 3) {
			if (showConvexHull)
				drawConvexHull(g2);
		}
		g2.setColor(new Color(255, 255, 0));
		
		// If the user is drawing a line or a printing rectangle and an event calls the paintComponent 
		// it is necessary to draw it
		if (drawingLine) {
			g2.drawLine(xStartPos, yStartPos, xPreviousPos, yPreviousPos);
		}
		if (printBox) {	
			int xDiffAnterior = Math.abs(boxXStartPos-boxXPreviousPos);
			int yDiffAnterior = Math.abs(boxYStartPos-boxYPreviousPos);
			g2.setColor(new Color(0, 0, 0));
			g.drawRect(boxXStartPos, boxYStartPos, Math.abs(xDiffAnterior), Math.abs(yDiffAnterior));
		}
		// Change color to be yellow
		g2.setColor(new Color(255, 255, 0));
		
		// If there's just the initial point drawn it is redrawn
		if (tmpPoint != null)
				drawCross(g2, tmpPoint.getX(), tmpPoint.getY());
		
		// Redraws all the points and lines
		for (Line l : polyline.getLines()) {
			if (showPolyline)
				g2.drawLine(l.getInitialPoint().getX(), l.getInitialPoint().getY(),
					l.getFinalPoint().getX(), l.getFinalPoint().getY());
			if (l.getInitialPoint().isBoxedPoint())
				drawCrossInsideBox(g2, l.getInitialPoint().getX(), l.getInitialPoint()
						.getY());
			else
				drawCross(g2, l.getInitialPoint().getX(), l.getInitialPoint()
					.getY());
			if (l.getFinalPoint().isBoxedPoint())
				drawCrossInsideBox(g2, l.getFinalPoint().getX(), l.getFinalPoint().getY());
			else
				drawCross(g2, l.getFinalPoint().getX(), l.getFinalPoint().getY());
		}
		
		// Redraws all the curves (if they are set to be visible)
		if (polyline.getLines().size() >= 3) {
			if (showBezierCurve)
				drawBezierCurves(g2);
			if (showBSplineCurve)
				drawBSplineCurves(g2);
			if (showCatmullRomCurve)
				drawCatmullRomCurves(g2);
			
		}
	}


	/**
	 * Clears the draw area and resets attributes of it
	 */
	public void clearDraw() {
		// Change application state
		drawingLine = false;
		tmpPoint = null;
		polyline = new Polyline(numPoints);
		showBezierCurve = false;
		showBSplineCurve = false;
		showCatmullRomCurve = false;
		showConvexHull = false;
		showPolyline = true;
		printBox = false;
		needsFirstPoint = false;
		currentPoints = 0;
		rectanglePointsDrawn = 0;
		// Force repaint
		repaint();
	}
	
	/**
	 * Changes Bezier Curves visibility
	 */
	public void changeBezierCurvesVisibility() {
		showBezierCurve = !showBezierCurve;
		if (showBezierCurve)
			frame.activatePrintItem();
		else if (!showPolyline)
			frame.disablePrintItem();
		Graphics2D g = (Graphics2D) getGraphics();
		this.paintComponent(g);
	}
	
		
	/**
	 * Changes B-Spline Curves visibility
	 */
	public void changeBSplineCurvesVisibility() {
		showBSplineCurve = !showBSplineCurve;
		Graphics2D g = (Graphics2D) getGraphics();
		this.paintComponent(g);
	}
	
	/**
	 * Changes Catmull-Rom Curves visibility
	 */
	public void changeCatmullRomCurvesVisibility() {
		showCatmullRomCurve = !showCatmullRomCurve;
		Graphics2D g = (Graphics2D) getGraphics();
		this.paintComponent(g);
	}
	
	/**
	 * Changes Convex Hull visibility
	 */
	public void changeConvexHullVisibility() {
		showConvexHull = !showConvexHull;
		Graphics2D g = (Graphics2D) getGraphics();
		this.paintComponent(g);
	}
	
	/**
	 * Changes Polyline visibility
	 */
	public void changePolylineVisibility() {
		showPolyline = !showPolyline;
		if (showPolyline)
			frame.activatePrintItem();
		else if (!showBezierCurve)
			frame.disablePrintItem();
		Graphics2D g = (Graphics2D) getGraphics();
		this.paintComponent(g);
	}
}
		