/*
 * ImageGenerator creates images with a background color and
 * randomly-sided, regular polygons of a different color.
 */
		
package imageGenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.*;
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
		height = 100;
		width = 100;
		igPanel = new IGPanel();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bgColor = getRandomColor(null);
		colorBackground(bgColor);
		shape = new IGShape(3, 3);
	}
	public ImageGenerator(int width, int height){
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
		shape = new IGShape(3, 20);
		Graphics2D g =  (Graphics2D) image.getGraphics();
		g.setPaint(shape.getShapeColor());
		g.draw(shape.getPolygon());
		g.fill(shape.getPolygon());
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
	
	/*
	 * This IGPanel subclass is a JPanel that contains the image and ports to other parts
	 * of the program such as the GUI. It is important to maintain the shape when the
	 * program repaints the graphic.
	 */
	private class IGPanel extends JPanel{
		//use the paint method to draw the image and polygon
		public void paint(Graphics g){
			Graphics2D g2D = (Graphics2D) g;
			g.drawImage(getImage(), 0, 0, this);
//			g2D.setPaint(shape.getShapeColor());
//			g2D.fill(shape.getPolygon());
//			g2D.draw(shape.getPolygon());	
		}
	}

	
	/*
	 * IGShape is a sbuclass that maintains the characteristics of a shape such as its number of sides.
	 * This will be useful during extensions to write that information to testing files to verify correct
	 * mapping of shapes. 
	 */
	private class IGShape{
		int sideNumber, minSides, maxSides, radius; //min/maxSides indicate the side limit parameters 
		Color shapeColor;
		float interiorAngle, sideLength;
		GeneralPath polygon;
		
		public IGShape(){
			this.minSides = 3;
			this.maxSides = 6;
			shapeColor = getRandomColor(bgColor);
			setSides();
			createPolygon();
		}
		public IGShape(int minSides, int maxSides){
			this.minSides = minSides;
			this.maxSides = maxSides;
			shapeColor = getRandomColor(bgColor);
			setSides();
			createPolygon();
		}
		//return shape color in order to paint it
		public Color getShapeColor(){
			return this.shapeColor;
		}
		//return the polygon instance to draw in other places
		public GeneralPath getPolygon(){
			return this.polygon;
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
		private void setSideLength(){
			radius = ((height < width) ? height : width) / 2 - 1; //find radius of image rectangle (-1 ensures gap from border)
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
			double theta = 0;
			//plot the points as if on a circle using the radius from the center of the image 
			for (int index = 0; index < sideNumber; index++){
				xPoints[index] = center[0] + (int) (radius * Math.cos(Math.toRadians(theta)));
				yPoints[index] = center[1] + (int) (radius * Math.sin(Math.toRadians(theta)));
				theta += 360 / sideNumber;
			}
			//code from: https://docs.oracle.com/javase/tutorial/2d/geometry/arbitrary.html
			polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
			polygon.moveTo(xPoints[0], yPoints[0]);
			//loop through points, drawing lines between each 
			for (int index = 1; index < xPoints.length; index++) {
		        polygon.lineTo(xPoints[index], yPoints[index]);
			}
			polygon.closePath(); //draw line from final point to first point
		}
	}
}