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
	 */
	public BTree(int degree, File file, boolean useCache, int cacheSize) {
		// block size = # of keys * size of keys + file offset of children * size of keys
		nodeSize = (KEY_SIZE * (2 * degree - 1));
		offsetFromRoot =  (KEY_SIZE * (2 * degree));
		blockInsert = nodeSize + offsetFromRoot;
		this.degree = degree;
		
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
				s.addChildToRear(r.getOffset());
				
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
	 *            	- child number
	 * @param y		
	 *				- x's ith child node 
	 */
	public void splitTree(BTreeNode x, int i, BTreeNode y) { // TODO not tested
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
				z.addChildToRear(y.removeChild(degree));
			}
		}
		// Add key to x from ith child
		x.addKeyAtNode(i, y.removeKey(degree - 1));
		
		//Update # keys in x and y
		x.setNumKeys(x.getNumKeys() + 1);
		y.setNumKeys(y.getNumKeys() - 1);
		
		// If x is not root with one key
		if(x == root && x.getNumKeys() == 1) {						// node being split is root and 1 key
			try {
				writeNode(y, blockInsert);							// write ith child node in new block
				blockInsert += nodeSize;	
				
				//System.out.println(i + " " + blockInsert + "\n");
				
				z.setOffset(blockInsert);								// set offset of new child to new block
				x.addChildAtNode(z.getOffset(), i + 1);					// add ith + 1 child to x from z offset
				writeNode(z, blockInsert);								// write new child in new block
				
				writeNode(x, offsetFromRoot);							// write parent node to offset from root
				
				blockInsert += nodeSize;
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		} else {													
			try {
				writeNode(y, y.getOffset());						// write ith child of x at y offset
				z.setOffset(blockInsert);								// set new child offset
				
				writeNode(z, blockInsert);							// write new child in new block
				x.addChildAtNode(z.getOffset(), i + 1);				// add ith + 1 child to x from z offset
				
				writeNode(x, x.getOffset());						// write parent node at offset
				blockInsert += nodeSize;								// add a node memory size to block
				
				//System.out.println(i + " " + blockInsert + "\n");
				
			} catch (IOException e) {
				e.printStackTrace(System.err);
				System.exit(-1);
			}
		}
	}

	/**
	 * Inserts key into tree rooted at the nonfull root node
	 * 
	 * Reference: Cormen, T. H., et al. 2009. Introduction to Algorithms (3rd
	 * edition). 496. MIT Press and McGraw-Hill. ISBN 0-262-03384-4.
	 * 
	 * @param node
	 *            - node within the BTree
	 * @param key
	 *            - key within the node
	 * @throws IOException
	 */
	public void insertNF(BTreeNode x, long key) throws IOException {

		// set int i to x.number of keys, declare tree object with key
		int i = x.getNumKeys();
		TreeObject childKey = new TreeObject(key);
		
		// if x is a leaf node
		if(x.isLeaf()) {
			
			//System.out.println("1 " + x.getOffset() + "\n");
			
			//if keys exist
			if(x.getNumKeys() != 0) {
			
				// case for x key being less than child key
				// while i >= 0 && key < x.key at i, x.key at i + 1 = x.key at i, decrement i
				while(i > 0 && childKey.compareTo(x.getKey(i - 1)) < 0)
					i--;
			}

			// case for x key and child key being equal
			if(i > 0 && childKey.compareTo(x.getKey(i - 1)) == 0)
				x.getKey(i - 1).incrementFrequency();
			
			// case for x key larger than child key
			// x.key at i + 1 = key
			// number of keys in node increment by 1
			else {
				x.addKeyAtNode(i, childKey);
				x.setNumKeys(x.getNumKeys() + 1);
				
				//System.out.println("2 " + x.getOffset() + "\n");
			}
		
			// writeNode(x, at offset)
			writeNode(x, x.getOffset());
			//System.out.println("3 " + x.getOffset() + "\n");
		}
		// if x is not leaf node
		//else while i > 0 and key < x.key at i (case for node not being leaf), decrememnt i
		else {
			
			//System.out.println("1 " + x.getOffset() + "\n");
			
			while(i > 0 && childKey.compareTo(x.getKey(i - 1)) < 0)
				i--;
			
			// Same tests as when x was leaf
			if(i > 0 && childKey.compareTo(x.getKey(i - 1)) == 0) {
				x.getKey(i - 1).incrementFrequency();
				writeNode(x, x.getOffset());
			}
			
			// readNode(child of node), create new node
			BTreeNode y = readNode(x.getChild(i));
			
			// if amount of keys in child node == 2t - 1
			if(y.getNumKeys() == (2 * degree - 1)) {
				
				//System.out.println("2 " + x.getOffset() + "\n");
				
				// Same error checking as above but for child node
				int j = y.getNumKeys();
				
				while(j > 0 && childKey.compareTo(y.getKey(j - 1)) < 0)
					j--;
				
				if(j > 0 && childKey.compareTo(y.getKey(j - 1)) == 0) {
					y.getKey(j - 1).incrementFrequency();
					
					// write child node
					writeNode(y, y.getOffset());
				}
				
				else {
					// splitTree(x, i, child node)
					splitTree(x, i, y);
				
					// if key > key of node at i, increment i
					if(childKey.compareTo(x.getKey(i)) > 0)
						i++;
				}
			}
			// insertNF(child node, key)
			BTreeNode childNode = readNode(x.getChild(i));
			
			//System.out.println(x.getChild(i) + "\n");
			insertNF(childNode, key);
			//System.out.println(x.getChild(i)+ "\n");
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
			
			// Set number of keys, parent, and leaf status
			readData.setNumKeys(randomAF.readInt());
			readData.setParent(randomAF.readInt());
			readData.setLeafStatus(randomAF.readBoolean());

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
					readData.addChildToRear(randomAF.readInt());
				}
				
				// If count is greater/equal to number of keys in node or leaf
				else if (count >= readData.getNumKeys() + 1 || readData.isLeaf()) {
					randomAF.seek(randomAF.getFilePointer() + KEY_SIZE);
				}
			}

			// If count equals number of keys and is not a leaf
			if (count == readData.getNumKeys() && !readData.isLeaf()) {
				readData.addChildToRear(randomAF.readInt());
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

	/**
	 * Overrides standard toString() method. Used for testing to see contents of
	 * BTree.
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
	
	// Metadata classes
	/**
	 * Writes tree metadata
	 */
	public void writeMetadata() {
		try {
			randomAF.seek(0);
			randomAF.writeInt(degree);
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
			nodeSize = randomAF.readInt();
			offsetFromRoot = randomAF.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
