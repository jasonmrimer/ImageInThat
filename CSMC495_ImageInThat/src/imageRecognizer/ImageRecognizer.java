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

public class ImageRecognizer {
	BufferedImage image;
	Color bgColor, shapeColor, newBGColor;
	boolean mark[][];
	
	public ImageRecognizer(BufferedImage image){
		this.image = image;
		bgColor = new Color(image.getRGB(0, 0)); //assume the top left pixel is the background since the shape never reaches the corner
		mark = new boolean[image.getWidth()][image.getHeight()];
		newBGColor = Color.black;
		flood(image, mark, 0, 0, bgColor, newBGColor);
	}
	
	/*
	 * change from fill to map
	 */
	//initial flood fill from: http://www.cis.upenn.edu/~cis110/13sp/hw/hw09/FloodFill.java
	private static void flood(BufferedImage img, boolean[][] mark, int row, int col, Color srcColor, Color tgtColor) {
		// make sure row and col are inside the image
		if (row < 0) return;
		else if (col < 0) return;
		else if (row >= img.getHeight()) return;
		else if (col >= img.getWidth()) return;
		
		// make sure this pixel hasn't been visited yet
		else if (mark[row][col]) return;
		
		// make sure this pixel is the right color to fill
		else if (img.getRGB(col, row) != srcColor.getRGB()) return;

		/*
		 * if changing from bg to shapeColor
		 * 	check sides
		 * if already shapeColor
		 * 	do not move to another shapeColor because that enters the inside of the shape, we can skip
		 * 	how to avoid discounting shape edges that actually touch? it may not matter because there will always be touching pixels in the lines
		 * 		but should focus on the aggregate lines slope rather than immediate neighbors
		 */
		
		else{
		
			// fill pixel with target color and mark it as visited
			img.setRGB(col, row, tgtColor.getRGB());
			mark[row][col] = true;
			
			// recursively fill surrounding pixels
			// (this is equivalent to depth-first search)
			flood(img, mark, row - 1, col, srcColor, tgtColor);
			flood(img, mark, row + 1, col, srcColor, tgtColor);
			flood(img, mark, row, col - 1, srcColor, tgtColor);
			flood(img, mark, row, col + 1, srcColor, tgtColor);
		}
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
