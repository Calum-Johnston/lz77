package lz77;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// Class encodes & compresses input using lz77 compression technique

public class Encode {
	
	// ArrayList to store the collection of tuples generated during the compression of some data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Search values for encoding of data
	int slidingWindowSize_Bits = 10 ;
	int lookAheadBuffer_Bits = 10;
	int slidingWindowSize;
	int lookAheadBuffer;
	 
	// Generic variables required for compression
	int i;
	int d;
	String data = "";
	String lookAheadData;
	boolean noMatch;
	
	
	
	// Creates a new instance of class
	public static void main(String[] args) {
		Encode lz77 = new Encode();
		lz77.encode(args[0], args[1]);
	}
	
	// Base function for encoding the data
	public void encode(String fileName, String fileExtension){
		
		// Defines the data and start variables
		i = 1;
		d = 0;
		
		// Calculates slidingWindowSize and lookAheadBuffer size based on the number of bits to store them in
		slidingWindowSize = (int) Math.pow(2, slidingWindowSize_Bits);
		lookAheadBuffer = (int) Math.pow(2, lookAheadBuffer_Bits);
		
		// Gets the data to compress (in binary)
		data = readFile(fileName, fileExtension);
		
		// Converts the data to binary
		byte[] b = data.getBytes();
		data = "";
		int count = 0;
		for(byte a : b) {
			String binaryData = Integer.toBinaryString(a);
			data += binaryData;
			count += 1;
		}
		
		System.out.println(data);
		System.out.println("Number of bytes in original data: " + count);
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
		writeFile(fileName, fileExtension);
		//printData();
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
	public String readFile(String fileName, String fileExtension) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName + "." + fileExtension));
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
	public void writeFile(String fileName, String fileExtension) {
		String binaryData = convertTupleToBinary();
		int[] tuples = convertBinaryToBytes(binaryData);
		
		FileOutputStream os;
		try {
			os = new FileOutputStream(fileName + "(" + fileExtension + ")_compressed.bin");
			for(int tuple : tuples) {
				try {	
					os.write(tuple);
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
	}

	// Converts the tuples created during compression into binary
	public String convertTupleToBinary() {
		
		String binary = "";
		
		for(Tuple tup : compressedData) {
			
			String binaryData = Integer.toBinaryString(tup.getOffset());
			if(binaryData.length() < slidingWindowSize_Bits) {
				int numberOfZeros = slidingWindowSize_Bits - binaryData.length();
				for(int i = 0; i < numberOfZeros; i++) {
					binaryData = "0" + binaryData;
				}
			}
			binary += binaryData;
			
			binaryData = Integer.toBinaryString(tup.getLength());
			if(binaryData.length() < slidingWindowSize_Bits) {
				int numberOfZeros = slidingWindowSize_Bits - binaryData.length();
				for(int i = 0; i < numberOfZeros; i++) {
					binaryData = "0" + binaryData;
				}
			}
			binary += binaryData;
			
			binary += tup.getCharacter().charAt(0);
			
		}
		return binary;
	}
	
	// Converts the binary string into an array of bytes
	public int[] convertBinaryToBytes(String binaryData){
		int[] tuples = new int[(int) (binaryData.length()/8) + 4];
		tuples[1] = slidingWindowSize_Bits;
		tuples[2] = lookAheadBuffer_Bits;
		
		int i = 0;
		int position = 3;
		int count = 0;

		System.out.println(binaryData);
		
		binaryData = binaryData + "11";
		System.out.println(binaryData);
		while(i < binaryData.length()) {
			if(binaryData.substring(i, Math.min(i + 8, binaryData.length())).contains("-")){
				if(!(binaryData.substring(i, Math.min(i + 8, binaryData.length())).equals("-"))){
					String temp = binaryData.substring(i, Math.min(i + 8, binaryData.length())).replaceAll("-", "");
					while(temp.length() < 8) {
						temp = temp + "0";
						count += 1;
					}
					tuples[position] = Integer.parseInt(temp, 2);
					position += 1;
					i += 8;
				}
			}else{
				tuples[position] = Integer.parseInt(binaryData.substring(i, Math.min(i + 8, binaryData.length())), 2);
				position += 1;
				i += 8;
			}
		}
		
		tuples[0] = Integer.parseInt(Integer.toString(count), 10);
		
		return tuples;
	}
	
}
