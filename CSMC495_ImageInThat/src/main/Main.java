
/*
 * Image In That
 * John Holl & Jason Rimer
 * This program sorts through images that contain basic
 * geometric shapes. As it maps those shapes, it receives input
 * from users about those shapes and memorizes the input. It saves
 * its knowledge and continues to learn more about shapes and recognize
 * the shapes it knows. 
 */

package main;
import java.awt.Point;

import javax.swing.JFrame;

import john.*;
import imageGenerator.ImageGenerator;
import imageRecognizer.ImageRecognizer;

public class Main { //extends JPanel {
	public static void main(String arg[]){
		//John's side
		//Open memory load
//		Driver driver = new Driver();
//		ig.getIGShape().getSideNmber()();	//John, use this to get the number of sides 
//		ig.getIGPanel();	//John, use this to get the panel and put in your GUI
		

		
		//Jason's side
//		//frame
//		JFrame frame = new JFrame();
//		ImageGenerator ig = new ImageGenerator(800, 800, 5, 5);
//		frame.getContentPane().add(ig.getIGPanel());
//		frame.setSize(800, 800);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
//		ImageRecognizer ir = new ImageRecognizer(ig.getImage());
		//test side values and accuracy
		for (int sides = 7; sides < 8; sides++){
			ImageGenerator ig = new ImageGenerator(800, 800, sides, sides);
			ImageRecognizer ir = new ImageRecognizer(ig.getImage());
			System.out.println("Sides created in IG:	" + ig.getIGShape().getNumberOfSides());
			System.out.println("Sides mapped in IR:	" + ir.getNumberOfSidesMapped());
//			for (Point pt : ig.getIGShape().getVertices()){
//				System.out.println(pt);
//			}
		}
		System.exit(0);
	}
}
