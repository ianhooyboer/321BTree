/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * GeneBankCreateBTree driver class creates a BTree from a given GeneBank file.  This requires parsing
 * data from a .gbk file and finding gene sequence values with the following binary conversion:
 * 		- A = 0b00
 * 		- C = 0b01
 * 		- G = 0b10
 * 		- T = 0b11
 * 		
 * The maximum length of sequence (when using 2-bit conversion into binary) is 31 ((64 bits / 2) - 1).
 * 
 * Usage is as follows:
 * java GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]
 * 
 * With cache option:
 * java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> cache size> [<debug level>]
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------

public class GeneBankCreateBTree {
	
	// -------------------Variables-------------------
	private static final long BIN_A = 0b00;
	private static final long BIN_C = 0b01;
	private static final long BIN_G = 0b10;
	private static final long BIN_T = 0b11;
	
	private static final int MAX_SEQUENCE = 31;
	
	public static void main(String[] args) {
		
		// Process Arguments
		
		// Create parser(filename, subSequenceLength)
		
		// Create BTree(degree, filename)
	}
	
	//IN-CLASS
	public DNAParser() {
		BTree b = new BTree(3, filename);
		
		while(key = getNextSubSequence) {
			b.insert(key);
		}
		//Close file
	}
}
