import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This class represents a Polyline
 */
public class Polyline {
	private List<Line> lineList;
	private boolean finished;
	private int length;
	
	/**
	 * Constructs a polyline with length points
	 * @param length number of points that the polyline will have
	 */
	public Polyline(int length) {
		lineList = new ArrayList<Line>();
		finished = false;
		// There will be length-1 lines
		this.length = length-1;
	}
	
	/**
	 * Adds a line to the polyline
	 * @param line line to add
	 * @return true if lines was added; false otherwise
	 */
	public boolean addLine(Line line) {
		// If it is the last line, then the polyline will be finished
		finished = (lineList.size()+1 == length);
		return lineList.add(line);
	}
	
	/**
	 * Returns line at position pos
	 * @param pos position of the line to get
	 * @return line at position pos
	 */
	public Line getLineAt(int pos) {
		if (pos < lineList.size())
			return lineList.get(pos);
		return null;
	}
	
	/**
	 * Calculates the Euclidian distance between two points
	 * @param x1 initial x coordinate
	 * @param y1 initial y coordinate
	 * @param x2 final x coordinate
	 * @param y2 final y coordinate
	 * @return Euclidian distance between points (x1, y1) and (x2, y2)
	 */
	private double euclideanDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
	}
	
	/**
	 * Returns the line that has the point with coordinates x, y
	 * @param x x coordinate of the desired point
	 * @param y y coordinate of the desired point
	 * @param radius radius of the neighborhood
	 * @return line that has the point with coordinates x, y; null if there's no point
	 */
	public Line getLineWithPoint(int x, int y, int radius) {
		boolean done = false;
		Iterator<Line> it = lineList.iterator();
		Line l = null;
		int iptX, iptY, fptX, fptY;
		// Iterate hover lines to get initial and final points of each line
		while (it.hasNext() && !done) {
			l = it.next();
			iptX = l.getInitialPoint().getX();
			iptY = l.getInitialPoint().getY();
			fptX = l.getFinalPoint().getX();
			fptY = l.getFinalPoint().getY();
			done = (euclideanDistance(iptX, iptY, x, y) <= radius) || 
			(euclideanDistance(fptX, fptY, x, y) <= radius);
		}
		if (done)
			return l;
		return null;
	}
			
	/**
	 * Changes all the lines' points which are in the x, y neighborhood to be the point pt 
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param radius radius of the neighborhood
	 * @param pt the new point to be set
	 */
	public void changePointReference(int x, int y, int radius, Point pt) {
		Iterator<Line> it = lineList.iterator();
		Line l = null;
		int iptX, iptY, fptX, fptY;
		// Iterate hover lines to change initial or final point if it is in the point x,y neighborhood
		while (it.hasNext()) {
			l = it.next();
			iptX = l.getInitialPoint().getX();
			iptY = l.getInitialPoint().getY();
			fptX = l.getFinalPoint().getX();
			fptY = l.getFinalPoint().getY();
			if (euclideanDistance(iptX, iptY, x, y) <= radius && l.getInitialPoint() != pt) {
				 l.setInitialPoint(pt);
			}
			if (euclideanDistance(fptX, fptY, x, y) <= radius && l.getFinalPoint() != pt) {
				l.setFinalPoint(pt);
			}
		}
	}
	
	/**
	 * Returns a neighbor of the point pt
	 * @param pt point to use to search for neighbors
	 * @param radius radius of the neighborhood
	 * @return a point in neighborhood; null if there's no point
	 */
	public Point getNeighbor(Point pt, int radius) {
		int x = pt.getX();
		int y = pt.getY();
		boolean found = false;
		Iterator<Line> it = lineList.iterator();
		Line l = null;
		int iptX, iptY, fptX, fptY;
		// Iterate hover lines to get the neighbor of point pt
		while (it.hasNext() && !found) {
			l = it.next();
			iptX = l.getInitialPoint().getX();
			iptY = l.getInitialPoint().getY();
			fptX = l.getFinalPoint().getX();
			fptY = l.getFinalPoint().getY();

			found = (euclideanDistance(iptX, iptY, x, y) <= radius) && 
			(pt != l.getInitialPoint());
			if (found)
				return l.getInitialPoint();
			found = (euclideanDistance(fptX, fptY, x, y) <= radius) && 
			(pt != l.getFinalPoint());
			if (found)
				return l.getFinalPoint();
		}
		return null;
	}
	
	/**
	 * Finishes the polyline
	 */
	public void finish() {
		finished = true;
	}
	
	/**
	 * Checks if the polyline has point with coordinates x, y or in x, y neighborhood
	 * @param x coordinate x to search
	 * @param y coordinate y to search
	 * @param radius radius of the neighborhood
	 * @return true if there's a point in neighborhood; false otherwise
	 */
	public boolean hasPoint(int x, int y, int radius) {
		return getLineWithPoint(x, y, radius) != null;
	}
		
	/**
	 * Returns the point in line l which has coordinates x, y or is in x, y neighborhood
	 * @param l line to use
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param radius radius of the neighborhood
	 * @return the point in line l which has coordinates x, y or is in x, y neighborhood
	 */
	public Point getExactPoint(Line l, int x, int y, int radius) {
		int iptX, iptY;
		iptX = l.getInitialPoint().getX();
		iptY = l.getInitialPoint().getY();
		
		if (euclideanDistance(iptX, iptY, x, y) <= radius)
			return l.getInitialPoint();
		return l.getFinalPoint();
	}
	
	/**
	 * Returns if the polyline is finished
	 * @return true if is finished; false otherwise
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Returns the lines list of the polyline
	 * @return lines list of the polyline
	 */
	public List<Line> getLines() {
		return lineList;
	}
	
	
}
