package emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RomReader {
	
	public RomReader(String filename) {
		try {
			FileInputStream fis = new FileInputStream(new File(filename));
		} catch(IOException e) {
			e.printStackTrace();
		}
		}
		
}
