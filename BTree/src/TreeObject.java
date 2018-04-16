/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * TreeObject is a class that implements Comparable to compare data within
 * a key.  The following information is required for a TreeObject:
 * 		- data stored in key (in binary)
 * 		- frequency for in-order traversal
 * 		 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------
import java.lang.Comparable;

public class TreeObject implements Comparable<TreeObject> {

	// -------------------Variables-------------------
	private long data;
	private int frequency;
	
	// -------------------Constructor-------------------
	public TreeObject(long data, int freq) {
		this.data = data;
		this.frequency = freq;
	}
	
	public TreeObject(long data) {
		this.data = data;
		this.frequency = 1;
	}
	
	// -------------------Mutators-------------------
	public long getData() {
		return data;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void incrementFrequency() {
		this.frequency++;
	}

	// -------------------Methods-------------------
	public int compareTo(TreeObject t) {
		// Greater returns one
		if(this.data > t.data) {
			return 1;
		}
		// Less returns -1
		else if(this.data < t.data) {
			return -1;
		}
		
		// Equal returns 0
		else {return 0;}
	}
}
