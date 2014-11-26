/*
 *AI Phase 1
 *Author John Holl
 *Last Edited 22Nov2014
 *
 *Designed to decide if image is already in memory
 *Phase 1 builds structures for memory in prep for
 *phase 2 & 3
 *
 * Builds interface for user
 * 
 * Saves memory in readable format
 */
package john;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

//AI control
public class AI {
	private int sides;
	private HashMap<Integer, String> mem;
	private File file;
	
	AI(HashMap<Integer, String> mem, File file){
		this.mem = mem;
		this.file = file;
		newIO();
	}
	
	//generate new interface for shape
	//remove suppression warning when replacing scanner with IR
	@SuppressWarnings("resource")
	public void newIO(){
		//here replace scanner with number of sides from IR
		Scanner in = new Scanner(System.in);
		System.out.println("Input sides");
		this.sides = in.nextInt();
		//open IO for new shape
		IO resume = new IO(getMem(this.sides), this.sides, this.mem);
		resume.pack();
		resume.setTitle("Interface");
		resume.setSize(300,200);
		resume.setLocationRelativeTo(null);
		resume.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		resume.setVisible(true);		
	}

	//return name from memory
	public String getMem(int side) {
		if(this.mem.get(side) == null)
			return "UNKNOWN";
		else
			return this.mem.get(side);
	}

	//interface
	class IO extends JFrame{

		private static final long serialVersionUID = 1L;
			
		IO(String AIguess, final int imin, HashMap<Integer, String> hold){
			final JTextArea guess = new JTextArea(AIguess);
			//tempEmpty is my reference for the GUI, change from JTextArea to the Image from the IR
			final JTextArea tempEmpty = new JTextArea(Integer.toString(imin));
			final JRadioButton JRBc = new JRadioButton("Correct");
			final JRadioButton JRBic = new JRadioButton("Incorrect");
			final JTextField correction = new JTextField();

			final JButton JBExit = new JButton("Save & Exit");
			final JButton JBCont = new JButton("Continue");
			
			ButtonGroup BGCor = new ButtonGroup();
			BGCor.add(JRBc);
			BGCor.add(JRBic);
			
			if(AIguess.equals("UNKNOWN"))
				JRBic.setSelected(true);
			else
				JRBc.setSelected(true);

			JPanel JPData = new JPanel();
			JPData.setLayout(new GridLayout(4,1,5,5));
			JPData.add(guess);
			JPData.add(JRBc);
			JPData.add(JRBic);
			JPData.add(correction);
			
			JPanel JPButtons = new JPanel();
			JPButtons.setLayout(new GridLayout(1,2,5,5));
			JPButtons.add(JBCont);
			JPButtons.add(JBExit);
			
			add(JPData, BorderLayout.EAST);
			add(tempEmpty, BorderLayout.WEST);
			add(JPButtons, BorderLayout.SOUTH);
			
			//move to next shape
			JBCont.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					//update shape if incorrect
					if(JRBic.isSelected()) {
						try{
							update(correction.getText(), imin);
						}catch (noTextError e1){
							System.out.println(e1.getErrorMessage());
						}
					//next shape if correct	
					}
					dispose();
					newIO();
				}
			});
			
			//save and exit
			JBExit.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					if(JRBic.isSelected()) {
						try{
							update(correction.getText(), imin);
						}catch (noTextError nte){
							System.out.println(nte.getErrorMessage());
						}
					}
					writeFile(makeString());
					System.exit(0);
				}
			});
		}
		
		//change value or add value to hashmap
		void update(String input, int imin) throws noTextError {
			if (input.isEmpty())
				throw new noTextError();
			else
				mem.put(imin, input);
		}
	}
	//convert hashmap to string for saving as .txt
	public String makeString(){
		String temp = "Memory storage";
		for(Map.Entry<Integer, String> i: mem.entrySet()){
			temp += "\r\n" + i.getKey() + ";" + i.getValue();
		}
		return temp;
	}
	
	//save map to .txt file
	void writeFile(String content) {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
	    chooser.setSelectedFile(file);
	    
	    int retrival = chooser.showSaveDialog(null);
	    if (retrival == JFileChooser.APPROVE_OPTION) {
	        try {
	            Writer fw = new FileWriter(chooser.getSelectedFile());
	            fw.write(content);
	            fw.close();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}
}


//error if no text in box
class noTextError extends Exception{
	private static final long serialVersionUID = 1L;
	private String errorMessage = "No text in field";
	
	noTextError(){}
	
	String getErrorMessage(){
		return this.errorMessage;
	}
}

