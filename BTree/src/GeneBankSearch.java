/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * GenerBankSearch driver class searches the B-Tree for sequences of a provided length.
 * The following conversion methods are in the search class:
 * 		- Converting a key to a string (from binary to a string character)
 * 		- Converting a string character to a key value (string to binary)
 * 
 * Usage is as follows:
 * java GeneBankSearch <btree file> <query file> [<debug level>]
 * 
 * With cache option:
 * java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> cache size> [<debug level>]
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------

public class GeneBankSearch {
	// -------------------Variables-------------------
	private String degree;
	private int filename;
	
	// Create class
	BTree myBTree = new BTree(filename, degree);  // get degree and subsequence length from BTree file 
							  // - write degree as first int in file or first item in node = degree
							  // subsequence length - check length of first subsequence.  subLength should be same
							  // in meta file, query file
	
	// Always keep root node in memory
	
	// Read subSequence File and append to linked-list of strings
	
	/*  For-each subsequence
	 *  if(BTree.find(subSequence) {print freq and data;}
	*/
	
	
	
	// -------------------Methods-------------------
	//convert to key written in DNAParser
	
	//long to subsequence written in DNAParser
}
