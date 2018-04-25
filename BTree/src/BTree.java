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

	/**
	 * Standard constructor for BTree object
	 * 
	 * @param degree - degree to be used during construction of BTree
	 * @param filename - filename to be read from and written to
	 */
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

	/**
	 * Creates a new BTree node using a file offset set to the current end of file 
	 * 
	 * @param randomAF - file to be written to
	 * @param degree - degree of current BTree to be stored
	 * @return new BTree node with stored fileoffset
	 */
	public BTreeNode createBTreeNode(RandomAccessFile randomAF, int degree) {
		BTreeNode res = new BTreeNode(degree);
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
	public TreeObject keySearch(BTreeNode x, long k) { //TODO not tested
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
	 * @throws IOException 
	 */
	public void insert(long key) throws IOException {//TODO not tested
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
	public void splitTree(BTreeNode x, int i) { //TODO not tested
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
		//TODO test
		
		//TODO check to see if key is already present in tree first
		
		if (node.isLeaf()) { //if node is a leaf
			//add the key at the correctly sorted position
			int pos = 0;
			if (!node.getKeys().isEmpty()) {
				for (TreeObject obj : node.getKeys()) {
					if (key < obj.getData()) {
						break;						
					}else {
						pos++;
					}
				}
				node.addKeyAtNode(pos, new TreeObject(key));
			}else {
				node.addKeyAtNode(0, new TreeObject(key));
			}
			writeNode(node, node.getFileOffset());
			
		}else { //node not a leaf
			//TODO implement
		}
		
		
	}

	/**
	 * Reads node data from disk block
	 * 
	 * @param fileoffset
	 *            - memory offset
	 * @return node data
	 */
	public BTreeNode readNode(Long fileoffset) {
		//TODO implement
		
		BTreeNode readData = null;

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
	public void writeNode(BTreeNode node, long fileoffset) throws IOException {
		//TODO broken, writing strange chars to file
		randomAF.seek(fileoffset);
		
		for (TreeObject obj : node.getKeys()) { //writes the list of keys as Long values
			randomAF.writeLong(obj.getData());
		}
		
		for (Long childOffset : node.getChildren()) { //writes the list of children's fileoffsets as Long values
			randomAF.writeLong(childOffset);
		}
	}

	/**
	 * Overrides standard toString() method.  Used for testing to see contents of BTree.
	 * 
	 */
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
