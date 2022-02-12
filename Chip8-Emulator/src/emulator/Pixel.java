package emulator;

import java.awt.Color;

public class Pixel {
		private static final Color WHITE = new Color(255,255,255);
		private static final Color BLACK = new Color(0,0,0);
		
		private int size,x,y;
		private int color;
	public Pixel(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		color = BLACK.getRGB();
		
	}
	
	public int getColor() {
		return this.color;
	}
	
	public void setColor(Color c) {
		this.color = c.getRGB();
	}
}
