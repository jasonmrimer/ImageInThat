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
	GeneralPath polygon;
	IGShape shape;
	IGPanel igPanel;
	//constructors
	public ImageGenerator(){
		height = 100;
		width = 100;
		igPanel = new IGPanel();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		colorBackground();
		shape = new IGShape(3, 3);
		createPolygon();
	}
	public ImageGenerator(int width, int height){
		this.width = width;
		this.height = height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		colorBackground();
		shape = new IGShape(3, 20);
		igPanel = new IGPanel();
		createPolygon();
	}
	@Override
	public String toString(){
		return "This ImageGenerator object has height = " + height +
				"width = " + width + ".";
		
	}
	private void colorBackground(){
		//color background
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				this.image.setRGB(w, h, new Color(100, 0, 0).getRGB());
			}
		}
	}
	/*
	 * By taking using a number of sides and an image rectangle, createPolygon
	 * will create the points/sides of a polygon by using the center of the image
	 * as the center of a circle then placing the vertices of a polygon at a determined
	 * radius from the center to create a regular polygon.
	 */
	private void createPolygon() {
		int xPoints[] = new int[shape.sideNumber];
		int yPoints[] = new int[shape.sideNumber];
		int center[] = {(width / 2), (height / 2)};
		double theta = 0;
		//plot the points as if on a circle using the radius from the center of the image 
		for (int index = 0; index < shape.sideNumber; index++){
			xPoints[index] = center[0] + (int) (shape.radius * Math.cos(Math.toRadians(theta)));
			yPoints[index] = center[1] + (int) (shape.radius * Math.sin(Math.toRadians(theta)));
			theta += 360 / shape.sideNumber;
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
	//return the image to place, resize, etc. 
	public BufferedImage getImage(){
		return image;
	}
	//return the panel to other draw methods such as GUI
	public IGPanel getIGPanel(){
		return igPanel;
	}
	//return the polygon instance to draw in other places
	public GeneralPath getPolygon(){
		return this.polygon;
	}
	/*
	 * This IGPanel subclass is a JPanel that contains the image and ports to other parts
	 * of the program such as the GUI. It is important to maintain the shape when the
	 * program repaints the graphic.
	 */
	private class IGPanel extends JPanel{
		
		
		public void paint(Graphics g){
			Graphics2D g2D = (Graphics2D) g;
			g.drawImage(getImage(), 0, 0, this);
			g2D.setPaint(Color.black);
			g2D.fill(getPolygon());
			g2D.draw(getPolygon());	
		}
	}

	/*
	 * IGShape is a sbuclass that maintains the characteristics of a shape such as its number of sides.
	 * This will be useful during extensions to write that information to testing files to verify correct
	 * mapping of shapes. 
	 */
	private class IGShape{
		int sideNumber, minSides, maxSides, radius; //min/maxSides indicate the side limit parameters 
		float interiorAngle, sideLength;
		public IGShape(){
			this.minSides = 3;
			this.maxSides = 6;
			setSides();
		}
		public IGShape(int minSides, int maxSides){
			this.minSides = minSides;
			this.maxSides = maxSides;
			setSides();
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
	}
}