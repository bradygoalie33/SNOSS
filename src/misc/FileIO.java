package misc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileIO {
	
	private String filePath = new String("./Storage/");

	
	public String[] loadFile(String fileToLoad) {
		BufferedReader br = null;
		FileReader fr = null;

		String[] toReturn = new String[255];
		// System.out.println("FILE TEST: " + filePath + fileToLoad);
		try {
			fr = new FileReader(filePath + fileToLoad);
			br = new BufferedReader(fr);
			
			String sCurrentLine;
			br = new BufferedReader(new FileReader(new File(filePath + fileToLoad)));
			int incrementer = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				toReturn[incrementer] = sCurrentLine;
				incrementer++;
//				System.out.println(sCurrentLine);					
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return toReturn;
	}
	
	public void writeToFile(String toWrite, String fileName) {
		try{
			fileName = fileName.replace(".txt", "");
		    PrintWriter writer = new PrintWriter(filePath + fileName, "UTF-8");
		    writer.println(toWrite);
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
		
	}
	
	public void appendToFile(String toWrite, String fileName) {
		try{
//			fileName = fileName.replace(".txt", "");
		    PrintWriter writer = new PrintWriter(filePath + fileName, "UTF-8");
		    writer.append(toWrite);
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
	}
	
	public void log(String toWrite) {
		
	}
	
	
}
