package cs455.scaling.resources;

public class Task {
	
	byte[] bytes;
	
	public Task(String someString) {
		bytes = someString.getBytes();
	}
	
	public byte[] getBytes() { return bytes; }
}
