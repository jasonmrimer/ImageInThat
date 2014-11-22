package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

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
		JFrame frame = new JFrame();
		frame.getContentPane().add(new Main());
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void paint(Graphics g){
		Graphics2D g2D = (Graphics2D) g;
		ImageGenerator ig = new ImageGenerator(400, 400);
		GeneralPath polygon = new GeneralPath();
		g.drawImage(ig.getImage(), 0, 0, this);
		g2D.setPaint(Color.black);
		g2D.fill(ig.getPolygon());
		g2D.draw(ig.getPolygon());
	}
	
	
}
