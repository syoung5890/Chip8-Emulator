 package emulator;

public class Chip8 {
	
	//constants
	static final int MEM_SIZE = 0x1000;
	static final int NUM_REGISTERS = 0x10;
	static final int STACK_SIZE = 0x10;
	static final int INSTRUCTS_PER_SEC = 733;
	
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
	

	
	public void decrementTimer() {
		if(soundTimer != 0) {
			soundTimer--;
		}
		if(delayTimer != 0) {
			delayTimer--;
		}
	}
	
	public void run() {
		long time = 0;
		long time2 = 0;
		long timer_delay = (1/60) * 1000;
		long delay = (1/INSTRUCTS_PER_SEC) * 1000000000;
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
