package imageRecognizer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;



public class FloodMap {
	//attempt iteration instead of recursion by going line-by line
	int width, height, bgRGB, shapeRGB;
	BufferedImage image;
	ArrayList<Point> pointList;
	ArrayList<IRLine> sideList;
	int vertices;
	Double slope;
	Stack<Point> points;
	
	public FloodMap(BufferedImage image){
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.bgRGB = image.getRGB(0, 0); //assume top left corner is background since shapes cannot reach
		pointList = new ArrayList<Point>();
		sideList = new ArrayList<IRLine>();
		vertices = 0;
		points = new Stack<Point>();
		points.push(new Point(0,0));
		map();
		
//		for (IRLine side : sideList){
//			System.out.println(side.getP1().distance(side.getP2()));
//		}
	}
	
	/*
	 * map taking advantage of the two-dimension comvex shapes by approaching in a line from the left
	 * then approaching from the right to capture all the points outlining the shape and skipping the 
	 * inside of the shape
	 * 
	 */
	private void map(){
		/*
		 * next, is pixel a corner pixel? because pixels are a rectangular grid, we only need to regard
		 * the corner pixels of a line to map its actual slop & endpoints. if any pixel above this pixel is the same color then 
		 * this pixel is not a corner pixel, disregard it
		 * also, vertices will mark a change in direction of lines and should have both neighbors (above/below) = background 
		 */
		
		//top to bottom, left to right
		for (int y = 0; y < height; y++){			//rows by height value *This is the Y-Coordinate*
			for (int x = 0; x < width; x++){		//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB){	//different from bg = shape
					if ((image.getRGB(x, y - 1) == bgRGB) || (image.getRGB(x, y + 1) == bgRGB)){		//check above/below to see if corner pixel
						checkPoint(x, y);			//process the point onto a side's line object
						slope = ((x - points.peek().getX()) == 0) ? null : (y - points.peek().getY()) / (x - points.pop().getX());
						if (slope == null) System.out.println("{" + x + ", " + y + "} _ undef");
						else System.out.println("{" + x + ", " + y + "} = " + slope);
//						System.out.println("{" + x + ", " + y + "} _ " + ((x - points.peek().getX()) - (y - points.pop().getY()) / 2)); //something important happened here...
						points.push(new Point(x, y));
//						if ((image.getRGB(x, y - 1) == bgRGB) && (image.getRGB(x, y + 1) == bgRGB)) vertices++;					
					}
					break;							//found boundary, move to next row
				}
			}
		}
		//top to bottom, right to left
		for (int y = 0; y < height; y++){			//rows by height value *This is the Y-Coordinate*
			for (int x = width - 1; x > -1; x--){	//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB) {	//different than background = shape
					if ((image.getRGB(x, y - 1) == bgRGB) || (image.getRGB(x, y + 1) == bgRGB)) {		//check above/below to see if corner pixel
						checkPoint(x, y);			//process the point onto a side's line object
						slope = ((x - points.peek().getX()) == 0) ? null : (y - points.peek().getY()) / (x - points.pop().getX());
						if (slope == null) System.out.println("{" + x + ", " + y + "} _ undef");
						else System.out.println("{" + x + ", " + y + "} = " + slope);
//						System.out.println("{" + x + ", " + y + "} _ " + ((x - points.peek().getX()) - (y - points.pop().getY()) / 2)); //something important happened here...
						points.push(new Point(x, y));
//						if ((image.getRGB(x, y - 1) == bgRGB) && (image.getRGB(x, y + 1) == bgRGB)) vertices++;					
					}
					break;							//found boundary, move to next row
				}
			}
		}
	}
	/*
	 * process each point through this method that will check whether lines already exist,
	 * create lines, and call methods to test whether points fall on an existing line
	 */
	private void checkPoint(int x, int y){
		//first, check if there are any sides to test the point against
		if (sideList.isEmpty()) {				//no sides yet, focus on the first two points
			if (pointList.size() < 2){			//if there are not enough points to create a line, add the point
				pointList.add(new Point(x, y));
				return;
			}
			else {								//enough points to make the first line
				sideList.add(new IRLine(pointList.get(0), pointList.get(1)));
				pointList.clear();				//remove all points to make way for new side endpoints
				return;
			}
		}
		else {	//there are line(s) already mapped
			Point tempPoint = new Point(x, y);
			//check if the point fits on any line
			for (IRLine line : sideList){
				Point removePoint = line.doesExtendLine(tempPoint);	//if the point extends the line it will return the replace endpoint and needs to become a vertex
				if (removePoint != null) { 								//extends the side, (may need to remove the old vertex inside the method)
					return;
				}
			}
			//it is not on a line, add it to the point list to create a new line
			if (pointList.size() < 2){			//if it does not fall on a line, add it to ready a new side object
				pointList.add(new Point(x, y));
				return;
			}
			else if (pointList.size() == 2){	//enough points to make another line
				sideList.add(new IRLine(pointList.get(0), pointList.get(1)));
				pointList.clear();
				return;
			}
		}
	}
	
	public int getSideNumber(){
		return sideList.size();
	}
	
	private class IRLine extends Line2D {
		double slope, slopeAllowance, yIntercept;	//slope used for line equation, allowance to account for anti-alias
		Point p1, p2;
		
		public IRLine(Point p1, Point p2){
			this.p1 = p1;
			this.p2 = p2;
			this.setLine(p1, p2);
			slopeAllowance = 0.2;					//slope at % allowance
			slope = (p1.getX() - p2.getX()) / (p1.getY() - p2.getY());
			yIntercept = p1.getY() - (slope * p1.getX());
		}
		
		public Point doesExtendLine(Point point){
			//use line functions to determine if point is on line by creating a fake line with one of the
			//current endpoints in between the tempPoint 
			
			//firstly, is the point between the endpoints and on the line?
			if (this.contains(point)) {
				return null;	//break condition - we do not care about the point because it does not extend the line
			}
			/*
			 * Use "Rectanlge Theory": substituting the new point with each endpoint to create a line
			 * will also create bounding rectangles. If the bounding rectangle contains any original endpoint
			 * then the new point can replace that endpoint as it effectively extends the line.
			 * All sides should have unique, non-overlapping rectangle bounds for each line (this avoids counting
			 * a single point for multiple sides).
			 */
			IRLine p1ToPoint = new IRLine(p1, point);
			IRLine p2ToPoint = new IRLine(p2, point);
			Point temp = new Point();
			if (p1ToPoint.getBounds2D().contains(p2) && Math.abs(1 - (p2ToPoint.slope / this.slope)) > slopeAllowance){		//new line contains old endpoint; discard old endpoint
				temp = p2;
				this.setLine(p1, point);
				return temp;
			}
			else if (p2ToPoint.getBounds2D().contains(p1) && Math.abs(1 - (p1ToPoint.slope / this.slope)) > slopeAllowance){	//new line contains old endpoint; discard old endpoint
				temp = p1;
				this.setLine(point, p2);
				return temp;
			}
			return null;
		}
			
		private Point switchPoints(Point removePoint, Point insertPoint) {
			Point tempPoint = removePoint;
			removePoint = insertPoint;
			return tempPoint;
		}

		@Override
		public Rectangle2D getBounds2D() {
			int x = ((getX1() < getX2()) ? (int) getX1() : (int) getX2()); //one accounts for points lying on the rectangle
			int y = ((getY1() < getY2()) ? (int) getY1() : (int) getY2());
			int width = Math.abs((int)(getX1() - getX2()));
			int height = Math.abs((int)(getY1() - getY2()));
			return new  Rectangle(new Point(x, y), new Dimension(width, height));
		}
		
		@Override
		public double getX1() {
			return p1.getX();
		}
		@Override
		public double getY1() {
			return p1.getY();
		}
		@Override
		public Point2D getP1() {
			return this.p1;
		}
		@Override
		public double getX2() {
			return p2.getX();
		}
		@Override
		public double getY2() {
			return p2.getY();
		}
		@Override
		public Point2D getP2() {
			return this.p2;
		}

		@Override
		public void setLine(double x1, double y1, double x2, double y2) {
			p1 = new Point((int) x1, (int) y1);
			p2 = new Point((int) x2, (int) y2);
//			this.p1.setLocation(x1, x2);
//			this.p2.setLocation(x2, y2);
		}
		
	}

}