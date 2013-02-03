/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This class represents a Line
 */
public class Line {
	private Point initialPoint;
	private Point finalPoint;
	
	/**
	 * Constructs a line
	 * @param initialPoint line's initial point
	 * @param finalPoint line's final point
	 */
	public Line(Point initialPoint, Point finalPoint) {
		this.initialPoint = initialPoint;
		this.finalPoint = finalPoint;
	}
	
	/**
	 * Returns the line's initial point
	 * @return line's initial points
	 */
	public Point getInitialPoint() {
		return initialPoint;
	}
	
	/**
	 * Returns the line's final point
	 * @return line's final points
	 */
	public Point getFinalPoint() {
		return finalPoint;
	}
	
	
	/**
	 * Sets the line's initial point
	 * @param pt new initial point
	 * @return new initial point
	 */
	public Point setInitialPoint(Point pt) {
		initialPoint = pt;
		return initialPoint;
	}
	
	/**
	 * Sets the line's final point
	 * @param pt new final point
	 * @return new final point
	 */
	public Point setFinalPoint(Point pt) {
		finalPoint = pt;
		return finalPoint;
	}
}
