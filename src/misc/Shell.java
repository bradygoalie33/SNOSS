package misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import assembler.Assembler;
import cpu.CPU;

public class Shell {

	private Scanner scanLee = new Scanner(System.in);
	private String filePath = new String("./Storage/");
	boolean execI = false;
	CPU cpu;
	public Shell(CPU cpu){
		this.cpu = cpu;
	}
	
	public void start() throws IOException{
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
				cpu.unloadProgram(firstLine[1]);
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
