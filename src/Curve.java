import java.util.List;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This interface represents a Curve
 */
public interface Curve {
	/**
	 * Returns the points to be used when drawing the curve
	 * @return points to be used when drawing the curve
	 */
	List<Point> getPoints();
	
	/**
	 * Returns the QFunction value of x
	 * @param t x value
	 * @return QFunction value of x
	 */
	double getQFunctionX(double t);
	
	/**
	 * Returns the QFunction value of y
	 * @param t y value
	 * @return QFunction value of y
	 */
	double getQFunctionY(double t);
}
