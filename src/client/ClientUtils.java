package client;

import java.io.File;

import util.BKULUtils;

public class ClientUtils {
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
	
	public static String getGDPath(String fileOrDir) { return BKULClient.gameDataPath + File.separator + fileOrDir; }
	
	public static String getUDPath(String fileOrDir) { return BKULClient.userDataPath + File.separator + fileOrDir; }
	
	public static String getLSPath(String server, String fileOrDir) {
		return getGDPath("servers" + File.separator + server + File.separator + fileOrDir);
	}
	
	public static String getOUPath(String otherUser, String serverName, String fileOrDir) {
		return getGDPath("servers" + File.separator + serverName + File.separator + "users" + File.separator + otherUser);
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
	
	public static void save() {
		System.out.print("Saving config files... ");
		try {
			BKULUtils.writeToFile(String.valueOf(BKULClient.gold), getUDPath("gold.txt"));
			BKULUtils.writeToFile(String.valueOf(BKULClient.xp), getUDPath("xp.txt"));
		} catch(Exception e) {
			System.out.print("Error. Details:\n" + e.getMessage());
		}
		System.out.print("Done!\n");
	}
	
	// method to simplify the printing of colored text using ANSI escape codes
			public static void printANSIColorRaw(String textToPrint, String escapeCode) {
				if(BKULUtils.isWindows()/* || BKULUtils.isIDE()*/) {
					System.out.print(textToPrint);
				} else {
					System.out.print(escapeCode + textToPrint + ANSI_RESET);
				}
			}
			
			// 
			public static void printANSIColor(String textToPrint, String escapeCode) { printANSIColorRaw(textToPrint + "\n", escapeCode); }
			public static void printError(String textToPrint) { printErrorRaw(textToPrint + "\n"); }
			public static void printErrorRaw(String textToPrint) { printANSIColorRaw(textToPrint, ANSI_RED); }
	
	// method to crate directory structure for user data folder
	public static void userSetup(String serverName) {
		if(!BKULUtils.doesDirectoryExist(getGDPath("servers" + File.separator +
															       serverName + File.separator + "users"))) {
			BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
		}
		BKULUtils.createDirectory(BKULClient.userDataPath);
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
}
