/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * BTreeNode is a node within a B-Tree data structure.  The node needs the following
 * information within it:
 * 		- If it is a leaf node
 * 		- Its file offset in memory
 * 		- Its parent (root's parent is -1)
 * 		- Its child or children
 * 		- The number of keys in the node
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------
import java.util.LinkedList;

public class BTreeNode {
	
	// -------------------Variables-------------------
	private int numKeys;
	private boolean isLeaf;
	private int fileOffset;
	private int parent;
	private LinkedList<Integer> children;
	private LinkedList<TreeObject> keys;
	
	// -------------------Constructor-------------------
	public BTreeNode() {
		parent = -1;
		keys = new LinkedList<TreeObject>();
		children = new LinkedList<Integer>();
		numKeys = 0;
	}
	
	// -------------------Mutators-------------------
	
	// numKey mutators
	public int getNumKeys() {
		return numKeys;
	}
	
	public void setNumKeys(int k) {
		numKeys = k;
	}
	
	// isLeaf mutator
	public void setLeafStatus(boolean leafStatus) {
		this.isLeaf = leafStatus;
	}
	
	// fileOffset mutators
	public int getFileOffset() {
		return fileOffset;
	}
	
	public void setFileOffse(int offset) {
		this.fileOffset = offset;
	}
	
	// parent mutators
	public int getParent() {
		return parent;
	}
	
	public void setParent(int p) {
		this.parent = p;
	}
	
	// children mutators
	public int getChild(int c) {
		return children.get(c).intValue();
	}
	
	public LinkedList<Integer> getChildren() {
		return children;
	}
	
	// key mutators
	public TreeObject getKey(int k) {
		TreeObject retVal = keys.get(k);
		return retVal;
	}
	
	public LinkedList<TreeObject> getKeys() {
		return keys;
	}
	
	// -------------------Methods-------------------
	public boolean isLeaf() {
		return isLeaf;
	}
	
	// Children nodes
	public void addChildToRear(int c) {
		children.add(c);
	}
	
	public void addChildAtNode(Integer ch, int c) {
		children.add(c, ch);
	}
	
	public int removeChild(int c) {
		return children.remove(c);
	}
	
	// Keys
	public void addKeyToRear(TreeObject k) {
		keys.add(k);
	}
	
	public void addKeyAtNode(TreeObject ke, int k) {
		keys.add(k, ke);
	}
	
	public TreeObject removeKey(int k) {
		return keys.remove(k);
	}
	
}
