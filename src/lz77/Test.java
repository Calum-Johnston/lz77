package lz77;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {

	public static void main(String args[]){
		DataOutputStream os;
		try {
			os = new DataOutputStream(new FileOutputStream("example.bin"));
			try {
				Byte a = (byte) 34;
				os.write(a);
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
