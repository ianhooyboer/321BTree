/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * The BTree class is the B-Tree data structure for this project.  A B-Tree has the 
 * following:
 * 		- A degree
 * 		- A root
 * 		- A memory block size (node size)
 * 		- An offset from the root (start of memory block)
 * 		- A file to read from
 * 		- RandomAccessFile functionality (seek)
 * 
 * Methods in BTree:
 * 		- search for key
 * 		- insert key
 * 		- insert nonfull
 * 		- disk Read
 * 		- disk Write
 * 		- split tree
 * 		- readNode
 * 		- writeNode
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayDeque;

public class BTree {

	// -------------------Variables-------------------
	private final int KEY_SIZE = 4;
	
	private int degree;
	private BTreeNode root;
	private int offsetFromRoot;
	private RandomAccessFile randomAF;
	private int nodeSize;
	private BTreeCache cache;
	private int blockInsert;
	private int sequenceLength;
	
	/**
	 * Standard constructor for BTree object
	 * 
	 * @param degree
	 *            	- degree to be used during construction of BTree
	 * @param filename
	 *            	- filename to be read from and written to
	 * @param useCache
	 * 				- boolean to determine if cache is used
	 * @param cacheSize
	 * 				- size of cache used
	 * @param sequenceLength
	 * 				- size of sequences stored in this BTree
	 */
	public BTree(int degree, File file, boolean useCache, int cacheSize, int sequenceLength) {
		// block size = # of keys * size of keys + file offset of children * size of keys
		nodeSize = (KEY_SIZE * (2 * degree - 1));
		offsetFromRoot =  (KEY_SIZE * (2 * degree));
		blockInsert = nodeSize + offsetFromRoot;
		this.degree = degree;
		this.sequenceLength = sequenceLength;
		
		// Cache option
		if(useCache) 
			cache = new BTreeCache(cacheSize);
		
		// set up new node in BTree
		BTreeNode newNode = new BTreeNode();
		newNode.setOffset(offsetFromRoot);
		
		// Add newNode to BTree, is leaf, with 0 keys
		newNode.setLeafStatus(true);
		newNode.setNumKeys(0);
		
		root = newNode; // newNode is the first root
		
		try {
			if(file.exists()) file.delete();    			// clear file if exists
			file.createNewFile();  							// create new file
			randomAF = new RandomAccessFile(file, "rw");	// create new random access file set for both read and write
		} catch(FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("File: " + file.getName() + " not found.\n");
			System.exit(-1);
		} catch(IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		writeMetadata();
	}
	
	/**
	 * constructor to read in BTree from file
	 */
	public BTree(File BTreeFile) {
		try {
			this.randomAF = new RandomAccessFile(BTreeFile, "r");
			
			readMetadata();
			
			offsetFromRoot =  (KEY_SIZE * (2 * degree));			
			root = readNode(offsetFromRoot);
						
			
		} catch(FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("File: " + BTreeFile.getName() + " not found.\n");
			System.exit(-1);
		}
	}

	/**
	 * Searches BTree for a Key.
	 * 
	 * Reference: Cormen, T. H., et al. 2009. Introduction to Algorithms (3rd
	 * edition). 492. MIT Press and McGraw-Hill. ISBN 0-262-03384-4.
	 * 
	 * @param x
	 *            - node within the BTree
	 * @param k
	 *            - key within the node
	 * @return keySearch
	 */
	public TreeObject keySearch(BTreeNode x, long k) {
		int i = 0;
		
		TreeObject key = new TreeObject(k);

		while (i <= x.getNumKeys() && key.compareTo(x.getKey(i)) > 0) {
			i++;
		}

		if (i <= x.getNumKeys() && key.compareTo(x.getKey(i)) == 0) {
			return x.getKey(i);
		}
		if (x.isLeaf()) {
			return null;
		} else {
			return keySearch(readNode(x.getChild(i)), k);
		}
	}

	/**
	 * Inserts key into BTree.
	 * 
	 * Reference: Cormen, T. H., et al. 2009. Introduction to Algorithms (3rd
	 * edition). 495. MIT Press and McGraw-Hill. ISBN 0-262-03384-4.
	 * 
	 * @param key
	 *            - key within the node
	 * @throws IOException
	 */
	public void insert(long k) throws IOException {// TODO not tested
		BTreeNode r = root;
		int keyCount = r.getNumKeys();
	
		if (keyCount == (2 * degree - 1)) {
			TreeObject key = new TreeObject(k);
			// Conditions
			// while keyCount > 0 and root has no room for keys, decrement # of keys in root
			while(keyCount > 0 && key.compareTo(r.getKey(keyCount - 1)) < 0) keyCount --;
			
			// if keyCount > 0 and root accepts key, increase frequency of keys in root
			if(keyCount > 0 && key.compareTo(r.getKey(keyCount - 1)) == 0) r.getKey(keyCount - 1).incrementFrequency();
			
			else {
				BTreeNode s = new BTreeNode();
				s.setOffset(r.getOffset());
				root = s;
				
				// set offset of root
				r.setOffset(nodeSize);

				r.setParent(s.getOffset());
				
				// set inserted node and add child to root offset
				s.setLeafStatus(false);
				s.childAddToRear(r.getOffset());
				
				// split tree and insert key to non full node
				splitTree(s, 0, r);
				
				insertNF(s, k);
			}
		} else insertNF(r, k);  // else insert key at non full root
	}

	/**
	 * Splits BTree child node.
	 * 
	 * References: 
	 * 
	 * Cormen, T. H., et al. 2009. Introduction to Algorithms (3rd
	 * edition). 494. MIT Press and McGraw-Hill. ISBN 0-262-03384-4.
	 * 
	 * Kaltenbrunner, A., et al.  B-trees.  14.  
	 * Retrieved from www.di.ufpb.br > lucidio > Btrees
	 * 
	 * @param x
	 *            	- node being split
	 * @param i
	 *            	- where to split
	 * @param y		
	 *				- x's ith child node 
	 */
	public void splitTree(BTreeNode x, int i, BTreeNode y) throws IOException { // TODO not tested
		BTreeNode z = new BTreeNode();				// New child of x
	
		z.setLeafStatus(y.isLeaf());
		z.setParent(y.getParent());
		
		// from 0 to min # of keys
		for(int j = 0; j < degree - 1; j++) {
			z.addKeyToRear(y.removeKey(degree));	// Remove key from y and add to z
			
			// Update # of keys in y and z
			z.setNumKeys(z.getNumKeys() + 1);
			y.setNumKeys(y.getNumKeys() - 1);
		}
		
		// if y (x's ith child) is not a leaf
		if(!y.isLeaf()) {
			// remove a child from y and add to z from 0 to min # of children
			for(int j = 0; j < degree; j++) {
				z.childAddToRear(y.removeChild(degree));
			}
		}
		// Add key to x from ith child
		x.addKeyAtNode(i, y.removeKey(degree - 1));
		
		//Update # keys in x and y
		x.setNumKeys(x.getNumKeys() + 1);
		y.setNumKeys(y.getNumKeys() - 1);
		
		// If x is not root and has one key
		if(x == root && x.getNumKeys() == 1) {
			// write ith child node in new block
			writeNode(y, blockInsert);							
			blockInsert += nodeSize;	
			
			//System.out.println(i + " " + blockInsert + "\n");
			
			// set offset of new child to new block
			z.setOffset(blockInsert);			
			// add ith + 1 child to x from z offset
			x.childAddAtIndex(z.getOffset(), i + 1);		
			// write new child in new block
			writeNode(z, blockInsert);								
			
			// write parent node to offset from root
			writeNode(x, offsetFromRoot);						
			blockInsert += nodeSize;
		} else {				
			// write ith child of x at y offset
			writeNode(y, y.getOffset());	
			// set new child offset
			z.setOffset(blockInsert);								
			
			// write new child in new block
			writeNode(z, blockInsert);							
			x.childAddAtIndex(z.getOffset(), i + 1);				
			
			// write parent node at offset
			writeNode(x, x.getOffset());	
			// add a node memory size to block
			blockInsert += nodeSize;								
			
			//System.out.println(i + " " + blockInsert + "\n");
		}
	}

	/**
	 * Inserts key into tree rooted at the nonfull root node
	 * 
	 * Reference: Cormen, T. H., et al. 2009. Introduction to Algorithms (3rd
	 * edition). 496. MIT Press and McGraw-Hill. ISBN 0-262-03384-4.
	 * 
	 * @param x
	 *            - node within the BTree
	 * @param key
	 *            - key within the node
	 * @throws IOException
	 */
	public void insertNF(BTreeNode x, long key) throws IOException {
		int numKeys = x.getNumKeys();
		TreeObject keyToInsert = new TreeObject(key);

		// x is not full
		if(numKeys < (2 * degree - 1)) {
			
			// Compare keys
			// keyToInsert is less than last NFNode key
			while(numKeys > 0 && (keyToInsert.compareTo(x.getKey(numKeys - 1)) < 0)) {
				// insert key before compared key and increment numKeys
				x.addKeyAtNode((numKeys), keyToInsert);
				numKeys++;
			}
			// keyToInsert is equal to last NFNode key
			if(numKeys > 0 && (keyToInsert.compareTo(x.getKey(numKeys - 1)) == 0)) {
				x.getKey(numKeys - 1).incrementFrequency();
			}
			// keyToInsert is greater than last NFNode key
			else {
				// insert key to end and increment numKeys
				x.addKeyToRear(keyToInsert);
				x.setNumKeys(numKeys + 1);		
			}
			writeNode(x, x.getOffset());
		}
		
		// x is full
		else { 
			// Compare keys
			// keyToInsert is less than last NFNode key
			while(numKeys > 0 && (keyToInsert.compareTo(x.getKey(numKeys - 1)) < 0)) {
				// insert key before compared key and increment numKeys
				x.addKeyAtNode((numKeys), keyToInsert);
				numKeys++;
			}
			// keyToInsert is equal to last NFNode key
			if(numKeys > 0 && (keyToInsert.compareTo(x.getKey(numKeys - 1)) == 0)) {
				x.getKey(numKeys - 1).incrementFrequency();
				writeNode(x,x.getOffset());
			}
			// keyToInsert is greater than last NFNode key
			else {			
				// if x is a leaf
				BTreeNode y = readNode(x.getChild(numKeys));
				if(x.isLeaf()) {
					// parent node is full
					if(x.getParentNode(numKeys) == (2 * degree - 1)) {
						splitTree(x, numKeys, y);
						if(keyToInsert.compareTo(x.getKey(numKeys)) > 0)
							numKeys--;
					} else
						// insertNF to parent node
						insertNF(readNode(x.getParentNode(numKeys)), keyToInsert.getData());
				}
				insertNF(readNode(y.getChild(y.getNumKeys())), keyToInsert.getData());
			}
		}
	}

	/**
	 * Helper method for insertNF
	 * 
	 * @param NFNode
	 * @param numKeys
	 * @param keyToInsert
	 */
/*	public void keyCompare(BTreeNode NFNode, int numKeys, TreeObject keyToInsert) throws IOException {
		// Compare keys
		
		// keyToInsert is less than last NFNode key
		while(numKeys > 0 && (keyToInsert.compareTo(NFNode.getKey(numKeys - 1)) < 0)) {
			// insert key before compared key and increment numKeys
			NFNode.addKeyAtNode((numKeys), keyToInsert);
			NFNode.setNumKeys(NFNode.getNumKeys() - 1);
		}
		// keyToInsert is equal to last NFNode key
		if(numKeys > 0 && (keyToInsert.compareTo(NFNode.getKey(numKeys - 1)) == 0)) {
			NFNode.getKey(numKeys - 1).incrementFrequency();
			writeNode(NFNode,NFNode.getOffset());
		}
		// keyToInsert is greater than last NFNode key
		else {
			// insert key to end and increment numKeys
			NFNode.addKeyToRear(keyToInsert);
			NFNode.setNumKeys(NFNode.getNumKeys() + 1);		
		}
	}
		/*
		// set keysInNFNode to number of keys in nonFullNode, declare tree object with key
		int numKeys = x.getNumKeys();
		TreeObject insertedKey = new TreeObject(key);
		
		//System.out.println("\t\t1: " + NFNode_Keys);
		
		// if nFNode is a leaf node
		if(x.isLeaf()) {
			
			//if keys exist
			if(x.getNumKeys() != 0) {
				
				//System.out.println("\t\t2: " + NFNode_Keys);
				
				// case for nFNode key being less than inserted Key
				// while more than 0 keys in nFNode and insertedKey is less than last key in nFNode
				while(numKeys > 0 && insertedKey.compareTo(x.getKey(numKeys - 1)) < 0) {
					// number of keys in nFNode decrease
					numKeys--;
				}
			}

			// case for nFNode key and inserted Key being equal
			// if more than 0 keys in nFNode and inserted Key and last key in nFNode are equal
			if(numKeys > 0 && insertedKey.compareTo(x.getKey(numKeys - 1)) == 0)
				// increment frequency of same key in node
				x.getKey(numKeys - 1).incrementFrequency();
			
			// case for inserted Key greater
			else {
				// add inserted Key to end of nFNode
				x.addKeyAtNode(numKeys, insertedKey);
				
				// increase number of keys in nFNode
				x.setNumKeys(x.getNumKeys() + 1);
			}
		
			// writeNode to file
			writeNode(x, x.getOffset());
		}
		// for when nFNode not a leaf node
		else {
			// same condition checks and adjustments for node
			while(numKeys > 0 && insertedKey.compareTo(x.getKey(numKeys - 1)) < 0)
				numKeys--;
			
			if(numKeys > 0 && insertedKey.compareTo(x.getKey(numKeys - 1)) == 0) {
				x.getKey(numKeys - 1).incrementFrequency();
				writeNode(x, x.getOffset());
			}
			
			// case where NFNode has children
			BTreeNode y = readNode(x.getChild(numKeys));
			int childKeys = y.getNumKeys();
			
			// if max # of keys reached in child node
			if (childKeys == (2 * degree - 1)){
				
				// while more than 0 keys in child node and inserted key is less than last key in child node
				while(childKeys > 0 && insertedKey.compareTo(y.getKey(childKeys - 1)) < 0)
					// decrement key count in child node
					childKeys--;
				
				//if more than 0 keys in child node and inserted key is equal to last key in child node
				if(childKeys > 0 && insertedKey.compareTo(y.getKey(childKeys - 1)) == 0) {
					// increase frequency of last key in child node
					y.getKey(childKeys - 1).incrementFrequency();
					
					// write child node at offset
					writeNode(y, y.getOffset());
				
				// case for when key is greater than last key in child node (splitTree)
				} else {
					// split tree from last key in nFNode to child node
					splitTree(x, numKeys, y);
				
					// if inserted key > key of node at last key in nFNode, increment key count
					if(insertedKey.compareTo(x.getKey(numKeys)) > 0) {
						numKeys++;
					}
				}
			}	
			// recursive call inserts key into child of full nFNode while keys in child are less than max
			insertNF(readNode(x.getChild(numKeys)), key);
			
			//System.out.println((2 * degree - 1) + " max vs keys in parent: " + NFNode_Keys);
			//System.out.println((2 * degree - 1) + " max vs keys in child : " + childOfNFNode_Keys);

		}
	}

	/**
	 * Reads node data from disk block
	 * 
	 * @param fileoffset
	 *            - memory offset
	 * @return node data
	 */
	public BTreeNode readNode(int fileOffset) {
		BTreeNode readData = null;
		TreeObject nodeObject = null;
		int count = 0;

		if(cache != null)
			readData = cache.nodeInCache(fileOffset);
		
		if(readData != null)
			return readData;
		
		readData = new BTreeNode();
		readData.setOffset(fileOffset);

		try {
			randomAF.seek(fileOffset);

			// read nodes from 0 to max keys
			for (count = 0; count < (2 * degree - 1); count++) {

				// If count is less than number of keys in node
				if (count < readData.getNumKeys()) {

					// read key data and frequency and add key to rear
					nodeObject = new TreeObject(randomAF.readLong(), randomAF.readInt());
					readData.addKeyToRear(nodeObject);
				}
				
				// If count is less than number of keys in node and not leaf
				if (count < readData.getNumKeys() + 1 && !readData.isLeaf()) {
					readData.childAddToRear(randomAF.readInt());
				}
				
				// If count is greater/equal to number of keys in node or leaf
				else if (count >= readData.getNumKeys() + 1 || readData.isLeaf()) {
					randomAF.seek(randomAF.getFilePointer() + KEY_SIZE);
				}
			}

			// If count equals number of keys and is not a leaf
			if (count == readData.getNumKeys() && !readData.isLeaf()) {
				readData.childAddToRear(randomAF.readInt());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return readData;
	}

	/**
	 * Writes data from node to disk
	 * 
	 * @param node
	 *            - node within the BTree
	 * @param fileoffset
	 *            - offset from root
	 * @throws IOException
	 */
	public void writeNode(BTreeNode writeData, int fileOffset) throws IOException {
	
		// If using cache
		if (cache != null) {
			BTreeNode cachedNode = cache.addNode(writeData, fileOffset);
			
			// see if cached node exists
			if(cachedNode != null)
				// write cache to file
				writeToFile(cachedNode, cachedNode.getOffset());
		} else
			// write nodes without caching to file
			writeToFile(writeData, fileOffset);
	}

	/**
	 * Method to write node to file.
	 * 
	 * @param writeData
	 * @param fileOffset
	 */
	public void writeToFile(BTreeNode writeData, int fileOffset) {
		int count = 0;
		
		try {
			// Metadata
			randomAF.seek(writeData.getOffset());
			randomAF.writeBoolean(writeData.isLeaf());
			randomAF.writeInt(writeData.getNumKeys());
			
			randomAF.writeLong(writeData.getParent());

			// write nodes from 0 to max keys
			for (count = 0; count < (2 * degree - 1); count++) {

				// If count is less than number of keys in node
				if (count < writeData.getNumKeys()) {
					// get key data and frequency
					randomAF.writeLong(writeData.getKey(count).getData());
					randomAF.writeLong(writeData.getKey(count).getFrequency());
				}

				// If count is less than number of keys in node and not leaf
				if (count < writeData.getNumKeys() && !writeData.isLeaf()) {
					randomAF.writeLong(writeData.getChild(count));
				}

				// If count is greater/equal to number of keys in node or leaf
				else if (count >= writeData.getNumKeys() || writeData.isLeaf()) {
					randomAF.writeLong(0);
				}
			}

			// If count equals number of keys and is not a leaf
			if (count == writeData.getNumKeys() && !writeData.isLeaf()) {
				randomAF.writeLong(writeData.getChild(count));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public BTreeNode getRoot() {
		return root;
	}
	
	public int getSequenceLength() {
		return sequenceLength;
	}
	
	public int getDegree() {
		return degree;
	}

	/**
	 * Overrides standard toString() method.
	 * 
	 */
	@Override
	public String toString() {
		String buf = "\n";
		ArrayDeque<BTreeNode> myQ = new ArrayDeque<BTreeNode>();
		myQ.add(root);

		while (!myQ.isEmpty()) { // breadth-first traversal
			BTreeNode d = myQ.remove();

			for (Integer fileOffset : d.getChildren()) {
				BTreeNode e = readNode(fileOffset);

				if (e != null) {
					myQ.add(e);
				}
			}

			buf += d.toString() + "\n"; // returns a linear list of nodes for now
		} // will come up with a more 'tree-like' representation

		return buf;
	}
	
	private String dumpString() {
		String buf = "<frequency> <DNA string>\n";
		DNAParser dummy = new DNAParser(sequenceLength);
		ArrayDeque<BTreeNode> myQ = new ArrayDeque<BTreeNode>();
		myQ.add(root);

		while (!myQ.isEmpty()) { // breadth-first traversal
			BTreeNode d = myQ.remove();

			for (Integer fileOffset : d.getChildren()) {
				BTreeNode e = readNode(fileOffset);

				if (e != null) {
					myQ.add(e);
				}
			}

			for (TreeObject to : d.getKeys()) {
				String w = dummy.longToSubSequence(to.getData());
				buf += to.getFrequency() + "\t\t" + w + "\n";
			}
		} 

		return buf;
	}
	
	public void dumpTree() {
		File dumpFile = new File("dump.txt");
		
		try {
			if(dumpFile.exists()) dumpFile.delete();    			// clear file if exists
			dumpFile.createNewFile();  							// create new file
			randomAF = new RandomAccessFile(dumpFile, "rw");	// create new random access file set for both read and write
			randomAF.seek(0);			
			String toWrite = this.dumpString();
			randomAF.writeBytes(toWrite);
			
		} catch(FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("File: " + dumpFile.getName() + " not found.\n");
			System.exit(-1);
		} catch(IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		
		
	}
	
	// Metadata classes
	/**
	 * Writes tree metadata
	 */
	public void writeMetadata() {
		try {
			randomAF.seek(0);
			randomAF.writeInt(degree);
			randomAF.writeInt(sequenceLength);
			randomAF.writeInt(nodeSize);
			randomAF.writeInt(offsetFromRoot);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Reads tree metadata
	 */
	public void readMetadata() {
		try {
			randomAF.seek(0);
			degree = randomAF.readInt();
			sequenceLength = randomAF.readInt();
			nodeSize = randomAF.readInt();
			offsetFromRoot = randomAF.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	
}
