//package cpu;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class SethsCpu {
//	RAM ram;
//	Register[] registers = new Register[6];
//	int instructionPointer = 0;
//	public Cpu(){
//		for(int i = 0; i < 6; i++){
//			registers[i] = new Register();
//		}
//	}
//	int i = 0;
//	
//	
//
//	public void doProgram(int pcbStart, boolean info){
//		int pId = ram.memory[pcbStart];
//		int start = byteArrayToInt(ram.memory[pcbStart + 1], ram.memory[pcbStart + 2]);
//		int end = byteArrayToInt(ram.memory[pcbStart + 3], ram.memory[pcbStart + 4]);
//		byte[] temp = new byte[4];
//		for(int i = start; i <= end; i++){
//			temp[0] = ram.memory[i];
//			temp[1] = ram.memory[i + 1];
//			temp[2] = ram.memory[i + 2];
//			temp[3] = ram.memory[i + 3];
//			i = i + 3;
//			processCommand(temp, start, end, info);
//			instructionPointer = i;
//		}
//	}
//	
//	public void processCommand(byte[] command, int pcbStart, int pcbEnd, boolean info){
//		switch(command[0]){
//		case 1:
//			load(command, pcbEnd);
//			break;
//		case 10:
//			loadc(command);
//			break;
//		case 2:
//			store(command, pcbEnd);
//			break;
//		case 3:
//			add(command);
//			break;
//		case 4:
//			sub(command);
//			break;
//		case 5:
//			mul(command);
//			break;
//		case 6:
//			div(command);
//			break;
//		case 7:
//			eq(command);
//			break;
//		case 8:
//			mygoto(command);
//			break;
//		case 11:
//			gotoif(command);
//			break;
//		case 9:
//			cprint(command, pcbEnd);
//			break;
//		case 16:
//			cread(command, pcbEnd);
//			break;
//		case 17:
//			exit(command, pcbStart, pcbEnd);
//			break;
//		}
//		if(info){
//			System.out.println("Instruction: " + command[0]);
//		}
//	}
//
//	public void load(byte[] command, int end) {
//		registers[command[1]].write(ram.read((byteArrayToInt(command[2],command[3]) + (end + 5))));
//	}
//
//	public void loadc(byte[] command) {
//		registers[command[1]].write(byteArrayToInt(command[2],command[3]));
//	}
//
//	public void store(byte[] command, int end) {
//		int memAddress = (byteArrayToInt(command[1],command[2]) + (end + 5));
//		if(memAddress > end + 4 && memAddress < end + 64){
//			ram.store(memAddress, registers[command[3]].read());
//		}
//		else{
//			System.out.println("CORE DUMP YA NERD");
//			writeToFile(ram,this,instructionPointer);
//			System.exit(0);
//		}
//	}
//
//	public void add(byte[] command) {
//		int x = registers[command[2]].read();
//		int y = registers[command[3]].read();
//		registers[command[1]].write(( x+y ));
//	}
//
//	public void sub(byte[] command) {
//		int x = registers[command[2]].read();
//		int y = registers[command[3]].read();
//		registers[command[1]].write(( x-y ));
//	}
//
//	public void mul(byte[] command) {
//		int x = registers[command[2]].read();
//		int y = registers[command[3]].read();
//		registers[command[1]].write(( x*y ));
//	}
//
//	public void div(byte[] command) {
//		int x = registers[command[2]].read();
//		int y = registers[command[3]].read();
//		registers[command[1]].write(( x/y ));
//	}
//
//	public void eq(byte[] command) {
//		if(registers[command[2]].read() == registers[command[3]].read() ){
//			registers[command[1]].write(1);
//		}
//		else{
//			registers[command[1]].write(0);
//		}
//	}
//
//	public void mygoto(byte[] command) {
//		instructionPointer = byteArrayToInt(command[1], command[2]);
//	}
//
//	public void gotoif(byte[] command) {
//		if(registers[command[3]].read() != 0){
//			instructionPointer = byteArrayToInt(command[1], command[2]);
//		}
//	}
//
//	public void cprint(byte[] command, int end) {
//		System.out.println("CPRINT: "  + ram.read((byteArrayToInt(command[1],command[2]) + (end + 5))));
//	}
//
//	public void cread(byte[] command, int end) {
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("Input: ");
//        try {
//			int charInt = br.read();
//			int memAddress = (byteArrayToInt(command[1],command[2]) + (end + 5));
//			if(memAddress > end + 4 && memAddress < end + 64){
//				ram.store(memAddress, charInt);
//			}
//			else{
//				System.out.println("CORE DUMP YA NERD");
//				writeToFile(ram,this,instructionPointer);
//				System.exit(0);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void exit(byte[] command, int start, int end) {
//		for(int i = start; i < end + 64; i++){
//			ram.memory[i] = (byte)0;
//		}
//	}
//	
//	public void putRam(RAM ram){
//		this.ram = ram;
//	}
//	
//	private int byteArrayToInt(byte x, byte y){
//		int first = x;
//		int second = y;
//		String firstString = addZeros(Integer.toBinaryString(first & 0xff));
//		String secondString = addZeros(Integer.toBinaryString(second & 0xff));
//		String finalString = firstString + secondString;
//		
//		return Integer.parseInt(finalString, 2);
//	}
//	
//	private String addZeros(String thing){
//		String emptyString = "";
//		for(int i = 0; i < 8 - thing.length(); i++){
//			emptyString += 0;
//		}
//		return emptyString + thing;
//	}
//	
//	private void writeToFile(RAM ram, Cpu cpu, int pointer){
//		
//		File snoFile = new File("/Users/sn255043/Documents/snossMem/CoreDump.txt");
//
//		try {
//			FileOutputStream fos = new FileOutputStream(snoFile);
//			fos.write(ram.memory);
//			for(Register reg : cpu.registers){
//				fos.write(reg.memory);
//			}
//			fos.write(pointer);
//			fos.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//}
