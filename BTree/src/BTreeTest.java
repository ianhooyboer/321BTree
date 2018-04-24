import java.io.IOException;

public class BTreeTest {

	public static void main (String[] args) {
		String filename = "myTest.txt";
		int degree = 2;
		
		BTree myBTree = new BTree(degree, filename); //tests constructor
		
		
		try {
			myBTree.insert(3); //test inserting a key into BTree
			myBTree.insert(1);
			myBTree.insert(2);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(myBTree.toString());
		
		
	}
	
}
