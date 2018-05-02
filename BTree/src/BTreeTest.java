
/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * BTreeTest class tests functionality
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

import java.io.File;
import java.io.IOException;

public class BTreeTest {

	public static void main(String[] args) {
		String filename = "myTest.txt";
		File file = new File(filename);
		int degree = 2;
		boolean useCache = true;
		int cacheSize = 500;
		int sequenceLength = 4;
		int numElementsToAdd = 1;

		BTree myBTree = new BTree(degree, file, useCache, cacheSize);
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
			 * 
			 * At the time of writing, the BTree breaks down about 9-12 elements.   
			 * 		-Ian
			 */
			
			numElementsToAdd = 8; // for testing, modify this number
			SubSequenceGenerator ssG = new SubSequenceGenerator(numElementsToAdd, sequenceLength);
						
			for (String s : ssG.getSSs()) {
//				System.out.println(s); // to see subsequences before they are added
				long l = testParser.convertToKey(s);
				myBTree.insert(l);
			}

			System.out.println(myBTree.toString()); //prints out final btree

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
	
	
	

	/* These need to be redone
	
	static boolean insertToEmptyTreeTest(BTree tree, int testVal) throws IOException {
		TreeObject insertEmptyExpectedVal = new TreeObject(testVal);

		tree.insert(testVal);

		if (tree.keySearch(tree.getRoot(), testVal).compareTo(insertEmptyExpectedVal) == 0
				&& tree.getRoot().getNumKeys() == 1) {
			System.out.println("insertToEmptyTreeTest: PASS");
			return true;
		} else if (tree.getRoot().getNumKeys() == 0) {
			System.out.println("insertToEmptyTreeTest: FAIL - no key inserted");
			return false;
		} else {
			System.out.println("insertToEmptyTreeTest: FAIL - unexpected result");
			return false;
		}
	}

	static boolean insertToTreeTest(BTree tree, int testVal) throws IOException {
		TreeObject insertExpectedVal = new TreeObject(testVal);

		tree.insert(testVal);

		if (tree.keySearch(tree.getRoot(), testVal).compareTo(insertExpectedVal) == 0
				&& tree.getRoot().getNumKeys() == 2
				&& tree.getRoot().getKeys().get(0).compareTo(tree.getRoot().getKeys().get(1)) == -1) {
			System.out.println("insertToTreeTest: PASS");
			return true;
		} else if (tree.getRoot().getNumKeys() < 2) {
			System.out.println("insertToTreeTest: FAIL - key not inserted");
			return false;
		} else if (tree.getRoot().getKeys().get(0).compareTo(tree.getRoot().getKeys().get(1)) == 1) {
			System.out.println("insertToTreeTest: FAIL - keys not sorted");
			return false;
		} else {
			System.out.println("insertToTreeTest: FAIL - unexpected result");
			return false;
		}
	}

	static boolean insertToTreeIncrementTest(BTree tree, int testVal) throws IOException {
		TreeObject insertIncrementExpectedVal = new TreeObject(testVal);

		tree.insert(testVal);

		if (tree.keySearch(tree.getRoot(), testVal).compareTo(insertIncrementExpectedVal) == 0
				&& tree.getRoot().getNumKeys() == 2 && tree.getRoot().getKeys().get(0).getFrequency() == 2
				|| tree.getRoot().getKeys().get(1).getFrequency() == 2) {
			System.out.println("insertToTreeIncrementTest: PASS");
			return true;
		} else if (tree.getRoot().getNumKeys() < 2) {
			System.out.println("insertToTreeIncrementTest: FAIL - one key in node");
			return false;
		} else if (tree.getRoot().getNumKeys() == 3 && tree.getRoot().getKeys().get(0).getFrequency() == 1
				&& tree.getRoot().getKeys().get(1).getFrequency() == 1) {
			System.out.println("insertToTreeIncrementTest: FAIL - key frequency not incremented, duplicate node added");
			return false;
		} else {
			System.out.println("insertToTreeIncrementTest: FAIL - unexpected result");
			return false;
		}
	}

	static boolean keySearchTest(BTree tree, long testVal) {
		long kstExpectedVal = testVal;
		TreeObject keySearchTestNode = tree.keySearch(tree.getRoot(), kstExpectedVal);

		if (keySearchTestNode != null) {
			if (keySearchTestNode.getData() == kstExpectedVal) {
				System.out.println("keySearchTest: PASS");
				return true;
			} else {
				System.out.println("keySearchTest: FAIL - returned unexpected value");
				return false;
			}
		} else {
			System.out.println("keySearchTest: FAIL - returned null");
			return false;
		}
	}
	
	*/
}
