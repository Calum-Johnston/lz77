package lz77;

import java.util.ArrayList;

//Class decodes & uncompresses input which has been compressed through lz77

public class Decode {

	// ArrayList that stores a collection of tuples created during compression of the data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Defines basic variables
	String data;
	
	// Creates a new instance of class
	public static void main(String args[]) {
		Decode lz77 = new Decode();
		lz77.decode();
	}
	
	// Basic function for decoding the data
	public void decode() {
		
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
