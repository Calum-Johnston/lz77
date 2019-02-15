package lz77;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// Class encodes & compresses input using lz77 compression technique

public class Encode {
	
	// ArrayList to store the collection of tuples generated during the compression of some data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Search values for encoding of data
	int slidingWindowSize_Bits = 14;
	int lookAheadBuffer_Bits = 8;
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
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter the text file to use");
		String fileName = reader.next();
		System.out.println("Enter the file type");
		String fileType = reader.next();
		reader.close();
		long startTime = System.nanoTime();
		lz77.encode(fileName, fileType);
		long finalTime = System.nanoTime();
		System.out.println("Time: " + (finalTime - startTime));
	}
	
	// Base function for encoding the data
	public void encode(String fileName, String fileExtension){
		
		// Defines the data and start variables
		i = 1;
		d = 0;
		
		// Calculates slidingWindowSize and lookAheadBuffer size based on the number of bits to store them in
		slidingWindowSize = (int) Math.pow(2, slidingWindowSize_Bits) - 1;
		lookAheadBuffer = (int) Math.pow(2, lookAheadBuffer_Bits) - 1;
				
		// Gets the data to compress (in binary)
		data = readFile(fileName, fileExtension);
	
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
					d = data.substring(i - slidingWindowSize, i).indexOf(lookAheadData);
					if(d != -1) {
						d = slidingWindowSize - d; 
						addTuple(data, lookAheadData.length());
						break;
					} 
					
				// If the Sliding Window max size is less than how much of the data has been compressed so far
				}else {
					d = data.substring(0, i).indexOf(lookAheadData);
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
		System.out.println("Encoding successful");
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
		StringBuilder readInData = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName + "." + fileExtension));
			try {
				String line = br.readLine();
				while(line != null) {
					readInData.append(line);
					line = br.readLine();
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return readInData.toString();
	}
	
	// Outputs compressed file
	public void writeFile(String fileName, String fileExtension) {
		String binaryData = convertTupleToBinary();
		int[] tuples = convertBinaryToInt(binaryData);
		FileOutputStream os;
		try {
			os = new FileOutputStream(fileName + "(" + slidingWindowSize_Bits + "," + lookAheadBuffer_Bits + ")_compressed.bin");
			int count = 0;
			for(int tuple : tuples) {
				try {	
					os.write(tuple);
					count += 1;
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
		StringBuilder binary = new StringBuilder();
		for(Tuple tup : compressedData) {
			String offset = Integer.toBinaryString(tup.getOffset());
			while(offset.length() < slidingWindowSize_Bits) {
				offset = "0" + offset;
			}
			binary.append(offset);
			
			String length = Integer.toBinaryString(tup.getLength());
			while(length.length() < lookAheadBuffer_Bits) {
				length = "0" + length;
			}
			binary.append(length);
			
			String character = Integer.toBinaryString(tup.getCharacter().charAt(0));
			while(character.length() < 7){
				character = "0" + character; 
			}
			binary.append(character);
		}
		return binary.toString();
	}
	
	// Converts the binary string into an array of integers
	public int[] convertBinaryToInt(String binaryData){
		int[] tuples;
		if(binaryData.length() % 8 == 0){
			tuples = new int[(int) (binaryData.length()/8) + 3];
		}else{
		tuples = new int[(int) (binaryData.length()/8) + 4];
		}
		tuples[1] = slidingWindowSize_Bits;
		tuples[2] = lookAheadBuffer_Bits;
		
		int i = 0;
		int position = 3;
		int count = 0;
		
		while(i < binaryData.length()) {
			// If it contains a "-"
			if(binaryData.substring(i, Math.min(i + 8, binaryData.length())).contains("-")){
				// If it is not just a "-" by itself, make it fit 8 bits
				if(!(binaryData.substring(i, Math.min(i + 8, binaryData.length())).equals("-"))){
					String temp = binaryData.substring(i, Math.min(i + 8, binaryData.length())).replaceAll("-", "");
					while(temp.length() < 8) {
						temp = temp + "0";
						count += 1;
					}
					tuples[position] = Integer.parseInt(temp, 2);
					position += 1;
					i += 8;
				}else{
					position += 1;
					i = i + 8;
				}
			// If it does not contain a "-" 
			}else{
				// If it is less than 8 bits, we need to make it 8 bits (only applies to final part)
				if(binaryData.length() < i + 8){
					String temp = binaryData.substring(i, Math.min(i + 8, binaryData.length()));
					while(temp.length() < 8){
						temp = temp + "0";
						count += 1;
					}
					tuples[position] = Integer.parseInt(temp, 2);					position += 1;
					i += 8;
				}else{
					tuples[position] = Integer.parseInt(binaryData.substring(i, Math.min(i + 8, binaryData.length())), 2);
					position += 1;
					i += 8;
				}
			}
		}
		tuples[0] = Integer.parseInt(Integer.toString(count), 10);
		
		return tuples;
	}
	
}
