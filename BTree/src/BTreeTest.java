
public class BTreeTest {

	public static void main (String[] args) {
		String filename = "myTest.txt";
		int degree = 4;
		
		BTree myBTree = new BTree(degree, filename);
		
		System.out.println(myBTree.toString());
	}
	
}
