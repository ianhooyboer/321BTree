
/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * BTreeTest class tests functionality
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

import java.io.File;

public class BTreeTest {

	public static void main(String[] args) {
		String filename = "myTest.txt";
		File file = new File(filename);
		int degree = 5;
		boolean useCache = false;
		int cacheSize = 10000;
		int sequenceLength = 2;
		int numElementsToAdd = 1;
		
		BTree myBTree = new BTree(degree, file, useCache, cacheSize, sequenceLength);
		BTreeCache cache = new BTreeCache(cacheSize);
		DNAParser testParser = new DNAParser(sequenceLength);

		try {
			
			/**
			 * The SubSequenceGenerator object generates a list of phony 'atcg' elements
			 * 
			 * The for loop adds them all to the test BTree
			 * 
			 * to test, change the value of numElementsToAdd.
			 * 
			 * additionally, the third Boolean argument of the SubSequenceGenerator locks it into the same seeded sequence of randoms.
			 * 
			 * 
			 * 3 May 18 - >= 26 numElementsToAdd intermittent, stack overflow at 29 --Eric
			 */
			numElementsToAdd = 35; // for testing, modify these number
			
			System.out.println("|Degree = " + degree + "|\t|Min # of children = " + degree + "|\t|Max # of children = " + (2 * degree) +
					"|\t|Max # of keys = " + (2 * degree - 1) + "|\t|Min number of keys = " + (degree - 1) + "|\t|Height = " 
					+ (int) (Math.log(2 * degree + 1) / Math.log(2 * degree)) + "|");
			
			SubSequenceGenerator ssG = new SubSequenceGenerator(numElementsToAdd, sequenceLength, true);
						
			for (String s : ssG.getSSs()) {
				long l = testParser.convertToKey(s);
				myBTree.insert(l);
			}

			
			myBTree.dumpTree(); //dumps out final btree to file
			System.out.println(myBTree.toString());
			
			// Cache testing
			System.out.print("Cache Hits: " + cache.getHits() + "\n");
			System.out.print("Cache Misses: " + cache.getMisses() + "\n");
			System.out.print("Cache Ratio: " + cache.getHitRatio());
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
