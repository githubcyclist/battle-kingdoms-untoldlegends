package game;

import java.io.*;
import java.util.Scanner;

import api.BKULFoundation;
import client.BKULClient;
import util.BKULUtils;
import util.BoolIntOutcome;

public class BKULEclipse extends BKULFoundation {
	
	// declare necessary variables
	public static final String LOCAL_CONFIG_PATH = BKULUtils.getCurrentWorkingDir() + File.separator + "local-config";
	public static String gameDataPath;
	public static String userDataPath;
	public static String displayName;
	public static Scanner userInputScanner = new Scanner(System.in);
	
	// main method, contains setup logic and game startup console
	public static void main(String[] args)  throws Exception {
		System.out.println("~~Battle Kingdoms: Untold Legends Eclipse~~");
		System.out.println("Note: This version of the game is for use in IDEs.\n"
				+ "It does not have screen clearing capabilities,\nor ANSI color printing ones.\n"
				+ "-------------------------------");
		if(BKULUtils.doesDirectoryExist(LOCAL_CONFIG_PATH)) {
			displayName = getFileFromLocalStorage("display-name.txt");
			gameDataPath = getFileFromLocalStorage("sync-folder.txt");
			System.out.println("Welcome back, " + displayName + "!");
			if(args.length != 0) {
				if(args[0].equalsIgnoreCase("--join")) {
					try {
						
						new BKULClient(gameDataPath, displayName, args[1], false).joinServer(args[1]);
					} catch(Exception e) {
						System.out.print("Could not join.");
						System.out.print(e.getMessage());
					}
				} else if(args[0].equalsIgnoreCase("--delete-local-config")) {
					System.out.print("Deleting the config folder " + LOCAL_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
					if(!userInputScanner.nextLine().equalsIgnoreCase("s")) {
						BKULUtils.deleteFileOrDirectory(LOCAL_CONFIG_PATH);
					    System.out.println("The config folder " + LOCAL_CONFIG_PATH + " has been successfully deleted.");
					} else {
						System.out.println("The folder was not deleted.");
					}
				} else {
					System.out.print("Unrecognized command line argument.\n"
							+ "Usage:\n"
							+ "--join <server name> - Join a server automagically!\n"
							+ "--delete-local-config - Delete local configuration files");
					System.out.print("Would you like to keep playing? [y]es or [n]o ");
					if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
						System.out.println("Continuing to server list...");
					} else {
						System.out.println("Goodbye.");
						System.exit(0);
					}
				}
			}
			try {
				System.out.println("Connected to group " + BKULUtils.readFile(getGDPath("group-name.txt")));
			    System.out.println("~~Servers~~");
		    	File[] files = new File(getGDPath("servers")).listFiles();
		    	int i = 1;
			    for (File file : files) {
			        if (file.isDirectory()) {
			            System.out.println(i + ". " + file.getName());
			        }
			        i++;
			    }
			} catch (Exception e) {
				System.out.print("Could not load group name. Error details:");
				System.out.print(e.getMessage());
			}
			console();
		} else {
			System.out.println("It looks like this is your first time playing. ");
			boolean gameDataFolderFound = false;
			boolean displayErr = false;
			String errorMessage = "";
			while(!gameDataFolderFound) {
				if(displayErr && !(errorMessage.equals(""))) {
					System.out.println(errorMessage);
					displayErr = false;
				}
				System.out.print("Please enter the path to a game data folder, or type q to quit> ");
				String rawInput = userInputScanner.nextLine();
				gameDataPath = rawInput + File.separator + "battlekingdoms-data";
				if(!rawInput.equalsIgnoreCase("q")) {
					if(BKULUtils.doesDirectoryExist(gameDataPath)) {
						// found game data folder!
						try {
							System.out.println("Yep, found a game data folder there!\n" +
														 "The folder contains the group " + BKULUtils.readFile(getGDPath("group-name.txt")));
							System.out.print("Join this group? [y]es or [n]o ");
							if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
								BKULUtils.createDirectory(LOCAL_CONFIG_PATH);
							    displayNamePrompt();
								gameDataFolderFound = true;
								writeToLocalStorage(gameDataPath, "sync-folder.txt");
								System.out.println("Setup is done! Exiting.");
								System.exit(0);
							} else {
								System.out.println("Please choose another folder:");
							}
						} catch (Exception e) {
							displayErr = true;
							errorMessage = "Could not find vital config files in this game data folder.";
						}
					} else {
						// didn't find folder :(
						System.out.print("Could not find a game data folder at that location. Would you like to create it? [y]es or [n]o ");
						if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
							// the user said yes to creating a game data folder
							if(!(BKULUtils.createDirectory(gameDataPath))) {
								displayErr = true;
								errorMessage = "There was a problem creating a game data folder at that location. Please try again with a different folder.";
							} else {
								System.out.println("Congrats! A new game data folder was created at that location.");
								System.out.print("Game data folders contain a group, which must be named.\n" +
																	 "Enter a name for the group: ");
								BKULUtils.createDirectory(LOCAL_CONFIG_PATH);
								writeToLocalStorage(gameDataPath, "sync-folder.txt");
							    BKULUtils.writeToFile(userInputScanner.nextLine(), getGDPath("group-name.txt"));
								BKULUtils.createDirectory(getGDPath("servers"));
								displayNamePrompt();
								System.out.print("Your new group doesn't have any servers on it. Create one now? [y]es or [n]o ");
								if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
									System.out.print("Please specify a name for the server: ");
									String serverName = userInputScanner.nextLine();
									displayName = getFileFromLocalStorage("display-name.txt");
									userDataPath = getGDPath("servers" + File.separator + serverName + File.separator + "users"
											+ File.separator + displayName);
									BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName));
									BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
									BKULUtils.createFile(getLSPath(serverName, "Marketfile"));
									BKULUtils.writeToFile("Fighter~300", getLSPath(serverName, "Marketfile"));
									userSetup(serverName);
									System.out.println("Server was created successfully!");
								}
								System.out.println("Setup is done!");
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
							// if the user says n or anything else, it shows the prompt to enter a sync folder again
						}
					}
				} else {
					BKULUtils.deleteFileOrDirectory(LOCAL_CONFIG_PATH);
					System.out.println("Goodbye.");
					System.exit(0);
				}
			}
		}
	}
	
	
	
	public static BoolIntOutcome hasGoldMine() {
		try {
			BufferedReader brForStr = new BufferedReader(new FileReader(getUDPath("structures.txt")));
			for (String line = brForStr.readLine(); line != null;) {
				String[] parts = line.split("~");
				if(parts[0].equals("Gold Mine")) {
					return new BoolIntOutcome(true, Integer.parseInt(parts[1]));
				} else {
					return new BoolIntOutcome(false, Integer.parseInt(parts[1]));
				}
			}
		} catch(Exception e) {
			return new BoolIntOutcome(false, 0);
		}
		return new BoolIntOutcome(false, 0);
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