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
	private long fileOffset;
	private int parent;
	private int degree;
	private LinkedList<Long> children;
	private LinkedList<TreeObject> keys;

	// -------------------Constructor-------------------

	public BTreeNode(int degree) {
		this.setDegree(degree);
		parent = -1;
		keys = new LinkedList<TreeObject>();
		children = new LinkedList<Long>();
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
	public long getFileOffset() {
		return fileOffset;
	}

	public void setFileOffset(long fileoffset2) {
		this.fileOffset = fileoffset2;
	}

	// parent mutators
	public int getParent() {
		return parent;
	}

	public void setParent(int p) {
		this.parent = p;
	}

	// children mutators
	public Long getChild(long c) {
		return children.get((int) c);
	}

	public LinkedList<Long> getChildren() {
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

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	// -------------------Methods-------------------
	public boolean isLeaf() {
		return isLeaf;
	}

	// Children nodes
	public void addChildToRear(long l) {
		children.add(l);
	}

	public void addChildAtNode(Long ch, int c) {
		children.add(c, ch);
	}

	public Long removeChild(int c) {
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
	
	@Override
	public String toString() {
		String buf = "";
		
		buf += "*** ***\n";
		buf += "Node: " + fileOffset + "\n";
		buf += "Keys: " + keys.toString() + "\n";
		buf += "--- ---\n";
		buf += "Children " + children.toString() +"\n";
		buf += "*** ***\n";
		
		return buf;
	}

}
