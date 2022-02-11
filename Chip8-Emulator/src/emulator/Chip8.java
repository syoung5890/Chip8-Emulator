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
		int instruction = fetch();
		execute(instruction);
	}
	
	private int fetch() {
		int instruction;
		instruction = (int)mem[pc];
		instruction = (instruction<<8) + (int)mem[pc+1];
		pc += 2;
		return instruction;
	}
	
	private void execute(int instruction) {
		//rename this variable
		String s = Integer.toHexString(instruction);
		
		switch(s.charAt(0)) {
		case '0':
			clearScreen();
			break;
		case '1':
			jump();
			break;
		case '6':
			setRegister();
			break;
		case '7':
			addToRegister();
			break;
		case 'a':
			setIndexRegister();
			break;
		case 'd':
			draw();
			break;
		default:
			break;
		}
	}
}
