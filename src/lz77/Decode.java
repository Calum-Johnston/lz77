package lz77;

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
	
	// Creates a new instance of class
	public static void main(String args[]) {
		Decode lz77 = new Decode();
		lz77.decode(args[0], args[1]);
	}
	
	// Basic function for decoding the data
	public void decode(String fileName, String fileExtension) {	
		
		data = readFile(fileName, fileExtension);
		
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
	public String readFile(String fileName, String fileExtension) {
		/*DataInputStream is;
		try {
			is = new DataInputStream(new FileInputStream(fileName + "(" + fileExtension + ")_compressed.bin"));
			try {
				int data = is.read();
				System.out.println(data);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		
		File file = new File(fileName + "(" + fileExtension + ")_compressed.bin");
		byte[] bytesArray = new byte[(int) file.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			fis.read(bytesArray);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(byte b : bytesArray){
			System.out.println(b);
		}
		
		return data;
	}
	
}
