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
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayDeque;

public class BTree {

	// -------------------Variables-------------------
	private int degree;
	private BTreeNode root;
	private int nodeSize;
	private int offsetFromRoot;
	private File file;
	private RandomAccessFile randomAF;

	/**
	 * Standard constructor for BTree object
	 * 
	 * @param degree
	 *            - degree to be used during construction of BTree
	 * @param filename
	 *            - filename to be read from and written to
	 */
	public BTree(int degree, File file) {
		this.offsetFromRoot = 0; // Root offset is 0
		this.degree = degree;
		this.file = file;

		clearFile(file);

		try {

			this.randomAF = new RandomAccessFile(file, "rw");

		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("\nFile: " + file.getName() + " not found.");
			System.exit(-1);
		}

		root = createBTreeNode(randomAF, degree);
	}

	/**
	 * zeros out file each time the program is run
	 * 
	 * @param file
	 */
	private void clearFile(File file) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace(System.err);
			System.exit(-1);
		}
		writer.print("");
		writer.close();
	}

	/**
	 * Creates a new BTree node using a file offset set to the current end of file
	 * 
	 * @param randomAF
	 *            - file to be written to
	 * @param degree
	 *            - degree of current BTree to be stored
	 * @return new BTree node with stored fileoffset
	 */
	public BTreeNode createBTreeNode(RandomAccessFile randomAF, int degree) {
		BTreeNode res = new BTreeNode();
		long fileoffset = 0;

		try {
			fileoffset = randomAF.length();
		} catch (IOException e) {
			e.printStackTrace();
		}

		res.setFileOffset(fileoffset);

		try {
			writeNode(res, fileoffset);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}

		return res;
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
		} else if (x.isLeaf()) {
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
	public void insert(long key) throws IOException {// TODO not tested
		BTreeNode r = root;

		if (r.getNumKeys() == ((2 * degree - 1))) {
			BTreeNode s = new BTreeNode();
			root = s;
			s.setLeafStatus(false);
			s.setNumKeys(0);
			s.addChildToRear(r.getFileOffset());
			splitTree(s, 1);
			insertNF(s, key);
		} else {
			insertNF(r, key);
		}
	}

	/**
	 * Splits BTree child node.
	 * 
	 * Reference: Cormen, T. H., et al. 2009. Introduction to Algorithms (3rd
	 * edition). 494. MIT Press and McGraw-Hill. ISBN 0-262-03384-4.
	 * 
	 * @param x
	 *            - node within the BTree
	 * @param i
	 *            - child to split
	 */
	public void splitTree(BTreeNode x, int i) { // TODO not tested
		BTreeNode z = new BTreeNode();
		BTreeNode y = new BTreeNode();

		y.setParent(x.getParent());
		z.setLeafStatus(y.isLeaf());
		z.setNumKeys(degree - 1);

		for (int j = 0; j < degree - 1; j++) {
			z.addKeyToRear(y.removeKey(degree));
			z.setNumKeys(z.getNumKeys() + 1);
			y.setNumKeys(y.getNumKeys() - 1);
		}

		if (!y.isLeaf()) {
			for (int j = 0; j < degree; j++) {
				z.addChildToRear(y.removeChild(degree));
			}
		}
		y.setNumKeys(degree - 1);
		x.setNumKeys(x.getNumKeys() + 1);

		for (int j = x.getNumKeys(); j < i; j--) { // getting stuck in infinite here
			x.addChildToRear(j);
			System.out.println(this);
		}
		x.addChildAtNode(z.getFileOffset(), i + 1);

		for (int j = x.getNumKeys(); j < i; j--) {
			x.removeKey(j + 1);
		}
		x.addKeyToRear(y.getKey(degree));

		x.setNumKeys(x.getNumKeys() + 1);

		try {
			writeNode(y, y.getFileOffset());
			writeNode(z, z.getFileOffset());
			writeNode(x, x.getFileOffset());
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(-1);
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
	public void insertNF(BTreeNode node, long key) throws IOException {
		// TODO write unit test(s)

		TreeObject isPresent = null;
		if (node.getNumKeys() != 0 && node.getNumKeys() < (2 * node.getDegree() - 1)) { // TODO broken, wip
			isPresent = keySearch(node, key);
		}

		if (isPresent != null) { // increment frequency and be done
			isPresent.incrementFrequency();
		} else {
			if (node.isLeaf()) { // if node is a leaf
									// add the key at the correctly sorted position
				int pos = 0;
				if (!node.getKeys().isEmpty()) {
					for (TreeObject obj : node.getKeys()) {
						if (key < obj.getData()) {
							break;
						} else {
							pos++;
						}
					}
					node.addKeyAtNode(pos, new TreeObject(key));
				} else {
					node.addKeyAtNode(0, new TreeObject(key));
				}
				writeNode(node, node.getFileOffset());

			} else { // node not a leaf
				// TODO implement (requires split tree to be working)
				while (!node.getKeys().isEmpty()) {

				}
			}
		}

	}

	/**
	 * Reads node data from disk block
	 * 
	 * @param fileoffset
	 *            - memory offset
	 * @return node data
	 */
	public BTreeNode readNode(Long fileOffset) {
		BTreeNode readData = null;
		int count = 0;

		readData = new BTreeNode();
		TreeObject nodeObject = null;
		readData.setFileOffset(fileOffset);

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
				if (count < readData.getNumKeys() && !readData.isLeaf()) {
					readData.addChildToRear(randomAF.readLong());
				}

				// If count is greater/equal to number of keys in node or leaf
				else if (count >= readData.getNumKeys() || readData.isLeaf()) {
					randomAF.seek(randomAF.getFilePointer() + fileOffset);
				}
			}

			// If count equals number of keys and is not a leaf
			if (count == readData.getNumKeys() && !readData.isLeaf()) {
				readData.addChildToRear(randomAF.readLong());
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
	public void writeNode(BTreeNode writeData, long fileoffset) throws IOException {
		int count = 0;

		try {
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

			for (Long fileoffset : d.getChildren()) {
				BTreeNode e = readNode(fileoffset);

				if (e != null) {
					myQ.add(e);
				}
			}

			buf += d.toString() + "\n"; // returns a linear list of nodes for now
		} // will come up with a more 'tree-like' representation

		return buf;
	}

}
