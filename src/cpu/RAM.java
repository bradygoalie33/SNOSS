package cpu;
import java.util.HashMap;
import java.util.Map;

public class RAM {
	
	byte[] memory = new byte[10000];
	private Map<String, MemoryBlock> memoryBlocks = new HashMap<String, MemoryBlock>();
	final int BLOCK_SIZE = 32;
	final int numOfBlocks = (memory.length / BLOCK_SIZE);
	
	public RAM() {
		for(int i = 0; i < memory.length; i++) {
//			int currentBlock = i * 32;
//			int nextBlock = ((i + 1) * 32) - 1;
//			String key = Integer.toHexString(i);
//			memoryBlocks.put(key, new MemoryBlock(currentBlock, nextBlock));
			memory[i] = 0;
		}		
	}
	
//	public int getFromMemory(String hexCode) {
//		int index = getValueFromHex(hexCode);
//		return (memory[index]);
//	}
	public byte getInstructionFromMemory(int hexValue) {
//		int index = getValueFromHex(hexCode);
		byte toReturn = (memory[hexValue]);
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
	
	public void storeInstructionInMemory(int hexValue, byte toStore) {
		memory[hexValue] = toStore;
		
//		int hex = Integer.parseInt(hexCode, 8);
//		MemoryBlock m = memoryBlocks.get(formatHex(hexCode));
//		int lengthToStore = (toStore + "").length();
	}
	
	public void storeInMemory(int hexValue, int toStore) {
//		ByteBuffer bb = ByteBuffer.allocate(7);
//		bb.putInt(toStore);
//		BigInteger bigInt = BigInteger.valueOf(toStore);     
//		byte[] storeInt = bigInt.toByteArray();;
		String binaryStore = formatAsBinary(Integer.toBinaryString(toStore));
		String splitBinary1 = binaryStore.substring(0, 8);
		String splitBinary2 = binaryStore.substring(8, 16);
		
		int leadingInt = Integer.parseInt(splitBinary1, 2);
		int trailingInt = Integer.parseInt(splitBinary2, 2);
		
		memory[hexValue] = (byte)leadingInt;
		memory[hexValue + 1] = (byte)trailingInt;
		         
	}
	
	public int getFromMemory(int hexValue) {
//		int index = getValueFromHex(hexCode);
		int leadingInt = memory[hexValue] & 0xff;
		int trailingInt = memory[hexValue + 1] & 0xff;

		String binaryStore1 = formatAsBinaryByte(Integer.toBinaryString(leadingInt));
		String binaryStore2 = formatAsBinaryByte(Integer.toBinaryString(trailingInt));
		String binaryStore = binaryStore1 + binaryStore2;
		int returnInt = Integer.parseInt(binaryStore, 2);
//		System.out.println("RETURNING: " + returnInt);
		return returnInt;
	}
	
	private String formatAsBinary(String unformatted) {
		int length = unformatted.length();
		String formatNeeded = "";
		
		for(int i = 0; i < (16 - length); i++) {
			formatNeeded += "0";
		}
		return formatNeeded + unformatted;
	}
	private String formatAsBinaryByte(String unformatted) {
		int length = unformatted.length();
		String formatNeeded = "";
		for(int i = 0; i < (8 - length); i++) {
			formatNeeded += "0";
		}
		return formatNeeded + unformatted;
	}
	
	@SuppressWarnings("unused")
	private String formatHex(String hexCode) {
		String format = hexCode.replace("0x", "");
		int hex = (Integer.parseInt(format, 16) - 1);
		String toReturn = Integer.toHexString(hex);
		return toReturn;
	}
	
	@SuppressWarnings("unused")
	private int getValueFromHex(String hexCode) {
		String format = hexCode.replace("0x", "");
		int hex = (Integer.parseInt(format, 16));
		return hex;
	}
}
