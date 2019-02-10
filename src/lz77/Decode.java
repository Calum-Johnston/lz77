package lz77;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//Class decodes & uncompresses input which has been compressed through lz77

public class Decode {

	// ArrayList that stores a collection of tuples created during compression of the data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Defines basic variables
	String data;
	
	// Creates a new instance of class
	public static void main(String args[]) {
		String fileName = args[0];
		Decode lz77 = new Decode();
		lz77.decode(fileName);
	}
	
	// Basic function for decoding the data
	public void decode(String fileName) {	
		
		data = readFile(fileName);
		
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
	
	// Reads in the file to be uncompressed
	public String readFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName + "_compressed.txt"));
			try {
				String line = br.readLine();
				
				// Read data into tuples??
				
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
}
