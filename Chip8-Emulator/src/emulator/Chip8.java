 package emulator;

import java.util.Random;
/*
 * Author: Stephen Young
 * Created: 2022
 */

public class Chip8 {
	
	//constants
	static final int MEM_SIZE = 0x1000;
	static final int NUM_REGISTERS = 0x10;
	static final int STACK_SIZE = 0x10;
	static final int INSTRUCTS_PER_SEC = 733;
	static final boolean SHIFT_IN_PLACE =  false;
	static final boolean JUMP_ADD_REGISTER = false;
	
	//memory 
	private char[] mem;
	private char[] V;
	private char[] stack;
	
	private int I;
	private char pc;
	private char delayTimer;
	private char soundTimer;
	private boolean running;
	private int stackIndex;
	
	private Random random;
	private Monitor monitor;
	private RomReader reader;
	private Keyboard  keyboard;
	private AudioController audio;
	
	public Chip8() {
		mem = new char[MEM_SIZE];
		V = new char[NUM_REGISTERS];
		stack = new char[STACK_SIZE];
		running = true;
		random = new Random();
		audio = new AudioController();
		keyboard = new Keyboard();
		monitor = new Monitor();
		monitor.setKeyListener(keyboard);
		reader = new RomReader(".\\src\\Roms\\SpaceInvaders.ch8");
		stackIndex = -1;
		pc = 0x200;
		I = 0;
		loadMemory();
		setFontSprites();
		run();
	}
	
	private void loadMemory() {
		for(int i = 0; i< MEM_SIZE;i++) {
			mem[i] = 0x0;
		}
		byte[] rom = reader.getMemoryArray();
		for(int i = 0;i<rom.length;i++) {
			mem[0x200+i] = (char)Byte.toUnsignedInt(rom[i]);
		}
	}
	

	
	public void decrementTimer() {
		if(soundTimer != 0) {
			soundTimer--;
			audio.startSound();
		}
		else {
			audio.stopSound();
		}
		if(delayTimer != 0) {
			delayTimer--;
		}
	
	}
	
	public void run() {
		long time = 0;
		long time2 = 0;
		long timer_delay = (1/60) * 1000;
		long delay =  (long)1000000000/INSTRUCTS_PER_SEC;
		while(running) {
			if(System.currentTimeMillis()>time2 + timer_delay) {
				decrementTimer();
				time2 = System.currentTimeMillis();
			}
			if(System.nanoTime()>time + delay) {
				time = System.nanoTime();
				System.out.println((int)pc);
				int instr = fetch();
				execute(instr);
				System.out.println("V0 " + (int)V[0] + " V1 " + (int)V[1] + " V2 " + (int)V[2] + " V3 " + (int)V[3] + " V4 " + (int)V[4] 
					+" V5 " +(int)V[0x5]	+" V6 " +(int)V[0x6]	+" V7 " +(int)V[0x7]+" V8 " +(int)V[0x8]	
							+" V9 " +(int)V[0x9]+" Va " +(int)V[0xa]+" VB " +(int)V[0xb]+" VC " +(int)V[0xc]	
									+" VD " +(int)V[0xD]
											+" VE " +(int)V[0xE]+" VF " + (int)V[0xF]+ " I "+I);
			}
		}
	}
	
	private int fetch() {
		int instr;
		instr = (int)mem[pc];
		instr = (instr<<8) + (int)mem[pc+1];
		pc += 2;
		return instr;
	}
	
	private void execute(int instr) {
		int lead = (instr - (instr % 0x1000)) / 0x1000;
		int x = ((instr - (lead * 0x1000)) - (instr %0x100))/0x100;
		int y = ((instr -(lead*0x1000) - (x * 0x100)) - (instr % 0x10))/0x10;
		int n = instr - (lead *0x1000) - (x*0x100) - (y * 0x10);
		int nn = instr % 0x100;
		int nnn = instr % 0x1000;
		System.out.println("Instruction: " + Integer.toHexString(instr));
		switch(lead) {
		case 0x0:
			if(nn== 0xE0) {
				clearScreen();
			}
			else if(nn == 0xEE) {
				exitSubroutine();
			}
			else {
				uknownCommand(instr);
			}
			
			break;
		case 0x1:
			jump(nnn);
			break;
		case 0x2:
			enterSubroutine(nnn);
			break;
		case 0x3:
			equal(V[x],nn);
			break;
		case 0x4:
			notEqual(V[x],nn);
			break;
		case 0x5:
			equal(V[x],V[y]);
			break;
		case 0x6:
			setRegister(x,nn);
			break;
		case 0x7:
			addToRegister(x,nn);
			break;
		case 0x8:
			x8XXX(x,y,n);
			break;
		case 0x9:
			notEqual(V[x],V[y]);
			break;
		case 0xA:
			setIndexRegister(nnn);
			break;
		case 0xB:
			//may not work with every program, add configurability if fails
			if(JUMP_ADD_REGISTER) {
				jump(nnn+V[x]);
			}
			else {
			jump(nnn + V[0]);
			}
			break;
		case 0xC:
			rand(x,nn);
			break;
		case 0xD:
			draw(x,y,n);
			break;
		case 0xE:
			if(nn ==0x9E) {
				skipIfPressed(x);
			}
			else if(nn == 0xA1) {
				skipIfNotPressed(x);
			}
			break;
		case 0xF:
			xFXXX(x,nn);
			break;
		default:
			uknownCommand(instr);
			break;
		}
	}
	

