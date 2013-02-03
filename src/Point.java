
/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This class represents a Point
 */
public class Point {

	private int x;
	private int y;
	private boolean boxedPoint;
	
	/**
	 * Constructs a point
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.boxedPoint = false;
	}
	
	/**
	 * Returns the point's x coordinate
	 * @return point's x coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the point's y coordinate
	 * @return point's y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets the point's x coordinate
	 * @param x new x coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Sets the point's y coordinate
	 * @param y new y coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Returns if the point is in a box
	 * @return true if the point is a box; false otherwise
	 */
	public boolean isBoxedPoint() {
		return boxedPoint;
	}
	
	
	/**
	 * Puts the point in a box
	 */
	public void putInBox() {
		this.boxedPoint = true;
	}
	
}
