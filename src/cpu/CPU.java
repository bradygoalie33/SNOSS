package cpu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
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
	public Map<Integer, Integer> programPCBs = new HashMap<Integer, Integer>();
	private Map<Integer,String> programNames = new HashMap<Integer,String>();
	public ArrayDeque<Integer> processes = new ArrayDeque<Integer>();
	public Stack<Integer> execQue = new Stack<Integer>();
	public boolean execI = false;
	int instructionPointer = 0;
	private final int PCB_SIZE = 20;
	private final int STACK_SIZE = 44;
	public static int pId = 1;
	public int loggingLevel = 0;
	public int sleepTime = 1000;




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
		try {
			byte[] temp = Files.readAllBytes(Paths.get(filePath + programName + ".sno"));

			int loopingMemByte = lastUsedMemByte;
			for (int i = lastUsedMemByte; i < (temp.length + loopingMemByte); i++) {
				memory.storeInstructionInMemory(lastUsedMemByte, temp[i - loopingMemByte]);
				lastUsedMemByte++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		allocatePCB(lastUsedMemByte, programName, startMem, pId);
		pId++;
	}

	public void processController(String programName){
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(processes.size() > 0){
			int top = processes.pollFirst();
			if(programPCBs.get(top) != null) {
				if((memory.getInstructionFromMemory(programPCBs.get(top) + 5)) == 0){
					swapToWaiting(top);
					runProgram(top);
					if(loggingLevel > 0){
						System.out.println("Time slice: " + top);
						printRegisters(String.valueOf(top));
					}
				}
				else {
					swapToWaiting(top);
					runProgram(top);
				}
			}
		}
		if(execQue.size() > 0){

			int i = (int) execQue.pop();
			if(i != 0){
				loadProgramIntoMemory(programName);
				processes.add(i);
			}
		}
		if(processes.size() > 0) {
			processController(programName);
		}

	}

	private void swapToWaiting(int currentProcess){
		memory.storeInstructionInMemory(programPCBs.get(currentProcess) + 5, (byte)1);
		for(int i : programPCBs.keySet()){
			if(i != currentProcess){
				memory.storeInstructionInMemory(programPCBs.get(i) + 5, (byte)0);
			}
		}
	}
	private void runProgram(Integer pId) {
		instructionPointer = memory.getFromMemory(programPCBs.get(pId) + 6);
		registers.get("R1").write(memory.getFromMemory(programPCBs.get(pId) + 8));
		registers.get("R2").write(memory.getFromMemory(programPCBs.get(pId) + 10));
		registers.get("R3").write(memory.getFromMemory(programPCBs.get(pId) + 12));
		registers.get("R4").write(memory.getFromMemory(programPCBs.get(pId) + 14));
		registers.get("R5").write(memory.getFromMemory(programPCBs.get(pId) + 16));
		registers.get("R6").write(memory.getFromMemory(programPCBs.get(pId) + 18));
		int counter = 0;
		while (counter < 5 && instructionPointer < (programPCBs.get(pId))) {
			int commandType = memory.getInstructionFromMemory(instructionPointer);
			if(loggingLevel > 0){
				System.out.println("Instruction: " + instructionPointer + " Command: " + commandType + " PID: " + pId);
			}
			if (execI) {
				System.out.print("COMMAND TYPE: " + commandType);
				System.out.println(" PROCESS: " + 1);
				for(int i = 1; i < registers.size() + 1; i++) {
					System.out.println(registers.get("R" + i).printRegister());
				}
			}
			counter++;
			instructionPointer++;
			boolean returnedBool = grabFullCommand(commandType, pId);
			if(returnedBool) {
				break;
			}
		}
		if(programPCBs.get(pId) != null) {
			memory.storeInMemory((programPCBs.get(pId) + 6), instructionPointer);
			memory.storeInMemory(programPCBs.get(pId) + 8, registers.get("R1").read());
			memory.storeInMemory(programPCBs.get(pId) + 10, registers.get("R2").read());
			memory.storeInMemory(programPCBs.get(pId) + 12, registers.get("R3").read());
			memory.storeInMemory(programPCBs.get(pId) + 14, registers.get("R4").read());
			memory.storeInMemory(programPCBs.get(pId) + 16, registers.get("R5").read());
			memory.storeInMemory(programPCBs.get(pId) + 18, registers.get("R6").read());
			processes.add(pId);
		}
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
			//			if ((memStart + memStoreIn) < programPCBs.get(pId)
			//					|| (memStart + memStoreIn) > ((programPCBs.get(pId) + PCB_SIZE + STACK_SIZE))) {
			//				coreDump(pId, (memStart + memStoreIn), "STORE");
			//			}
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
			int programStart = memory.getFromMemory(programPCBs.get(pId) + 1);
			//			System.out.println("GOTO: " + (programStart + memStoreIn) + "|| pStart: " + programStart);
			instructionPointer = (programStart + memStoreIn);
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
		programPCBs.put(pId, firstOpenByte);
		programNames.put(pId, programName);
		int pcbIncrementer = firstOpenByte;
		memory.storeInstructionInMemory(pcbIncrementer, (byte)pId);
		memory.storeInMemory(pcbIncrementer+=1, startMem);
		memory.storeInMemory(pcbIncrementer+=2, lastUsedMemByte - 1);
		memory.storeInstructionInMemory(pcbIncrementer+=1, (byte)0);
		memory.storeInMemory(pcbIncrementer+=2, startMem);
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R1").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R2").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R3").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R4").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R5").read());
		memory.storeInMemory(pcbIncrementer+=2, registers.get("R6").read());		
	}

	public void unloadProgram(int pId) {
		int pcbStart = programPCBs.get(pId);
		int programstart = memory.getFromMemory(pcbStart + 1);
		programPCBs.remove(pId);
		processes.remove(pId);
		programNames.remove(pId);
		for (int i = programstart; i < pcbStart + 19; i++) {
			memory.storeInstructionInMemory(i, (byte)0);
		}
		lastUsedMemByte = programstart;
	}	

	public void printProcessInfo() {
//		int processID = Integer.valueOf(pId);
//		int memStart = programPCBs.get(processID);
		if(programPCBs.size() > 0){
			for(int i : programPCBs.keySet()){
				System.out.println("Process ID: " + i);
				System.out.println("Process Name: "  + programNames.get(i));
				System.out.println("Process State: " + memory.getInstructionFromMemory(programPCBs.get(i) + 5));
			}
		}
		else{
			System.out.println("No Processes Loaded");
		}
	}

	private void printRegisters(String pId){
		int processID = Integer.valueOf(pId);
		int r1 = (memory.getFromMemory(programPCBs.get(processID) + 8));
		int r2 = (memory.getFromMemory(programPCBs.get(processID) + 10));
		int r3 = (memory.getFromMemory(programPCBs.get(processID) + 12));
		int r4 = (memory.getFromMemory(programPCBs.get(processID) + 14));
		int r5 = (memory.getFromMemory(programPCBs.get(processID) + 16));
		int r6 = (memory.getFromMemory(programPCBs.get(processID) + 18));
		System.out.println("Register 1: " + r1);
		System.out.println("Register 2: " + r2);
		System.out.println("Register 3: " + r3);
		System.out.println("Register 4: " + r4);
		System.out.println("Register 5: " + r5);
		System.out.println("Register 6: " + r6);
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
