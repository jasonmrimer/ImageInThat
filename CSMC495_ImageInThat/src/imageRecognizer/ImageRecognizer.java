/*
 * ImageRecognizer reads an image from ImageGenerator then
 * crawls each pixel to map shapes in the image
 */
package imageRecognizer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImageRecognizer {
	BufferedImage image;
	JPanel panel;
	Color bgColor, shapeColor, newBGColor;
	boolean mark[][];
	int maxStackSize = 500; // will be increased as needed
	int[] xstack = new int[maxStackSize];
	int[] ystack = new int[maxStackSize];
	int stackSize;
	int max;
	boolean isFloat;
	
	public ImageRecognizer(BufferedImage image){
		this.image = image;
		bgColor = new Color(image.getRGB(0, 0)); //assume the top left pixel is the background since the shape never reaches the corner
		mark = new boolean[image.getWidth()][image.getHeight()];
		newBGColor = Color.black;
//		flood(image, mark, 0, 0, bgColor, newBGColor);
		FloodMap fm = new FloodMap(image);
		
	}	
	
	
	
	/*
	 * IRShape will contain the information relating to the shape being mapped
	 * by IR. It will have an dynamic number of sides and will hold all the information will
	 * IR maps.
	 */
	private class IRShape{
		ArrayList<Line2D> sides;
		int sideNumber, radius; //min/maxSides indicate the side limit parameters 
		Color shapeColor;
		float interiorAngle, sideLength;
		IRShape(){
			sides = new ArrayList<Line2D>();
		}
	}
}
