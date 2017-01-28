package game;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextField;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BKUL {
	
	// declare necessary variables
	public static final String LOCAL_CONFIG_PATH = BKULUtils.getCurrentWorkingDir() + File.separator + "local-config";
	public static String gameDataPath;
	public static String userDataPath;
	public static String displayName;
	public static Scanner userInputScanner = new Scanner(System.in);
	
	// main method, contains setup logic and game startup console
	public static void main(String[] args) throws Exception {
		BKULUtils.clearScreen();
		printANSIColor("~~Battle Kingdoms: Untold Legends~~", ANSI_CYAN);
		if(BKULUtils.doesDirectoryExist(LOCAL_CONFIG_PATH)) {
			displayName = getFileFromLocalStorage("display-name.txt");
			gameDataPath = getFileFromLocalStorage("sync-folder.txt");
			printANSIColor("Welcome back, " + displayName + "!", ANSI_CYAN);
			if(args.length != 0) {
				if(args[0].equalsIgnoreCase("--join")) {
					try {
						joinServer(args[1]);
					} catch(Exception e) {
						printError("Could not join.");
						printError(e.getMessage());
					}
				} else if(args[0].equalsIgnoreCase("--delete-local-config")) {
					printErrorRaw("Deleting the config folder " + LOCAL_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
					if(!userInputScanner.nextLine().equalsIgnoreCase("s")) {
						BKULUtils.deleteFileOrDirectory(LOCAL_CONFIG_PATH);
					    printANSIColor("The config folder " + LOCAL_CONFIG_PATH + " has been successfully deleted.", ANSI_GREEN);
					} else {
						printANSIColor("The folder was not deleted.", ANSI_GREEN);
					}
				} else {
					printError("Unrecognized command line argument.\n"
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
				printANSIColor("Connected to group " + BKULUtils.readFile(getGDPath("group-name.txt")), ANSI_GREEN);
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
				printError("Could not load group name. Error details:");
				printError(e.getMessage());
			}
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
														"j - Join server\n" +
														"o - Options\n" +
														"q - Quit");
				} else if(consoleInput.equalsIgnoreCase("c")) {
					BKULUtils.clearScreen();
				} else if(consoleInput.equalsIgnoreCase("q")) {
					System.out.println("Goodbye.");
					System.exit(0);
				} else if(consoleInput.equalsIgnoreCase("j")) {
					System.out.print("Enter the name of the server to join: ");
					joinServer(userInputScanner.nextLine());
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
					printError("Command not found. Type <h> for a list of commands.");
				}
			}
		} else {
			System.out.println("It looks like this is your first time playing. ");
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
								BKULUtils.createDirectory(LOCAL_CONFIG_PATH);
							    displayNamePrompt();
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
					} else {
						// didn't find folder :(
						System.out.print("Could not find a game data folder at that location. Would you like to create it? [y]es or [n]o ");
						if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
							// the user said yes to creating a game data folder
							if(!(BKULUtils.createDirectory(gameDataPath))) {
								displayErr = true;
								errorMessage = "There was a problem creating a game data folder at that location. Please try again with a different folder.";
							} else {
								printANSIColor("Congrats! A new game data folder was created at that location.", ANSI_GREEN);
								printANSIColorRaw("Game data folders contain a group, which must be named.\n" +
																	 "Enter a name for the group: ", ANSI_GREEN);
								BKULUtils.createDirectory(LOCAL_CONFIG_PATH);
								writeToLocalStorage(gameDataPath, "sync-folder.txt");
							    BKULUtils.writeToFile(userInputScanner.nextLine(), getGDPath("group-name.txt"));
								BKULUtils.createDirectory(getGDPath("servers"));
								displayNamePrompt();
								printANSIColorRaw("Your new group doesn't have any servers on it. Create one now? [y]es or [n]o ", ANSI_GREEN);
								if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
									System.out.print("Please specify a name for the server: ");
									String serverName = userInputScanner.nextLine();
									displayName = getFileFromLocalStorage("display-name.txt");
									userDataPath = getGDPath("servers" + File.separator + serverName + File.separator + "users"
											+ File.separator + displayName);
									BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName));
									BKULUtils.createFile(getGDPath("servers" + File.separator + serverName + File.separator + "users.txt"));
									BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
									BKULUtils.createFile(getLSPath(currentServer, "Marketfile"));
									BKULUtils.writeToFile("Fighter~300", getLSPath(currentServer, "Marketfile"));
									userSetup(serverName);
									BKULUtils.appendFileNewLn(getFileFromLocalStorage("display-name.txt"),
									getGDPath("servers" + File.separator + serverName + File.separator + "users.txt"));
									printANSIColor("Server was created successfully!", ANSI_GREEN);
								}
								printANSIColor("Setup is done! Exiting.", ANSI_GREEN);
								System.exit(0);
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
	
	// declare server related variables
	private static String currentServer;
	private static final String USER_DATA_FOLDER = getGDPath("servers" + File.separator + currentServer + File.separator + "users.txt");
	public static int gold, xp;
	public static JTextField goldField;
	
	// this method contains the main game loop
	public static void joinServer(String name) throws Exception {
		//if(BKULUtils.doesDirectoryExist(getGDPath("servers" + File.separator + currentServer))) {
		System.out.println("Connecting to server " + name + "...");
		currentServer = name;
		userDataPath = getGDPath("servers" + File.separator + currentServer + File.separator + "users"
				+ File.separator + displayName);
		gold = Integer.parseInt(BKULUtils.readFile(getUDPath("gold.txt")));
		xp = Integer.parseInt(BKULUtils.readFile(getUDPath("xp.txt")));
		BufferedReader br;
		new Thread(new InfiniteBKULRunner()).start();
		System.out.println("Connected! Switching to console. <h> for help");
		if(!BKULUtils.doesDirectoryExist(getGDPath("servers" + File.separator + currentServer +
				File.separator + "users" + File.separator + displayName))) {
			System.out.println("Welcome! You start with:\n"
					+ "- 500 Gold\n"
					+ "- A Level 1 Gold Mine to help you get more Gold\n"
					+ "Here is the help screen to get you started:");
			printHelp();
			userSetup(currentServer);
		}
		boolean consoleOn = true;
		String userConsoleInput;
		
		// Begin main game loop
		while(consoleOn) {
			System.out.print(currentServer + "> ");
			userConsoleInput = userInputScanner.nextLine();
			if(userConsoleInput.equalsIgnoreCase("h")) {
				printHelp();
			} else if(userConsoleInput.equalsIgnoreCase("c")) {
				BKULUtils.clearScreen();
			} else if(userConsoleInput.equalsIgnoreCase("q")) {
				save();
				System.out.println("Goodbye.");
				System.exit(0);
			} else if(userConsoleInput.equalsIgnoreCase("u")) {
				System.out.println("~~Users on this Server~~");
				try {
					BufferedReader bufread =
							new BufferedReader(new FileReader(getGDPath("servers" + File.separator + currentServer +
									File.separator + "users.txt")));
					for (String line = bufread.readLine(); line != null; line = bufread.readLine()) {
					   System.out.println(line);
					}
				} catch (Exception e) {
					printError("Could not list users.");
					printError(e.getMessage());
				}
			} else if(userConsoleInput.equalsIgnoreCase("d")) {
				System.out.println("Disconnecting...");
				consoleOn = false;
			} else if(userConsoleInput.equalsIgnoreCase("s")) {
				System.out.println("Structures (press <m> for market):");
				BufferedReader brForStr = new BufferedReader(new FileReader(getUDPath("structures.txt")));
				for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
					String[] parts = line.split("~");
					System.out.println("A Level " + parts[1] + " " + parts[0]);
				}
			} else if(userConsoleInput.equalsIgnoreCase("p")) {
				printANSIColor("XP: " + BKULUtils.readFile(getUDPath("xp.txt")), ANSI_GREEN);
				printANSIColor("Gold: " + gold/*BKULUtils.readFile(getUDPath("gold.txt"))*/, ANSI_YELLOW);
			} else if(userConsoleInput.equalsIgnoreCase("updnecfiles")) {
				userSetup(currentServer);
			} else if(userConsoleInput.equalsIgnoreCase("e")) {
				System.out.println("Achievements:");
				throw new NotImplementedException();
			} else if(userConsoleInput.equalsIgnoreCase("m")) {
				BufferedReader brForStr = new BufferedReader(new FileReader(getGDPath("servers" + File.separator +
						currentServer + File.separator + "Marketfile")));
				System.out.println("~~Market~~");
				for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
					String[] parts = line.split("~");
					System.out.println(parts[0] + ": " + parts[1] + " gold");
				}
			} else if(userConsoleInput.equalsIgnoreCase("v")) {
				System.out.println("Remember: autosave activates every 1 1/2 minutes.");
				save();
			} else if(userConsoleInput.equalsIgnoreCase("i")) {
				JFrame statusFrame = new JFrame("BKUL Status Window");
				statusFrame.setLayout(null);
				statusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				goldField = new JTextField(160);
				goldField.setLocation(20, 20);
				goldField.setSize(160, 30);
				goldField.setEditable(false);
				statusFrame.setSize(400, 100);
				statusFrame.add(goldField);
				statusFrame.setVisible(true);
			} else {
				printError("Command not found. Type <h> for a list of commands.");
			}
		}
		/*} else {
			printError("That server does not exist. Please enter another.");
		}*/
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
		BKULUtils.appendFileNewLn("Gold Mine~1", getUDPath("structures.txt"));
		BKULUtils.createFile(getUDPath("gold.txt"));
		BKULUtils.writeToFile("500", getUDPath("gold.txt"));
		BKULUtils.createFile(getUDPath("xp.txt"));
		BKULUtils.writeToFile("0", getUDPath("xp.txt"));
		BKULUtils.createFile(getUDPath("achievements.txt"));
		//BKULUtils.writeToFile("", getUDPath("achievements.txt"));
	}
	
	public static void printHelp() {
		System.out.println("~~HELP~~\n" +
				"h - Display this screen\n" +
				"c - Clear screen\n" +
				"d - Disconnect and go back to lobby\n" +
				"u - List all users on the server\n" + 
				"s - List all structures you own\n" +
				"p - List your progress - xp, coins, & more\n" +
				"o - Options\n" +
				"q - Quit");
	}
	
	public static void save() {
		System.out.print("Saving config files... ");
		try {
			BKULUtils.writeToFile(String.valueOf(gold), getUDPath("gold.txt"));
			BKULUtils.writeToFile(String.valueOf(xp), getUDPath("xp.txt"));
		} catch(Exception e) {
			printErrorRaw("Error. Details:\n" + e.getMessage());
		}
		printANSIColorRaw("Done!\n", ANSI_GREEN);
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
	
	public static String getLocalStoragePath(String fileName) { return LOCAL_CONFIG_PATH + File.separator + fileName; }
	
	// method to get file from local-config folder
	public static String getFileFromLocalStorage(String fileName) {
		try {
			return BKULUtils.readFile(LOCAL_CONFIG_PATH + File.separator + fileName);
		} catch (Exception e) {
			printErrorRaw("There was a problem reading this local configuration file: " + fileName + ".\nError details: " + e.getMessage() + "\n"
								+ "If you've tampered with the file, re-create it: " + fileName + ".\n"
								+	"Would you like to delete the local-config folder? WARNING: This will delete display name and sync folder info.\n"
								+ "[y]es or [n]o ");
			if(userInputScanner.nextLine().equalsIgnoreCase("y")) {
				printErrorRaw("Deleting the config folder " + LOCAL_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
				if(!userInputScanner.nextLine().equalsIgnoreCase("s")) {
					BKULUtils.deleteFileOrDirectory(LOCAL_CONFIG_PATH);
				    printANSIColor("The config folder " + LOCAL_CONFIG_PATH + " has been successfully deleted.", ANSI_GREEN);
				} else {
					printANSIColor("The folder was not deleted.", ANSI_GREEN);
				}
			} else {
				printANSIColor("The folder was not deleted.", ANSI_GREEN);
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
}
class InfiniteBKULRunner implements Runnable {
	
	// declare recursive counter variable
	public static int recursiveCounter = 0;
	
	// declare variable that confirms if the recursive counter variable is over or not
	public static boolean recCounterOver = false;
	
	@Override
	public void run() {
			// do recursive stuff here
			if(recursiveCounter == 6)  {
				recursiveCounter = 0;
				recCounterOver = true;
			}
			BoolIntOutcome hasGM = BKUL.hasGoldMine();
			if(hasGM.bool) {
				if(hasGM.integer == 1 && recCounterOver == true) {
					BKUL.gold++;
					BKUL.goldField.setText(String.valueOf(BKUL.gold) + "gold");
					recCounterOver = false;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			recursiveCounter++;
			run();
	}
	
}