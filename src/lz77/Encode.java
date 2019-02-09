package lz77;

import java.util.ArrayList;

// Class encodes & compresses input using lz77 compression technique

public class Encode {
	
	// ArrayList to store the collection of tuples generated during the compression of some data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Search values for encoding of data
	int slidingWindowSize = 30;
	int lookAheadBuffer = 8;
	
	// Generic variables required for compression
	int i;
	int d;
	String data;
	String lookAheadData;
	boolean noMatch;
	
	
	
	// Creates a new instance of class
	public static void main(String[] args) {
		Encode lz77 = new Encode();
		lz77.encode();
	}
	
	// Base function for encoding the data
	public void encode(){
		
		// Defines the data and start variables
		i = 1;
		d = 0;
		data = "abracadabra";
		
		// The first character will be default have no previous matches
		compressedData.add(new Tuple(0, 0, data.substring(0, 1)));
		
		while (i < data.length()){
			
			// noMatch refers to whether the lookAheadData matches any String in the Sliding Window
			noMatch = true;
			
			for (int l = lookAheadBuffer; l > 0; l--){
				
				// Gets the look-ahead data required
				lookAheadData = getlookAheadData(data, i, l);	
				
				// If the Sliding Window max size is greater than how much of the data has been compressed so far
				if(i > slidingWindowSize) {
					d = data.substring(i - slidingWindowSize, i).lastIndexOf(lookAheadData);
					if(d != -1) {
						d = slidingWindowSize - d; 
						addTuple(data, lookAheadData.length());
						break;
					} 
					
				// If the Sliding Window max size is less than how much of the data has been compressed so far
				}else {
					d = data.substring(0, i).lastIndexOf(lookAheadData);
					if(d != -1) {
						d = i - d;
						addTuple(data, lookAheadData.length());
						break;
					} 
				}
			}
			checkforNoMatch(data);
		}
		printData();
		System.out.println(data);
		decode();
	}
	
	// Returns the lookAheadData needed based on the current size of the lookAheadBuffer
	public String getlookAheadData(String data, int i, int lookAheadBuffer) {
		if(i + lookAheadBuffer < data.length()) {
			return data.substring(i, i + lookAheadBuffer);
		}else {
			return data.substring(i);
		}
	}
	
	// Adds a tuple to the ArrayList if a matching String is found
	public void addTuple(String data, int l) {
		if(l + i + 1 > data.length()) {
			compressedData.add(new Tuple(d, l, "-"));
		}else {
			compressedData.add(new Tuple(d, l, data.substring(l + i, l + i + 1))); 
		}
		i += l + 1;
		noMatch = false;
	}
	
	// Checks if the previous lookAheadData matched any String in the Sliding WIndow
	public void checkforNoMatch(String data) {
		if(noMatch == true) {
			compressedData.add(new Tuple(0, 0, data.substring(i, i + 1)));
			i += 1;
			noMatch = false;
		}
	}
	
	// Prints out the compressed data
	public void printData() {
		for(Tuple tup : compressedData) {
			System.out.println(tup.toString());
		}
	}
	
	public void decode() {
		data = "";
		
		// Defines the data and start variables
		for(Tuple tup : compressedData) {
			if(tup.getOffset() == 0) {
				data += tup.getCharacter();
			}else {
				if(tup.getCharacter() == "-"){
					data += data.substring(data.length() - tup.getOffset(), data.length() - tup.getOffset() + tup.getLength());
				}else {
					data += data.substring(data.length() - tup.getOffset(), data.length() - tup.getOffset() + tup.getLength()) + tup.getCharacter();
				}
			}
		}
		
		System.out.println(data);
	}
	
}