	private void xFXXX(int x, int nn) {
		switch(nn) {
		case 0x7:
			readDelayTimer(x);
			break;
		case 0x15:
			setDelayTimer(x);
			break;
		case 0x18:
			setSoundTimer(x);
			break;
		case 0x1E:
			addToIndex(x);
			break;
		case 0x0A:
			getKey(x);
			break;
		case 0x29:
			setFontSprite(x);
			break;
		case 0x33:
			binaryDecConvert(x);
			break;
		case 0x55:
			storeRegistersInMem(x);
			break;
		case 0x65:
			loadRegistersFromMem(x);
			break;
		}
		
	}

	private void x8XXX(int x, int y, int n) {
		switch(n) {
		case 0x0:
			setVxVy(x,y);
			break;
		case 0x1:
			binaryOr(x,y);
			break;
		case 0x2:
			binaryAnd(x,y);
			break;
		case 0x3:
			logicalXor(x,y);
			break;
		case 0x4:
			addRegisters(x,y);
			break;
		case 0x5:
			subtractRegister(x,y,0);
			break;
		case 0x6:
			shiftRight(x,y);
			break;
		case 0x7:
			subtractRegister(x,y,1);
			break;
		case 0xE:
			shiftLeft(x,y);
			break;
		}
		
	}

	private void binaryDecConvert(int x) {
		mem[I] = (char) (V[x] / 100);
		mem[I+1] = (char) ((V[x]%100)/10);
		mem[I+2] = (char) ((V[x]%100)%10);
	}

	private void setFontSprite(int x) {
		System.out.println("entering set font");
		System.out.println((int)V[x]);
		switch((int)V[x]){
		case 0x0:
			I = 0x50;
			break;
		case 0x1:
			I = 0x55;
			break;
		case 0x2:
			I = 0x5a;
			break;
		case 0x3:
			I = 0x5f;
			break;
		case 0x4:
			I = 0x64;
			break;
		case 0x5:
			I = 0x69;
			break;
		case 0x6:
			I = 0x6e;
			break;
		case 0x7:
			I = 0x73;
			break;
		case 0x8:
			I = 0x78;
			break;
		case 0x9:
			I = 0x7d;
			break;
		case 0xA:
			I = 0x82;
			break;
		case 0xB:
			I = 0x87;
			break;
		case 0xC:
			I = 0x8C;
			break;
		case 0xD:
			I = 0x91;
			break;
		case 0xE:
			I = 0x96;
			break;
		case 0xF:
			I = 0x9b;
			break;
		default:
			System.out.println("Error, charachter sprite not found");
			break;
		}
	}

	private void setSoundTimer(int x) {
		soundTimer = V[x];
		
	}

	private void readDelayTimer(int x) {
		V[x] = delayTimer;
		
	}

	private void getKey(int x) {
		if(keyboard.isKeyPressed()) {
			V[x] = keyboard.getKey();
		}
		else {
			pc -= 2;
		}
		
	}

	private void skipIfNotPressed(int x) {
		if(keyboard.getKey() != V[x]) {
			pc += 2;
		}
		
	}

	private void skipIfPressed(int x) {
		if((keyboard.getKey() == V[x])&& keyboard.isKeyPressed()) {
			pc += 2;
		}
		
	}

	private void shiftRight(int x, int y) {
		if(SHIFT_IN_PLACE) {
			if((V[x] & 1) == 1) {
				V[0xf] = 1;
			}
			else {
				V[0xf] = 0;
			}
			V[x] = (char) (((char) (V[x]>>>1)) & 0x00FF);
		}
		else {
			V[x] = V[y];
			if((V[x] & 1) == 1) {
				V[0xf] = 1;
			}
			else {
				V[0xf] = 0;
			}
			V[x] = (char) (((char) (V[x]>>>1)) & 0x00FF);
		}
		
	}

