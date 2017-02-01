package cpu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import assembler.Assembly;
import misc.FileIO;

public class CPU {

	private int lastUsedMemByte = 0;
	private Scanner scanLee = new Scanner(System.in);
	private Map<String, Register> registers = new HashMap<String, Register>();
	private RAM memory;
	private static CPU cpu;
	private static FileIO fileIO;
	private Map<String, Integer> programPCBs = new HashMap<String, Integer>();
	private boolean execI = false;
	int instructionPointer = 0;

	public static void main(String args[]) {
		fileIO = new FileIO();
//		Assembly assembly = new Assembly();
//		assembly.translateProgram();


		cpu = new CPU();
		cpu.initRegisters();
		cpu.initRAM();

		// cpu.initFile();

		// cpu.testRegisters();
		// cpu.testRAM();
//		cpu.loadProgramIntoMemory("TEST");

		cpu.runConsole();

	}
	
	private void loadProgramIntoMemory(String programName) {
		programName = programName.replace(".txt", "");
		String[] fileToLoad = fileIO.loadFile(programName + ".sno");
		String instructionSet = fileToLoad[0];
		String testString = "";
		ArrayList<Object> test = new ArrayList<>();

		for (int i = 0; i < instructionSet.length(); i++) {
			testString += instructionSet.charAt(i);
			if (i + 2 > instructionSet.length() - 1) {
				// System.out.println("BREAK");
				break;
			} else if (instructionSet.charAt(i + 2) == 'x' || instructionSet.charAt(i + 2) == 'X') {
				test.add(testString);
				testString = "";
				
			}
		}
		testString += instructionSet.charAt(instructionSet.length() - 1);
		test.add(testString);
		
		int startMem = lastUsedMemByte;
		int hexTracker = lastUsedMemByte;
		for (Object s : test) {
			// System.out.println("VAL: " + getValueFromHex(s.toString()));
			memory.storeInMemory(hexTracker, getValueFromHex(s.toString()));
			hexTracker++;
		}
		allocatePCB(hexTracker, programName, startMem);
		runProgram(programName);
		// System.out.println(memory.getFromMemory("0x00"));
		// System.out.println(memory.getFromMemory("0x01"));
		// System.out.println(memory.getFromMemory("0x02"));
		// System.out.println(memory.getFromMemory("0x03"));
		// String[] thisCombo2 = fileToLoad[0].split("(?<=\\G.{" + 4 + "})");
	}
	
	private void unloadProgram(String programName) {
		
		int pcbStart = programPCBs.get(programName);
		int programstart = memory.getFromMemory(pcbStart);
		
		for(int i = programstart; i< pcbStart; i++) {
			memory.storeInMemory(i, 0);
		}
		
	}

	private void runProgram(String programName) {
//		System.out.println("\n\n\n\nRUN " + programName + ": " + programPCBs.get(programName));
		instructionPointer = memory.getFromMemory(programPCBs.get(programName));

		while (instructionPointer < (programPCBs.get(programName))) {
//			System.out.println("POINTER: " + instructionPointer);
			int commandType = memory.getFromMemory(instructionPointer);
			if(execI) {
				System.out.print("COMMAND TYPE: " + commandType);
				System.out.println(" PROCESS: " + 1);
			}
			instructionPointer++;
			
			grabFullCommand(commandType, programName);
//			int startingInstruction = 0;/*Get this from the PCB*/;
			//55 == the 13th number in the sequence and is the last working number = 233
//			if(testInc == )
//				break;
//			}
//			else {
////				System.out.println("WRAPPING");
//				testInc++;
//			}
		}

	}

