package emulator;

import java.awt.*;

import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Monitor {
	
	//Size constants
	private static final int WIDTH = 64;
	private static final int HEIGHT = 32;
	private static final int PIXEL_SIZE = 20;
	
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
		loadImage();
		initiateWindow();
	}
	
	//Maps "Pixel" to the actual pixels it encompasses 
	public void loadImage() {
		for(int i = 0; i<WIDTH*PIXEL_SIZE;i++) {
			for(int j = 0;j<HEIGHT*PIXEL_SIZE;j++) {
				image.setRGB(i, j, pixels[(i-(i%PIXEL_SIZE))/PIXEL_SIZE][(j-(j%PIXEL_SIZE))/PIXEL_SIZE].getColor());
				//System.out.println("x: " + i + " y: " + j + " pixel" +((i-(i%PIXEL_SIZE))/PIXEL_SIZE) + " "  +((j-(j%PIXEL_SIZE))/PIXEL_SIZE) );
			}
		}
	}
	
	public boolean flipPixel(int x, int y) {
		boolean off = false;
		if(pixels[x][y].getColor() == WHITE.getRGB()) {
			pixels[x][y].setColor(BLACK);
			off = true;
		}
		else {
			System.out.println("Updating pixel "+ x + " " + y);
			pixels[x][y].setColor(WHITE);
		}
		
		for(int i = (x * PIXEL_SIZE);i<((x+1)* PIXEL_SIZE);i++) {
			for(int j = (y* PIXEL_SIZE); j<((y+1)*PIXEL_SIZE);j++){
				image.setRGB(i, j, pixels[x][y].getColor());
			}
		}
		
		return off;
		//label.repaint();
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
		panel = new JPanel(new FlowLayout(0,0,0));
		label = new JLabel();
		label.setIcon(new ImageIcon(image));
		panel.add(label);
		frame.add(panel);
		frame.setSize((WIDTH*PIXEL_SIZE),(HEIGHT*PIXEL_SIZE));
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	
	public void setPixel(int x,int y, int color) {
		if(color == 1) {
			pixels[x][y].setColor(WHITE);
		}
		else if(color == 0) {
			pixels[x][y].setColor(BLACK);
		}
		
	}
	
	public void clearScreen() {
		for(int i = 0;i<WIDTH;i++) {
			for(int j = 0;j<HEIGHT;j++) {
				pixels[i][j].setColor(BLACK);
			}
		}
		loadImage();
		label.repaint();
	}
	
	public void updateScreen() {
		label.repaint();
		label.revalidate();
	}
	
	public void setKeyListener(Keyboard keyboard) {
		frame.addKeyListener(keyboard);
	}
	
	public static void main(String[]args) {
		Monitor monitor = new Monitor();
		Keyboard keyboard = new Keyboard();
		monitor.setKeyListener(keyboard);
		monitor.flipPixel(63, 31);
		monitor.flipPixel(0, 0);
		
	}
}

