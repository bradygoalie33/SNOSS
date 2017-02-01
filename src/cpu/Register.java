package cpu;
public class Register {

	byte[] reg = new byte[16];
	
	public Register() {
		for(int i = 0; i < 16; i++) {
			reg[i] = 0;
		}
	}
	
	public int read() {
		String toReturn = "";
		
		for(byte b : reg) {
			toReturn += b;
		}
//		System.out.println("READ: " + toReturn);
		return Integer.valueOf(toReturn, 2);
	}
	
	public void write(int toWrite) {
		clearRegister();
		char[] writeToRegister = Integer.toBinaryString(toWrite).toCharArray();
		int writingPoint = 16 - writeToRegister.length;
		int incrementer = 0;
		for(int i = writingPoint; i < 16; i++) {
			reg[i] = convertToByte(writeToRegister[incrementer]);
			incrementer++;
		}
		
//		System.out.println(printRegister());
		
	}
	

	private byte convertToByte(char c) {
		
		if(c =='1') {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	private void clearRegister() {	
		for(int i = 0; i < reg.length; i++) {
			reg[i] = 0;
		}
//		for(byte b : reg) {
//			b = 0;
//		}
//		System.out.println("CLEARED: " + printRegister());
	}
	
	public String printRegister() {
		String toReturn = "";
		for(byte b : reg) {
			toReturn += b;
		}
		return toReturn;
	}
	
}
