import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 * 
 * This class represents a PostScriptConverter
 */
public class PostScriptConverter {
	
	// A4 page
	private static final int FINAL_WIDTH = 595;
	private static final int FINAL_HEIGHT = 841;
	
	private static final String FILE_NAME = "Print.ps";
	
	private Polyline polyline;
	// initial x, y of the printing rectangle
	private int startX;
	private int startY;
	// final x, y of the printing rectangle
	private int endX;
	private int endY;
	
	// String which will contain the generated post script
	private String generatedPostScript;
	
	// Control variables to know if it will print the polyline and/or the curve
	private boolean drawLine;
	private boolean drawCurve;
	
	
	/**
	 * Constructs a PostScriptConverter
	 * @param p polyline to use
	 * @param startX initial x of the printing rectangle
	 * @param startY initial y of the printing rectangle
	 * @param endX final x of the printing rectangle
	 * @param endY final y of the printing rectangle
	 * @param drawLine if the polyline will be printed
	 * @param drawCurve if the curve will be printed
	 */
	public PostScriptConverter(Polyline p, int startX, int startY, int endX, int endY, boolean drawLine, boolean drawCurve) {
		this.polyline = p;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.drawLine = drawLine;
		this.drawCurve = drawCurve;
		generatedPostScript = "%!PS\n% Poligonal guia numa curva de Bézier\n/cm {28.35 mul} def\n";
	}
	
	/**
	 * Converts the coordinates of point pt to the A4 coordinates
	 * @param pt point to convert coordinates
	 * @return a point with the converted coordinates
	 */
	private Point convertCoordinates(Point pt) {
		int x=pt.getX();
		int y=pt.getY();
		int x1=startX;
		int y1=endY;
		int x2=endX;
		int y2=startY;
		int x1L=0;
		int y1L=FINAL_HEIGHT;
		int x2L=FINAL_WIDTH;
		int y2L=0;
		int newX=((x-x1)*(x2L-x1L) / (x2-x1))+x1L;
		int newY=((y-y1)*(y2L-y1L) / (y2-y1))+y1L;
		
		return new Point(newX, newY);
	}
	
	/**
	 * Generates the PostScript of the polyline
	 */
	public void convertPolyline() {
		List<Point> pointList = new LinkedList<Point>();
		Point i, f;
		int newiX, newiY;
		int newfX=0, newfY=0;
		int j = 0;
		for (Line l : polyline.getLines()) {
			i = l.getInitialPoint();
			f = l.getFinalPoint();
			newiX = convertCoordinates(i).getX();
			newiY = convertCoordinates(i).getY();
			newfX = convertCoordinates(f).getX();
			newfY = convertCoordinates(f).getY();
			if (j==0) {
				generatedPostScript+=(double)((newiX) / 28.35) + " cm " + (double)((FINAL_HEIGHT-newiY) / 28.35) + " cm moveto\n";
				generatedPostScript+="gsave\n";
			}
			if (j != 0)
				pointList.add(new Point(newiX, newiY));
			if (drawLine)
				generatedPostScript+=(double)((newfX) / 28.35) + " cm " + (double)((FINAL_HEIGHT-newfY) / 28.35) + " cm lineto\n";
			j++;
		}
		// Writes the line PostScript
		if (drawLine) {
			generatedPostScript+="[ 0.2 cm 0.2 cm ] 0 setdash\n0.02 cm setlinewidth\n1.0 0.0 0.0 setrgbcolor\nstroke\n";
			generatedPostScript+="grestore\n";
		}
		// Writes the curve PostScript
		if (drawCurve) {
			pointList.add(new Point(newfX, newfY));
			int nCurves = polyline.getLines().size()+1 == 4 ? 1 : ((polyline.getLines().size() + 1) / 4) + 1;
			Iterator<Point> pit = pointList.iterator();
			Point pt;
			for (int n = 0;n<nCurves;n++) {
				for (int k=0;k<3 && pit.hasNext();k++) {
					pt = pit.next();
					generatedPostScript+=(double)(pt.getX()) / 28.35 + " cm " + (double)(FINAL_HEIGHT-pt.getY()) / 28.35 + " cm ";
				}
				generatedPostScript+="curveto\n";
			}
			generatedPostScript+="stroke\n";
		}
			generatedPostScript+="showpage";
	}
	

	/**
	 * Converts the polyline and curves to PostScript and stores it
	 * @throws IOException
	 */
	public void printToFile() throws IOException{
		convertPolyline();
		storeAsText(generatedPostScript);

	}
	
	/**
	 * Writes the generatedPostScript to a file
	 * @param output file to write the generated PostScript
	 * @throws IOException
	 */
	private void storeAsText(String output) throws IOException {
		PrintWriter outStream = new PrintWriter(FILE_NAME);
		outStream.print(output);
		outStream.flush();
		outStream.close();
	}

}
