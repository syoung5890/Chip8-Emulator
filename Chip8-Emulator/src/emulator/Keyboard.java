package emulator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
	private int keyCode;
	private char key;
	private boolean keyPressed;
	
	public Keyboard() {
		keyCode = 0;
		keyPressed = false;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyCode =e.getKeyCode();
		keyPressed = true;
		convertKey();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyCode = 0;
		keyPressed = false;	
		convertKey();
	}
	
	public char getKey() {
		return key;
	}
	
	public boolean isKeyPressed() {
		return keyPressed;
	}
	
	public void convertKey() {
		switch(keyCode) {
		case 49:
			key = 0x1;
			break;
		case 50:
			key = 0x2;
			break;
		case 51:
			key = 0x3;
			break;
		case 52:
			key = 0xC;
			break;
		case 81:
			key = 0x4;
			break;
		case 87:
			key = 0x5;
			break;
		case 82:
			key = 0x6;
			break;
		case 84:
			key = 0xD;
			break;
		case 65:
			key = 0x7;
			break;
		case 83:
			key = 0x8;
			break;
		case 68:
			key = 0x9;
			break;
		case 70:
			key = 0xE;
			break;
		case 90:
			key = 0xA;
			break;
		case 88:
			key = 0x0;
			break;
		case 67:
			key = 0xB;
			break;
		case 86:
			key = 0xF;
			break;
		default:
			key = 0xFF;
			break;
		}
	}
	


}
