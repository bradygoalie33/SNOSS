package cpu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import assembler.Assembler;
import assembler.Assembly;
import misc.FileIO;

public class CPU {

	private String filePath = new String("./Storage/");
	private int lastUsedMemByte = 0;
	private Scanner scanLee = new Scanner(System.in);
	private Map<String, Register> registers = new HashMap<String, Register>();
	private RAM memory;
	private static CPU cpu;
	private static FileIO fileIO;
	private Map<String, Integer> programPCBs = new HashMap<String, Integer>();
	private boolean execI = false;
	int instructionPointer = 0;
	private final int PCB_SIZE = 10;
	private final int STACK_SIZE = 54;
	

	public static void main(String args[]) {
		fileIO = new FileIO();
		// Assembler assembler = new Assembler();
		// fileIO.loadFile(fileToLoad)
		// assembly.translateProgram();

		cpu = new CPU();
		cpu.initRegisters();
		cpu.initRAM();

		// cpu.initFile();

		// cpu.testRegisters();
		// cpu.testRAM();
		// cpu.loadProgramIntoMemory("TEST");

		cpu.runConsole();

	}
	

	private void loadProgramIntoMemory(String programName) {
		programName = programName.replace(".txt", "");
		int startMem = lastUsedMemByte;
		try {
			byte[] temp = Files.readAllBytes(Paths.get(filePath + programName + ".sno"));
			
			for (int i = lastUsedMemByte; i < temp.length; i++) {
				memory.storeInstructionInMemory(lastUsedMemByte, temp[i]);
				lastUsedMemByte++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		allocatePCB(lastUsedMemByte, programName, startMem);
		runProgram(programName);
	}
	
	private void unloadProgram(String programName) {

		int pcbStart = programPCBs.get(programName);
		int programstart = memory.getFromMemory(pcbStart);

		for (int i = programstart; i < pcbStart; i++) {
			memory.storeInMemory(i, 0);
		}

	}

	private void runProgram(String programName) {
		instructionPointer = memory.getInstructionFromMemory(programPCBs.get(programName));
//		System.out.println("INSTRUCTION: " + memory.getFromMemory(0));
		while (instructionPointer < (programPCBs.get(programName))) {
			int commandType = memory.getInstructionFromMemory(instructionPointer);
//			System.out.println("COMMAND: " + commandType);
			
			if (execI) {
				System.out.print("COMMAND TYPE: " + commandType);
				System.out.println(" PROCESS: " + 1);
			}

			instructionPointer++;
			grabFullCommand(commandType, programName);		
//			grabFullCommand(programPCBs.get(programName), programName);
		}

	}

	
	private void grabFullCommand(int command, String programName) {
		int memStart = programPCBs.get(programName) + PCB_SIZE;
		switch (command) {
		case 1: // Load
			int destRegister = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			int memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			if ((memStart + memStoreIn) < programPCBs.get(programName)
					|| (memStart + memStoreIn) > (programPCBs.get(programName) + PCB_SIZE + STACK_SIZE)) {
				coreDump(programName, (memStart + memStoreIn), "LOAD");
			}
			int data = memory.getFromMemory((memStart + memStoreIn));
			registers.get("R" + destRegister).write(data);
			break;
		case 2: // Store
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			int register = memory.getInstructionFromMemory(instructionPointer) + 1;
			instructionPointer++;
			int valueToStore = binaryToInt(registers.get("R" + register).read());
			System.out.println("STORING IN: " + (memStoreIn + memStart) + " || VALUE: " + programPCBs.get(programName));
			if ((memStart + memStoreIn) < programPCBs.get(programName)
					|| (memStart + memStoreIn) > ((programPCBs.get(programName) + PCB_SIZE + STACK_SIZE))) {
				coreDump(programName, (memStart + memStoreIn), "STORE");
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
			instructionPointer = (memStart + memStoreIn);
			break;
		case 9: // Cprint
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer += 2;
			if ((memStart + memStoreIn) < programPCBs.get(programName)
					|| (memStart + memStoreIn) > (programPCBs.get(programName) + PCB_SIZE + STACK_SIZE)) {
				coreDump(programName, (memStart + memStoreIn), "CPRINT");
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
			if ((memStart + memStoreIn) < programPCBs.get(programName)
					|| (memStart + memStoreIn) > (programPCBs.get(programName) + PCB_SIZE + STACK_SIZE)) {
				coreDump(programName, (memStart + memStoreIn), "CREAD");
			}
			memory.storeInMemory((memStart + memStoreIn), c);
			instructionPointer++;
			break;
		case 17: // Exit
			unloadProgram(programName);
			break;
		}

	}

	private int binaryToInt(int binary) {
		int binaryVal = Integer.parseInt("" + binary);
		return binaryVal;
	}

	private void allocatePCB(int hexValue, String programName, int startMem) {
		int allocated = hexValue + PCB_SIZE;
		lastUsedMemByte = allocated;
		programPCBs.put(programName, hexValue);
		memory.storeInMemory(hexValue, startMem);
	}

	private void coreDump(String programName, int memAccess, String command) {
		System.err.println("You've crashed the system");

		fileIO.appendToFile("You've broken the program at pointer:" + instructionPointer + "\nYou were trying to access: "
				+ memAccess + "\nYou were running the command: " + command,
				programName + "DumpFile.DUMP");

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

	private void runConsole() {
		while (true) {
			String input = scanLee.nextLine();
			String[] firstLine = input.split(" ");

			switch (firstLine[0]) {

			case "ls":
				filesInDirectory();
				break;
			case "ps":
				System.out.println("0");
				break;
			case "exec":
				Assembler assembler = new Assembler();
				Path path = Paths.get(filePath + firstLine[1]);
				try {
					assembler.processFile(path.toFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				cpu.loadProgramIntoMemory(firstLine[1]);
				break;
			case "exec_i":
				execI = true;

				Assembler assembler2 = new Assembler();
				Path path2 = Paths.get(filePath + firstLine[1]);
				try {
					assembler2.processFile(path2.toFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
				cpu.loadProgramIntoMemory(firstLine[1]);
				execI = false;
				break;
			case "kill":
				unloadProgram(firstLine[1]);
				break;
			case "exit":
				System.exit(0);
				break;
			}

		}
	}

	private void filesInDirectory() {
		File folder = new File("./Storage");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile() && file.toString().contains(".txt")) {
				System.out.println(file.getName());
			}
		}
	}

}
