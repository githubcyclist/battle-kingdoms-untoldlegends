package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextField;

import menu.InputGetter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.BKULUtils;

public class BKULClient {
	public static Scanner userInputScanner = new Scanner(System.in);
	
	private static class UserInputGet {
		public static String nextLine() {
			if(inConsole) {
				return InputGetter.nextLine();
			} else {
				return userInputScanner.nextLine();
			}
		}
	}
	
	public static String gameDataPath;
	private static String currentServer;
	public static String userDataPath;
	public static String displayName;
	public static boolean inConsole;
	
	public BKULClient(String gameDataFolder, String displayNameConstructor, String currentServer, boolean isConsole) {
		gameDataPath = gameDataFolder;
		displayName = displayNameConstructor;
	}
	
	public static int gold, xp;
	public static JTextField goldField;
	
	// this method contains the main game loop
	public void joinServer(String name) throws Exception {
		if(BKULUtils.doesDirectoryExist(ClientUtils.getGDPath("servers" + File.separator + name))) {
			System.out.println("Connecting to server " + name + "...");
			currentServer = name;
			userDataPath = ClientUtils.getGDPath("servers" + File.separator + currentServer + File.separator + "users"
					+ File.separator + displayName);
			BufferedReader br;
			InfiniteBKULRun infiniRunner = new InfiniteBKULRun(this);
			Thread sessionInfiniteThread = new Thread((Runnable) infiniRunner);
			sessionInfiniteThread.start();
			TempChecker declareWarDetector = new TempChecker(currentServer);
			Thread warDetectorRunner = new Thread(declareWarDetector);
			warDetectorRunner.start();
			System.out.println("Connected! Switching to console. <h> for help");
			if(!BKULUtils.doesDirectoryExist(ClientUtils.getGDPath("servers" + File.separator + currentServer +
					File.separator + "users" + File.separator + displayName))) {
				System.out.println("Welcome! You start with:\n"
						+ "- 500 Gold\n"
						+ "- A Level 1 Gold Mine to help you get more Gold\n"
						+ "- A Level 1 Wall to provide basic protection against enemies\n"
						+ "Here is the help screen to get you started:");
				ClientUtils.printHelp();
				ClientUtils.userSetup(currentServer);
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
				userConsoleInput = UserInputGet.nextLine();
				if(userConsoleInput.equalsIgnoreCase("h")) {
					ClientUtils.printHelp();
				} else if(userConsoleInput.equalsIgnoreCase("c")) {
					BKULUtils.clearScreen();
					ClientUtils.printANSIColor("~~Battle Kingdoms: Untold Legends~~\n"
										 + "Connected to server Sanger", ClientUtils.ANSI_CYAN);
				} else if(userConsoleInput.equalsIgnoreCase("q")) {
					ClientUtils.save();
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
					ClientUtils.save();
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
				    	String userToAttack = UserInputGet.nextLine();
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
							new BufferedReader(new FileReader(ClientUtils.getUDPath(currentServer, "users.txt")));
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
					ClientUtils.userSetup(currentServer);
				} else if(userConsoleInput.equalsIgnoreCase("e")) {
					System.out.println("Achievements:");
					throw new NotImplementedException();
				} else if(userConsoleInput.equalsIgnoreCase("m")) {
					BufferedReader brForStr = new BufferedReader(new FileReader(ClientUtils.getGDPath("servers" + File.separator +
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
					String userInputPurchase = UserInputGet.nextLine();
					boolean isPurchaseValid = false;
					if(!userInputPurchase.equalsIgnoreCase("q")) {
						for(String purchase : goodPurchases) {
							if(purchase.equalsIgnoreCase(userInputPurchase)) {
								int goldPrice = 0;
								BufferedReader brForStr2 = new BufferedReader(new FileReader(ClientUtils.getGDPath("servers" + File.separator +
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
					ClientUtils.save();
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
	
		
}
