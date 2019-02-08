package lz77;

// Class acts to store a tuple of data 
// produced during the encoding of data 
// in the LZ77 algorithm

public class Tuple {

	int offset;
	int length;
	char character;
	
	public Tuple(int offset, int length, char character){
		this.offset = offset;
		this.length = length;
		this.character = character;
	}
	
	public String toString(){
		return offset + "," + length + "," + character;
	}
}
