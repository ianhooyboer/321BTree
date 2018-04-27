/**
 * CS 321 B-Tree Project Spring 2018
 * 
 * The BTreeCache class is the implementation of a cache for a B-Tree data structure.
 * 
 * @author Eric Hieronymus, Ian Hooyboer, and Parker Crawford
 */

// -------------------Libraries-------------------
import java.util.Iterator;
import java.util.LinkedList;

public class BTreeCache implements Iterable<BTreeNode>{

	// -------------------Variables-------------------
	private int hits, misses, cacheSize;
	private LinkedList<BTreeNode> nodeList;
	
	// -------------------Constructor-------------------
	/**
	 * Constructor of specified cache size
	 * @param cacheSize - size of cache
	 */
	public BTreeCache(int cacheSize) {
		this.cacheSize = cacheSize;
		nodeList = new LinkedList<BTreeNode>();
	}
	
	// -------------------Mutators-------------------
	/**
	 * Adds node to BTreeCache.
	 * 
	 * @param nodeCache - node to be added to cache
	 * @param offset - memory offset on node
	 * @return retVal - last node removed from cache (if full) or null (if not full)
	 */
	public BTreeNode addNode(BTreeNode nodeCache, long offset) {
		BTreeNode retVal = null;
		
		if(nodeList.size() == cacheSize) {
			retVal = nodeList.removeLast();
		}
		nodeList.addFirst(nodeCache);
		return retVal;
	}
	
	/**
	 * nodeInCache returns a node in the cache if found or null if not.
	 * hits or misses are incremented based on the result
	 * 
	 * @param offset - memory offset of node
	 * @return retVal - node if found in cache or null if not
	 */
	public BTreeNode nodeInCache(long offset) {
		
		// For each node object in nodeList
		for(BTreeNode retVal : nodeList) {
			
			// if offset matches
			if(retVal.getOffset() == offset) {
				
				// remove retVal from within cache, add it to front of cache and increment hits
				nodeList.remove(retVal);
				nodeList.addFirst(retVal);
				hits++;
				
				return retVal;
			}
		}
		
		// If no hits
		misses++;
		return null;
		
	}
	/**
	 * Get the number of cache hits
	 * 
	 * @return hits in cache
	 */
	public int getHits() {
		return hits;
	}
	
	/**
	 * Get number of cache misses
	 * 
	 * @return misses in cache
	 */
	public int getMisses() {
		return misses;
	}
	
	/**
	 * Get number of references in cache
	 * 
	 * @return hits plus misses (references)
	 */
	public int getReferences() {
		return hits + misses;
	}
	
	/**
	 * Get cache hit ratio
	 * 
	 * @return hits divided by references 
	 */
	public long getHitRatio() {
		return ((long) getHits() / (long) getReferences());
	}
	
	/**
	 * Empty cache
	 */
	public void emptyCache() {nodeList.clear();}
	
	@Override
	public Iterator<BTreeNode> iterator() {
		return nodeList.iterator();
	}

}
