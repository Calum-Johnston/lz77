package lz77;

import java.util.ArrayList;

// Class encodes & compresses binary input
// using lz77 compression technique

public class Encode {
	
	// ArrayList to store the collection of tuples generated
	// during the compression of an input
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Generic Tuple to store information
	Tuple newTuple;
	
	// Search values for encoding of data
	int slidingWindow = 30;
	int lookAheadBuffer = 8;
	
	// Generic variables required for compression
	int pointer;
	String lookaheadData;
	
	// Creates a new instance of class
	public static void main(String[] args) {
		Encode lz77 = new Encode();
		lz77.encode();
	}
	
	public void encode(){
		String data = "1111100011100001011101110000100010001100100010010000101111010001001010011110000101011100101010000011011010101010100100101101010011001000";
		pointer = 0;
		while (pointer < data.length()){
			
		}
	}
	
}
