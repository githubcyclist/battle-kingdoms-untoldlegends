package game;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.Random;
import java.util.Scanner;

public abstract class BKULUtils {
	// Method to write to file
	public static void writeToFile(String toWrite, String filePath) {
		File fout = new File(filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(toWrite);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to read from file
	public static String readFile(String filePath) throws Exception {
		String returnString = "0";
		Scanner scanner = new Scanner(new FileReader(filePath));
		while(scanner.hasNextLine()){
			returnString = scanner.nextLine();
		}
		scanner.close();
		return returnString;
	}
	
	// Method to check if directory exists
	public static boolean doesDirectoryExist(String path) {
		return(Files.isDirectory(Paths.get(path)));
	}
	
	// Method to check if file exists
	public static boolean doesFileExist(String path) {
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) { 
			return true;
		} else {
			return false;
		}
	}
	
	// Method to get the current working directory
	public static String getCurrentWorkingDir() {
		return Paths.get(".").toAbsolutePath().normalize().toString();
	}
	
	// Method to create a new directory
	public static boolean createDirectory(String path) {
		File dir = new File(path);
	    // attempt to create the directory here
	    boolean successful = dir.mkdir();
	    if (successful) {
	      // creating the directory succeeded
	      return true;
	    } else {
	      // creating the directory failed
	      return false;
	    }
	}
	
	// Method to delete a directory
		public static boolean deleteFileOrDirectory(String path) {
			File dir = new File(path);
		    // attempt to create the directory here
		    boolean successful = dir.delete();
		    if (successful) {
		      // creating the directory succeeded
		      return true;
		    } else {
		      // creating the directory failed
		      return false;
		    }
		}
		
		// method to delete a file
		public static boolean deleteDirectory(String path) {
			File dir = new File(path);
		    // attempt to create the directory here
		    boolean successful = dir.delete();
		    if (successful) {
		      // creating the directory succeeded
		      return true;
		    } else {
		      // creating the directory failed
		      return false;
		    }
		}
		
	public static boolean createFile(String path) {
		File file = new File(path);
	    // attempt to create the directory here
	    boolean successful;
		try {
			successful = file.createNewFile();
		    if (successful) {
			      // creating the directory succeeded
			      return true;
			    } else {
			      // creating the directory failed
			      return false;
			    }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static String OS = null;
	   public static String getOsName() {
	      if(OS == null) {
	    	  OS = System.getProperty("os.name");
	      }
	      return OS;
	   }
	   public static boolean isWindows() {
	      return getOsName().startsWith("Windows");
	   }
	   public static boolean isMac() {

			return (getOsName().indexOf("mac") >= 0);

		}
	   public static boolean isLinux() {
		   return getOsName().startsWith("Linux");
	   }
	public static void clearScreen() {
		try {
			if(!isIDE()) {
				if(isWindows()) {
					new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
				} else {
					new ProcessBuilder("clear").inheritIO().start().waitFor();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isIDE() {
	    boolean isEclipse = true;
	    if (System.getenv("eclipse42") == null) {
	        isEclipse = false;
	    }
	    return isEclipse;
	}

	public static void appendFileNewLn(String toAppend, String filename) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			String data = toAppend + "\n";
			File file = new File(filename);

			// if file doesn't exist, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public static String randomFighterName() {
		int nameID = new Random().nextInt(11) + 1;
		String name = "undefined name";
		if(nameID == 1) name = "Darel";
		else if(nameID == 2) name = "Ponto";
		else if(nameID == 3) name = "Burnu";
		else if(nameID == 4) name = "Balin";
		else if(nameID == 5) name = "Dwalin";
		else if(nameID == 6) name = "Fili";
		else if(nameID == 7) name = "Kili";
		else if(nameID == 8) name = "James";
		else if(nameID == 9) name = "John";
		else if(nameID == 9) name = "Abraham";
		else if(nameID == 10) name = "Albert";
		else if(nameID == 11) name = "Aragorn";
		return name;
	}
	public static int getLengthOfFile(String filePath) {
		try {
			int i = 0;
			BufferedReader brForStr = new BufferedReader(new FileReader(filePath));
			for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
				i++;
			}
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public void setConsoleTitle(String title) {
		try {
			if(isWindows()) {
				new ProcessBuilder("cmd", "/c", "title", title).inheritIO().start().waitFor();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

