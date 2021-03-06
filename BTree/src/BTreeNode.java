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
	private int offset;
	private int parent;
	private int degree;
	private int ssLength;
	private LinkedList<Integer> parentNode;
	private LinkedList<Integer> children;
	private LinkedList<TreeObject> keys;

	// -------------------Constructor-------------------

	public BTreeNode() {
		parent = -1;
		parentNode = new LinkedList<Integer>();
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
	
	public boolean getLeafStatus() {
		return isLeaf;
	}

	// fileOffset mutators
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	// parent mutators
	public Integer getParentNode(int p) {
		return parentNode.get(p);
	}
	
	public int getParent() {
		return parent;
	}

	public void setParent(int p) {
		this.parent = p;
	}

	// children mutators
	public Integer getChild(int c) {
		return children.get(c);
	}

	public LinkedList<Integer> getChildren() {
		return children;
	}
	
	public LinkedList<Integer> getParents() {
		return parentNode;
	}

	// key mutators
	public TreeObject getKey(int k) {
		//System.out.println(numKeys + " [" + retVal.getData() + "]");
		return keys.get(k);
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
	
	public int getSubSequenceLength() {
		return ssLength;
	}
	
	public void setSSLength(int ssLength) {
		this.ssLength = ssLength;
	}

	// -------------------Methods-------------------
	public boolean isLeaf() {
		if (this.children.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}
	
	// Parent nodes
	public void parentAddToRear(int l) {
		parentNode.add(l);
	}

	public void parentAddAtIndex(int ch, int c) {
		parentNode.add(c, ch);
	}
	
	// Children nodes
	public void childAddToRear(int l) {
		children.add(l);
	}

	public void childAddAtIndex(int ch, int c) {
		children.add(c, ch);
	}

	public Integer removeChild(int c) {
		return children.remove(c);
	}

	// Keys
	public void addKeyToRear(TreeObject k) {
		keys.add(k);
	}

	public void addKeyAtNode(int k, TreeObject key) {
		keys.add(k, key);
	}

	public TreeObject removeKey(int k) {
		return keys.remove(k);
	}
	
	@Override
	public String toString() {
		String buf = "";
		
		buf += "***B***\n";
		buf += "Node offset: " + offset + "\n";
		
		buf += "Keys: [ ";
		for (TreeObject k : keys) { buf += k.getData() + "(" + k.getFrequency() + ")" + " ";}
		buf += " ]\n";
		
//		buf += "--- ---\n";
		buf += "Children (addressed by offsets): [ ";
		for (Integer i : children) {buf += i.toString() +" ";}
		buf += " ]\n";
		
		buf += "***E***\n";
		
		return buf;
	}

}
