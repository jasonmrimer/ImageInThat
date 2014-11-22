package main;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import imageGenerator.ImageGenerator;

public class Main extends JPanel {
/*
 * This is just the master for now, without any functionality.
 * We can each branch off the master to start our code and push those
 * branches into the master when we want.
 */
	static JPanel panel;
	
	public static void main(String arg[]){
		ImageGenerator ig = new ImageGenerator(100, 100);
		JFrame frame = new JFrame();
		frame.getContentPane().add(new Main());
		frame.setSize(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		ig.getImage().getGraphics().drawImage(ig.getImage(), 1, 1, null, frame);
		ig.repaint();
	}
	
	public void paint(Graphics g){
		ImageGenerator ig = new ImageGenerator(100, 100);
		g.drawImage(ig.getImage(), 0, 0, this);
	}
	
	
}
