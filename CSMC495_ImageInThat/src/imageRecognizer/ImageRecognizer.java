/*
 * ImageRecognizer reads an image from ImageGenerator then
 * crawls each pixel to map shapes in the image
 */
package imageRecognizer;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageRecognizer {
	/*
	 * change from fill to map
	 */
	//initial flood fill from: http://www.cis.upenn.edu/~cis110/13sp/hw/hw09/FloodFill.java
	 private static void flood(BufferedImage img, boolean[][] mark, int row, int col, Color srcColor, Color tgtColor) {
		// make sure row and col are inside the image
		if (row < 0) return;
		if (col < 0) return;
		if (row >= img.getHeight()) return;
		if (col >= img.getWidth()) return;
		
		// make sure this pixel hasn't been visited yet
		if (mark[row][col]) return;
		
		// make sure this pixel is the right color to fill
		if (img.getRGB(col, row) != srcColor.getRGB()) return;
		
		// fill pixel with target color and mark it as visited
//		img.set(col, row, tgtColor);
		mark[row][col] = true;
		
		// recursively fill surrounding pixels
		// (this is equivelant to depth-first search)
		flood(img, mark, row - 1, col, srcColor, tgtColor);
		flood(img, mark, row + 1, col, srcColor, tgtColor);
		flood(img, mark, row, col - 1, srcColor, tgtColor);
		flood(img, mark, row, col + 1, srcColor, tgtColor);
		}
}
