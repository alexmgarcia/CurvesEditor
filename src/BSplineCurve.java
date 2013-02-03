import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This class represents a B-Spline Curve
 */
public class BSplineCurve implements Curve {
	
	private List<Point> points;
	private List<Point> controlPoints;
	private int nPoints;
	
	/**
	 * Constructs a B-Spline Curve
	 * @param controlPoints control points of the curve
	 * @param nPoints number of points to be used when drawing the curve
	 */
	public BSplineCurve(List<Point> controlPoints, int nPoints) {
		this.controlPoints = controlPoints;
		points = new ArrayList<Point>(nPoints);
		this.nPoints = nPoints;
		createPoints();
	}
	
	/**
	 * Returns the blending function 0 value of the B-Spline Curve
	 * @param t
	 * @return blending function 0 value
	 */
	private double getBlendingFunction0(double t) {
		return (-1*Math.pow(t, 3) + 3*Math.pow(t, 2) - 3*t + 1)/6.0;
	}
	
	/**
	 * Returns the blending function 1 value of the B-Spline Curve
	 * @param t
	 * @return blending function 1 value
	 */
	private double getBlendingFunction1(double t) {
		return (3*Math.pow(t, 3) - 6*Math.pow(t, 2) + 4)/6.0;
	}
	
	/**
	 * Returns the blending function 2 value of the B-Spline Curve
	 * @param t
	 * @return blending function 2 value
	 */
	private double getBlendingFunction2(double t) {
		return (-3*Math.pow(t, 3) + 3*Math.pow(t, 2) + 3*t + 1)/6.0;
	}
	
	/**
	 * Returns the blending function 3 value of the B-Spline Curve
	 * @param t
	 * @return blending function 3 value
	 */
	private double getBlendingFunction3(double t) {
		return Math.pow(t, 3)/6.0;
	}
	
	/* (non-Javadoc)
	 * @see Curve#getQFunctionX(double)
	 */
	public double getQFunctionX(double t) {
		Point p0, p1, p2, p3;
		p0 = controlPoints.get(0);
		p1 = controlPoints.get(1);
		p2 = controlPoints.get(2);
		p3 = controlPoints.get(3);
		
		return p0.getX()*getBlendingFunction0(t) + p1.getX()*getBlendingFunction1(t) + 
		p2.getX()*getBlendingFunction2(t) + p3.getX()*getBlendingFunction3(t); 
	}
	
	/* (non-Javadoc)
	 * @see Curve#getQFunctionY(double)
	 */
	public double getQFunctionY(double t) {
		Point p0, p1, p2, p3;
		p0 = controlPoints.get(0);
		p1 = controlPoints.get(1);
		p2 = controlPoints.get(2);
		p3 = controlPoints.get(3);
		
		return p0.getY()*getBlendingFunction0(t) + p1.getY()*getBlendingFunction1(t) + 
		p2.getY()*getBlendingFunction2(t) + p3.getY()*getBlendingFunction3(t); 		
	}
	
	/**
	 * Creates the points of the curve to be used when drawing it
	 */
	private void createPoints() {
		double inc = 1.0 / (double)nPoints;
		double t=0;
		int x, y;
		for (int i = 0;i<nPoints+1;i++) {
			x = (int)getQFunctionX(t);
			y = (int)getQFunctionY(t);
			points.add(new Point(x, y));
			t += inc;
		}
	}
	
	/* (non-Javadoc)
	 * @see Curve#getPoints()
	 */
	public List<Point> getPoints() {
		return points;
	}

}
