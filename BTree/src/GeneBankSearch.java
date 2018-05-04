
// -------------------Libraries-------------------
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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

	private class MetaData {

		public int degree;
		public int sequenceLength;

		public MetaData(File BTreeFile) {
			try {
				RandomAccessFile randomAF = new RandomAccessFile(BTreeFile, "r");

				this.degree = randomAF.readInt();
				this.sequenceLength = randomAF.readInt();
			} catch (FileNotFoundException e) {
				e.printStackTrace(System.err);
				System.err.println("File: " + BTreeFile.getName() + " not found.\n");
				System.exit(-1);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}

		}

	}

	// -------------------Variables-------------------
	private static boolean useCache;
	private static File BTreeFile; // file to read BTree from (generated from GeneBankCreateBTree)
	private static File QueryFile; // file with all possible sequences of specified length (provided)
	private static int cacheSize;
	private static int debugLevel = 0;

	private static int degree, sequence;
	private File filename;

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

		// get degree and subsequence length from BTree file
		// - write degree as first int in file or first item in node = degree

		// subsequence length - check length of first subsequence. subLength should be
		// same
		// in meta file, query file

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