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
	
	// Create class
	BTree(filename, degree);  // get degree and subsequence length from BTree file 
							  // - write degree as first int in file or first item in node = degree
							  // subsequence length - check length of first subsequence.  subLength should be same
							  // in meta file, query file
	
	// Always keep root node in memory
	
	// Read subSequence File and append to linked-list of strings
	
	/*  For-each subsequence
	 *  if(BTree.find(subSequence) {print freq and data;}
	*/
	
	
	
	// -------------------Methods-------------------
	
	/**
	 * Converts string to binary key
	 * @param string to convert
	 * @return converted key
	 */
	public long convertToKey(String subSequence) {
		long key = 0x00;
		int tBits, posVal;
		
		for(int i = 0; i < subSequence.length(); i++) {
			// Convet char to 0, 1, 2, 0r 3
			switch(subSequence(i)) {
			case 'a':
				tBits = 0x00;
			case 'c': 
				tBits = 0x01;
			case 'g':
				tBits = 0x02;
			case 't':
				tBits = 0x03;
			}
			
			// tBits has value for current gene
			
			// Left shift to add to key
			posVal = tBits << (2 * i);
			
			// combines tBits into key
			key = key | posVal;
		}
		return key;
	}
	
	/**
	 * Converts binary key into string
	 * @param sequence to convert
	 * @param seqLength - length of sequence
	 * @return converted string
	 */
	public String convertToString(long key, int subSeqLength) {
		String subSequence = "";
		
		char[] geneMap = ['a', 'c', 'g', 't'];
		String gene = "";
		
		long temp;
		
		for(int i = 0; i < subSeqLength; i++) {
			temp = key;
			
			// Right shift to lsb
			temp = temp >> (2 * i);
		
			// Mask 2 lsbs
			temp = temp & 0x03;
			
			gene = geneMap[temp];
			
			subSequence += gene;
		}
		
		return subSequence;
	}
}
