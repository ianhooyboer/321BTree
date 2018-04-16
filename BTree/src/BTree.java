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
import java.io.RandomAccessFile;

public class BTree {
	
	// -------------------Variables-------------------
	private int degree;
	private BTreeNode root;
	private int nodeSize;
	private int offsetFromRoot;
	private File file;
	private RandomAccessFile randomAF;
	
	// -------------------Constructor-------------------
	public BTree(int degree) {
		
	}
	
	// -------------------Methods-------------------
	/**
	 * Searches BTree for a Key.
	 * 
	 * Reference: Cormen, T. H., et al.  2009.  Introduction to Algorithms (3rd edition).  492. 
	 * 			  MIT Press and McGraw-Hill.  ISBN 0-262-03384-4.  
	 * 
	 * @param x - node within the BTree
	 * @param k - key within the node
	 * @return keySearch
	 */
	public TreeObject keySearch(BTreeNode x, long k) {
		int i = 0;
		TreeObject key = new TreeObject(k);

		while(i <= x.getNumKeys() && key.compareTo(x.getKey(i)) > 0) {i++;}
		
		if(i <= x.getNumKeys() && key.compareTo(x.getKey(i)) == 0) {return x.getKey(i);}
		
		else if(x.isLeaf()) {return null;}
		
		else {return keySearch(readNode(x.getChild(i)), k);}
	}
	
	/**
	 * Inserts key into BTree.
	 * 
	 * Reference: Cormen, T. H., et al.  2009.  Introduction to Algorithms (3rd edition).  495. 
	 * 			  MIT Press and McGraw-Hill.  ISBN 0-262-03384-4.   
	 * 
	 * @param key - key within the node
	 */
	public void insert(long key) {
		BTreeNode r = root;
		
		if(r.getNumKeys() == ((2 * degree) - 1)) {
			BTreeNode s = new BTreeNode();
			root = s;
			s.setLeafStatus(false);
			s.setNumKeys(0);
			s.addChildToRear(r.getFileOffset());
			splitTree(s, 1);
			insertNF(s, key);
		}
		else {insertNF(r, key);}
	}
	
	/**
	 * Splits BTree child node.
	 * 
	 * Reference: Cormen, T. H., et al.  2009.  Introduction to Algorithms (3rd edition).  494. 
	 * 			  MIT Press and McGraw-Hill.  ISBN 0-262-03384-4.   
	 * 
	 * @param x - node within the BTree
	 * @param i - child to split
	 */
	public void splitTree(BTreeNode x, int i) {
		BTreeNode z = new BTreeNode();
		BTreeNode y = new BTreeNode();
		
		y.setParent(x.getParent());
		z.setLeafStatus(y.isLeaf());
		z.setNumKeys(degree - 1);
		
		for(int j = 1; j < degree - 1; j++) {
			z.addKeyToRear(y.removeKey(degree));
			z.setNumKeys(z.getNumKeys() + 1);
			y.setNumKeys(y.getNumKeys() - 1);
		}
		
		if(!y.isLeaf()) {
			for(int j = 1; j < degree; j++) {
				z.addChildToRear(y.removeChild(degree));
			}
		}
		y.setNumKeys(degree - 1);
		x.setNumKeys(x.getNumKeys() + 1);
		
		for(int j = x.getNumKeys(); j < i + 1; j--) {x.addChildToRear(j);}
		x.addChildAtNode(z.getFileOffset(), i + 1);
		
		for(int j = x.getNumKeys(); j < i; j--) {
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
	 * Reference: Cormen, T. H., et al.  2009.  Introduction to Algorithms (3rd edition).  496. 
	 * 			  MIT Press and McGraw-Hill.  ISBN 0-262-03384-4. 
	 * 
	 * @param x - node within the BTree
	 * @param key - key within the node 
	 */
	public void insertNF(BTreeNode x, long key) {
		
	}
	
	/**
	 * Reads node data from disk block
	 * 
	 * @param offset - memory offset
	 * @return node data
	 */
	public BTreeNode readNode(int offset) {
		BTreeNode readData = null;
		
		return readData;
	}
	
	/**
	 * Writes data from node to disk
	 * 
	 * @param x - node within the BTree
	 * @param offset - offset from root
	 */
	public void writeNode(BTreeNode x, int offset) {
		
	}

}
