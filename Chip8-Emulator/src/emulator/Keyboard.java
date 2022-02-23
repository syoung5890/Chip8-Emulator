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
			keyPressed = true;
			break;
		case 50:
			key = 0x2;
			keyPressed = true;
			break;
		case 51:
			key = 0x3;
			keyPressed = true;
			break;
		case 52:
			key = 0xC;
			keyPressed = true;
			break;
		case 81:
			key = 0x4;
			keyPressed = true;
			break;
		case 87:
			key = 0x5;
			keyPressed = true;
			break;
		case 69:
			key = 0x6;
			keyPressed = true;
			break;
		case 82:
			key = 0xD;
			keyPressed = true;
			break;
		case 65:
			key = 0x7;
			keyPressed = true;
			break;
		case 83:
			key = 0x8;
			keyPressed = true;
			break;
		case 68:
			key = 0x9;
			keyPressed = true;
			break;
		case 70:
			key = 0xE;
			keyPressed = true;
			break;
		case 90:
			key = 0xA;
			keyPressed = true;
			break;
		case 88:
			key = 0x0;
			keyPressed = true;
			break;
		case 67:
			key = 0xB;
			keyPressed = true;
			break;
		case 86:
			key = 0xF;
			keyPressed = true;
			break;
		default:
			key = 0;
			keyPressed = false;
			break;
		}
	}
	


}
