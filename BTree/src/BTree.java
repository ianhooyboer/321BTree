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
	private int degree;
	private BTreeNode root;
	private int nodeSize;
	private int offsetFromRoot;
	private File file;
	private RandomAccessFile randomAF;

	// -------------------Constructor-------------------
	public BTree(int degree, String filename) {
		this.offsetFromRoot = 0; // Root offset is 0
		this.degree = degree;
		this.file = new File(filename);

		try {

			this.randomAF = new RandomAccessFile(file, "rw");

		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.err.println("\nFile: " + file.getName() + " not found.");
			System.exit(-1);
		}
		
		root = createBTreeNode(randomAF, degree);

	}

	// -------------------Methods-------------------
	public BTreeNode createBTreeNode(RandomAccessFile randomAF, int degree) {
		BTreeNode res = new BTreeNode(degree);
		long fileoffset = 0;
		
		try {
			fileoffset = randomAF.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		res.setFileOffset(fileoffset);
		writeNode(res, fileoffset);
		
		
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
		}

		else if (x.isLeaf()) {
			return null;
		}

		else {
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
	 */
	public void insert(long key) {
		BTreeNode r = root;

		if (r.getNumKeys() == ((2 * degree) - 1)) {
			BTreeNode s = new BTreeNode(degree);
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
	public void splitTree(BTreeNode x, int i) {
		BTreeNode z = new BTreeNode(degree);
		BTreeNode y = new BTreeNode(degree);

		y.setParent(x.getParent());
		z.setLeafStatus(y.isLeaf());
		z.setNumKeys(degree - 1);

		for (int j = 1; j < degree - 1; j++) {
			z.addKeyToRear(y.removeKey(degree));
			z.setNumKeys(z.getNumKeys() + 1);
			y.setNumKeys(y.getNumKeys() - 1);
		}

		if (!y.isLeaf()) {
			for (int j = 1; j < degree; j++) {
				z.addChildToRear(y.removeChild(degree));
			}
		}
		y.setNumKeys(degree - 1);
		x.setNumKeys(x.getNumKeys() + 1);

		for (int j = x.getNumKeys(); j < i + 1; j--) {
			x.addChildToRear(j);
		}
		x.addChildAtNode(z.getFileOffset(), i + 1);

		for (int j = x.getNumKeys(); j < i; j--) {
			x.removeKey(j + 1);
		}
		x.addKeyToRear(y.getKey(degree));

		x.setNumKeys(x.getNumKeys() + 1);
		writeNode(y, y.getFileOffset());
		writeNode(z, z.getFileOffset());
		writeNode(x, x.getFileOffset());
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
	 */
	public void insertNF(BTreeNode x, long key) {

	}

	/**
	 * Reads node data from disk block
	 * 
	 * @param fileoffset
	 *            - memory offset
	 * @return node data
	 */
	public BTreeNode readNode(Long fileoffset) {
		BTreeNode readData = null;

		return readData;
	}

	/**
	 * Writes data from node to disk
	 * 
	 * @param x
	 *            - node within the BTree
	 * @param l
	 *            - offset from root
	 */
	public void writeNode(BTreeNode x, long l) {

	}

	@Override
	public String toString() {
		String buf = "";
		ArrayDeque<BTreeNode> myQ = new ArrayDeque<BTreeNode>();
		myQ.add(root);
		
		while(!myQ.isEmpty()) { //breadth-first traversal
			BTreeNode d = myQ.remove();
			
			for (Long fileoffset : d.getChildren()) {
				BTreeNode e = readNode(fileoffset);
				myQ.add(e);
			}
			
			buf += d.toString() + "\n"; //returns a linear list of nodes for now
		}								// will come up with a more 'tree-like' representation
		
		return buf;
	}
	
}
