package lz77;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

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
		lz77.decode(args[0], args[1]);
	}
	
	// Basic function for decoding the data
	public void decode(String fileName, String fileExtension) {	
		
		String compressed_binaryData = "";
		compressed_binaryData = readFile(fileName, fileExtension);
		convertBinaryToTuple(compressed_binaryData);
		
		String binaryData = "";

		for(Tuple tup : compressedData) {
			if(tup.getOffset() == 0) {
				binaryData += tup.getCharacter();
			}else {
				if(tup.getCharacter().equals("-")){
					binaryData += binaryData.substring(binaryData.length() - tup.getOffset(), binaryData.length() - tup.getOffset() + tup.getLength());
				}else {
					binaryData += binaryData.substring(binaryData.length() - tup.getOffset(), binaryData.length() - tup.getOffset() + tup.getLength()) + tup.getCharacter();
				}
			}
		}
		System.out.println(binaryData);

	}
	
	// Reads in the file to be uncompressed
	public String readFile(String fileName, String fileExtension) {
		FileInputStream fis;
		String inputData = "";
		String inputTotalData = "";
		File file = new File(fileName + "(" + fileExtension + ")_compressed.bin");
		try {
			fis = new FileInputStream(file);
			wasted_bits = fis.read();
			offset_bits = fis.read();
			length_bits = fis.read();
			for(int i = 0; i < file.length() - 3; i++) {
				int temp = fis.read();
				inputData = Integer.toBinaryString(temp);
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
		return inputTotalData;
	}
	
	public void convertBinaryToTuple(String compressed_binaryData) {
		int i = 0;
		int offset = 0; int length = 0; int character = 0;
		while(i < compressed_binaryData.length() - wasted_bits - 1) {
			System.out.println(compressed_binaryData.length() - wasted_bits - 1);
			System.out.println(i + offset_bits + length_bits + 1);
			offset = Integer.parseInt(compressed_binaryData.substring(i, i + offset_bits), 2);
			length = Integer.parseInt(compressed_binaryData.substring(i + offset_bits, i + offset_bits + length_bits), 2);
			if(i + offset_bits + length_bits + 1 == compressed_binaryData.length() - wasted_bits - 1) {
				compressedData.add(new Tuple(offset, length, "-"));
			}else {
				character = Integer.parseInt(compressed_binaryData.substring(i + offset_bits + length_bits, i + offset_bits + length_bits + 1), 2);
				compressedData.add(new Tuple(offset, length, Integer.toString(character)));
			}
			i += offset_bits + length_bits + 1;
		}
	}
}
