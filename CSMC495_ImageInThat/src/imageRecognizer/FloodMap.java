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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;



public class FloodMap {
	//attempt iteration instead of recursion by going line-by line
	private int width, height, bgRGB, shapeRGB;
	private BufferedImage image;
	private ArrayList<Point> pointList;
	private ArrayList<Point> tempVertexList;
	private ArrayList<Point> vertexList;
	private Point center;
	private double radius;
	private double area;
	
	public FloodMap(BufferedImage image){
		//initialize variables
		this.area = 0.0;
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.bgRGB = image.getRGB(0, 0); //assume top left corner is background since shapes cannot reach
		this.center = new Point();
		pointList = new ArrayList<Point>();
		tempVertexList = new ArrayList<Point>();
		vertexList = new ArrayList<Point>();
		//run methods
		map();				//get all the potential vertices and place into pointList
		tempVertexList = createTempVertices();	//get guaranteed 4 vertices
		center = new Point(150, 105);
//		center = findCenterWithOrthogonalBisectors();		//get center from temp vertices
		radius = createRadius();
		vertexList = createVertices();		//get all vertices from center 
		for (Point pt : tempVertexList){
			System.out.println(pt);
		}
		System.out.println(center);
	}
	
	private Double createRadius() {
		//check whether the center exists and there are vertices
		if (center != null && tempVertexList.size() > 0)
			return Math.sqrt(Math.pow(tempVertexList.get(0).getX() - center.getX(), 2) + Math.pow(tempVertexList.get(0).getY() - center.getY(), 2));
		else
			return null;
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
		for (int y = 0; y < height; y++){				//rows by height value *This is the Y-Coordinate*
			for (int x = 0; x < width; x++){			//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB){		//different from bg = shape
//					if ((image.getRGB(x, y - 1) == bgRGB) || (image.getRGB(x, y + 1) == bgRGB)){		//check above/below to see if corner pixel
						pointList.add(new Point(x, y));	//add point to the list as a potential vertex
//					}
					break;								//found boundary, move to next row
				}
			}
		}
		//top to bottom, right to left
		for (int y = height - 1; y > -1; y--){			//rows by height value *This is the Y-Coordinate*
			for (int x = width - 1; x > -1; x--){		//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB) {		//different than background = shape
//					if ((image.getRGB(x, y - 1) == bgRGB) || (image.getRGB(x, y + 1) == bgRGB)) {		//check above/below to see if corner pixel
						pointList.add(new Point(x, y)); //add point to the list as a potential vertex
//					}
					break;								//found boundary, move to next row
				}
			}
		}
	}
	
	public int getSideNumber(){
		return vertexList.size();
	}
	
	/*
	 * retrieves the left, right, top, and bottom-most points on the shape
	 * that must represent four vertices
	 */
	private ArrayList<Point> createTempVertices(){
		ArrayList<Point> createTempVerticesList = new ArrayList<Point>();
		//set the values at the minimum value within the image in order to grow the vertices  
		int leftMostX = Integer.MAX_VALUE, rightMostX = Integer.MIN_VALUE, topMostY = Integer.MAX_VALUE, bottomMostY = Integer.MIN_VALUE;
		Point tempLeftPoint = new Point(), tempRightPoint = new Point(), tempTopPoint = new Point(), tempBottomPoint = new Point();
		//find each of the 4 cardinal vertices
		for (Point pt : pointList){
			//test left
			if (pt.getX() <= leftMostX) {
				leftMostX = (int) pt.getX();
				tempLeftPoint = pt;						
			}
			//test right
			if (pt.getX() >= rightMostX) {
				rightMostX = (int) pt.getX();
				tempRightPoint = pt;						
			}
			//test bottom
			if (pt.getY() >= bottomMostY) {
				bottomMostY = (int) pt.getY();
				tempBottomPoint = pt;						
			}
			//test right
			if (pt.getY() <= topMostY) {
				topMostY = (int) pt.getY();
				tempTopPoint = pt;						
			}
		}
		//add vertices to array list
		/*
		 * for now, i only care about 3 vertices
		 */
		createTempVerticesList.add(tempLeftPoint);
		createTempVerticesList.add(tempRightPoint);
		createTempVerticesList.add(tempBottomPoint);
		createTempVerticesList.add(tempTopPoint);
		return createTempVerticesList;
	}
	/*
	 * using the radial theory, iterate through all points placing the points farthest from the center in the 
	 * vertex list
	 */
	private ArrayList<Point> createVertices(){
		ArrayList<Point> createVertexList = new ArrayList<Point>();
		double  distance = 0.0;
		HashMap<Point, Double> distanceHash = new HashMap<Point, Double>();
		for (Point pt : pointList){
			distance = Math.sqrt(Math.pow(pt.getX() - center.getX(), 2) + Math.pow(pt.getY() - center.getY(), 2));
			distanceHash.put(pt, distance);
		}
		for (Map.Entry<Point, Double> entry : distanceHash.entrySet()){
//			System.out.println(entry.getValue() + " - " + radius + " = " + (entry.getValue() - radius));
			if (Double.compare(entry.getValue(), radius) == 0) {
				createVertexList.add(entry.getKey());
			}
		}
		return createVertexList;
	}
	/*
	 * determine the area from all points
	 */
	private void getArea(){
		Point p1, p2;
		for (int pt = 0; pt < tempVertexList.size(); pt++){
			if (pt == tempVertexList.size() - 1){
				p1 = tempVertexList.get(pt);
				p2 = tempVertexList.get(0);
			}
			else {
				p1 = tempVertexList.get(pt);
				p2 = tempVertexList.get(pt + 1);
			}
			area += (double) (p1.getX() * p2.getY()) - (double) (p2.getX() * p1.getY());
		}
		area *= 0.5;
	}
	/*
	 * get the centeroid using the 
	 */
	private Point createCenter(){
		int x = 0, y = 0;
		for (Point pt : tempVertexList){
			x += pt.getX();
			y += pt.getY();
		}
		x /= tempVertexList.size();
		y /= tempVertexList.size();
		return new Point((int) x, (int) y);
	}
	
	private Point findCenterWithOrthogonalBisectors(){
		/*
		 * alpha represent lines (A = line from 1 to 2) and numeric represent vertex values
		 * double mA, mB, mC, bA, bB, bC;	//m = slope, b = y-intercept
		 * double midAx, midAy, midBx, midBy, midCx, midCy;	//midpoints 
		 * double wA, wB, wC, pA, pB, pC;	//w = inverse slope, p = othor bisector y-intercepts
		 * int x0, x1, x2, y0, y1, y2;		//use values for readbility throughout code
		 */
		int obX = 00, obY = 0;
		//set all values
		if (tempVertexList.size() == 3) {
			//set readability variables
			int x0 = (int) tempVertexList.get(0).getX(), x1 = (int) tempVertexList.get(1).getX(), x2 = (int) tempVertexList.get(2).getX();	//set x values
			int y0 = (int) tempVertexList.get(0).getY(), y1 = (int) tempVertexList.get(1).getY(), y2 = (int) tempVertexList.get(2).getY();	//set y values
			double mA = (y0 - y1) / (x0 - x1), mB = (y1 - y2) / (x1 - x2), mC = (y0 - y2) / (x0 - x2);	//set slopes		
			//change 0 slope to suuuuuuuuuuuper small slope to prevent undefined problems when iverse comes around
			if (mA == 0) mA = 0.0000000000000001;
			if (mB == 0) mB = 0.0000000000000001;
			if (mC == 0) mC = 0.0000000000000001;
			System.out.println(mA + " | " + mB + " | " + mC + " | ");
			double	midAx = (x0 + x1) / 2,
					midBx = (x1 + x2) / 2,
					midCx = (x0 + x2) / 2,		//midpoint x values 
					midAy = (y0 + y1) / 2, 
					midBy = (y1 + y2) / 2, 
					midCy = (y0 + y2) / 2;
			double	wA = -(1 / mA), 
					wB = -(1 / mB), 
					wC = -(1 / mC);				//set inverse slopes
			double	pA = midAy - (wA * midAx),
					pB = midBy - (wB * midBx),
					pC = midCy - (wC * midCx);	//set inverse y-intercepts
			obX = (int) ((midBy - midAy + (wA * midAx) - (wB * midBx)) / (wA - wB));
			System.out.println("x = " + obX);
			obY = (int) ((wC * obX) + midCy - (wC * midCx));
			System.out.println("y = " + obY);
		}
		else System.out.println("Vertex error inside of findCenter...");
		return new Point(obX, obY);
	}
}