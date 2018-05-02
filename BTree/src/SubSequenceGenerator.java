import java.util.ArrayList;

public class SubSequenceGenerator {

	private ArrayList<String> SSs;
	private int numSeqs;
	private int ssLength;

	public SubSequenceGenerator(int numSeqs, int ssLength) {

		this.numSeqs = numSeqs;
		this.ssLength = ssLength;

		this.SSs = generateSubSequences(numSeqs, ssLength);
	}

	private ArrayList<String> generateSubSequences(int numSeqs, int ssLength) {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < numSeqs; i++) {
			String nextSeq = "";

			for (int j = 0; j < ssLength; j++) {
				double nextChar = Math.random() * 4;
				int intChar = (int) nextChar;
				switch (intChar) {
				case 0:
					nextSeq += 'a';
					break;
				case 1:
					nextSeq += 'c';
					break;
				case 3:
					nextSeq += 't';
					break;
				case 2:
					nextSeq += 'g';
					break;
				}

				if (nextSeq.length() == ssLength) {
//					System.out.println(nextSeq);
					list.add(nextSeq);
				}
			}
		}
		return list;
	}
	
	public ArrayList<String> getSSs() {
		return this.SSs;
	}
}
