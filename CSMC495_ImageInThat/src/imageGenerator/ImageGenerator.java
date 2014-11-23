/*
 * ImageGenerator creates images with a background color and
 * randomly-sided, regular polygons of a different color.
 */
		
package imageGenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.image.*;
import java.util.Random;

public class ImageGenerator {
	BufferedImage image;
	int height, width;
	GeneralPath polygon;
	IGShape shape;
	//constructors
	public ImageGenerator(){
		height = 100;
		width = 100;
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
	public BufferedImage getImage(){
		return image;
	}
	
	public GeneralPath getPolygon(){
		return this.polygon;
	}
	
	private class IGShape{
		int sideNumber, minSides, maxSides, radius;
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