package imageRecognizer;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;



public class FloodMap {
	//attempt iteration instead of recursion by going line-by line
	int width, height, bgRGB, shapeRGB;
	BufferedImage image;
	ArrayList<Point> pointList;
	
	public FloodMap(BufferedImage image){
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.bgRGB = image.getRGB(0, 0); //assume top left corner is background since shapes cannot reach
		pointList = new ArrayList<Point>();
		map();
	}
	
	/*
	 * map taking advantage of the two-dimension comvex shapes by approaching in a line from the left
	 * then approaching from the right to capture all the points outlining the shape and skipping the 
	 * inside of the shape
	 */
	private void map(){
		//top to bottom, left to right
		for (int row = 0; row < height; row++){		//rows by height value
			for (int col = 0; col < width; col++){	//columns by width
				if (image.getRGB(row, col) != bgRGB){
					//send the point somewhere
					pointList.add(new Point(row, col));
					break;
				}
			}
		}
		//top to bottom, right to left
		for (int row = 0; row < height; row++){			//rows by height value
			for (int col = width - 1; col > -1; col--){	//columns by width
				if (image.getRGB(row, col) != bgRGB){
					//send the point somewhere
					pointList.add(new Point(row, col));
					break;
				}
			}
		}
	}
	
	public ArrayList<Point> getPointList(){
		return this.pointList;
	}

}