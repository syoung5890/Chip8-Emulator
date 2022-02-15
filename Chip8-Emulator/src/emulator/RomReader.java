package emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RomReader {
	
	private byte[] rom;
	
	public RomReader(String filename) {
		try {
			FileInputStream fis = new FileInputStream(new File(filename));
			rom = fis.readAllBytes();
		} catch(IOException e) {
			e.printStackTrace();
			rom = null; 
		}
		
		
	}
	
	public byte[] getMemoryArray() {
		return rom;
	}
	
		
}
