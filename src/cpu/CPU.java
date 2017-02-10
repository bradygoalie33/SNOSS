package cpu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import misc.FileIO;
import misc.Shell;

public class CPU {

	private String filePath = new String("./Storage/");
	private int lastUsedMemByte = 0;
	private Scanner scanLee = new Scanner(System.in);
	private Map<String, Register> registers = new HashMap<String, Register>();
	private RAM memory;
	private static CPU cpu;
	private static FileIO fileIO;
	private Map<Integer, Integer> programPCBs = new HashMap<Integer, Integer>();
	private Map<Integer,String> programNames = new HashMap<Integer,String>();
	private Queue<Integer> processes = new LinkedList<Integer>();
	public Stack<Integer> execQue = new Stack<Integer>();
	public boolean execI = false;
	int instructionPointer = 0;
	private final int PCB_SIZE = 20;
	private final int STACK_SIZE = 44;
	public static int pId = 1;
	public int loggingLevel = 0;


	public static void main(String args[]) {
		fileIO = new FileIO();
		cpu = new CPU();
		cpu.initRegisters();
		cpu.initRAM();
		
		Shell shell = new Shell(cpu);
		try {
			shell.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void loadProgramIntoMemory(String programName) {
		programName = programName.replace(".txt", "");
		int startMem = lastUsedMemByte;
		System.out.println("LAST USED: " + lastUsedMemByte);
		try {
			byte[] temp = Files.readAllBytes(Paths.get(filePath + programName + ".sno"));

//			System.out.println("LENGTH: " + temp.length);
			for (int i = lastUsedMemByte; i < temp.length; i++) {
				memory.storeInstructionInMemory(lastUsedMemByte, temp[i]);
				lastUsedMemByte++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		allocatePCB(lastUsedMemByte, programName, startMem, pId);
		//processes.add(pId);
//		execQue.push(pId);
		pId++;
		processController(programName);
	}

	public void processController(String programName){
//		System.out.println("PROCESS CONTROLLER");
		if(processes.size() > 0){
			int top = processes.poll();
//			System.out.println("TOP: " + top);
			if(programPCBs.get(top) != null) {
				if((memory.getInstructionFromMemory(programPCBs.get(top) + 5)) == 0){
//					System.out.println("RUN: " + top);
					runProgram(top);
				}
			}
		}
		if(execQue.size() > 0){
			int i = (int) execQue.pop();
			if(i != 0){
//				System.out.println("ADDING TO PROCESS");
				loadProgramIntoMemory(programName);
				processes.add(i);
				processController(programName);
			}
		}
		if(processes.size() > 0) {
//			System.out.println("GREATER THAN ZERO");
			processController(programName);
		}

	}

	private void runProgram(Integer pId) {
//		System.out.println("RETRIEVE: " + programPCBs.get(pId) + 6);
		instructionPointer = memory.getFromMemory(programPCBs.get(pId) + 6);
		int timeSlice = instructionPointer + 20;
//		System.out.println("INSTRUCTION: " + instructionPointer);
		
		while (instructionPointer < timeSlice && instructionPointer < (programPCBs.get(pId) + 3)) {
			int commandType = memory.getInstructionFromMemory(instructionPointer);
			if (execI) {
				System.out.print("COMMAND TYPE: " + commandType);
				System.out.println(" PROCESS: " + 1);
				for(int i = 1; i < registers.size() + 1; i++) {
					System.out.println(registers.get("R" + i).printRegister());
				}
			}

			instructionPointer++;
			processes.add(pId);
			boolean returnedBool = grabFullCommand(commandType, pId);
			if(returnedBool) {
				break;
			}
			
		}
		if(programPCBs.get(pId) != null) {
			memory.storeInMemory((programPCBs.get(pId) + 6), instructionPointer);
		}
		
//		System.out.println("ENDING INSTRUCTION: " + instructionPointer);
//		System.out.println("MEMORY: " + memory.getFromMemory((programPCBs.get(pId) + 6)));
	}

	private boolean grabFullCommand(int command, Integer pId) {
		boolean returnBool = false;
		int memStart = programPCBs.get(pId) + PCB_SIZE;
		switch (command) {
		case 1: // Load
			int destRegister = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			int memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			if ((memStart + memStoreIn) < programPCBs.get(pId)
					|| (memStart + memStoreIn) > (programPCBs.get(pId) + PCB_SIZE + STACK_SIZE)) {
				coreDump(pId, (memStart + memStoreIn), "LOAD");
			}
			int data = memory.getFromMemory((memStart + memStoreIn));
			registers.get("R" + destRegister).write(data);
			break;
		case 2: // Store
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			int register = memory.getInstructionFromMemory(instructionPointer) + 1;
//			System.out.println("REG: " + register);
			instructionPointer++;
			int valueToStore = binaryToInt(registers.get("R" + register).read());
			if ((memStart + memStoreIn) < programPCBs.get(pId)
					|| (memStart + memStoreIn) > ((programPCBs.get(pId) + PCB_SIZE + STACK_SIZE))) {
				coreDump(pId, (memStart + memStoreIn), "STORE");
			}
			memory.storeInMemory((memStart + memStoreIn), valueToStore);

			break;
		case 3: // Add
			int register1 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			int register2 = memory.getInstructionFromMemory(instructionPointer) + 1;
			int registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			int register3 = memory.getInstructionFromMemory(instructionPointer) + 1;
			int registerValue3 = binaryToInt(registers.get("R" + register3).read());
			instructionPointer++;
			registers.get("R" + register1).write(registerValue2 + registerValue3);
			break;
		case 4: // Sub
			register1 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			register2 = memory.getInstructionFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getInstructionFromMemory(instructionPointer) + 1;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			instructionPointer++;

			int subtractedValue = registerValue2 - registerValue3;
			registers.get("R" + register1).write(subtractedValue);
			break;
		case 5: // Mul
			register1 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			register2 = memory.getInstructionFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			registers.get("R" + register1).write(registerValue2 * registerValue3);
			break;
		case 6: // Div
			register1 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			register2 = memory.getInstructionFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			registers.get("R" + register1).write(registerValue2 / registerValue3);
			break;
		case 7: // Eq
			register1 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			register2 = memory.getInstructionFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			if (registerValue2 == registerValue3) {
				registers.get("R" + register1).write(1);
			} else {
				registers.get("R" + register1).write(0);
			}
			break;
		case 8: // Goto
			memStoreIn = memory.getFromMemory(instructionPointer);
			//System.out.println("GOTO: " + (memStart + memStoreIn) + "|| INSTRUCTION POINTER: " + instructionPointer);
			instructionPointer = (memStoreIn);
			break;
		case 9: // Cprint
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			if ((memStart + memStoreIn) < programPCBs.get(pId)
					|| (memStart + memStoreIn) > (programPCBs.get(pId) + PCB_SIZE + STACK_SIZE)) {
				coreDump(pId, (memStart + memStoreIn), "CPRINT");
			}
			int toPrint = memory.getFromMemory(memStart + memStoreIn);
			System.out.println(toPrint);
			instructionPointer++;
			break;
		case 10: // Loadc
			int registerValue = memory.getInstructionFromMemory(instructionPointer) + 1;
			Register r = registers.get("R" + registerValue);
			instructionPointer++;
			r.write(memory.getFromMemory(instructionPointer));
			instructionPointer += 2;
			break;
		case 11: // Goto If
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			registerValue = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
			if (binaryToInt(registers.get("R" + registerValue).read()) == 0) {
				instructionPointer = (memStart + memStoreIn);
			}
			instructionPointer++;

			break;
		case 16: // Cread
			char c = scanLee.next().charAt(0);
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			if ((memStart + memStoreIn) < programPCBs.get(pId)
					|| (memStart + memStoreIn) > (programPCBs.get(pId) + PCB_SIZE + STACK_SIZE)) {
				coreDump(pId, (memStart + memStoreIn), "CREAD");
			}
			memory.storeInMemory((memStart + memStoreIn), c);
			instructionPointer++;
			break;
		case 17: // Exit
			System.out.println("EXIT");
			unloadProgram(pId);
			returnBool = true;
			break;
		}
		return returnBool;
	}

	private int binaryToInt(int binary) {
		int binaryVal = Integer.parseInt("" + binary);
		return binaryVal;
	}

	private void allocatePCB(int firstOpenByte, String programName, int startMem, int pId) {
		lastUsedMemByte = firstOpenByte + PCB_SIZE + STACK_SIZE;
		System.out.println("FIRST: " + firstOpenByte + "|| PID: " + pId + "|| START: " + startMem);
		programPCBs.put(pId, firstOpenByte);
		programNames.put(pId, programName);
		int pcbIncrementer = firstOpenByte;
		memory.storeInstructionInMemory(pcbIncrementer, (byte)pId);
		memory.storeInMemory(pcbIncrementer+=1, startMem);
		memory.storeInMemory(pcbIncrementer+=2, firstOpenByte - 1);
		memory.storeInstructionInMemory(pcbIncrementer+=1, (byte)0);
		memory.storeInMemory(pcbIncrementer+=2, startMem);
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R1").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R2").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R3").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R4").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R5").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R6").read());		
	}