	private void shiftLeft(int x, int y) {
		if(SHIFT_IN_PLACE) {
			if((V[x] &128) == 128) {
				V[0xf] = 1;
			}
			else {
				V[0xf] = 0;
			}
			V[x] = (char) (((char) (V[x]<<1)) & 0x00FF);
		}
		else {
			V[x] = V[y];
			
			if((V[x] & 0x80) == 0x80) {
				V[0xf] = 1;
			}
			else {
				V[0xf] = 0;
			}
			V[x] = (char) (((char) (V[x]<<1)) & 0x00FF);
		}
		
	}

	private void logicalXor(int x, int y) {
		V[x] = (char) (V[x] ^ V[y]);
		V[x] = (char) (V[x] & 0x00FF);
		
	}

	private void binaryAnd(int x, int y) {
		V[x] = (char) (V[x] & V[y]);
		V[x] = (char) (V[x] & 0x00FF);
		
	}

	private void binaryOr(int x, int y) {
		V[x] = (char) (V[x] | V[y]);
		V[x] = (char) (V[x] & 0x00FF);
		
	}
	//add vf flag to this method
	private void subtractRegister(int x, int y, int cse) {
		if(cse == 0) {
			if(V[x] > V[y]) {
				V[0xf] = 1;
			}
			else {
				V[0xf] = 0;
			}
			V[x] = (char) (((V[x] - V[y]))&0xFF);
		}
		else {
			if(V[y] > V[x]) {
				V[0xf] = 1;
			}
			else {
				V[0xf] = 0;
			}
			V[x] = (char) ((V[y] - V[x])&0xFF);
		}
		
	}

	private void setVxVy(int x, int y) {
		V[x] = V[y];
		
	}

	private void setDelayTimer(int x) {
		delayTimer = V[x];
		
	}

	private void rand(int x, int nn) {
		int r = (int)(random.nextInt(0xFF));
		V[x] = (char) (r & nn);
	}

	private void equal(int i,int j) {
		if(i == j) {
			pc+=2;
		}
		
	}
	
	private void notEqual(int i, int j) {
		if(i!= j) {
			pc+= 2;
		}
	}

	private void loadRegistersFromMem(int x) {
		for(int i=0;i<=x;i++) {
			V[i] = mem[I + i];
			V[i] = (char) (V[i] & 0x00FF);
		}
		
	}

	private void storeRegistersInMem(int x) {
		for(int i=0;i<=x;i++) {
			mem[I +i] = V[i];
		}
		
	}

	private void addRegisters(int x, int y) {
		if(V[x] + V[y] >255) {
			V[0xf] = 1;
		}
		else {
			V[0xf] = 0;
		}
		V[x] = (char) ( (V[x] + V[y]) & 0x00FF);
		
	}

	private void addToIndex(int x) {
		
		if(I +V[x]> 0xFFF) {
			V[0xF] = 1;
		}
		else {
			V[0xF] = 0;
		}
		I = (I+ V[x]) & 0xFFF;
		
	}



	private void exitSubroutine() {
		pc = pop();
		
	}

	private void enterSubroutine(int nnn) {
		push(pc);
		jump(nnn);
		
	}

	//Needs some fixing up
	private void draw(int x, int y, int n) {
		int xCoord = V[x] % 64;
		int yCoord = V[y] % 32; 
		V[0xf] = 0x0;
		for(int j = yCoord; j < yCoord + n;j++) {
			if(j>=32) {
				break;
			}
			
			for(int i = xCoord; i< xCoord + 8;i++) {
				if(i>= 64) {
					break;
				}
				
				if((mem[I+(j-yCoord)] & (int)Math.pow(2, 7-(i-xCoord))) == (int)Math.pow(2, 7-(i-xCoord))) {
					if(monitor.flipPixel(i, j)) {
						V[0xf] = 1;
					}
				}
			}
		}
		monitor.updateScreen();
		
	}

	private void setIndexRegister(int nnn) {
		I = nnn;
		
	}

	private void addToRegister(int x, int nn) {
		V[x] += nn;
		V[x] = (char) (V[x] & 0x00FF);
		
	}

	private void setRegister(int x, int nn) {
		V[x] = (char)nn;
		V[x] = (char) (V[x] & 0xFF);
		
	}

	private void jump(int nnn) {
		pc = (char) nnn;
		
	}

	private void clearScreen() {
		monitor.clearScreen();
		
	}
	
	private void push(char val) {
		if(stackIndex == STACK_SIZE) {
			System.out.println("Stack Full");
		}
		else {
		stackIndex++;
		stack[stackIndex] = val;
		}
		
	}
	
