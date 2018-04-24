import java.io.File;

public class ParserTest {

	public static void main (String args[]) {
		
		File file = new File("test1.gbk");
		int k = 31;
		
		DNAParser myParser = new DNAParser(file, k);
		
		long data = myParser.nextSubSeq();
		System.out.println(data);
		System.out.println(myParser.longToSubSequence(data, k));
		
		data = myParser.nextSubSeq();
		System.out.println(data);
		System.out.println(myParser.longToSubSequence(data, k));
		
	}
	
	
}
