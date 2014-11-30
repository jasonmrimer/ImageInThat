/*
 * Shape phase 2
 * Author John Holl
 * Last Edited 30Nov2014
 * 
 * Stores values for shapes in memory by side number
 * 
 * I have include structures for future algorithms, the main variables being sides.
 * In the current phase that variable is superfluous but I don't want to change the structures later.
 */
package phase1;

import java.util.*;

public class Shape {
	//stores values in memory for the shape
	private HashMap<String, ArrayList<Dimensions>> known = new HashMap<String, ArrayList<Dimensions>>();
	private int angles;
	//constructor
	Shape(int angles){this.angles = angles;}
	
	//generates string
	@Override
	public String toString(){
		String a = "";
		for(Map.Entry<String, ArrayList<Dimensions>> i: known.entrySet()){
			a += "\r\nn;" + i.getKey() ;
			java.util.Iterator<Dimensions> j = i.getValue().iterator();
			while (j.hasNext()){
				a += "\r\n" + j.next().toString();
			}
		}
		return a;
	}
	
	//add shape with 1 dimension in the array
	void addShape(String name, Dimensions temp){
		ArrayList<Dimensions> hold = new ArrayList<Dimensions>();
		hold.add(temp);
		known.put(name, hold);
	}

	//add a new set of dimensions to the shape
	void addDimension(String name, Dimensions temp){
		known.get(name).add(temp);
	}
	
	//determines if the name being called is in memory
	boolean hasName(String a){
		return known.containsKey(a);
	}
	
	//algorithm to determine most probable shape from memory. Starts from unknown, generates a number which correlates to how far
	//angles in memory must be changed to match the image image being 'viewed'. Later versions will also include side proportion 
	//differentials
	String mostProbable(Dimensions temp){
		String name = "UNKNOWN";
		double num = (angles - 1) * 90;
		
		for(Map.Entry<String, ArrayList<Dimensions>> i: known.entrySet()){
			java.util.Iterator<Dimensions> j = i.getValue().iterator();
			while (j.hasNext()){
				Dimensions hold =  j.next();
				double holdDist = leastChange(hold.getAngle(), temp.getAngle());
				System.out.println(holdDist + i.getKey());
				if( holdDist < num){
					name = i.getKey();
					num = holdDist;
				}
			}
		}
		return name;
	}
	
	//returns a value for the least number of changes an array in memory must make. Accounts for rotated images. 
	//later versions may include mirror images, I am still trying to write a mirror algorithm.
	double leastChange(double[] a, double[] b){
		double i = change(a,b);
		int j = 1;
		double[] temp = rotate(a);
		do{
			if(i > change(temp, b))
				i = change(temp, b);
			temp = rotate(temp);
			j++;
		}while(j < a.length - 1);
		return i;
	}
	
	//algorithm to rotate a shape counter clockwise by 1.
	double[] rotate(double[] a){
		double[] temp = new double[a.length];
		for (int i = 1; i < a.length; i++){
			temp[i-1] = a[i];
		}
		temp[a.length - 1] = a[0];
		return temp;
	}
	
	//returns a value for how different each parameter, angle or side proportion, is between 2 shapes.
	double change(double[] a, double[] b){
		double ret = 0;
		for(int i = 0; i < a.length; i++){
			ret += Math.abs(a[i] - b[i]);
		}		
		return ret;
	}
	
}

//stores values for a dimension. 
class Dimensions{
	
	private double[] angle;
	private double[] side;
	
	//constructor
	Dimensions(double side[],double[] angle){
		this.angle = angle;
		this.side = side;
	}
	
	//getters
	double[] getAngle(){
		return this.angle;
	}
	
	double[] getSide(){
		return this.side;
	}
	
	//toString, formatted for output file.
	@Override
	public String toString(){
		String returnable = "d;";
		for(int i = 0; i < angle.length; i++){
			returnable += angle[i];
			if(i != angle.length-1)
				returnable += ",";
		}
		returnable += ";";
		for(int i = 0; i < side.length; i++){
			returnable += side[i];
			if(i != side.length-1)
				returnable += ",";
		}
		return returnable;
	}
}