	private void uknownCommand(int instr) {
		System.out.println("Uknown instruction " + Integer.toHexString(instr));
		running = false;
	}
	private char pop() {
		if(stackIndex!= -1) {
			stackIndex--;
			return stack[stackIndex+1];
		}
		return 0;
		
	}
	
	public void status() {
		System.out.println("V0: " + (int)V[0] + " V1: " + (int)V[1] + " V2: " + (int)V[2]+ " V3: " + (int)V[3]);
		System.out.println("V4: " + (int)V[4] + " V5: " +(int) V[5] + " V6: " + (int)V[6]+ " V7: " +(int) V[7]);
		System.out.println("V8: " + (int)V[8] + " V9: " +(int) V[9] + " VA: " + (int)V[0xA]+ " VB: " +(int) V[0xB]);
		System.out.println("VC: " + (int)V[0xc] + " VD: " +(int) V[0xD] + " VE: " +(int) V[0xE]+ " VF: " + (int)V[0xF]);
		System.out.println("I: " + (int)I + "  PC: " +(int) pc + " DelayTimer " + (int)delayTimer+ " soundTimer " + (int)soundTimer);
		System.out.println("MEM[I]: "+(int)mem[I]);
	}
	public static void main(String[] args) {
		Chip8 emu = new Chip8();
	}
	
	private void setFontSprites() {
		//0
		mem[0x50] = 0xF0;
		mem[0x51] = 0x90;
		mem[0x52] = 0x90;
		mem[0x53] = 0x90;
		mem[0x54] = 0xF0;
		//1
		mem[0x55] = 0x20;
		mem[0x56] = 0x60;
		mem[0x57] = 0x20;
		mem[0x58] = 0x20;
		mem[0x59] = 0x70;
		//2
		mem[0x5a] = 0xF0;
		mem[0x5b] = 0x10;
		mem[0x5c] = 0xF0;
		mem[0x5d] = 0x80;
		mem[0x5e] = 0xF0;
		//3
		mem[0x5f] = 0xF0;
		mem[0x60] = 0x10;
		mem[0x61] = 0xF0;
		mem[0x62] = 0x10;
		mem[0x63] = 0xF0;
		//4
		mem[0x64] = 0x90;
		mem[0x65] = 0x90;
		mem[0x66] = 0xF0;
		mem[0x67] = 0x10;
		mem[0x68] = 0x10;
		//5
		mem[0x69] = 0xF0;
		mem[0x6a] = 0x80;
		mem[0x6b] = 0xF0;
		mem[0x6c] = 0x10;
		mem[0x6d] = 0xF0;
		//6
		mem[0x6e] = 0xF0;
		mem[0x6f] = 0x80;
		mem[0x70] = 0xF0;
		mem[0x71] = 0x90;
		mem[0x72] = 0xF0;
		//7
		mem[0x73] = 0xF0;
		mem[0x74] = 0x10;
		mem[0x75] = 0x20;
		mem[0x76] = 0x40;
		mem[0x77] = 0x40;
		//8
		mem[0x78] = 0xF0;
		mem[0x79] = 0x90;
		mem[0x7a] = 0xF0;
		mem[0x7b] = 0x90;
		mem[0x7c] = 0xF0;
		//9
		mem[0x7d] = 0xF0;
		mem[0x7e] = 0x90;
		mem[0x7f] = 0xF0;
		mem[0x80] = 0x10;
		mem[0x81] = 0xF0;
		//A
		mem[0x82] = 0xF0;
		mem[0x83] = 0x90;
		mem[0x84] = 0xF0;
		mem[0x85] = 0x90;
		mem[0x86] = 0x90;
		//B
		mem[0x87] = 0xE0;
		mem[0x88] = 0x90;
		mem[0x89] = 0xE0;
		mem[0x8a] = 0x90;
		mem[0x8b] = 0xE0;
		//C
		mem[0x8c] = 0xF0;
		mem[0x8d] = 0x80;
		mem[0x8e] = 0x80;
		mem[0x8f] = 0x80;
		mem[0x90] = 0xF0;
		//D
		mem[0x91] = 0xE0;
		mem[0x92] = 0x90;
		mem[0x93] = 0x90;
		mem[0x94] = 0x90;
		mem[0x95] = 0xE0;
		//E
		mem[0x96] = 0xF0;
		mem[0x97] = 0x80;
		mem[0x98] = 0xF0;
		mem[0x99] = 0x80;
		mem[0x9a] = 0xF0;
		//F
		mem[0x9b] = 0xF0;
		mem[0x9c] = 0x80;
		mem[0x9d] = 0xF0;
		mem[0x9e] = 0x80;
		mem[0x9f] = 0x80;
	}
}