	private void grabFullCommand(int commandType, String programName) {
//		 System.out.println("COMMAND: " + commandType);
		int memStart = programPCBs.get(programName) + 10;
		switch (commandType) {
		case 1: //Load
			// System.out.println("LOAD");
			int destRegister = memory.getFromMemory(instructionPointer) + 1;
//			System.out.println("DESTREGISTER: R" + destRegister);
			instructionPointer++;
			int memStoreIn = memory.getFromMemory(instructionPointer);
//			System.out.println("MEMSTOREIN : "+ (memStoreIn + memStart));			
			instructionPointer++;
			if((memStart + memStoreIn) < programPCBs.get(programName) || (memStart + memStoreIn) > (programPCBs.get(programName) + 64)) {
				//TODO Core Dump
				coreDump(programName);
			}
			int data = memory.getFromStack((memStart + memStoreIn));
//			System.out.println("DATA: " + data);
			registers.get("R" + destRegister).write(data);
//			memory.storeInMemory((memStart + memStoreIn), registers.get("R" + destRegister).read());		
			break;
		case 2: //Store
			// System.out.println("STORE");
//			int memStart = programPCBs.get(programName) + 10;
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer++;
			int register = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
//			System.out.println(registers.get("R" + register).read());
			int valueToStore = binaryToInt(registers.get("R" + register).read());
			System.out.println("STORING IN: " + (memStoreIn + memStart));
			if((memStart + memStoreIn) < programPCBs.get(programName) || (memStart + memStoreIn) > (programPCBs.get(programName) + 64)) {
				//TODO Core Dump
				coreDump(programName);
			}
			memory.storeInStack((memStart + memStoreIn), valueToStore);
			// System.out.println("VALUE STORED: " + valueToStore);
			
			break;
		case 3: //Add
			// System.out.println("ADDING");
			int register1 = memory.getFromMemory(instructionPointer) + 1;
//			int registerValue1 = binaryToInt(registers.get("R" + register1).read());
//			System.out.println("REGISTER VALUE: " + binaryToInt(registers.get("R" + registerValue1).read()));
			instructionPointer++;
			int register2 = memory.getFromMemory(instructionPointer) + 1;
			int registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			int register3 = memory.getFromMemory(instructionPointer) + 1;
			int registerValue3 = binaryToInt(registers.get("R" + register3).read());
			instructionPointer++;
			// System.out.println("REG: " + (registerValue3 + registerValue2));
			registers.get("R" + register1).write(registerValue2 + registerValue3);
//			int registerValue1 = binaryToInt(registers.get("R" + register1).read());
			// System.out.println(registerValue1 + "||" + registerValue2 + "||" + registerValue3);
			// System.out.println("ADDED VALUE: " + memory.getFromMemory(memVal));
			break;
		case 4: //Sub
			// System.out.println("SUBTRACTING");
			register1 = memory.getFromMemory(instructionPointer) + 1;
//			System.out.println(register1);
//			int registerValue1 = binaryToInt(registers.get("R" + register1).read());
//			System.out.println("REGISTER VALUE: " + binaryToInt(registers.get("R" + registerValue1).read()));
//			memVal = instructionPointer;
			instructionPointer++;
			register2 = memory.getFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
//			System.out.println("REGVAL2: " + registerValue2);
			register3 = memory.getFromMemory(instructionPointer) + 1;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			instructionPointer++;
//			System.out.println("REGVAL3: " + registerValue3);
			
			int subtractedValue = registerValue2 - registerValue3;
//			System.out.println("REGDIFFERENCE: " + subtractedValue);
			registers.get("R" + register1).write(subtractedValue);
//			registerValue1 = binaryToInt(registers.get("R" + register1).read());
//			 System.out.println(registerValue1 + "||" + registerValue2 + "||" + registerValue3);
			// System.out.println("SUBTRACTED VALUE: " + memory.getFromMemory(memVal));
			break;
		case 5: //Mul
			// System.out.println("MULTIPLYING");
			register1 = memory.getFromMemory(instructionPointer) + 1;
//			int registerValue1 = binaryToInt(registers.get("R" + register1).read());
//			System.out.println("REGISTER VALUE: " + binaryToInt(registers.get("R" + registerValue1).read()));
			instructionPointer++;
			register2 = memory.getFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
//			 System.out.println("REGMULT: " + (registerValue3 * registerValue2));
			registers.get("R" + register1).write(registerValue2 * registerValue3);
//			registerValue1 = binaryToInt(registers.get("R" + register1).read());
			// System.out.println(registerValue1 + "||" + registerValue2 + "||" + registerValue3);
			// System.out.println("MULTIPLIED VALUE: " + memory.getFromMemory(memVal));
			break;
		case 6: //Div
			// System.out.println("DIVIDING");
			register1 = memory.getFromMemory(instructionPointer) + 1;
//			int registerValue1 = binaryToInt(registers.get("R" + register1).read());
//			System.out.println("REGISTER VALUE: " + binaryToInt(registers.get("R" + registerValue1).read()));
			instructionPointer++;
			register2 = memory.getFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			// System.out.println("REG: " + (registerValue3 / registerValue2));
			registers.get("R" + register1).write(registerValue2 / registerValue3);
//			registerValue1 = binaryToInt(registers.get("R" + register1).read());
			// System.out.println(registerValue1 + "||" + registerValue2 + "||" + registerValue3);
			// System.out.println("DIVIDED VALUE: " + memory.getFromMemory(memVal));
			break;
		case 7: //Eq
			// System.out.println("EQUALS");
			register1 = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
			register2 = memory.getFromMemory(instructionPointer) + 1;
			registerValue2 = binaryToInt(registers.get("R" + register2).read());
			instructionPointer++;
			register3 = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
			registerValue3 = binaryToInt(registers.get("R" + register3).read());
			// System.out.println("REG: " + (registerValue3 / registerValue2));
			if(registerValue2 == registerValue3) {
				registers.get("R" + register1).write(1);
			}
			else {
				registers.get("R" + register1).write(0);
			}
//			registers.get("R" + register1).write(registerValue2 / registerValue3);
//			registerValue1 = binaryToInt(registers.get("R" + register1).read());
//			System.out.println(registerValue1 + "||" + registerValue2 + "||" + registerValue3);
//			System.out.println("DIVIDED VALUE: " + memory.getFromMemory(memVal));
			break;
		case 8: //Goto
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer = (memStart + memStoreIn);
			break;
		case 9: //Cprint
			// System.out.println("CPRINT: " + instructionPointer);
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer++;
//			System.out.println("PULLED NUM : " + (memStart + memStoreIn));
			if((memStart + memStoreIn) < programPCBs.get(programName) || (memStart + memStoreIn) > (programPCBs.get(programName) + 64)) {
				//TODO Core Dump
				coreDump(programName);
			}
			int toPrint = memory.getFromStack(memStart + memStoreIn);
			System.out.println(toPrint);
			break;
		case 10: //Loadc
			// System.out.println("LOADC");
//			System.out.println(memory.getFromMemory(instructionPointer) + 1);
			int registerValue = memory.getFromMemory(instructionPointer) + 1;
//			System.out.println("REG VAL: " + registerValue);
			Register r = registers.get("R" + registerValue);
			instructionPointer++;
			
//			System.out.println("NEXT VALUE: " + memory.getFromMemory(instructionPointer));
			r.write(memory.getFromMemory(instructionPointer));
//			System.out.println("REGISTER PRINT: " + r.printRegister());
			instructionPointer++;
			break;
		case 11: //Goto If
			memStoreIn = memory.getFromMemory(instructionPointer);
			instructionPointer++;
			registerValue = memory.getFromMemory(instructionPointer) + 1;
			instructionPointer++;
			if(binaryToInt(registers.get("R" + registerValue).read()) == 0){
				instructionPointer = (memStart + memStoreIn);
			}			
			
			break;
		case 16: //Cread
			char c = scanLee.next().charAt(0);
//			System.out.println(c);
			memStoreIn = memory.getFromMemory(instructionPointer);
			System.out.println(programPCBs.get(programName) + " |STORE IN : " + (memStart + memStoreIn) + "| MEMADD: " + memStoreIn);
			if((memStart + memStoreIn) < programPCBs.get(programName) || (memStart + memStoreIn) > (programPCBs.get(programName) + 64)) {
				//TODO Core Dump
				coreDump(programName);
			}
			memory.storeInStack((memStart + memStoreIn), c);
			instructionPointer++;
			break;
		case 17: //Exit
			// System.out.println("EXIT");
			unloadProgram(programName);
//			System.exit(0); 
			break;
		}
		
	}
	
