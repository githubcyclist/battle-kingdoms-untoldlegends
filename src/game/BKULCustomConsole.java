package game;

import java.awt.Font;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextField;

import javaConsole.BKULConsole;
import menu.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.BKULUtils;
import util.BoolIntOutcome;
import client.*;

public class BKULCustomConsole {
	
	// declare necessary variables
	public static final String LOCAL_CONFIG_PATH = BKULUtils.getCurrentWorkingDir() + File.separator + "local-config";
	public static String gameDataPath;
	public static String userDataPath;
	public static String displayName;
	public static Scanner userInputScanner = new Scanner(System.in);
	public static BKULConsole bkulConsole = new BKULConsole();
	
	// main method, contains setup logic and game startup console
	public static void main(String[] args) throws Exception {
		bkulConsole.setTitle("Battle Kingdoms: Untold Legends");
		bkulConsole.setFont(new Font(Font.SANS_SERIF, 30, 20));
		System.out.println("~~Battle Kingdoms: Untold Legends~~");
		if(BKULUtils.doesDirectoryExist(LOCAL_CONFIG_PATH)) {
			displayName = getFileFromLocalStorage("display-name.txt");
			gameDataPath = getFileFromLocalStorage("sync-folder.txt");
			System.out.println("Welcome back, " + displayName + "!");
			if(args.length != 0) {
				if(args[0].equalsIgnoreCase("--join")) {
					try {
						joinServer(args[1]);
					} catch(Exception e) {
						System.out.print("Could not join.");
						System.out.print(e.getMessage());
					}
				} else if(args[0].equalsIgnoreCase("--delete-local-config")) {
					System.out.print("Deleting the config folder " + LOCAL_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
					if(!InputGetter.nextLine().equalsIgnoreCase("s")) {
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
					if(InputGetter.nextLine().equalsIgnoreCase("y")) {
						System.out.println("Continuing to server list...");
					} else {
						System.out.println("Goodbye.");
						System.exit(0);
					}
				}
			}
			try {
				System.out.println("Connected to group " + BKULUtils.readFile(ClientUtils.getUDPath("group-name.txt")));
			    System.out.println("~~Servers~~");
		    	File[] files = new File(ClientUtils.getUDPath("servers")).listFiles();
		    	int i = 1;
			    for (File file : files) {
			        if (file.isDirectory()) {
			            System.out.println(i + ". " + file.getName());
			        }
			        i++;
			    }
			} catch (Exception e) {
				System.out.print("Could not load group name. Details of error:");
				System.out.print(e.getMessage());
			}
			System.out.println("<j> to join a server, <h> for help, and <o> for options.");
			Menu lobbyMenu = new Menu(bkulConsole);
			lobbyMenu.add("h", new MenuCallback() {
				@Override
				public void Invoke() {
					System.out.println("~~HELP~~\n" +
							"h - Display this screen\n" +
							"c - Clear screen\n" +
							"l - List servers\n" +
							"j - Join server\n"+
							"a - Add/create server\n" +
							"o - Options\n" +
							"q - Quit");
				}
			});
			lobbyMenu.add("c", new MenuCallback() {
				@Override
				public void Invoke() {
					System.out.println("clearing screen");
					bkulConsole.clear();
				}
			});
			lobbyMenu.add("l", new MenuCallback() {
				@Override
				public void Invoke() {
					System.out.println("~~Servers~~");
			    	File[] files = new File(ClientUtils.getUDPath("servers")).listFiles();
			    	int i = 1;
				    for (File file : files) {
				        if (file.isDirectory()) {
				            System.out.println(i + ": " + file.getName());
				        }
				        i++;
				    }
				}
			});
			lobbyMenu.add("q", new MenuCallback() {
				@Override
				public void Invoke() {
					System.out.println("Goodbye.");
					System.exit(0);
				}
			});
			lobbyMenu.add("j", new MenuCallback() {
				@Override
				public void Invoke() {
					System.out.print("Enter the name of the server to join: ");
					try {
						joinServer(InputGetter.nextLine());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			boolean consoleOn = true;
			while(consoleOn) {
				System.out.print("lobby> ");
				lobbyMenu.show();
			}
			/*
			while(consoleMode) {
				} else if(consoleInput.equalsIgnoreCase("a")) {
					System.out.print("Please specify a name for the server, or <c> to cancel creation: ");
					String serverNam = InputGetter.nextLine();
					if(!serverNam.equalsIgnoreCase("c")) {
						String serverName = serverNam;
						displayName = getFileFromLocalStorage("display-name.txt");
						userDataPath = ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users"
								+ File.separator + displayName);
						BKULUtils.createDirectory(ClientUtils.getUDPath("servers" + File.separator + serverName));
						BKULUtils.createFile(ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users.txt"));
						BKULUtils.createDirectory(ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users"));
						BKULUtils.createFile(ClientUtils.getLSPath(serverName, "Marketfile"));
						BKULUtils.writeToFile("Fighter~300", ClientUtils.getLSPath(currentServer, "Marketfile"));
						userSetup(serverName);
						BKULUtils.appendFileNewLn(getFileFromLocalStorage("display-name.txt"),
							ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users.txt"));
						System.out.println("Server was created successfully!");
					}
		 		else if(consoleInput.equalsIgnoreCase("j")) {
					System.out.print("Enter the name of the server to join: ");
					joinServer(InputGetter.nextLine());
				} else if(consoleInput.equalsIgnoreCase("l")) {
					System.out.println("~~Servers~~");
			    	File[] files = new File(ClientUtils.getUDPath("servers")).listFiles();
			    	int i = 1;
				    for (File file : files) {
				        if (file.isDirectory()) {
				            System.out.println(i + ": " + file.getName());
				        }
				        i++;
				    }
				} else {
					System.out.print("Command not found. Type <h> for a list of commands.");
				}
			}*/
		} else {
			System.out.println("It looks like this is your first time playing. ");
			boolean gameDataFolderFound = false;
			boolean displayErr = false;
			String errorMessage = "";
			while(!gameDataFolderFound) {
				if(displayErr && !(errorMessage.equals(""))) {
					System.out.print(errorMessage);
					displayErr = false;
				}
				System.out.print("Please enter the path to a game data folder, or type q to quit> ");
				String rawInput = InputGetter.nextLine();
				gameDataPath = rawInput + File.separator + "battlekingdoms-data";
				if(!rawInput.equalsIgnoreCase("q")) {
					if(BKULUtils.doesDirectoryExist(gameDataPath)) {
						// found game data folder!
						try {
							System.out.println("Yep, found a game data folder there!\n" +
														 "The folder contains the group " + BKULUtils.readFile(ClientUtils.getUDPath("group-name.txt")));
							System.out.print("Join this group? [y]es or [n]o ");
							if(InputGetter.nextLine().equalsIgnoreCase("y")) {
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
						if(InputGetter.nextLine().equalsIgnoreCase("y")) {
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
							    BKULUtils.writeToFile(InputGetter.nextLine(), ClientUtils.getUDPath("group-name.txt"));
								BKULUtils.createDirectory(ClientUtils.getUDPath("servers"));
								displayNamePrompt();
								System.out.print("Your new group doesn't have any servers on it. Create one now? [y]es or [n]o ");
								if(InputGetter.nextLine().equalsIgnoreCase("y")) {
									System.out.print("Please specify a name for the server: ");
									String serverName = InputGetter.nextLine();
									displayName = getFileFromLocalStorage("display-name.txt");
									userDataPath = ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users"
											+ File.separator + displayName);
									BKULUtils.createDirectory(ClientUtils.getUDPath("servers" + File.separator + serverName));
									BKULUtils.createDirectory(ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users"));
									BKULUtils.createFile(ClientUtils.getLSPath(serverName, "Marketfile"));
									BKULUtils.writeToFile("Fighter~300", ClientUtils.getLSPath(serverName, "Marketfile"));
									userSetup(serverName);
									System.out.println("Server was created successfully!");
								}
								System.out.println("Setup is done! Exiting.");
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
	private static final String USER_DATA_FOLDER = ClientUtils.getUDPath("servers" + File.separator + currentServer + File.separator + "users.txt");
	public static int gold, xp;
	public static JTextField goldField;
	
	// this method contains the main game loop
	public static void joinServer(String name) throws Exception {
		if(BKULUtils.doesDirectoryExist(ClientUtils.getUDPath("servers" + File.separator + name))) {
		System.out.println("Connecting to server " + name + "...");
		currentServer = name;
		userDataPath = ClientUtils.getUDPath("servers" + File.separator + currentServer + File.separator + "users"
				+ File.separator + displayName);
		BufferedReader br;
		InfiniteBKULRunner infiniRunner = new InfiniteBKULRunner();
		Thread sessionInfiniteThread = new Thread(infiniRunner);
		sessionInfiniteThread.start();
		TempFileChecker declareWarDetector = new TempFileChecker(currentServer);
		Thread warDetectorRunner = new Thread(declareWarDetector);
		warDetectorRunner.start();
		System.out.println("Connected! Switching to console. <h> for help");
		if(!BKULUtils.doesDirectoryExist(ClientUtils.getUDPath("servers" + File.separator + currentServer +
				File.separator + "users" + File.separator + displayName))) {
			System.out.println("Welcome! You start with:\n"
					+ "- 500 Gold\n"
					+ "- A Level 1 Gold Mine to help you get more Gold\n"
					+ "- A Level 1 Wall to provide basic protection against enemies\n"
					+ "Here is the help screen to get you started:");
			printHelp();
			userSetup(currentServer);
		}
		try {
			gold = Integer.parseInt(BKULUtils.readFile(ClientUtils.getUDPath("gold.txt")));
			xp = Integer.parseInt(BKULUtils.readFile(ClientUtils.getUDPath("xp.txt")));
		} catch(Exception e) {
			System.out.print("There was a problem reading config files in your user folder. Error details:");
			System.out.print(e.getMessage());
		}
		boolean consoleOn = true;
		String userConsoleInput;
		
		// Begin main game loop
		while(consoleOn) {
			System.out.print(currentServer + "> ");
			userConsoleInput = InputGetter.nextLine();
			if(userConsoleInput.equalsIgnoreCase("h")) {
				printHelp();
			} else if(userConsoleInput.equalsIgnoreCase("c")) {
				bkulConsole.clear();
				System.out.println("~~Battle Kingdoms: Untold Legends~~\n"
									 + "Connected to server Sanger");
			} else if(userConsoleInput.equalsIgnoreCase("q")) {
				save();
				System.out.println("Goodbye.");
				System.exit(0);
			} else if(userConsoleInput.equalsIgnoreCase("u")) {
				System.out.println("~~Users on this Server including you~~");
				System.out.println("You are: " + displayName);
				try {
					int i = 1;
					File[] files = new File(ClientUtils.getLSPath(currentServer, "users")).listFiles();
				    for (File file : files) {
				        if (file.isDirectory()) {
				            System.out.println(i + ". " + file.getName());
				            i++;
				        }
				    }
				} catch (Exception e) {
					System.out.print("Could not list users.");
					System.out.print(e.getMessage());
				}
			} else if(userConsoleInput.equalsIgnoreCase("q")) {
				
			} else if(userConsoleInput.equalsIgnoreCase("d")) {
				System.out.print("Disconnecting... ");
				infiniRunner.stopRunning();
				save();
				System.out.println("Done!");
				consoleOn = false;
			} else if(userConsoleInput.equalsIgnoreCase("a")) {
				System.out.println("~~ATTACK!!!~~");
				System.out.println("Users you can attack:");
				File[] files = new File(ClientUtils.getLSPath(currentServer, "users")).listFiles();
		    	int i = 1;
			    for (File file : files) {
			        if (file.isDirectory() && !(file.getName().equals(displayName))) {
			            System.out.println(i + ". " + file.getName());
			            i++;
			        }
			    }
			    if(i == 1) System.out.println("(none)");
			    int fightersAmount = BKULUtils.getLengthOfFile(ClientUtils.getUDPath("fighters.txt"));
			    if(fightersAmount >= 3) {
			    	System.out.println("Fighters needed to attack: " + fightersAmount + "/3\n"
			    						 + "You can fight!");
			    	System.out.println("Choose a user to attack, or press <q> to quit the menu: ");
			    	String userToAttack = InputGetter.nextLine();
			    	if(!userToAttack.equalsIgnoreCase("q")) {
			    		System.out.println("Declaring war on " + userToAttack + "...");
			    		String pathToAttackedUser = ClientUtils.getLSPath(currentServer, "users" + File.separator + userToAttack + File.separator);
			    		if(!BKULUtils.doesFileExist(pathToAttackedUser + "warfile.tmp")) {
			    			BKULUtils.createFile(pathToAttackedUser + "warfile.tmp");
			    			BKULUtils.writeToFile(displayName, pathToAttackedUser + "warfile.tmp");
			    			System.out.println("Waiting for other user to accept...");
			    		} else {
			    			System.out.print("Temporary file already exists. Perhaps you have already attacked this person?\n"
			    						 + "If not, then someone else has already declared war on this user.");
			    		}
			    	} else {
			    		System.out.println("Quitting attack menu...");
			    	}
			    } else {
			    	System.out.print("Fighters needed to attack: " + fightersAmount + "/3");
			    	System.out.print("Get more Fighters and try again.");
			    }
				/*BufferedReader bufread =
						new BufferedReader(new FileReader(ClientUtils.getLSPath(currentServer, "users.txt")));
				for (String line = bufread.readLine(); line != null; line = bufread.readLine()) {
					if(!line.equals(displayName)) {
						System.out.println(line);
					}
				}*/
			} else if(userConsoleInput.equalsIgnoreCase("s")) {
				System.out.println("Structures (press <m> for market):");
				BufferedReader brForStr = new BufferedReader(new FileReader(ClientUtils.getUDPath("structures.txt")));
				for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
					String[] parts = line.split("~");
					System.out.println("A Level " + parts[1] + " " + parts[0]);
				}
			} else if(userConsoleInput.equalsIgnoreCase("p")) {
				System.out.println("XP: " + BKULUtils.readFile(ClientUtils.getUDPath("xp.txt")));
				System.out.println("Gold: " + gold/*BKULUtils.readFile(ClientUtils.getUDPath("gold.txt"))*/);
			} else if(userConsoleInput.equalsIgnoreCase("updnecfiles")) {
				userSetup(currentServer);
			} else if(userConsoleInput.equalsIgnoreCase("e")) {
				System.out.println("Achievements:");
				throw new NotImplementedException();
			} else if(userConsoleInput.equalsIgnoreCase("m")) {
				BufferedReader brForStr = new BufferedReader(new FileReader(ClientUtils.getUDPath("servers" + File.separator +
						currentServer + File.separator + "Marketfile")));
				System.out.println("~~Market~~");
				String[] goodPurchases = new String[100];
				String[][] goodPurchases2;
				Arrays.fill(goodPurchases, "");
				for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
					String[] parts = line.split("~");
					System.out.println(parts[0] + ": " + parts[1] + " gold");
					goodPurchases[goodPurchases.length - 1] = parts[0];
				}
				System.out.print("Type the name of the item you would like to buy, or <q> to quit: ");
				String userInputPurchase = InputGetter.nextLine();
				boolean isPurchaseValid = false;
				if(!userInputPurchase.equalsIgnoreCase("q")) {
					for(String purchase : goodPurchases) {
						if(purchase.equalsIgnoreCase(userInputPurchase)) {
							int goldPrice = 0;
							BufferedReader brForStr2 = new BufferedReader(new FileReader(ClientUtils.getUDPath("servers" + File.separator +
									currentServer + File.separator + "Marketfile")));
							for (String line = brForStr2.readLine(); line != null; line = brForStr2.readLine()) {
								String[] parts = line.split("~");
								if(parts[0].equalsIgnoreCase(userInputPurchase)) {
									System.out.println(parts[1]);
									try { goldPrice = Integer.parseInt(parts[1]); } catch(Exception e) {
										System.out.print("Could not buy this item. Details of error:");
										System.out.print(e.getMessage());
									}
								}
							}
							gold = gold - goldPrice;
							System.out.println("Your purchase has been made! A " + userInputPurchase + " for "
													+ goldPrice + " gold.");
							if(userInputPurchase.equalsIgnoreCase("Fighter")) {
								int i = 0;
								BufferedReader brForStr3 = new BufferedReader(new FileReader(ClientUtils.getUDPath("fighters.txt")));
								for (String line = brForStr3.readLine(); line != null; line = brForStr3.readLine()) {
									i++;
								}
								String fightersName = BKULUtils.randomFighterName();
								BKULUtils.appendFileNewLn(i+1 + "~" + fightersName + "~1", ClientUtils.getUDPath("fighters.txt"));
								System.out.println("You have a new Fighter, " + fightersName + "! Welcome " +
														fightersName + " to the team!");
							}
							isPurchaseValid = true;
						}
					}
					if(!isPurchaseValid) System.out.print("Purchase invalid");
				} else {
					System.out.println("Exiting market...");
				}
			} else if(userConsoleInput.equalsIgnoreCase("v")) {
				//System.out.println("Remember: autosave activates every 1 1/2 minutes.");
				save();
			} else if(userConsoleInput.equalsIgnoreCase("i")) {
				JFrame statusFrame = new JFrame("BKUL Status Window");
				statusFrame.setLayout(null);
				statusFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				goldField = new JTextField(250);
				goldField.setLocation(20, 20);
				goldField.setSize(250, 30);
				goldField.setEditable(false);
				goldField.setText(String.valueOf(gold) + " gold");
				statusFrame.setSize(295, 80);
				statusFrame.add(goldField);
				statusFrame.setVisible(true);
			} else if(userConsoleInput.equalsIgnoreCase("l")) {
				System.out.println("~~Alliance Menu~~");
				System.out.println("You are allied with:");
			} else {
				System.out.print("Command not found. Type <h> for a list of commands.");
			}
		}
		} else {
			System.out.print("That server does not exist. Please enter another.");
		}
	}
	
	
	public static BoolIntOutcome hasGoldMine() {
		try {
			BufferedReader brForStr = new BufferedReader(new FileReader(ClientUtils.getUDPath("structures.txt")));
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
		if(!BKULUtils.doesDirectoryExist(ClientUtils.getUDPath("servers" + File.separator +
															       serverName + File.separator + "users"))) {
			BKULUtils.createDirectory(ClientUtils.getUDPath("servers" + File.separator + serverName + File.separator + "users"));
		}
		BKULUtils.createDirectory(userDataPath);
		BKULUtils.createFile(ClientUtils.getUDPath("structures.txt"));
		BKULUtils.writeToFile("Gold Mine~1\nWall~1", ClientUtils.getUDPath("structures.txt"));
		BKULUtils.createFile(ClientUtils.getUDPath("gold.txt"));
		BKULUtils.writeToFile("500", ClientUtils.getUDPath("gold.txt"));
		BKULUtils.createFile(ClientUtils.getUDPath("xp.txt"));
		BKULUtils.writeToFile("0", ClientUtils.getUDPath("xp.txt"));
		BKULUtils.createFile(ClientUtils.getUDPath("wall-health.txt"));
		BKULUtils.writeToFile("250", ClientUtils.getUDPath("wall-health.txt"));
		BKULUtils.createFile(ClientUtils.getUDPath("achievements.txt"));
		BKULUtils.createFile(ClientUtils.getUDPath("fighters.txt"));
		//BKULUtils.writeToFile("", ClientUtils.getUDPath("achievements.txt"));
	}
	
	public static void options() {
		System.out.println("~~Options~~\n"
									+ "(1) Sync folder\n"
									+ "(2) Delete local configuration folder");
		System.out.print("Choose one (letter or number), or <q> to quit: ");
		String option = InputGetter.nextLine();
		if(equalsICTwo(option, "1", "Sync folder")) {
			
		}
	}
	
	public static boolean equalsICTwo(String string, String integer, String secondString) {
		return(string.equalsIgnoreCase(secondString) || string.equalsIgnoreCase(integer));
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
			BKULUtils.writeToFile(String.valueOf(gold), ClientUtils.getUDPath("gold.txt"));
			BKULUtils.writeToFile(String.valueOf(xp), ClientUtils.getUDPath("xp.txt"));
		} catch(Exception e) {
			System.out.print("Error. Details:\n" + e.getMessage());
		}
		System.out.print("Done!\n");
	}
	
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
			if(InputGetter.nextLine().equalsIgnoreCase("y")) {
				System.out.print("Deleting the config folder " + LOCAL_CONFIG_PATH + ", press <enter> to continue or s to stop... ");
				if(!InputGetter.nextLine().equalsIgnoreCase("s")) {
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
		writeToLocalStorage(InputGetter.nextLine(), "display-name.txt");
	}

}
class InfiniteBKULRunner implements Runnable {
	
	// declare recursive counter variable
	public static int recursiveCounter = 0;
	
	// declare variable that confirms if the recursive counter variable is over or not
	public static boolean recCounterOver = false;
	
	// variable that allows the thread to be stopped
	public volatile boolean isRunning = true;
	
	@Override
	public void run() {
		// do recursive stuff here
		while(isRunning) {
			if(recursiveCounter == 6)  {
				recursiveCounter = 0;
				recCounterOver = true;
			}
			BoolIntOutcome hasGM = BKULCustomConsole.hasGoldMine();
			if(hasGM.bool) {
				if(hasGM.integer == 1 && recCounterOver == true) {
					BKULCustomConsole.gold++;
					boolean doesGoldFieldExist = false;
					try { BKULCustomConsole.goldField.setText("test"); doesGoldFieldExist = true; } catch(Exception e) {}
					if(doesGoldFieldExist) {
						BKULCustomConsole.goldField.setText(String.valueOf(BKULCustomConsole.gold) + " gold");
					}
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
	
	public void stopRunning() { isRunning = false; }
	
}
class TempFileChecker implements Runnable {
	
	// variable that allows the thread to be stopped
	public volatile boolean isRunning = true;
	
	// name of server
	public String nameOfServer = "noname";
	
	public TempFileChecker(String serverName) { nameOfServer = serverName; }
	
	@Override
	public void run() {
		while(isRunning) {
			try {
				if(BKULUtils.doesFileExist(ClientUtils.getUDPath("warfile.tmp"))) {
					System.out.print("\n" + BKULUtils.readFile(ClientUtils.getUDPath("warfile.tmp")) + " has declared war on you!");
					BKULUtils.deleteFileOrDirectory(ClientUtils.getUDPath("warfile.tmp"));
					System.out.print(nameOfServer + "> ");
				}
				Thread.sleep(250);
			} catch (Exception e) {
				e.printStackTrace();
			}
			run();
		}
	}
	
	public void stopRunning() { isRunning = false; }
	
}