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



public class FloodMap {
	//attempt iteration instead of recursion by going line-by line
	int width, height, bgRGB, shapeRGB;
	BufferedImage image;
	ArrayList<Point2D> pointList;
	ArrayList<Point2D> vertexList;
	ArrayList<IRLine> sideList;
	
	public FloodMap(BufferedImage image){
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.bgRGB = image.getRGB(0, 0); //assume top left corner is background since shapes cannot reach
		pointList = new ArrayList<Point2D>();
		vertexList = new ArrayList<Point2D>();
		sideList = new ArrayList<IRLine>();
		map();
	}
	
	/*
	 * map taking advantage of the two-dimension comvex shapes by approaching in a line from the left
	 * then approaching from the right to capture all the points outlining the shape and skipping the 
	 * inside of the shape
	 * 
	 * now, i can take further advantage of slope such that, as long as the slope != 0 the first point the line
	 * finds from the left must be a vertex then each next point will be on that line until the slope changes then that
	 * point was a vertex. then reverse it from the right. 
	 * oh! just use the slopes - for each change in slope there should be a side and that is the number of sides... except
	 * there will be sides with equal slopes - but not equal equations of the line
	 */
	private void map(){
		//next, is pixel a corner pixel? because pixels are a rectangular grid, we only need to regard
		//the corner pixels of a line to map its actual slop & endpoints. if any pixel above this pixel is the same color then 
		//this pixel is not a corner pixel, disregard it
		//top to bottom, left to right
		for (int y = 0; y < height; y++){			//rows by height value *This is the Y-Coordinate*
			for (int x = 0; x < width; x++){		//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB){	//different from bg = shape
					if ((image.getRGB(x, y - 1) == bgRGB) || (image.getRGB(x, y + 1) == bgRGB)){		//check above/below to see if corner pixel
						checkPoint(x, y);			//process the point onto a side's line object
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
					}
					break;								//found boundary, move to next row
				}
			}
		}
	}
	/*
	 * process each point through this method that will check whether lines already exist,
	 * create lines, and call methods to test whether points fall on an existing line
	 */
	private void checkPoint(int x, int y){
		if (sideList.isEmpty()) {				//no sides yet, focus on the first two points
			if (pointList.size() < 2){			//if there are not points, add the first one
				pointList.add(new Point(x, y));
				return;
			}
			else {								//enough points to make the first line
//				sideList.add(new IRLine(pointList.get(0), pointList.get(1)));
//				pointList.clear();				//remove all points to make way for new side endpoints
				sideList.add(new IRLine(pointList.get(0), pointList.get(1)));
				pointList.clear();
//				vertexList.add(pointList.remove(0));
//				vertexList.add(pointList.remove(0));
				return;
			}
		}
		else {	//there are line(s) already mapped
			Point tempPoint = new Point(x, y);
			//next, is pixel a corner pixel? because pixels are a rectangular grid, we only need to regard
			//the corner pixels of a line to map its actual slop & endpoints. if any pixel above this pixel is the same color then 
			//this pixel is not a corner pixel, disregard it
			
			//check if the point fits on any line
			for (IRLine line : sideList){
				Point2D removePoint = line.doesExtendLine(tempPoint);	//if the point extends the line it will return the replace endpoint and needs to become a vertex
				if (removePoint != null) { //extends the side, (may need to remove the old vertex inside the method)
//					vertexList.remove(removePoint);
//					vertexList.add(tempPoint);
					return;
				}
			}
			//it is not on a line, add it to the point list to create a new line
			if (pointList.size() < 2){	//if it does not fall on a line, add it to ready a new side object
				pointList.add(new Point(x, y));
				return;
			}
			else if (pointList.size() == 2){//enough points to make the first line
				sideList.add(new IRLine(pointList.get(0), pointList.get(1)));
				pointList.clear();
//				vertexList.add(pointList.remove(0));
//				vertexList.add(pointList.remove(0));
				return;
			}
		}
	}
	
	public ArrayList<Point2D> getPointList(){
		return this.pointList;
	}
	public int getSideNumber(){
		return sideList.size();
	}
	public ArrayList<Point2D> getVertexList(){
		return this.vertexList;
	}
	
	private class IRLine extends Line2D {
		double slope, slopeAllowance, yIntercept;	//slope used for line equation, allowance to account for anti-alias
		Point2D p1, p2;
		
		public IRLine(Point2D p1, Point2D p2){
			this.p1 = p1;
			this.p2 = p2;
			this.setLine(p1, p2);
			slopeAllowance = 0.2;					//slope at % allowance
			slope = (p1.getX() - p2.getX()) / (p1.getY() - p2.getY());
			yIntercept = p1.getY() - (slope * p1.getX());
		}
		
		public Point2D doesExtendLine(Point2D point){
			//use line functions to determine if point is on line by creating a fake line with one of the
			//current endpoints in between the tempPoint 
			
			//firstly, is the point between the endpoints and on the line?
			if (this.contains(point)) {
				return null;	//break condition - we do not care about the point because it does not extend the line
			}
			
			
			
			
			//check whether the point extends the line
			//using the midpoint, there will always be one endpoint at a negative distance
			//and one endpoint at a positive distance (to the right/left of endpoint). then check
			//whether the new point is to the left or right and compare it with the endpoint that 
			//shares direction
//			Point2D midPoint = new Point((int) (getX1() + (getX1() - getX2()) / 2),(int) (getY1() + (getY1() - getY2()) / 2));	//create midpoint
//			double p1Dist = p1.distance(midPoint); 			//get distance from mid
//			double p2Dist = p2.distance(midPoint);			//get distance from mid
//			double pointDist = point.distance(midPoint);	//get inputPoint distance from mid
//			p1Dist = (p1.getX() - midPoint.getX() < 0) ? -(p1Dist) : p1Dist;		//change distance based on if to the left/right of point
//			p2Dist = (p2.getX() - midPoint.getX() < 0) ? -(p2Dist) : p2Dist;		//change distance based on if to the left/right of point
//			pointDist = (point.getX() - midPoint.getX() < 0) ? -(pointDist) : pointDist;	//change distance based on if to the left/right of point
			
			IRLine p1ToPoint = new IRLine(p1, point);
			IRLine p2ToPoint = new IRLine(p2, point);
			
			if (p1ToPoint.getBounds2D().contains(p2)){		//new line contains old endpoint; discard old endpoint
				Point2D temp = p2;
				this.setLine(p1, point);
				return p2;
			}
			else if (p2ToPoint.getBounds2D().contains(p1)){	//new line contains old endpoint; discard old endpoint
				Point2D temp = p1;
				this.setLine(point, p1);
				return temp;
			}
			return null;
		}
			
		private Point2D switchPoints(Point2D removePoint, Point2D insertPoint) {
			Point2D tempPoint = removePoint;
			removePoint = insertPoint;
			return tempPoint;
		}

		@Override
		public Rectangle2D getBounds2D() {
			int x = (getX1() < getX2()) ? (int) getX1() : (int) getX2();
			int y = (getY1() < getY2()) ? (int) getY1() : (int) getY2();
			int width = Math.abs((int)(getX1() - getX2()));
			int height = Math.abs((int)(getY1() - getY2()));
			return new Rectangle(new Point(x, y), new Dimension(width, height));
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