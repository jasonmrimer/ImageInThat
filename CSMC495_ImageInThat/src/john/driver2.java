/*
 * Driver phase 2
 * Author John Holl
 * Last Edited 30Nov2014
 * 
 * Has the user determine which memory to load
 * 
 * If new memory, generates new hashmap to build memory
 * 
 * If old memory, reads and builds hashmap for logic center
 */
package phase1;

import java.io.*;
import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class driver {
		
	public static void main(String[] args) {
		//Open memory load 
		selecter resume = new selecter();
		resume.pack();
		resume.setTitle("Memory Select");
		resume.setLocationRelativeTo(null);
		resume.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		resume.setVisible(true);
		
		
	}
	//new memory or load memory
	static class selecter extends JFrame{

		private static final long serialVersionUID = 1L;

		selecter(){
			final JButton jbNew = new JButton("New Memory");
			final JButton jbOpen = new JButton("Open Memory");
			add(jbNew, BorderLayout.EAST);
			add(jbOpen, BorderLayout.WEST);
			
			jbNew.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					dispose();
					HashMap<Integer, Shape> temp = new HashMap<Integer, Shape>();
					new AI(temp, null);
				}
			});
			
			jbOpen.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					dispose();
					new GUIUpload();
				}
			});
		}
	}
	
	static class GUIUpload extends JFrame{
		//load memory from .txt file
		private static final long serialVersionUID = 1L;
		private HashMap<Integer, Shape> temp = new HashMap<Integer, Shape>();

		public GUIUpload(){
			JFileChooser fileChooser = new JFileChooser();
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				uploadFile(selectedFile);
				System.out.println(this.temp);
				dispose();
				new AI(temp, selectedFile);
			}
		}
		
		//upload file method
		public  void uploadFile(File file){
			//upload file
			try {
				Scanner scan = new Scanner(new FileInputStream(file));
				scan.nextLine();
				int sideNum = 0;
				String name = "";
				//for now I am using this as a file reader, I am not convinced it is the most efficient way to read the file 
				//but it is good enough for this phase. in phase 3 I will tinker with this section to make it more efficient.
				while (scan.hasNext()) {
					String tempString = scan.nextLine();
					String[] holdValue = tempString.split(";");
					//s; in file generates new sized shapes in memory (size in number of sides)
					if(holdValue[0].equals("s")){
						sideNum = Integer.parseInt(holdValue[1]);
						Shape hold = new Shape(sideNum);
						this.temp.put(sideNum, hold);
					//n; is new name of shape
					}else if(holdValue[0].equals("n")){
						name = holdValue[1];
						String tempString2 = scan.nextLine();
						String[] holdValue2 = tempString2.split(";");
						double[] angle = makeDimensions(holdValue2[1]);
						double[] sideLength = makeDimensions(holdValue2[2]);
						Dimensions hold = new Dimensions(sideLength, angle);
						this.temp.get(sideNum).addShape(name, hold);
					//d; is saved dimensions for shape of previously pulled name
					}else if(holdValue[0].equals("d")){
						double[] angle = makeDimensions(holdValue[1]);
						double[] sideLength = makeDimensions(holdValue[2]);
						Dimensions hold = new Dimensions(sideLength, angle);
						this.temp.get(sideNum).addDimension(name, hold);
					}
				}
				scan.close();
				//catch exceptions
			} catch (FileNotFoundException fnfe) {
	            fnfe.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("\nProgram terminated Safely...");
	        }  

		}

		//generate arrays to pass to new dimensions in file reader
		double[] makeDimensions(String in) {
			String[] temp = in.split(",");
			double[] tempDim = new double[temp.length];
			for(int i = 0; i < temp.length; i++){
				tempDim[i] = Double.parseDouble(temp[i]);
			}
			return tempDim;
		}
	}
}

