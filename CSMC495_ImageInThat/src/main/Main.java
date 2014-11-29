
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
		
		ImageGenerator ig = new ImageGenerator(300, 300);
		ImageRecognizer ir = new ImageRecognizer(ig.getImage());

//		System.exit(0);
		//Jason's side
		//frame
		JFrame frame = new JFrame();
		frame.getContentPane().add(ig.getIGPanel());
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
