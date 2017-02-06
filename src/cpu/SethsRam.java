//package cpu;
//
//public class SethsRam {
//	public void loadProcess(byte[] process){
//		for(int i = firstOpenSpot; i < process.length; i++){
//			memory[i] = process[i];
//		}
//		firstOpenSpot += process.length - 1;
//	}
//	
//	public void storePCBAndStack(int id, int start, int end){
//		memory[firstOpenSpot] = (byte)id;
//		byte[] Start = intToByteArray(start);
//		byte[] End = intToByteArray(end);
//		memory[firstOpenSpot + 1] = Start[0];
//		memory[firstOpenSpot + 2] = Start[1];
//		memory[firstOpenSpot + 3] = End[0];
//		memory[firstOpenSpot + 4] = End[1];
//		firstOpenSpot += 64;
//	}
//
//	public int getFirstOpenSpot() {
//		return firstOpenSpot;
//	}
//	
//	public void store(int loc, int val){
//		byte[] temp = intToByteArray(val);
//		memory[loc] = temp[0];
//		memory[loc + 1] = temp[1];
//	}
//	
//	public int read(int loc){
//		 return byteArrayToInt(memory[loc],memory[loc+1]);
//	}
//	
//	
//	public byte[] intToByteArray(int value) {
//		byte[] bytes = new byte[2];
//	   String bitString = Integer.toBinaryString(value);
//	   String emptyString = "";
//	   for(int i = 0; i < 16 - bitString.length(); i++){
//		   emptyString += 0;
//	   }
//	   String finalString = emptyString + bitString;
//	   String finalStringOne = finalString.substring(0, 8);
//	   String finalStringTwo = finalString.substring(8, 16);
//	   bytes[0] = (byte) Integer.parseInt(finalStringOne, 2);
//	   bytes[1] = (byte)Integer.parseInt(finalStringTwo,2);
//	   return bytes;
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
//}
