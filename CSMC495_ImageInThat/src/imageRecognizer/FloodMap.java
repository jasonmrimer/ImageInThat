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
		map();							//get all the potential vertices and place into pointList
		center = new Point(image.getWidth() / 2, image.getHeight() / 2);	//using the cheat method knowledge of IG making the cetner {width / 2, height / 2}
		radius = createRadius();
		vertexList = createVertices();	//get all vertices from center 
	}
	//find the farthest point from center - that distance = radius
	private double createRadius() {
		//check whether the center exists and there are vertices
		double createRadius = 0.0;
		if (center != null) {
			for(Point pt : pointList) {
				if (Math.sqrt(Math.pow(pt.getX() - center.getX(), 2) + Math.pow(pt.getY() - center.getY(), 2)) > createRadius){
					createRadius = Math.sqrt(Math.pow(pt.getX() - center.getX(), 2) + Math.pow(pt.getY() - center.getY(), 2));
				}
			}
			return Math.sqrt(Math.pow(tempVertexList.get(0).getX() - center.getX(), 2) + Math.pow(tempVertexList.get(0).getY() - center.getY(), 2));
		}
		else
			return (Double) null;
	}
	/*
	 * map taking advantage of the two-dimension comvex shapes by approaching in a line from the left
	 * then approaching from the right to capture all the points outlining the shape and skipping the 
	 * inside of the shape
	 * 
	 */
	private void map(){
		/*
		 * get border of polygon by sending the first pixels with changing color to an arraylist 
		 */
		//top to bottom, left to right
		for (int y = 0; y < height; y++){				//rows by height value *This is the Y-Coordinate*
			for (int x = 0; x < width; x++){			//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB){		//different from bg = shape
					pointList.add(new Point(x, y));		//add point to the list as a potential vertex
					break;								//found boundary, move to next row
				}
			}
		}
		//top to bottom, right to left
		for (int y = height - 1; y > -1; y--){			//rows by height value *This is the Y-Coordinate*
			for (int x = width - 1; x > -1; x--){		//columns by width *This is the X-Coordinate*
				if (image.getRGB(x, y) != bgRGB) {		//different than background = shape
					pointList.add(new Point(x, y)); 	//add point to the list as a potential vertex
					break;								//found boundary, move to next row
				}
			}
		}
	}
	
	public int getSideNumber(){
		return vertexList.size();
	}
	/*
	 * using the radial theory, iterate through all points placing the points farthest from the center in the 
	 * vertex list
	 */
	private ArrayList<Point> createVertices(){
		ArrayList<Point> createVertexList = new ArrayList<Point>();
		double  distance = 0.0;
		HashMap<Point, Double> distanceHash = new HashMap<Point, Double>();
		//maintain all the distances from center to iterate through after
		for (Point pt : pointList){
			distance = Math.sqrt(Math.pow(pt.getX() - center.getX(), 2) + Math.pow(pt.getY() - center.getY(), 2));
			distanceHash.put(pt, distance);
		}
		//iterate through distance to find radii and vertices
		for (Map.Entry<Point, Double> entry : distanceHash.entrySet()){
			if (entry.getValue() >= 0.9995 * radius) {	//percentage allows for fluctuation in precision
				createVertexList.add(entry.getKey());
			}
		}
		return createVertexList;
	}
}