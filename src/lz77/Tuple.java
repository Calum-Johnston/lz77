package lz77;

// Class acts to store a tuple of data produced during the encoding of data in the LZ77 algorithm

public class Tuple {

	int offset;
	int length;
	String character;
	
	// Stores the values for offset, length & character when a new instance is created
	public Tuple(int offset, int length, String character){
		this.offset = offset;
		this.length = length;
		this.character = character;
	}
	
	// Overrides the toString() method to return correct information
	public String toString(){
		return "" + offset + length + character;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getCharacter() {
		return character;
	}
	
	public void setCharacter(String character) {
		this.character = character;
	}
}
