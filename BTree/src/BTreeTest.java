import java.io.File;
import java.io.IOException;

public class BTreeTest {

	public static void main(String[] args) {
		String filename = "myTest.txt";
		File file = new File(filename);
		int degree = 4;

		BTree myBTree = new BTree(degree, file); // tests constructor

		try {
			myBTree.insert(3); // test inserting a key into BTree
			myBTree.insert(1);
			myBTree.insert(2);
			myBTree.insert(2); // tests incrementing frequency instead of adding a duplicate node
			
			myBTree.insert(5); // beginning to test splitTree

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(myBTree.toString());
		
		boolean kstBool = keySearchTest(myBTree, 3);

	}

	/**
	 * Unit test of keySearchTest
	 * 
	 */
	static boolean keySearchTest(BTree tree, long testVal) {
		long kstExpectedVal = testVal;
		TreeObject keySearchTestNode = tree.keySearch(tree.getRoot(), kstExpectedVal);

		if (keySearchTestNode != null) {
			if (keySearchTestNode.getData() == kstExpectedVal) {
				System.out.println("keySearchTest: PASS");
				return true;
			} else {
				System.out.println("keySearchTest: FAIL - returned unexpected value");
				return false;
			}
		} else {
			System.out.println("keySearchTest: FAIL - returned null");
			return false;
		}
	}

}
