package experimental.server;

import java.io.File;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.*;
import javax.swing.*;

import util.BKULUtils;

public class BKULServer {
	public static final String SERVER_CONFIG_PATH = BKULUtils.getCurrentWorkingDir() + File.separator + "server-config";
	public static Scanner userInputScanner = new Scanner(System.in);
	public static String gameDataPath;
	public static String userDataPath;
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		if(!BKULUtils.doesDirectoryExist(SERVER_CONFIG_PATH)) {
			System.out.println("It looks like this is your first time playing.");
			boolean gameDataFolderFound = false;
			boolean displayErr = false;
			String errorMessage = "";
			while(!gameDataFolderFound) {
				if(displayErr && !(errorMessage.equals(""))) {
					printError(errorMessage);
					displayErr = false;
				}
				System.out.print("Please enter the path to a game data folder, or type q to quit> ");
				String rawInput = userInputScanner.nextLine();
				gameDataPath = rawInput + File.separator + "battlekingdoms-data";
				if(!rawInput.equalsIgnoreCase("q")) {
					if(BKULUtils.doesDirectoryExist(gameDataPath)) {
						// found game data folder!
						try {
							printANSIColor("Yep, found a game data folder there!\n" +
														 "The folder contains the group " + BKULUtils.readFile(getGDPath("group-name.txt")), ANSI_GREEN);
							System.out.print("Join this group? [y]es or [n]o ");
							if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
								BKULUtils.createDirectory(SERVER_CONFIG_PATH);
								gameDataFolderFound = true;
								writeToLocalStorage(gameDataPath, "sync-folder.txt");
								printANSIColor("Setup is done! Exiting.", ANSI_GREEN);
								System.exit(0);
							} else {
								System.out.println("Please choose another folder:");
							}
						} catch (Exception e) {
							displayErr = true;
							errorMessage = "Could not find vital config files in this game data folder.";
						}
					}
				}
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Enter a password:");
			JPasswordField pass = new JPasswordField(10);
			panel.add(label);
			panel.add(pass);
			String[] options = new String[]{"OK"};
			int option = JOptionPane.showOptionDialog(null, panel, "Enter an admin password.\nThis will secure your admin account.",
			                         JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
			                         null, options, options[0]);
			char[] password = pass.getPassword();
			String passwd = new String(password);
			BKULUtils.createDirectory(SERVER_CONFIG_PATH);
		}
		}
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
			public static String getLocalStoragePath(String fileName) { return SERVER_CONFIG_PATH + File.separator + fileName; }
			
			// method to get file from local-config folder
			public static String getFileFromLocalStorage(String fileName) {
				try {
					return BKULUtils.readFile(SERVER_CONFIG_PATH + File.separator + fileName);
				} catch (Exception e) {
					System.out.print("There was a problem reading this local configuration file: " + fileName + ".\nError details: " + e.getMessage() + "\n"
										+ "If you've tampered with the file, re-create it: " + fileName + ".\n"
										+	"Would you like to delete the local-config folder? WARNING: This will delete display name and sync folder info.\n"
										+ "[y]es or [n]o ");
					if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
						System.out.print("Deleting the config folder " + SERVER_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
						if(!userInputScanner.nextLine().equalsIgnoreCase("s")) {
							BKULUtils.deleteFileOrDirectory(SERVER_CONFIG_PATH);
						    System.out.println("The config folder " + SERVER_CONFIG_PATH + " has been successfully deleted.");
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
					if(!(new File(SERVER_CONFIG_PATH + File.separator + filePath).exists())) {
						BKULUtils.createFile(SERVER_CONFIG_PATH + File.separator + filePath);
					}
					BKULUtils.writeToFile(text, SERVER_CONFIG_PATH + File.separator + filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
