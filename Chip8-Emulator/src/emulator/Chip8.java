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
	
	public Chip8() {
		mem = new char[MEM_SIZE];
		V = new char[NUM_REGISTERS];
		stack = new char[STACK_SIZE];
	}
	
	public void decrementTimer() {
		if(soundTimer != 0) {
			soundTimer--;
		}
	}
	
	public void run() {
		int instr = fetch();
		execute(instr);
	}
	
	private int fetch() {
		int instr;
		instr = (int)mem[pc];
		instr = (instr<<8) + (int)mem[pc+1];
		pc += 2;
		return instr;
	}
	
	private void execute(int instr) {
		//rename this variable
		int lead = (instr - (instr % 0x1000)) / 0x1000;
		int x = ((instr - (lead * 0x1000)) - (instr %0x100))/0x100;
		int y = ((instr -(lead*0x1000) - (x * 0x100)) - (instr % 0x10))/0x10;
		int n = instr - (lead *0x1000) - (x*0x100) - (y * 0x10);
		int nn = instr % 0x100;
		int nnn = instr % 0x1000;
		String s = Integer.toHexString(instr);
		
		switch(lead)) {
		case 0x0:
			clearScreen();
			break;
		case 0x1:
			jump();
			break;
		case 0x6:
			setRegister();
			break;
		case 0x7:
			addToRegister();
			break;
		case 0xA:
			setIndexRegister();
			break;
		case 0xD:
			draw();
			break;
		default:
			break;
		}
	}
}
