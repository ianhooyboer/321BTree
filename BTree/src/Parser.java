import java.io.RandomAccessFile;
import java.util.Scanner;

public class Parser {
	
	private Scanner scan;
	
	public Parser (Readable file) {
		scan = new Scanner (file);
	}
	
}