	private int binaryToInt(int binary) {
		int binaryVal = Integer.parseInt("" + binary);
//		System.out.println("BINARY: " + binaryVal);
		return binaryVal;
	}

	private void allocatePCB(int hexValue, String programName, int startMem) {
		int bytesPerSection = 64;
		int allocated = hexValue + bytesPerSection;
		lastUsedMemByte = allocated;
		programPCBs.put(programName, hexValue);
		// System.out.println("START MEM: " + startMem);
		memory.storeInMemory(hexValue, startMem);
		// System.out.println("PCB START: " + memory.getFromMemory(hexValue));
	}

	private int getValueFromHex(String hexCode) {
		String format = hexCode.replace("0x", "");
		// System.out.println("format: " + format);
		int hex = (Integer.parseInt(format, 16));
		return hex;
	}
	private void coreDump(String programName) {
		System.err.println("FAIL");
		
		fileIO.appendToFile("You've broken the program, this is an uninformative core dump.", programName + "DumpFile.DUMP");
		
		System.exit(0);
	}

	@SuppressWarnings("unused")
	private void testRegisters() {
		Register r1 = registers.get("R1");
		r1.write(2);
		Register r2 = registers.get("R2");
		r2.write(4);
		// System.out.println(r1.read());
		// System.out.println(r2.read());
		r2.write(255);
		// System.out.println(r2.read());

		String testHex = "0x0000000f";
		// System.out.println("TEST: " + Integer.toHexString(35));
		// memory.storeInMemory(testHex, 11111111);

	}

	@SuppressWarnings("unused")
	private void testRAM() {
//		memory.storeInMemory("0x0000000f", 1);
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
				Assembly assembly = new Assembly();
				assembly.translateProgram(firstLine[1]);

				cpu.loadProgramIntoMemory(firstLine[1]);
				break;
			case "exec_i":
				execI = true;
				
				assembly = new Assembly();
				assembly.translateProgram(firstLine[1]);

				cpu.loadProgramIntoMemory(firstLine[1]);
				execI = false;
				break;
			case "kill":
//				System.out.println("kill");
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
