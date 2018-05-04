/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * DNAParser parses subsequences from gbk files
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class DNAParser {

	private File gbkfile;
	private int k; // maximum value is 31
	private Scanner scan;
	private Set<Character> actg = new HashSet<Character>();
	private ArrayList<String> SSs = new ArrayList<String>();
	private int ssCounter;

	private static final long BIN_A = 0b00;
	private static final long BIN_C = 0b01;
	private static final long BIN_G = 0b10;
	private static final long BIN_T = 0b11;

	public DNAParser(File gbkfile, int k) {
		this.gbkfile = gbkfile;
		this.k = k;
		initACTG(actg);

		try {
			this.scan = new Scanner(gbkfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("\nFile: " + gbkfile.getName() + " not found.");
			System.exit(-1);
		}
		this.populateSSs();
		this.ssCounter = 0;
	}
	
	public DNAParser(int sequenceLength) {
		this.k = sequenceLength;
	}

	/**
	 *  
	 * @return a reference to gbkfile
	 */
	@SuppressWarnings("unused")
	private File getGbkFile() {
		return gbkfile;
	}

	/**
	 * Helper method to populate a set with acceptable characters.
	 * 
	 * @param s - Set to use for comparison
	 */
	private void initACTG(Set<Character> s) {
		s.add('a');
		s.add('c');
		s.add('t');
		s.add('g');
	}

	/**
	 * Helper method that uses Queues to tokenize file input and store ArrayList of subsequences
	 * 
	 */
	private void populateSSs() {
		String dummy = "";

		while (!dummy.equals("ORIGIN      ")) { // skip header first
			dummy = scan.nextLine();
		}

		ArrayDeque<Character> myQ = new ArrayDeque<Character>();
		ArrayDeque<Character> dumpQ = new ArrayDeque<Character>();

		while (scan.hasNextLine()) {
			dummy = scan.nextLine();
			dummy = dummy.replaceAll("", " ");
			StringTokenizer tok = new StringTokenizer(dummy);

			while (tok.hasMoreTokens()) {
				char token = tok.nextToken().charAt(0);

				if (!actg.contains(token)) {
					// do nothing, toss the token
				} else if (token == 'N') {
					myQ.clear();
				} else if (myQ.size() < k) {
					myQ.add(token);
				} else if (myQ.size() == k) {
					dumpQ = myQ.clone();
					String buf = "";
					for (int i = 0; i < k; i++) {
						buf += dumpQ.remove();
					}
					SSs.add(buf);
					myQ.remove();
					myQ.add(token);
				}
			}
		}
	}

	/**
	 * returns next subsequence from DNAParser's stored list of sequences.
	 * 
	 * @return the next subseq if the parser has more, otherwise returns -1
	 */
	public long nextSubSeq() {
		long subSeq = -1;
		
		if (ssCounter < k) {
			String data = SSs.get(ssCounter);
			subSeq = convertToKey(data);
			ssCounter++;
		}
		return subSeq;
	}

	/**
	 * Converts string to binary key
	 * @param string to convert
	 * @return converted key
	 */
	public long convertToKey(String data) {
		long key = 0;
		long tbits = 0b00;

		for (int i = 0; i < k; i++) {
			switch (data.charAt(i)) {
			case 'a':
				tbits = BIN_A;
				break;
			case 'c':
				tbits = BIN_C;
				break;
			case 't':
				tbits = BIN_T;
				break;
			case 'g':
				tbits = BIN_G;
				break;
			}
			long posval = tbits << (2 * i);
			key = key | posval;
		}
		return key;
	}

	/**
	 * Converts binary key into string
	 * @param key - long value to convert
	 * @param k - length of sequence
	 * @return converted string
	 */
	public String longToSubSequence(long key) {
		String res = "";

		for (int i = 0; i < k; i++) {
			long tmp = key;
			tmp = tmp >> (2 * i);
			tmp = tmp & 0x03;

			int tmp2 = (int) tmp;
			char gene = 'Y'; //some value

			switch (tmp2) {
			case 0:
				gene = 'a';
				break;
			case 1:
				gene = 'c';
				break;
			case 3:
				gene = 't';
				break;
			case 2:
				gene = 'g';
				break;
			}
			res += gene;
		}
		return res;
	}
	
	public ArrayList<String> getSSs() {
		return SSs;
	}
	
	public void dumpParser() {
		File dumpFile = new File("parserDump.txt");

		try {
			if (dumpFile.exists())
				dumpFile.delete(); // clear file if exists
			dumpFile.createNewFile(); // create new file
			RandomAccessFile randomAF = new RandomAccessFile(dumpFile, "rw"); // create new random access file set for both read and
																// write
			// randomAF.seek(0);

			String toWrite = "";
			
			for (String s : this.getSSs()) {
				toWrite += s + "\n";
			}
			
			
			randomAF.writeBytes(toWrite);
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("File: " + dumpFile.getName() + " not found.\n");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}
}
