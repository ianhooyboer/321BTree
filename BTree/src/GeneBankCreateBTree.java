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
 * Usage is as follows: java GeneBankCreateBTree <0/1(no/with Cache)> <degree>
 * <gbk file> <sequence length> <cache size> [<debug level>]
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
		
		String fileOutName = gbkFile.getName();
		fileOutName += ".btree.data.";
		fileOutName += sequenceLength + ".";
		fileOutName += degree;
		fileOut = new File(fileOutName);

		DNAParser myParser = new DNAParser(gbkFile, sequenceLength);
		BTree myBTree = new BTree(degree, fileOut, useCache, cacheSize, sequenceLength);

		for (String s : myParser.getSSs()) {
			try {
				long d = myParser.convertToKey(s);
				myBTree.insert(myBTree, d);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		}

		if (debugLevel == 1) {
			myBTree.dumpTree();
		} else {
			testArgs();
			System.err.println("\nWriting BTree file: " + fileOut.getName());
		}
	}

	/**
	 * Prints out current args, for testing
	 */
	private static void testArgs() {
		String buf = "";
		buf += (useCache == true) ? "Using cache " : "Not using cache";
		buf += (useCache == true) ? "of size " + cacheSize : "";
		buf += " for a tree of degree of " + degree + ". ";
		buf += "Reading from a file: " + gbkFile.getName() + ",\n";
		buf += "generating sequences of size: " + sequenceLength + ", ";
		buf += (debugLevel == -1) ? " with no debug level." : "with debug level: " + debugLevel;

		System.err.println(buf);
	}

	/**
	 * Prints usage instructions, and exits with an unsuccessful exit code.
	 */
	private static void exitWithUsage() {
		System.err.println("Usage is as follows:");
		System.err.println(
				"java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> <cache size> [<debug level>]");
		System.exit(-1);

	}
}
