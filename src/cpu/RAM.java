package cpu;
import java.util.HashMap;
import java.util.Map;

public class RAM {
	
	byte[] memory = new byte[10000];
	private Map<String, MemoryBlock> memoryBlocks = new HashMap<String, MemoryBlock>();
	final int BLOCK_SIZE = 32;
	final int numOfBlocks = (memory.length / BLOCK_SIZE);
	
	public RAM() {
		for(int i = 0; i < numOfBlocks; i++) {
			int currentBlock = i * 32;
			int nextBlock = ((i + 1) * 32) - 1;
			String key = Integer.toHexString(i);
//			System.out.println(currentBlock + "||" + nextBlock + "||" + key);
			memoryBlocks.put(key, new MemoryBlock(currentBlock, nextBlock));
		}		
	}
	
	public int getFromMemory(String hexCode) {
		int index = getValueFromHex(hexCode);
//		System.out.println("INDEX: " + index);
		return (memory[index]);
	}
	public int getFromMemory(int hexValue) {
//		int index = getValueFromHex(hexCode);
//		System.out.println("HEXVALUE: " + hexValue);
//		System.out.println(hexValue);
		int toReturn = (memory[hexValue]);
//		System.out.println("RAMTORETURN: " + toReturn);
		return toReturn;
	}
	
//	public void storeInMemory(String hexCode, int toStore) {
//		System.out.println("STORE: " + getValueFromHex(hexCode));
//		int hex = Integer.parseInt(hexCode, 8);
//		System.out.println(hex);
//		System.out.println("1: " + memoryBlocks.get("0").getStart());
//		MemoryBlock m = memoryBlocks.get(formatHex(hexCode));
//		int lengthToStore = (toStore + "").length();
//		System.out.println("hi: " + lengthToStore);
//		System.out.println("m: " + m.getStart());
//		System.out.println("test: " + memoryBlocks.get("14").getStart());
//	}
	
	public void storeInMemory(int hexValue, int toStore) {
		byte storeValue = (byte) toStore;
//		 System.out.println("TO STORE: " + toStore + " BECAME: " + storeValue);
		memory[hexValue] = storeValue;
//		System.out.println("STORE: " + getValueFromHex(hexCode));
//		int hex = Integer.parseInt(hexCode, 8);
//		System.out.println(hex);
//		System.out.println("1: " + memoryBlocks.get("0").getStart());
//		MemoryBlock m = memoryBlocks.get(formatHex(hexCode));
//		int lengthToStore = (toStore + "").length();
//		System.out.println("hi: " + lengthToStore);
//		System.out.println("m: " + m.getStart());
//		System.out.println("test: " + memoryBlocks.get("14").getStart());
	}
	
	public void storeInStack(int hexValue, int toStore) {
//		ByteBuffer bb = ByteBuffer.allocate(7);
//		bb.putInt(toStore);
//		BigInteger bigInt = BigInteger.valueOf(toStore);     
//		byte[] storeInt = bigInt.toByteArray();;
//		System.out.println("STORING: " + toStore);
		String binaryStore = formatAsBinary(Integer.toBinaryString(toStore));
		String splitBinary1 = binaryStore.substring(0, 8);
		String splitBinary2 = binaryStore.substring(8, 16);
		// System.out.println(splitBinary1 + " lead|trail " + splitBinary2);
		// System.out.println("STORE IN STACK: | Where: " + hexValue + " | What: " + binaryStore);
		
		int leadingInt = Integer.parseInt(splitBinary1, 2);
		int trailingInt = Integer.parseInt(splitBinary2, 2);
		
		// System.out.println("STORING: " + leadingInt + "|" + trailingInt);
		// System.out.println("STORING IN BYTES: " + (byte)leadingInt + "|" + (byte)trailingInt);
		memory[hexValue] = (byte)leadingInt;
		memory[hexValue + 1] = (byte)trailingInt;
		
//		System.out.println("READING RAM: " + memory[hexValue + 1]);
		         
	}
	
	public int getFromStack(int hexValue) {
//		int index = getValueFromHex(hexCode);
//		System.out.println("HEXVALUE: " + hexValue);
//		System.out.println(hexValue);
		int leadingInt = memory[hexValue] & 0xff;
//		System.out.println("LEADING: " + leadingInt);
		int trailingInt = memory[hexValue + 1] & 0xff;
//		System.out.println("TRAILING: " + trailingInt);

		String binaryStore1 = formatAsBinaryByte(Integer.toBinaryString(leadingInt));
//		System.out.println("LEADING BINARYSTORE: " + binaryStore1);
		String binaryStore2 = formatAsBinaryByte(Integer.toBinaryString(trailingInt));
//		System.out.println("TRAILING BINARYSTORE: " + binaryStore2);
		String binaryStore = binaryStore1 + binaryStore2;
		int returnInt = Integer.parseInt(binaryStore, 2);
//		System.out.println("GETFROMSTACK: " + returnInt);
		return returnInt;
	}
	
	private String formatAsBinary(String unformatted) {
		int length = unformatted.length();
		String formatNeeded = "";
		
		for(int i = 0; i < (16 - length); i++) {
			formatNeeded += "0";
		}
//		System.out.println(formatNeeded + unformatted);
		return formatNeeded + unformatted;
	}
	private String formatAsBinaryByte(String unformatted) {
		int length = unformatted.length();
		String formatNeeded = "";
//		System.out.println("SIZE: " + unformatted);
		for(int i = 0; i < (8 - length); i++) {
			formatNeeded += "0";
		}
//		System.out.println(formatNeeded + unformatted);
		return formatNeeded + unformatted;
	}
	
	@SuppressWarnings("unused")
	private String formatHex(String hexCode) {
		String format = hexCode.replace("0x", "");
//		System.out.println("f: " + format);
		int hex = (Integer.parseInt(format, 16) - 1);
//		System.out.println("hex: " + format);
		String toReturn = Integer.toHexString(hex);
		return toReturn;
	}
	
	private int getValueFromHex(String hexCode) {
		String format = hexCode.replace("0x", "");
		int hex = (Integer.parseInt(format, 16));
		return hex;
	}
}
