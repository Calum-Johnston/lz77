package lz77;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//Class decodes & uncompresses input which has been compressed through lz77

public class Decode {

	// ArrayList that stores a collection of tuples created during compression of the data
	ArrayList<Tuple> compressedData = new ArrayList<Tuple>();
	
	// Defines basic variables
	StringBuilder data;
	int offset_bits;
	int length_bits;
	int wasted_bits;
	
	int slidingWindowSize_Bits = 14;
	int lookAheadBuffer_Bits = 12;
	
	// Creates a new instance of class
	public static void main(String args[]) {
		Decode lz77 = new Decode();
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter the text file to use");
		String n = reader.next();
		reader.close();
		long startTime = System.nanoTime();
		lz77.decode(n, "txt");
		long finalTime = System.nanoTime();
		System.out.println("Time: " + (finalTime - startTime));
	}
	
	// Basic function for decoding the data
	public void decode(String fileName, String fileExtension) {	
		
		String compressed_binaryData = "";
		compressed_binaryData = readFile(fileName, fileExtension);
		convertBinaryToTuple(compressed_binaryData);
		
		data = new StringBuilder();

		for(Tuple tup : compressedData) {
			if(tup.getOffset() == 0) {
				data.append(tup.getCharacter());
			}else {
				data.append(data.toString().substring(data.toString().length() - tup.getOffset(), data.toString().length() - tup.getOffset() + tup.getLength()) + tup.getCharacter());
			}
		}
		System.out.println(data.toString().replaceAll("-", ""));
		System.out.println("Decoding Successful");
	}
	
	// Reads in the file to be uncompressed
	public String readFile(String fileName, String fileExtension) {
		FileInputStream fis;
		String inputData = "";
		StringBuilder inputTotalData = new StringBuilder();
		File file = new File(fileName + "(" + slidingWindowSize_Bits + "," + lookAheadBuffer_Bits + ")_compressed.bin");
		
		int[] tuples = new int[(int) file.length()];
		
		try {
			
			fis = new FileInputStream(file);
			for(int i = 0; i < file.length(); i++){
				int temp = fis.read();
				tuples[i] = temp;
			}
		
			wasted_bits = tuples[0];
			offset_bits = tuples[1];
			length_bits = tuples[2];
			for(int i = 3; i < tuples.length; i++) {
				inputData = Integer.toBinaryString(tuples[i]);
				while(inputData.length() < 8) {
					inputData = 0 + inputData;
				}
				if(inputData != null) {
					inputTotalData.append(inputData);
				}
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		//Return statement removes the extra zero's added
		return inputTotalData.toString().substring(0, inputTotalData.length() - wasted_bits);
	}
	
	public void convertBinaryToTuple(String compressed_binaryData) {
		int i = 0;
		int offset = 0; int length = 0; int character = 0; String character_value = "";
		while(i < compressed_binaryData.length() - 1) {	
			offset = Integer.parseInt(compressed_binaryData.substring(i, i + offset_bits), 2);
			length = Integer.parseInt(compressed_binaryData.substring(i + offset_bits, i + offset_bits + length_bits), 2);
			if(compressed_binaryData.substring(i + offset_bits + length_bits, i + offset_bits + length_bits +7).charAt(0) == '0'){
				character = Integer.parseInt(compressed_binaryData.substring(i + offset_bits + length_bits + 1, i + offset_bits + length_bits +7), 2);
			}else{
				character = Integer.parseInt(compressed_binaryData.substring(i + offset_bits + length_bits, i + offset_bits + length_bits +7), 2);
			}
			character_value = (char) character + "";
			compressedData.add(new Tuple(offset, length, character_value));
			i += offset_bits + length_bits + 7;
		}
	}
}
