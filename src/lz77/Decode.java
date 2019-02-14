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
	String data;
	int offset_bits;
	int length_bits;
	int wasted_bits;
	
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
		
		data = "";

		for(Tuple tup : compressedData) {
			if(tup.getOffset() == 0) {
				data += tup.getCharacter();
			}else {
				data += data.substring(data.length() - tup.getOffset(), data.length() - tup.getOffset() + tup.getLength()) + tup.getCharacter();
			}
		}
		System.out.println(data.replaceAll("-", ""));
		System.out.println("Decoding Successful");
	}
	
	// Reads in the file to be uncompressed
	public String readFile(String fileName, String fileExtension) {
		FileInputStream fis;
		String inputData = "";
		String inputTotalData = "";
		File file = new File(fileName + "(" + fileExtension + ")_compressed.bin");
		
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
					inputTotalData += inputData;
				}
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		//Return statement removes the extra zero's added
		return inputTotalData.substring(0, inputTotalData.length() - wasted_bits);
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
