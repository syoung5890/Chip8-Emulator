package emulator;

import java.awt.*;

import java.awt.image.BufferedImage;

import javax.swing.*;

public class Monitor {
	
	//Size constants
	private static final int WIDTH = 64;
	private static final int HEIGHT = 32;
	private static final int PIXEL_SIZE = 10;
	
	//Color constants
	private static final Color WHITE = new Color(255, 255, 255); 
	private static final Color BLACK = new Color(0,0,0);
	
	private JFrame frame;
	private JPanel panel;
	private JLabel label;
	private Pixel[][] pixels;
	private BufferedImage image;

	
	public Monitor() {
		pixels = new Pixel[WIDTH][HEIGHT];
		setPixels();
		image = new BufferedImage(WIDTH * PIXEL_SIZE, HEIGHT * PIXEL_SIZE,BufferedImage.TYPE_INT_RGB);
		flipPixel(5,7);
		flipPixel(5,20);
		flipPixel(5,7);
		loadImage();
		initiateWindow();
	}
	
	//Maps "Pixel" to the 100 actual pixels it encompasses 
	public void loadImage() {
		for(int i = 0; i<WIDTH*PIXEL_SIZE;i++) {
			for(int j = 0;j<HEIGHT*PIXEL_SIZE;j++) {
				image.setRGB(i, j, pixels[(i-(i%PIXEL_SIZE))/PIXEL_SIZE][(j-(j%PIXEL_SIZE))/PIXEL_SIZE].getColor());
				System.out.println("x: " + i + " y: " + j + " pixel" +((i-(i%PIXEL_SIZE))/PIXEL_SIZE) + " "  +((j-(j%PIXEL_SIZE))/PIXEL_SIZE) );
			}
		}
	}
	
	public void flipPixel(int x, int y) {
		if(pixels[x][y].getColor() == WHITE.getRGB()) {
			pixels[x][y].setColor(BLACK);
		}
		else {
			pixels[x][y].setColor(WHITE);
		}
		
		for(int i = (x * PIXEL_SIZE);i<((x+1)* PIXEL_SIZE);i++) {
			for(int j = (y* PIXEL_SIZE); j<((y+1)*PIXEL_SIZE);j++){
				image.setRGB(i, j, pixels[x][y].getColor());
			}
		}
	}
	
	private void setPixels() {
		for(int i = 0;i<WIDTH;i++) {
			for(int j = 0;j<HEIGHT;j++) {
				pixels[i][j] = new Pixel(i,j,PIXEL_SIZE);
			}
		}
	}
	
	private void initiateWindow() {
		frame = new JFrame("CHIP-8 Emulator");
		panel = new JPanel();
		label = new JLabel();
		label.setIcon(new ImageIcon(image));
		panel.add(label);
		frame.add(panel);
		frame.setSize(WIDTH*PIXEL_SIZE,HEIGHT*PIXEL_SIZE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	
	public static void main(String[]args) {
		Monitor m = new Monitor();
	}
}

