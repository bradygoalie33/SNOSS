

import misc.FileIO;

public class Assembly {

	FileIO fileIO;
	
	public Assembly() {
		fileIO = new FileIO();
	}
	
	public void translateProgram(String fileName) {
		String[] assemblyProgram = fileIO.loadFile(fileName);
		
		String binaryString = stringToBinary(assemblyProgram);
		fileIO.writeToFile(binaryString, "/"+ fileName +".sno");
	}
	
	
	@SuppressWarnings("unused")
	private String binaryToString(String input) {
		
		String output = "";
			
		for(int i = 0; i <= input.length() - 8; i+=8)
		{
		    int k = Integer.parseInt(input.substring(i, i+8), 2);
		    output += (char) k;
		}   
//		System.out.println(output);
		
		return "";
	}
	
	private String stringToBinary(String[] assemblyProgram) {
		 
//		System.out.println(assemblyProgram);
		String binaryString = "";
		for(String s : assemblyProgram) {
			if(s != null) {
//				System.out.println("STRING TO BINARY: " + s);
				String[] splitString = s.split(" ");
//				System.out.println(splitString[0]);
				switch(splitString[0]) {
				
				case "LOAD":
					binaryString += "0x01";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += splitString[2];
					break;
				case "LOADC":
					binaryString += "0x0a";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += getValueAsHex(splitString[2]);
					break;
				case "STORE":
					binaryString += "0x02";
					binaryString += splitString[1].replace(",", "");
//					System.out.println("STORE: " + getRegisterAsString(splitString[1]));
					binaryString += getRegisterAsString(splitString[2]);
					break;
				case "ADD":
					binaryString += "0x03";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += getRegisterAsString(splitString[2]);
//					System.out.println("ADD: " + getRegisterAsString(splitString[3]));
					binaryString += getRegisterAsString(splitString[3]);
					break;
				case "SUB":
					binaryString += "0x04";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += getRegisterAsString(splitString[2]);
					binaryString += getRegisterAsString(splitString[3]);
					break;
				case "MUL":
					binaryString += "0x05";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += getRegisterAsString(splitString[2]);
					binaryString += getRegisterAsString(splitString[3]);
					break;
				case "DIV":
					binaryString += "0x06";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += getRegisterAsString(splitString[2]);
					binaryString += getRegisterAsString(splitString[3]);
					break;
				case "EQ":
					binaryString += "0x07";
					binaryString += getRegisterAsString(splitString[1]);
					binaryString += getRegisterAsString(splitString[2]);
					binaryString += getRegisterAsString(splitString[3]);
					break;
				case "GOTO":
					binaryString += "0x08";
					binaryString += splitString[1];				
					break;
				case "GOTOIF":
					binaryString += "0x0b";
					binaryString += splitString[1].replace(",", "");
					binaryString += getRegisterAsString(splitString[2]);
					break;
				case "CPRINT":
					binaryString += "0x09";
					binaryString += splitString[1];
					break;
				case "CREAD":
					binaryString += "0x10";
					binaryString += splitString[1];
					break;
				case "EXIT":
					binaryString += "0x11";
					break;
				}			
			}
		}
		
		return binaryString;
	}
	
	private String getValueAsHex(String value) {
		String outputValue = "";
		int val = Integer.valueOf(value);
		outputValue = Integer.toHexString(val);
		outputValue = formatAsHex(outputValue);
//		System.out.println("OUTPUT: " + outputValue);
		return outputValue;
	}
	
	private String formatAsHex(String unformatted) {
		int length = unformatted.length();
		String formatNeeded = "0x";
		
		for(int i = 0; i < (8-length); i++) {
			formatNeeded += "0";
		}
//		System.out.println(formatNeeded + unformatted);
		return formatNeeded + unformatted;
	}
	
	private String getRegisterAsString(String reg) {
		String outputString = reg.replace(",", "");
		
		switch(outputString) {
		case "R1":
			outputString = "0x00";
			break;
		case "R2":
			outputString = "0x01";
			break;
		case "R3":
			outputString = "0x02";
			break;
		case "R4":
			outputString = "0x03";
			break;
		case "R5":
			outputString = "0x04";
			break;
		case "R6":
			outputString = "0x05";
			break;
		}
		
//		System.out.println("OUTPUT: " + outputString);
		return outputString;
		
	}

}
