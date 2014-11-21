package imageGenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.GeneralPath;
import java.awt.image.*;

public class ImageGenerator extends Component {
	BufferedImage image;
	int height, width;
	//constructors
	public ImageGenerator(){
		image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		image.setRGB(1, 1, new Color(100, 0, 0).getRGB());
		image.getGraphics().drawImage(image, 0, 0, null);
	}
	public ImageGenerator(int width, int height){
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				image.setRGB(w, h, new Color(100, 0, 0).getRGB());
			}
		}
//				image.getGraphics().drawImage(image, 0, 0, null);
	}
	public BufferedImage getImage(){
		return image;
	}
	
	//code from: https://docs.oracle.com/javase/tutorial/2d/geometry/arbitrary.html
	public void drawPolygon(){
		//path
		GeneralPath side = new GeneralPath();
		
		//vertices kept as {x, y}
		int triVert1[] = {0, height};
		int triVert2[] = {width, height};
		int triVert3[] = {width / 2, 0};
		
		// draw GeneralPath (polygon)
		int x1Points[] = {0, width, width / 2};
		int y1Points[] = {height, height, 0};
		GeneralPath polygon = 
		        new GeneralPath(GeneralPath.WIND_EVEN_ODD,
		                        x1Points.length);
		polygon.moveTo(x1Points[0], y1Points[0]);

		for (int index = 1; index < x1Points.length; index++) {
		        polygon.lineTo(x1Points[index], y1Points[index]);
		};

		polygon.closePath();
		image.getGraphics().drawPolygon(x1Points, y1Points, 21);
//		image.getGraphics().drawPolygon
	}
}
