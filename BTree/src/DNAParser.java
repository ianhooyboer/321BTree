import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Scanner;

public class DNAParser {

	private File gbkfile; 
	private int k; //maximum value is 31
	private Scanner scan;
	
	public DNAParser (File gbkfile, int k) {
		this.gbkfile = gbkfile;
		this.k = k;
		
		try {
			this.scan = new Scanner(gbkfile);
		} catch (FileNotFoundException e) {
			System.err.println("File: " + gbkfile.getName() + " not found.");
			System.exit(-1);
		}
		
	}
	
	long nextSubSeq () {
		String dummy = "";		
		
		while (!dummy.equals("ORIGIN      ")) { //skip header first
			dummy = scan.nextLine();
		}
		
		Scanner lineScan = new Scanner(dummy);
		ArrayDeque myQ = new ArrayDeque();
		
		//for test
//		dummy = scan.nextLine(); 
//		System.out.println(dummy);
//		System.exit(0);		
		
		
		while (scan.hasNextLine()) {
			dummy = scan.nextLine();
			int lineLength = dummy.length();
			
			int pos = 0;
			if (pos % 10 == 0) {
				pos = 0;
			}
		}
		
		
		return 0; //TODO return long value	
	}
	
}
