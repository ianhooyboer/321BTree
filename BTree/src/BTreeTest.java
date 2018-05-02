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

		BTree myBTree = new BTree(degree, file, useCache, cacheSize); // tests constructor
		
		BTreeCache cache = new BTreeCache(cacheSize);

		try {
			
			insertToEmptyTreeTest(myBTree, 3); // test inserting a key into BTree
			insertToTreeTest(myBTree, 1);
			insertToTreeIncrementTest(myBTree, 1);
			keySearchTest(myBTree, 3);
			
			myBTree.insert(4);		
			
			myBTree.insert(6);
			System.out.println(myBTree.toString());	
			
			// Cache testing
			System.out.print("Cache Hits: " + cache.getHits() + "\n");
			System.out.print("Cache Misses: " + cache.getMisses()+ "\n");
			System.out.print("Cache Ratio: " + cache.getHitRatio());
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Unit test of insert(), to be called immediately after creating a new BTree
	 * 
	 * @param tree the tree to insert into
	 * @param testVal the value to insert
	 * @return true if the test succeeds, false otherwise
	 * @throws IOException 
	 */
	static boolean insertToEmptyTreeTest(BTree tree, int testVal) throws IOException {
		TreeObject insertEmptyExpectedVal = new TreeObject(testVal);
		
		tree.insert(testVal);
		
		if (tree.keySearch(tree.getRoot(), testVal).compareTo(insertEmptyExpectedVal) == 0 &&
				tree.getRoot().getNumKeys() == 1) {
			System.out.println("insertToEmptyTreeTest: PASS");
			return true;
		}else if (tree.getRoot().getNumKeys() == 0) {
			System.out.println("insertToEmptyTreeTest: FAIL - no key inserted");
			return false;
		}else{
			System.out.println("insertToEmptyTreeTest: FAIL - unexpected result");
			return false;
		}
	}
	
	/**
	 * Unit test of insert(), to be called following insertToEmptyTreeTest()
	 * 
	 * @param tree the tree to insert into
	 * @param testVal the value to insert
	 * @return true if the test succeeds, false otherwise
	 * @throws IOException
	 */
	static boolean insertToTreeTest(BTree tree, int testVal) throws IOException {
		TreeObject insertExpectedVal = new TreeObject(testVal);
		
		tree.insert(testVal);
		
		if (tree.keySearch(tree.getRoot(), testVal).compareTo(insertExpectedVal) == 0 &&
				tree.getRoot().getNumKeys() == 2 &&
				tree.getRoot().getKeys().get(0).compareTo(tree.getRoot().getKeys().get(1)) == -1) {
			System.out.println("insertToTreeTest: PASS");
			return true;
		}else if (tree.getRoot().getNumKeys() < 2) {
			System.out.println("insertToTreeTest: FAIL - key not inserted");
			return false;
		}else if (tree.getRoot().getKeys().get(0).compareTo(tree.getRoot().getKeys().get(1)) == 1) {
			System.out.println("insertToTreeTest: FAIL - keys not sorted");
			return false;
		}else {
			System.out.println("insertToTreeTest: FAIL - unexpected result");
			return false;
		}
	}
	
	/**
	 * Unit test of insert(), to be called following insertToTreeTest()
	 * 
	 * @param tree the tree to insert into
	 * @param testVal the value to insert
	 * @return true if the test succeeds, false otherwise
	 * @throws IOException
	 */
	static boolean insertToTreeIncrementTest(BTree tree, int testVal) throws IOException {
		TreeObject insertIncrementExpectedVal = new TreeObject(testVal);
		
		tree.insert(testVal);
		
		if (tree.keySearch(tree.getRoot(), testVal).compareTo(insertIncrementExpectedVal) == 0 &&
				tree.getRoot().getNumKeys() == 2 &&
				tree.getRoot().getKeys().get(0).getFrequency() == 2 ||
				tree.getRoot().getKeys().get(1).getFrequency() == 2) {
			System.out.println("insertToTreeIncrementTest: PASS");
			return true;
		}else if (tree.getRoot().getNumKeys() < 2) {
			System.out.println("insertToTreeIncrementTest: FAIL - one key in node");
			return false;
		}else if (tree.getRoot().getNumKeys() == 3 &&
				tree.getRoot().getKeys().get(0).getFrequency() == 1 &&
				tree.getRoot().getKeys().get(1).getFrequency() == 1) {
			System.out.println("insertToTreeIncrementTest: FAIL - key frequency not incremented, duplicate node added");
			return false;
		}else {
			System.out.println("insertToTreeIncrementTest: FAIL - unexpected result");
			return false;
		}
	}
	
	/**
	 * Unit test of keySearchTest
	 * 
	 */
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

}
