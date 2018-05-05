
// -------------------Libraries-------------------
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * GenerBankSearch driver class searches the B-Tree for sequences of a provided
 * length.
 * 
 * Usage is as follows: java GeneBankSearch <0/1(no/with Cache)> <btree file>
 * <query file> <cache size> [<debug level>]
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

public class GeneBankSearch {

	// -------------------Variables-------------------
	private static boolean useCache;
	private static File BTreeFile; // file to read BTree from (generated from GeneBankCreateBTree)
	private static File QueryFile; // file with all possible sequences of specified length (provided)
	private static int cacheSize;
	private static int debugLevel = 0;
	
	private static BTree treeFromFile;

	public static void main(String[] args) {

		if (args.length == 4 || args.length == 5) {
			try {
				useCache = (Integer.parseInt(args[0]) == 1) ? true : false;
				BTreeFile = new File(args[1]);
				QueryFile = new File(args[2]);

				if (useCache == false) {
					debugLevel = (Integer.parseInt(args[3]) == 1) ? 1 : 0;
				} else {
					cacheSize = Integer.parseInt(args[3]);
					debugLevel = (Integer.parseInt(args[4]) == 1) ? 1 : 0;
				}

			} catch (NumberFormatException e) {
				exitWithUsage();
			} catch (NullPointerException e) {
				System.err.println("BTree File or Query file not found.");
				exitWithUsage();
			}

		} else
			exitWithUsage();
		
		
		
		
		
		treeFromFile = new BTree(BTreeFile);	
		BTreeNode root = treeFromFile.getRoot();
		
		testArgs();
		
		
		
		DNAParser parser = new DNAParser(treeFromFile.getSequenceLength());
		try {
			Scanner queryScan = new Scanner(QueryFile);
			
			while (queryScan.hasNextLine()) {
				String next = queryScan.nextLine();
				
//				System.out.println("Searching for " + next + "(" + parser.convertToKey(next) + ")");
				
				if (root.getKeys().isEmpty()) {
					System.err.println("Root has no keys");
					System.out.println(treeFromFile);
					System.exit(-1);					
				}else {
					long findMe = parser.convertToKey(next);
					TreeObject found = treeFromFile.keySearch(root, findMe);
					if (found != null) {
						System.out.println("Found " + found.getFrequency() + " occurences of key: " + found.getData());
					}else {
						System.out.println("Key: " + findMe + " was not found");
					}
					
				}				
			}
			
			queryScan.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("File: " + QueryFile.getName() + " not found.\n");
			System.exit(-1);
		}
		
		

		// get degree and subsequence length from BTree file
		// - write degree as first int in file or first item in node = degree

		// subsequence length - check length of first subsequence. subLength should be
		// same
		// in meta file, query file

	}

	
	/**
	 * Prints out current args, for testing
	 */
	private static void testArgs() {
		String buf = "";
		buf += (useCache == true) ? "Using cache " : "Not using cache";
		buf += (useCache == true) ? "of size " + cacheSize : "";
		buf += " for a tree of degree of " + treeFromFile.getDegree() + ". ";
		buf += "Reading from a file: " + BTreeFile.getName() + ",\n";
		buf += "generating sequences of size: " + treeFromFile.getSequenceLength() + ", ";
		buf += (debugLevel == -1) ? " with no debug level." : "with debug level: " + debugLevel;

		System.out.println(buf);
	}
	
	private static void exitWithUsage() {
		System.err.println("Usage is as follows:");
		System.err.println(
				"java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> <cache size> [<debug level>]");
		System.exit(-1);
	}

	// -------------------Notes-------------------
	// Create class
	// constructor (theoretically) working
	// get degree and subsequence length from BTree file
	// - write degree as first int in file or first item in node = degree
	// subsequence length - check length of first subsequence. subLength should be
	// same
	// in meta file, query file

	// Always keep root node in memory

	// Read subSequence File and append to linked-list of strings

	/*
	 * For-each subsequence if(BTree.find(subSequence) {print freq and data;}
	 */

}