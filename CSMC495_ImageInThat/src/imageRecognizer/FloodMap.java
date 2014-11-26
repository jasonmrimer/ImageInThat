package imageRecognizer;
import java.awt.Color;
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
	ArrayList<Point> pointList;
	ArrayList<IRLine> sideList;
	
	public FloodMap(BufferedImage image){
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.bgRGB = image.getRGB(0, 0); //assume top left corner is background since shapes cannot reach
		pointList = new ArrayList<Point>();
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
		//top to bottom, left to right
		for (int row = 0; row < height; row++){		//rows by height value
			for (int col = 0; col < width; col++){	//columns by width
				if (image.getRGB(row, col) != bgRGB){
					checkPoint(row, col);	//process the point onto a side's line object
					break;
				}
			}
		}
		//top to bottom, right to left
		for (int row = 0; row < height; row++){			//rows by height value
			for (int col = width - 1; col > -1; col--){	//columns by width
				if (image.getRGB(row, col) != bgRGB){
					checkPoint(row, col);	//process the point onto a side's line object
					break;
				}
			}
		}
	}
	
	private void checkPoint(int x, int y){
		if (sideList.isEmpty()) {				//no sides yet, focus on the first two points
			if (pointList.size() < 2){			//if there are not points, add the first one
				pointList.add(new Point(x, y));
				return;
			}
			else {								//enough points to make the first line
				sideList.add(new IRLine(pointList.get(0), pointList.get(1)));
				pointList.clear();				//remove all points to make way for new side endpoints
				return;
			}
		}
		else {
			Point tempPoint = new Point(x, y);
			//check if the point fits on any line
			for (IRLine line : sideList){
				if (line.doesExtendLine(tempPoint)) { //extends the side, removes the old vertex inside the method
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
				pointList.clear();			//remove all points to make way for new side endpoints
				return;
			}
		}
	}
	
	public ArrayList<Point> getPointList(){
		return this.pointList;
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
			slopeAllowance = 0.1;					//slope at 5% allowance
			slope = (p1.getX() - p2.getX()) / (p1.getY() - p2.getY());
			yIntercept = p1.getY() - (slope * p1.getX());
		}
		
		public boolean doesExtendLine(Point point){
			//use equation of line to determine if point is on line (not segment but "infinite" line
			if ((point.getY() - yIntercept <= (slope * (1 + slopeAllowance)) * point.getX()) && 
					(point.getY() - yIntercept <= (slope * (1 - slopeAllowance) * point.getX()))) { //on line within allowance
				//check whether the point extends the line
				Point midPoint = new Point((int) (getX1() + (getX1() - getX2()) / 2),(int) (getY1() + (getY1() - getY2()) / 2));
				double p1Dist = this.getP1().distance(midPoint); //get distance from mid
				double p2Dist = this.getP2().distance(midPoint); //get distance from mid
				double pointDist = point.distance(midPoint);
				p1Dist = (this.getP1().getX() - midPoint.getX() > 0) ? -(p1Dist) : p1Dist;		//change distance based on if to the left/right of point
				p2Dist = (this.getP2().getX() - midPoint.getX() > 0) ? -(p2Dist) : p2Dist;		//change distance based on if to the left/right of point
				pointDist = (point.getX() - midPoint.getX() > 0) ? -(pointDist) : pointDist;	//change distance based on if to the left/right of point
				//check whether to compare to endpoint 1 or 2 based on direction from midpoint
				if ((p1Dist < 0 && pointDist < 0) || (p1Dist > 0 && pointDist > 0)){ 			//both share direction so compare
					if (point.distance(midPoint) > this.getP1().distance(midPoint)) { 			//point is farther from mid than endpoint, replace endpoint
						this.setLine(point, this.getP2());
					}
				}
				else { //shares with p2
					if (point.distance(midPoint) > this.getP2().distance(midPoint)) { 			//point is farther from mid than endpoint, replace endpoint
						this.setLine(this.getP1(), point);
					}
				}
				return true;
			}
			else return false;
		}
		
		@Override
		public Rectangle2D getBounds2D() {
			// TODO Auto-generated method stub
			return null;
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
			p1.setLocation(x1, x2);
			p2.setLocation(x2, y2);
		}
		
	}

}