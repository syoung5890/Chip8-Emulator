 package emulator;

public class Chip8 {
	
	//constants
	static final int MEM_SIZE = 0x1000;
	static final int NUM_REGISTERS = 0x10;
	static final int STACK_SIZE = 0x10;
	
	//memory 
	private char[] mem;
	private char[] V;
	private char[] stack;
	
	private int I;
	private char pc;
	private char delayTimer;
	private char soundTimer;
	private boolean running;
	
	private Monitor monitor;
	private RomReader reader;
	
	public Chip8() {
		mem = new char[MEM_SIZE];
		V = new char[NUM_REGISTERS];
		stack = new char[STACK_SIZE];
		running = true;
		monitor = new Monitor();
		reader = new RomReader(".\\src\\Roms\\IBMLogo.ch8");
		pc = 0x200;
		I = 0;
		loadMemory();
		run();
	}
	
	private void loadMemory() {
		byte[] rom = reader.getMemoryArray();
		for(int i = 0;i<rom.length;i++) {
			mem[0x200+i] = (char)Byte.toUnsignedInt(rom[i]);
			System.out.println(Integer.toHexString((int)mem[0x200+i]));
		}
	}
	
	private void setSprites() {
		
	}
	
	public void decrementTimer() {
		if(soundTimer != 0) {
			soundTimer--;
		}
	}
	
	public void run() {
		while(running) {
			System.out.println((int)pc);
			int instr = fetch();
			execute(instr);
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
			clearScreen();
			break;
		case 0x1:
			//jump(nnn);
			break;
		case 0x6:
			setRegister(x,nn);
			break;
		case 0x7:
			addToRegister(x,nn);
			break;
		case 0xA:
			setIndexRegister(nnn);
			break;
		case 0xD:
			draw(x,y,n);
			break;
		default:
			System.out.println("Uknown instruction " + Integer.toHexString(instr));
			running = false;
			break;
		}
	}
	
	//Needs some fixing up
	private void draw(int x, int y, int n) {
		System.out.println(I);
		int xCoord = V[x] % 64;
		int yCoord = V[y] % 32;
		V[0xf] = 0x0;
		for(int j = yCoord; j < yCoord + n;j++) {
			for(int i = xCoord; i<= xCoord + 8;i++) {
				if((mem[I+(j-yCoord)] & (int)Math.pow(2, 8-(i-xCoord))) == Math.pow(2, 8-(i-xCoord))) {
					System.out.println("flipping pixel");
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
		
	}

	private void setRegister(int x, int nn) {
		V[x] = (char)nn;
		
	}

	private void jump(int nnn) {
		pc = (char) nnn;
		
	}

	private void clearScreen() {
		monitor.clearScreen();
		
	}
	
	public static void main(String[] args) {
		Chip8 emu = new Chip8();
	}
}
