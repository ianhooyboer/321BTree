
// -------------------Libraries-------------------
import java.io.File;
import java.io.IOException;

/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * GenerBankCreateBTree driver class creates a BTree from a specified .gbk file.
 * The following conversion methods are in the DNAParser class: - Converting a
 * key to a string (from binary to a string character) - Converting a string
 * character to a key value (string to binary)
 * 
 * Usage is as follows: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> <cache size> [<debug level>]
 * 														0				 1			 2				3 			4 			5
 *
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

public class GeneBankCreateBTree {
	// -------------------Variables-------------------
	private static boolean useCache;
	private static int degree;
	private static File gbkFile;
	private static int sequenceLength;
	private static int cacheSize;
	private static int debugLevel = -1;

	private static File fileOut;

	public static void main(String[] args) {
		// Process Arguments (we will assume that cache is implemented for now)
		if (args.length == 5 || args.length == 6) {
			try {
				useCache = (Integer.parseInt(args[0]) == 1) ? true : false;
				degree = Integer.parseInt(args[1]);
				gbkFile = new File(args[2]);
				sequenceLength = Integer.parseInt(args[3]);

				if (useCache == false) {
					debugLevel = (Integer.parseInt(args[4]) == 1) ? 1 : 0;
				} else {
					cacheSize = Integer.parseInt(args[4]);
				}
				
				if (args.length == 6) {
					debugLevel = (Integer.parseInt(args[5]) == 1) ? 1 : 0;
				}

			} catch (NumberFormatException e) {
				exitWithUsage();
			} catch (NullPointerException e) {
				System.err.println(".gbk file not found.");
				exitWithUsage();
			}
		} else {
			exitWithUsage();
		}

		testArgs();
		
//		If the name of the GeneBank file is xyz.gbk, the sequence length is k, the BTree degree
//		is t, then the name of the btree file should be: xyz.gbk.btree.data.k.t
		String fileName = "test1.gbk.btree.data.31.4";
		fileOut = new File(fileName);
		
		// Create parser(filename, subSequenceLength)
		DNAParser myParser = new DNAParser(gbkFile, sequenceLength);
		BTree myBTree = new BTree(degree, fileOut, useCache, cacheSize);
		
		for (String s : myParser.getSSs()) { // insert all SSs contained in the parser into the BTree
			try {
				long d = myParser.convertToKey(s);
				System.out.println(d + "\t" + myParser.longToSubSequence(d, 12));
				myBTree.insert(d);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		}
		
		//TODO write this BTree to a file to be used by GeneBankSearch
		
		System.out.println(myBTree); //demonstrating wonky BTree behavior at time of writing

	}

	/**
	 * Prints out current args, for testing
	 */
	private static void testArgs() {
		String buf = "";
		buf += (useCache == true) ? "Using cache " : "Not using cache ";
		buf += (useCache == true) ? "of size " + cacheSize : "";
		buf += " for a tree of degree of " + degree + ". ";
		buf += "Reading from a file: " + gbkFile.getName() + ",\n";
		buf += "generating sequences of size: " + sequenceLength + ", ";
		buf += (debugLevel == -1) ? " with no debug level." : "with debug level: " + debugLevel;
		
		System.out.println(buf);
	}
	
	/**
	 * Prints usage instructions, and exits with an unsuccessful exit code.
	 */
	private static void exitWithUsage() {
		System.err.println("Usage is as follows:");
		System.err.println("java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> <cache size> [<debug level>]");
		System.exit(-1);

	}
}