	public void unloadProgram(Integer pId) {
		int pcbStart = programPCBs.get(pId);
		int programstart = memory.getFromMemory(pcbStart + 1);
		programPCBs.remove(pId);
		processes.remove(pId);
		programNames.remove(pId);
		for (int i = programstart; i < pcbStart + 19; i++) {
			memory.storeInstructionInMemory(i, (byte)0);
		}
//		System.out.println("PROGRAM START: " + programstart);
		lastUsedMemByte = programstart;
	}	

	private void coreDump(Integer pId, int memAccess, String command) {
		System.err.println("You've crashed the system");

		fileIO.appendToFile("You've broken the program at pointer:" + instructionPointer + "\nYou were trying to access: "
				+ memAccess + "\nYou were running the command: " + command,
				programNames.get(pId) + "DumpFile.DUMP");

		System.exit(0);
	}

	@SuppressWarnings("unused")
	private void testRegisters() {
		Register r1 = registers.get("R1");
		r1.write(2);
		Register r2 = registers.get("R2");
		r2.write(4);
		r2.write(255);

		String testHex = "0x0000000f";
	}

	@SuppressWarnings("unused")
	private void testRAM() {
	}

	private void initRegisters() {

		registers.put("R1", new Register());
		registers.put("R2", new Register());
		registers.put("R3", new Register());
		registers.put("R4", new Register());
		registers.put("R5", new Register());
		registers.put("R6", new Register());
	}

	private void initRAM() {
		memory = new RAM();

	}

}
