package misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import assembler.Assembler;
import cpu.CPU;

public class Shell {

	private Scanner scanLee = new Scanner(System.in);
	private String filePath = new String("./Storage/");
	
	Assembler assembler;
	CPU cpu;
	public Shell(CPU cpu){
		this.cpu = cpu;
		assembler = new Assembler();
	}

	public void start() throws IOException{
		while (true) {
			boolean ampersand = false;
			String input = scanLee.nextLine();
			String[] firstLine = input.split(" ");
			if(firstLine.length > 2){
				if(firstLine[2] == "&"){
					ampersand = true;
				}
			}

			switch (firstLine[0]) {

			case "ls":
				filesInDirectory();
				break;
			case "ps":
				System.out.println("0");
				break;
			case "exec":
				final String threadString = firstLine[1];
				if(ampersand){
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							Path path2 = Paths.get(filePath + threadString);
							try {
								assembler.processFile(path2.toFile());
							} catch (IOException e) {
								e.printStackTrace();
							}
							cpu.loadProgramIntoMemory(threadString);
						}
					});
				}
				else{
					Path path = Paths.get(filePath + firstLine[1]);
					try {
						assembler.processFile(path.toFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					cpu.loadProgramIntoMemory(firstLine[1]);
				}
				break;
			case "exec_i":
				cpu.execI = true;
				if(ampersand){
					final String threadString2 = firstLine[1];
					Thread t2 = new Thread(new Runnable() {
						@Override
						public void run() {
							Path path2 = Paths.get(filePath + threadString2);
							try {
								assembler.processFile(path2.toFile());
							} catch (IOException e) {
								e.printStackTrace();
							}
							cpu.loadProgramIntoMemory(threadString2);
							cpu.execI = false;
						}
					});
				}
				else{
					Path path2 = Paths.get(filePath + firstLine[1]);
					try {
						assembler.processFile(path2.toFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
					cpu.loadProgramIntoMemory(firstLine[1]);
					cpu.execI = false;
				}
				break;
			case "kill":
				cpu.unloadProgram(Integer.parseInt(firstLine[1]));
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
