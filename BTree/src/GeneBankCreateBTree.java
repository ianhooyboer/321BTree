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
 * java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> <cache size> [<debug level>]
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------
import java.io.File;

// Functions
public class GeneBankCreateBTree {
	
	// -------------------Variables-------------------
	private static final long BIN_A = 0b00;
	private static final long BIN_C = 0b01;
	private static final long BIN_G = 0b10;
	private static final long BIN_T = 0b11;
	
	private static final int MAX_SEQUENCE = 31;
	
	public static void main(String[] args) {
		
	// Process Arguments (we will assume that cache is implemented for now)
	boolean useCache = false;
	
	int cache = Integer.parseInt(args[1]);
	int degree = Integer.parseInt(args[2]);
	File filename = new File(args[3]);
	int subSequenceLength = Integer.parseInt(args[4]);
	int cacheSize = Integer.parseInt(args[5]);
	int debugLevel = Integer.parseInt(args[6]);
		
	// Create parser(filename, subSequenceLength)
	DNAParser myParser = new DNAParser(filename, subSequenceLength);
	
	if(cache == 0) useCache = false;
	else if(cache == 1) useCache = true;
	
	// Create BTree(degree, filename, useCache, cacheSize)
	BTree myBTree = new BTree(degree, filename, useCache, cacheSize);
	}
	
	// TODO display usage
	// TODO input error protection
}
