package api;

import java.io.File;
import java.util.Scanner;

import client.BKULClient;
import util.BKULUtils;

public class BKULFoundation {
	// declare necessary variables
		public static final String LOCAL_CONFIG_PATH = BKULUtils.getCurrentWorkingDir() + File.separator + "local-config";
		public static String gameDataPath;
		public static String userDataPath;
		public static String displayName;
		public static Scanner userInputScanner = new Scanner(System.in);
	
	public static void console() throws Exception {
		System.out.println("<j> to join a server, <h> for help, and <o> for options.");
		boolean consoleMode = true;
		while(consoleMode) {
			System.out.print("lobby> ");
			String consoleInput = userInputScanner.nextLine();
			if(consoleInput.equalsIgnoreCase("h")) {
				System.out.println("~~HELP~~\n" +
													"h - Display this screen\n" +
													"c - Clear screen\n" +
													"l - List servers\n" +
													"j - Join server\n"+
													"a - Add/create server\n" +
													"o - Options\n" +
													"q - Quit");
			} else if(consoleInput.equalsIgnoreCase("c")) {
				System.out.println("Sorry, but this version of the game doesn't support clearing the screen.\n"
						+ "If you are playing in a terminal/command prompt, switch to the regular\nversion instead.");
			} else if(consoleInput.equalsIgnoreCase("a")) {
				System.out.print("Please specify a name for the server, or <c> to cancel creation: ");
				String serverNam = userInputScanner.nextLine();
				if(!serverNam.equalsIgnoreCase("c")) {
					String serverName = serverNam;
					displayName = getFileFromLocalStorage("display-name.txt");
					userDataPath = getGDPath("servers" + File.separator + serverName + File.separator + "users"
							+ File.separator + displayName);
					BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName));
					BKULUtils.createFile(getGDPath("servers" + File.separator + serverName + File.separator + "users.txt"));
					BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
					BKULUtils.createFile(getLSPath(serverName, "Marketfile"));
					BKULUtils.writeToFile("Fighter~300", getLSPath(serverName, "Marketfile"));
					userSetup(serverName);
					BKULUtils.appendFileNewLn(getFileFromLocalStorage("display-name.txt"),
						getGDPath("servers" + File.separator + serverName + File.separator + "users.txt"));
					System.out.println("Server was created successfully!");
				}
			} else if(consoleInput.equalsIgnoreCase("q")) {
				System.out.println("Goodbye.");
				System.exit(0);
			} else if(consoleInput.equalsIgnoreCase("c")) {
				
			} else if(consoleInput.equalsIgnoreCase("j")) {
				System.out.print("Enter the name of the server to join: ");
				String server = userInputScanner.nextLine();
				new BKULClient(gameDataPath, displayName, server, false).joinServer(server);
			} else if(consoleInput.equalsIgnoreCase("l")) {
				System.out.println("~~Servers~~");
		    	File[] files = new File(getGDPath("servers")).listFiles();
		    	int i = 1;
			    for (File file : files) {
			        if (file.isDirectory()) {
			            System.out.println(i + ": " + file.getName());
			        }
			        i++;
			    }
			} else {
				System.out.println("Command not found. Type <h> for a list of commands.");
			}
		}
	}
	
	public static void options() {
		System.out.println("~~Options~~\n"
									+ "(1) Sync folder\n"
									+ "(2) Delete local configuration folder");
		System.out.print("Choose one (letter or number), or <q> to quit: ");
		String option = userInputScanner.nextLine();
		if(equalsICTwo(option, "1", "Sync folder")) {
			
		}
	}
	
	public static boolean equalsICTwo(String string, String integer, String secondString) {
		return(string.equalsIgnoreCase(secondString) || string.equalsIgnoreCase(integer));
	}
	
	// method to crate directory structure for user data folder
		public static void userSetup(String serverName) {
			if(!BKULUtils.doesDirectoryExist(getGDPath("servers" + File.separator +
																       serverName + File.separator + "users"))) {
				BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
			}
			BKULUtils.createDirectory(userDataPath);
			BKULUtils.createFile(getUDPath("structures.txt"));
			BKULUtils.writeToFile("Gold Mine~1\nWall~1", getUDPath("structures.txt"));
			BKULUtils.createFile(getUDPath("gold.txt"));
			BKULUtils.writeToFile("500", getUDPath("gold.txt"));
			BKULUtils.createFile(getUDPath("xp.txt"));
			BKULUtils.writeToFile("0", getUDPath("xp.txt"));
			BKULUtils.createFile(getUDPath("wall-health.txt"));
			BKULUtils.writeToFile("250", getUDPath("wall-health.txt"));
			BKULUtils.createFile(getUDPath("achievements.txt"));
			BKULUtils.createFile(getUDPath("fighters.txt"));
			//BKULUtils.writeToFile("", getUDPath("achievements.txt"));
		}
		
		public static void printHelp() {
			System.out.println("~~HELP~~\n" +
					"h - Display this screen\n" +
					"c - Clear screen\n" +
					"d - Disconnect and go back to lobby\n" +
					"u - List all users on the server\n" +
					"a - Attack other players! (BETA)\n" +
					"s - List all structures you own\n" +
					"m - Open market\n" +
					"p - List your progress - xp, coins, & more\n" +
					"l - Open alliance menu (NOT WORKING)\n" +
					"o - Options\n" +
					"q - Quit");
		}
		
		
		// declare ansi escape codes for terminal colors
			public static final String ANSI_RESET = "\u001B[0m";
			public static final String ANSI_BLACK = "\u001B[30m";
			public static final String ANSI_RED = "\u001B[31m";
			public static final String ANSI_GREEN = "\u001B[32m";
			public static final String ANSI_YELLOW = "\u001B[33m";
			public static final String ANSI_BLUE = "\u001B[34m";
			public static final String ANSI_PURPLE = "\u001B[35m";
			public static final String ANSI_CYAN = "\u001B[36m";
			public static final String ANSI_WHITE = "\u001B[37m";
			
		public static String getLocalStoragePath(String fileName) { return LOCAL_CONFIG_PATH + File.separator + fileName; }
		
		// method to get file from local-config folder
		public static String getFileFromLocalStorage(String fileName) {
			try {
				return BKULUtils.readFile(LOCAL_CONFIG_PATH + File.separator + fileName);
			} catch (Exception e) {
				System.out.print("There was a problem reading this local configuration file: " + fileName + ".\nError details: " + e.getMessage() + "\n"
									+ "If you've tampered with the file, re-create it: " + fileName + ".\n"
									+	"Would you like to delete the local-config folder? WARNING: This will delete display name and sync folder info.\n"
									+ "[y]es or [n]o ");
				if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
					System.out.print("Deleting the config folder " + LOCAL_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
					if(!userInputScanner.nextLine().equalsIgnoreCase("s")) {
						BKULUtils.deleteFileOrDirectory(LOCAL_CONFIG_PATH);
					    System.out.println("The config folder " + LOCAL_CONFIG_PATH + " has been successfully deleted.");
					} else {
						System.out.println("The folder was not deleted.");
					}
				} else {
					System.out.println("The folder was not deleted.");
				}
				System.exit(1);
			}
			return "Could not read file";
		}
		
		// method to get file from local-config folder
		public static void writeToLocalStorage(String text, String filePath) {
			try {
				if(!(new File(LOCAL_CONFIG_PATH + File.separator + filePath).exists())) {
					BKULUtils.createFile(LOCAL_CONFIG_PATH + File.separator + filePath);
				}
				BKULUtils.writeToFile(text, LOCAL_CONFIG_PATH + File.separator + filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void displayNamePrompt() {
			System.out.print("You need to choose a display name to play. Please enter a display name to use: ");
			writeToLocalStorage(userInputScanner.nextLine(), "display-name.txt");
		}
		public static String getGDPath(String fileOrDir) { return gameDataPath + File.separator + fileOrDir; }
		public static String getUDPath(String fileOrDir) { return userDataPath + File.separator + fileOrDir; }
		public static String getLSPath(String server, String fileOrDir) {
			return getGDPath("servers" + File.separator + server + File.separator + fileOrDir);
		}
		public static String getOUPath(String otherUser, String serverName, String fileOrDir) {
			return getGDPath("servers" + File.separator + serverName + File.separator + "users" + File.separator + otherUser);
		}
}
