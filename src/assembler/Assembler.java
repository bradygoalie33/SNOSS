package assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class Assembler{

	private String filePath = new String("./Storage/");

	OutputStream os;
	public Assembler()
	{

	};

	public void processFile(File assemblyCode) throws FileNotFoundException, IOException{
		List<byte[]> commands = new ArrayList<byte[]>();
		try(BufferedReader br = new BufferedReader(new FileReader(assemblyCode))) {
			for(String line; (line = br.readLine()) != null; ) {
				String[] split = line.split(" ");
				switch(split[0]){
				case "LOAD":
					commands.add(load(split[1],split[2]));
					break;
				case "LOADC":
					commands.add(loadc(split[1],split[2]));
					break;
				case "STORE":
					commands.add(store(split[1],split[2]));
					break;
				case "ADD":
					commands.add(add(split[1],split[2],split[3]));
					break;
				case "SUB":
					commands.add(sub(split[1],split[2],split[3]));
					break;
				case "MUL":
					commands.add(mul(split[1],split[2],split[3]));
					break;
				case "DIV":
					commands.add(div(split[1],split[2],split[3]));
					break;
				case "EQ":
					commands.add(eq(split[1],split[2],split[3]));
					break;
				case "GOTO":
					commands.add(mygoto(split[1]));
					break;
				case "GOTOIF":
					commands.add(gotoif(split[1],split[2]));
					break;
				case "CPRINT":
					commands.add(cprint(split[1]));
					break;
				case "CREAD":
					commands.add(cread(split[1]));
					break;
				case "EXIT":
					commands.add(exit());
					break;
				}
			}
			writeToFile(commands, assemblyCode.getName());
		}
	}

	public byte[] load(String dest, String mem) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)1;
		bytes[1] = (byte)parseRegister(dest);
		byte[] temp = intToByteArray(Integer.parseInt(mem.substring(2), 16));
		bytes[2] = temp[0];
		bytes[3] = temp[1];
		return bytes;
	}

	public byte[] loadc(String dest, String val) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)10;
		bytes[1] = (byte)parseRegister(dest);
		byte[] temp = intToByteArray(Integer.parseInt(val));
		bytes[2] = temp[0];
		bytes[3] = temp[1];
		return bytes;
	}

	public byte[] store(String mem, String src) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)2;
		bytes[3] = (byte)parseRegister(src);
		byte[] temp = intToByteArray(Integer.parseInt(mem.substring(2), 16));
		bytes[1] = temp[0];
		bytes[2] = temp[1];
		return bytes;
	}

	public byte[] add(String dest, String src1, String src2) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)3;
		bytes[1] = (byte)parseRegister(dest);
		bytes[2] = (byte)parseRegister(src1);
		bytes[3] = (byte)parseRegister(src2);
		return bytes;
	}

	public byte[] sub(String dest, String src1, String src2) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)4;
		bytes[1] = (byte)parseRegister(dest);
		bytes[2] = (byte)parseRegister(src1);
		bytes[3] = (byte)parseRegister(src2);
		return bytes;
	}

	public byte[] mul(String dest, String src1, String src2) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)5;
		bytes[1] = (byte)parseRegister(dest);
		bytes[2] = (byte)parseRegister(src1);
		bytes[3] = (byte)parseRegister(src2);
		return bytes;
	}

	public byte[] div(String dest, String src1, String src2) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)6;
		bytes[1] = (byte)parseRegister(dest);
		bytes[2] = (byte)parseRegister(src1);
		bytes[3] = (byte)parseRegister(src2);
		return bytes;
	}

	public byte[] eq(String dest, String src1, String src2) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)7;
		bytes[1] = (byte)parseRegister(dest);
		bytes[2] = (byte)parseRegister(src1);
		bytes[3] = (byte)parseRegister(src2);
		return bytes;
	}

	public byte[] mygoto(String jump) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)8;
		byte[] temp = intToByteArray(Integer.parseInt(jump.substring(2), 16));
		bytes[1] = temp[0];
		bytes[2] = temp[1];
		bytes[3] = (byte)0;
		return bytes;
	}

	public byte[] gotoif(String jump, String src) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)11;
		byte[] temp = intToByteArray(Integer.parseInt(jump.substring(2), 16));
		bytes[1] = temp[0];
		bytes[2] = temp[1];
		bytes[3] = (byte)parseRegister(src);
		return bytes;
	}

	public byte[] cprint(String mem) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)9;
		byte[] temp = intToByteArray(Integer.parseInt(mem.substring(2), 16));
		bytes[1] = temp[0];
		bytes[2] = temp[1];
		bytes[3] = (byte)0;
		return bytes;
	}

	public byte[] cread(String mem) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)16;
		byte[] temp = intToByteArray(Integer.parseInt(mem.substring(2), 16));
		bytes[1] = temp[0];
		bytes[2] = temp[1];
		bytes[3] = (byte)0;
		return bytes;
	}

	public byte[] exit() {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)17;
		bytes[1] = (byte)0;
		bytes[2] = (byte)0;
		bytes[3] = (byte)0;
		return bytes;
	}

	private int parseRegister(String register){
		int toReturn = 0;
		switch(register){
		case "R1":
			toReturn = 0;
			break;
		case "R2":
			toReturn = 1;
			break;
		case "R3":
			toReturn = 2;
			break;
		case "R4":
			toReturn = 3;
			break;
		case "R5":
			toReturn = 4;
			break;
		case "R6":
			toReturn = 5;
			break;
		}
		return toReturn;
	}

	public byte[] intToByteArray(int value) {
		byte[] bytes = new byte[2];
		String bitString = Integer.toBinaryString(value);
		String emptyString = "";
		for(int i = 0; i < 16 - bitString.length(); i++){
			emptyString += 0;
		}
		String finalString = emptyString + bitString;
		String finalStringOne = finalString.substring(0, 8);
		String finalStringTwo = finalString.substring(8, 16);
		bytes[0] = (byte) Integer.parseInt(finalStringOne, 2);
		bytes[1] = (byte)Integer.parseInt(finalStringTwo,2);
		return bytes;
	}


	private void writeToFile(List<byte[]> commands, String filename){
		int inc = 0;
		byte[] bytes = new byte[commands.size() * 4];
		for(byte[] array : commands){
			for(int i = 0; i < 4; i++){
				bytes[inc] = array[i];
				inc++;
			}
		}
		//File snoFile = new File("/Users/sn255043/Documents/snossMem/" + concatFileType(filename) + ".sno");
		File snoFile = new File(filePath + concatFileType(filename) + ".sno");

		try {
			FileOutputStream fos = new FileOutputStream(snoFile);
			fos.write(bytes);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String concatFileType(String name){
		int length = name.length();
		return name.substring(0, length - 4);
	}

}
