package lz77;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// Class encodes & compresses input using lz77 compression technique

public class Encode {
	
	// ArrayList to store the collection of tuples generated during the compression of some data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Search values for encoding of data
	int slidingWindowSize = 1000;
	int lookAheadBuffer = 100;
	
	// Generic variables required for compression
	int i;
	int d;
	String data = "";
	String lookAheadData;
	boolean noMatch;
	
	
	
	// Creates a new instance of class
	public static void main(String[] args) {
		String fileName = args[0];
		Encode lz77 = new Encode();
		lz77.encode(fileName);
	}
	
	// Base function for encoding the data
	public void encode(String fileName){
		
		// Defines the data and start variables
		i = 1;
		d = 0;
		
		// Gets the data to compress
		data = readFile(fileName);
		
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
		writeFile(fileName);
		printData();
		//decode();
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
	
	// Reads in a file to compress
	public String readFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName + ".txt"));
			try {
				String line = br.readLine();
				while(line != null) {
					data += line;
					line = br.readLine();
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	// Outputs compressed file
	public void writeFile(String fileName) {
		try {
			PrintWriter writer = new PrintWriter(fileName + "_compressed.txt", "UTF-8");
			for(Tuple tup : compressedData) {
				writer.print(tup.toString());
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/*public void writeFile(String fileName) {
		try {
			DataOutputStream os = new DataOutputStream(new FileOutputStream("D:\\\\Programming\\eclipse-workspace\\lz77\\binary.bin"));
			for(Tuple tup : compressedData) {
				try {
					os.writeChars(Integer.toBinaryString(tup.getOffset()));
					os.writeChars(Integer.toBinaryString(tup.getLength()));
					os.writeChars(Integer.toBinaryString(tup.getCharacter().charAt(0)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/
	
}
