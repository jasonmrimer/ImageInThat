/*
 * ImageRecognizer reads an image from ImageGenerator then
 * crawls each pixel to map shapes in the image
 */
package imageRecognizer;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImageRecognizer {
	//variables
	BufferedImage image;
	JPanel panel;
	Color bgColor, shapeColor, newBGColor;
	FloodMap fm;
	//constructor
	public ImageRecognizer(BufferedImage image){
		this.image = image;
		bgColor = new Color(image.getRGB(0, 0)); //assume the top left pixel is the background since the shape never reaches the corner
		fm = new FloodMap(image);
	}	
	//return number of sides mapped in floodmap
	public int getNumberOfSidesMapped(){
		return fm.getSideNumber();
	}
}
