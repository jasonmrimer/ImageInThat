/*
 * ImageGenerator creates images with a background color and
 * randomly-sided, regular polygons of a different color.
 */
		
package imageGenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class ImageGenerator {
	BufferedImage image;
	int height, width;
	Color bgColor, shapeColor;
	GeneralPath polygon;
	IGShape shape;
	IGPanel igPanel;
	//constructors
	public ImageGenerator(){
		this(100, 100, 3, 5);
	}
	public ImageGenerator(int width, int height){
		this(width, height, 3, 5);
	}
	public ImageGenerator(int width, int height, int minSides, int maxSide){
		//set variables
		this.width = width;
		this.height = height;
		//create image
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		igPanel = new IGPanel();
		//colors
		bgColor = getRandomColor(null);
		colorBackground(bgColor);
		//draw the shape into the image
		shape = new IGShape(minSides, maxSide);
		Graphics2D g =  (Graphics2D) image.getGraphics();
		g.setPaint(shape.getShapeColor());
		g.draw(shape.getPolygon());
//		g.fill(shape.getPolygon());
		shape.testArea();
	}
	@Override
	public String toString(){
		return "This ImageGenerator object has height = " + height +
				"width = " + width + ".";
		
	}
	//color the background a given color by iterating through each pixel
	private void colorBackground(Color bgColor){
		//color background
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				this.image.setRGB(w, h, bgColor.getRGB());
			}
		}
	}
	//use this method to calculate random colors either new or different from an input
	private Color getRandomColor(Color notEqualTo){
		Color color;
		int inRed, outRed, inGreen, outGreen, inBlue, outBlue;
		Random random = new Random();
		//new random color, not compared to any pre-existing color
		if (notEqualTo == null) {
			outRed = random.nextInt(255 - 0 + 1);
			outGreen = random.nextInt(255 - 0 + 1);
			outBlue = random.nextInt(255 - 0 + 1);
			color = new Color(outRed, outGreen, outBlue);
		}
		//random color different than input color
		else {
			//since notEqualTo exists, extract color values
			inRed = notEqualTo.getRed();
			inGreen = notEqualTo.getGreen();
			inBlue = notEqualTo.getBlue();
			//"flip a coin" to decide whether the value will be randomly less than the input or greater
			outRed = (random.nextInt(2) < 1) ? random.nextInt(inRed - 0 + 1) : random.nextInt(255 - inRed + 1);
			outGreen = (random.nextInt(2) < 1) ? random.nextInt(inGreen - 0 + 1) : random.nextInt(255 - inGreen + 1);
			outBlue = (random.nextInt(2) < 1) ? random.nextInt(inBlue - 0 + 1) : random.nextInt(255 - inBlue + 1);
			color = new Color(outRed, outGreen, outBlue);
		}
		return color;
	}
	
	//return the image to place, resize, etc. 
	public BufferedImage getImage(){
		return image;
	}
	//return the panel to other draw methods such as GUI
	public IGPanel getIGPanel(){
		return igPanel;
	}
	//return the IGShape (1 for now)
	public IGShape getIGShape(){
		return this.shape;
	}
	
	/*
	 * This IGPanel subclass is a JPanel that contains the image and ports to other parts
	 * of the program such as the GUI. It is important to maintain the shape when the
	 * program repaints the graphic.
	 */
	private class IGPanel extends JPanel{
		//use the paint method to draw the image and polygon
		public void paint(Graphics g){
			g.drawImage(getImage(), 0, 0, this);
		}
	}
	/*
	 * IGShape is a sbuclass that maintains the characteristics of a shape such as its number of sides.
	 * This will be useful during extensions to write that information to testing files to verify correct
	 * mapping of shapes. 
	 */
	public class IGShape{
		ArrayList<Point> vertices;
		int sideNumber, minSides, maxSides, radius; //min/maxSides indicate the side limit parameters 
		Color shapeColor;
		float interiorAngle, sideLength;
		GeneralPath polygon;
		
		public IGShape(){
			vertices = new ArrayList<Point>();
			this.minSides = 3;
			this.maxSides = 6;
			shapeColor = getRandomColor(bgColor);
			setSides();
			createPolygon();
		}
		public IGShape(int minSides, int maxSides){
			vertices = new ArrayList<Point>();
			this.minSides = minSides;
			this.maxSides = maxSides;
			shapeColor = getRandomColor(bgColor);
			setSides();
			createPolygon();
		}
		//return number of sides
		public int getNumberOfSides(){
			return vertices.size();
		}
		//return shape color in order to paint it
		public Color getShapeColor(){
			return this.shapeColor;
		}
		//return the polygon instance to draw in other places
		public GeneralPath getPolygon(){
			return this.polygon;
		}
		//return vertex collection
		public ArrayList<Point> getVertices(){
			return this.vertices;
		}
		private void setSides(){
			setSideNumber();
			setSideLength();
		}
		private void setSideNumber(){
			//calculate number of sides
			Random random = new Random();
			sideNumber = random.nextInt(maxSides - minSides + 1) + minSides;
			interiorAngle = 360 / sideNumber;
		}
		//this is the sidelength to use later; the radius is more important as IG uses it to place vertices
		private void setSideLength(){
			radius = ((height < width) ? (int) (height * 0.95) : (int) (width * 0.95)) / 2 - 1; //find radius of image rectangle (95% and -1 ensures gap from border)
			sideLength = (float) (radius / (2 * Math.cos(90 - (180 / sideNumber))));
		}
		/*
		 * By taking using a number of sides and an image rectangle, createPolygon
		 * will create the points/sides of a polygon by using the center of the image
		 * as the center of a circle then placing the vertices of a polygon at a determined
		 * radius from the center to create a regular polygon.
		 */
		private void createPolygon() {
			int xPoints[] = new int[sideNumber];
			int yPoints[] = new int[sideNumber];
			int center[] = {(width / 2), (height / 2)};
			double theta = 1;
			//plot the points as if on a circle using the radius from the center of the image 
			for (int index = 0; index < sideNumber; index++){
				vertices.add(new Point(center[0] + (int) (radius * Math.cos(Math.toRadians(theta))),
						center[1] + (int) (radius * Math.sin(Math.toRadians(theta)))));
				theta += (double) 360 / sideNumber;
			}
			//code from: https://docs.oracle.com/javase/tutorial/2d/geometry/arbitrary.html
			polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
			polygon.moveTo(vertices.get(0).getX(), vertices.get(0).getY());
			//loop through points, drawing lines between each 
			for (Point vertex : vertices){
				polygon.lineTo(vertex.getX(), vertex.getY());
			}
			polygon.closePath(); //draw line from final point to first point
		}
		//test the area method
		private void testArea(){
//			int[] xPoints = new int[pointList.size()];
//			int[] yPoints = new int[pointList.size()];
//			Polygon polygon = new Polygon();
//			for (int point = 0; point < pointList.size(); point++){
//				polygon.addPoint((int) pointList.get(point).getX(), (int) pointList.get(point).getY());
//
//				xPoints[point] = (int) pointList.get(point).getX();
//				yPoints[point] = (int) pointList.get(point).getY();
//			}
			Area area = new Area(polygon); // The value is set elsewhere in the code    
			ArrayList<double[]> areaPoints = new ArrayList<double[]>();
			ArrayList<Line2D.Double> areaSegments = new ArrayList<Line2D.Double>();
			double[] coords = new double[6];
		
			for (PathIterator pi = area.getPathIterator(null); !pi.isDone(); pi.next()) {
			    // The type will be SEG_LINETO, SEG_MOVETO, or SEG_CLOSE
			    // Because the Area is composed of straight lines
			    int type = pi.currentSegment(coords);
			    // We record a double array of {segment type, x coord, y coord}
			    double[] pathIteratorCoords = {type, coords[0], coords[1]};
			    areaPoints.add(pathIteratorCoords);
			}
		
			double[] start = new double[3]; // To record where each polygon starts
		
			for (int i = 0; i < areaPoints.size(); i++) {
			    // If we're not on the last point, return a line from this point to the next
			    double[] currentElement = areaPoints.get(i);
		
			    // We need a default value in case we've reached the end of the ArrayList
			    double[] nextElement = {-1, -1, -1};
			    if (i < areaPoints.size() - 1) {
			        nextElement = areaPoints.get(i + 1);
			    }
		
			    // Make the lines
			    if (currentElement[0] == PathIterator.SEG_MOVETO) {
			        start = currentElement; // Record where the polygon started to close it later
			    } 
		
			    if (nextElement[0] == PathIterator.SEG_LINETO) {
			        areaSegments.add(
			                new Line2D.Double(
			                    currentElement[1], currentElement[2],
			                    nextElement[1], nextElement[2]
			                )
			            );
			    } else if (nextElement[0] == PathIterator.SEG_CLOSE) {
			        areaSegments.add(
			                new Line2D.Double(
			                    currentElement[1], currentElement[2],
			                    start[1], start[2]
			                )
			            );
			    }
			}
		}
	}
}