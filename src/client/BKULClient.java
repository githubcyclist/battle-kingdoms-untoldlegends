package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextField;

import api.BKULRunner;
import api.ServerJoinCallback;
import game.BKULText;
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
		public static String basicDataPath;
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
			BKULRunner.run(currentServer, new ServerJoinCallback() {
				@Override
				public void join(String server) throws Exception {
					if(BKULUtils.doesDirectoryExist(ClientUtils.getGDPath("servers" + File.separator + name, gameDataPath))) {
						System.out.println("Connecting to server " + name + "...");
						currentServer = name;
						basicDataPath = ClientUtils.getGDPath("servers" + File.separator + currentServer + File.separator
								+ "users", gameDataPath);
						userDataPath = basicDataPath + File.separator + displayName;
						InfiniteBKULRun infiniRunner = new InfiniteBKULRun(BKULClient.this);
						Thread sessionInfiniteThread = new Thread((Runnable) infiniRunner);
						sessionInfiniteThread.start();
						TempChecker declareWarDetector = new TempChecker(currentServer);
						Thread warDetectorRunner = new Thread(declareWarDetector);
						warDetectorRunner.start();
						System.out.println("Connected! Switching to console. <h> for help");
						if(!BKULUtils.doesDirectoryExist(ClientUtils.getGDPath("servers" + File.separator + currentServer +
								File.separator + "users" + File.separator + displayName, gameDataPath))) {
							System.out.println("Welcome! You start with:\n"
									+ "- 500 Gold\n"
									+ "- A Level 1 Gold Mine to help you get more Gold\n"
									+ "- A Level 1 Wall to provide basic protection against enemies\n"
									+ "Here is the help screen to get you started:");
							ClientUtils.printHelp();
							ClientUtils.userSetup(currentServer);
						}
						try {
							gold = Integer.parseInt(BKULUtils.readFile(getUDPath("gold.txt")));
							xp = Integer.parseInt(BKULUtils.readFile(getUDPath("xp.txt")));
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
													 + "Connected to server " + currentServer, ClientUtils.ANSI_CYAN);
							} else if(userConsoleInput.equalsIgnoreCase("q")) {
								ClientUtils.save();
								System.out.println("Goodbye.");
								System.exit(0);
							} else if(userConsoleInput.equalsIgnoreCase("u")) {
								System.out.println("~~Users on this Server including you~~");
								System.out.println("You are: " + displayName);
								try {
									int i = 1;
									File[] files = new File(getLSPath(currentServer, "users")).listFiles();
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
								File[] files = new File(getLSPath(currentServer, "users")).listFiles();
						    	int i = 1;
							    for (File file : files) {
							        if (file.isDirectory() && !(file.getName().equals(displayName))) {
							            System.out.println(i + ". " + file.getName());
							            i++;
							        }
							    }
							    if(i == 1) System.out.println("(none)");
							    int fightersAmount = BKULUtils.getLengthOfFile(getUDPath("fighters.txt"));
							    int wallHealth = Integer.parseInt(BKULUtils.readFile(getUDPath("wall-health.txt")));
							    if(fightersAmount >= 3 ) {
							    	System.out.println("Fighters needed to attack: " + fightersAmount + "/3\n"
							    						 + "You can fight!");
							    	System.out.println("Choose a user to attack, or press <q> to quit the menu: ");
							    	String userToAttack = UserInputGet.nextLine();
							    	if(!userToAttack.equalsIgnoreCase("q")) {
							    		System.out.println("Declaring war on " + userToAttack + "...");
							    		String pathToAttackedUser = getLSPath(currentServer, "users" + File.separator + userToAttack + File.separator);
							    		if(!BKULUtils.doesFileExist(pathToAttackedUser + "warfile.tmp")) {
							    			BKULUtils.createFile(pathToAttackedUser + "warfile.tmp");
							    			BKULUtils.writeToFile(displayName, pathToAttackedUser + "warfile.tmp");
							    			System.out.println("Waiting for other user to accept...");
							    			AttackChecker acceptCheck = new AttackChecker(getUDPath("acceptfile.tmp"));
							    			Thread sessionCheckThread = new Thread(acceptCheck);
							    			sessionCheckThread.start();
							    			sessionCheckThread.join();
							    			if(BKULUtils.readFile(getUDPath("acceptfile.tmp")).equals(userToAttack)) {
							    				BKULUtils.deleteFileOrDirectory(getUDPath("acceptfile.tmp"));
								    			System.out.println("User has accepted! Beginning fight...");
								    			attack(userToAttack);
							    			} else if(BKULUtils.readFile(getUDPath("acceptfile.tmp")).equals(userToAttack + "~decline")){
							    				System.out.println("User has declined.");
							    			} else {
							    				System.out.println("Encountered accept file, but it is empty or doesn't contain the correct user.");
							    			}
							    		} else {
							    			System.out.println("Temporary file already exists. Perhaps you have already attacked this person?\n"
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
										new BufferedReader(new FileReader(getUDPath(currentServer, "users.txt")));
								for (String line = bufread.readLine(); line != null; line = bufread.readLine()) {
									if(!line.equals(displayName)) {
										System.out.println(line);
									}
								}*/
							} else if(userConsoleInput.equalsIgnoreCase("s")) {
								System.out.println("Structures (press <m> for market):");
								BufferedReader brForStr = new BufferedReader(new FileReader(getUDPath("structures.txt")));
								for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
									String[] parts = line.split("~");
									System.out.println("A Level " + parts[1] + " " + parts[0]);
								}
							} else if(userConsoleInput.equalsIgnoreCase("p")) {
								System.out.println("XP: " + BKULUtils.readFile(getUDPath("xp.txt")));
								System.out.println("Gold: " + gold/*BKULUtils.readFile(getUDPath("gold.txt"))*/);
							} else if(userConsoleInput.equalsIgnoreCase("updnecfiles")) {
								ClientUtils.userSetup(currentServer);
							} else if(userConsoleInput.equalsIgnoreCase("e")) {
								System.out.println("Achievements:");
								throw new NotImplementedException();
							} else if(userConsoleInput.equalsIgnoreCase("m")) {
								BufferedReader brForStr = new BufferedReader(new FileReader(ClientUtils.getGDPath("servers" + File.separator +
										currentServer + File.separator + "Marketfile", gameDataPath)));
								System.out.println("~~Market~~");
								String[] goodPurchases = new String[100];
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
													currentServer + File.separator + "Marketfile", gameDataPath)));
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
											if(gold >= goldPrice) {
												gold = gold - goldPrice;
												System.out.println("Your purchase has been made! A " + userInputPurchase + " for "
																	+ goldPrice + " gold. You now have " + gold + " gold.");
												if(userInputPurchase.equalsIgnoreCase("Fighter")) {
													int i = 0;
													BufferedReader brForStr3 = new BufferedReader(new FileReader(getUDPath("fighters.txt")));
													for (String line = brForStr3.readLine(); line != null; line = brForStr3.readLine()) {
														i++;
													}
													String fightersName = BKULUtils.randomFighterName();
													BKULUtils.appendFileNewLn(i+1 + "~" + fightersName + "~1~10", getUDPath("fighters.txt"));
													System.out.println("You have a new Fighter, " + fightersName + "! Welcome " +
																		fightersName + " to the team!");
												}
												isPurchaseValid = true;
											} else {
												System.out.println("Sorry, but you can't afford this: " + userInputPurchase + ". Get " + (goldPrice - gold) + " more gold first.");
											}
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
							} else if(userConsoleInput.equalsIgnoreCase("o")) {
								System.out.println("Welcome to Developer Toolkit (BETA)!");
								System.out.println("Would you like to:\n"
										+ "a) Create a new workspace\n"
										+ "b) Use an existing one");
								System.out.print("Choose one: ");
								String workspaceInput = new Scanner(System.in).nextLine();
								if(workspaceInput.equalsIgnoreCase("a")) {
									System.out.print("Make sure you have an active Internet connection.\nThen press <enter> to continue...");
									new Scanner(System.in).nextLine();
									System.out.println("Downloading API files from\n" +
											"https://raw.githubusercontent.com/githubcyclist/battle-kingdoms-api/master/...");
									String rawGithub = "https://raw.githubusercontent.com/githubcyclist/battle-kingdoms-api/master/";
									String workspace = BKULUtils.getCurrentWorkingDir() +
											File.separator + "workspace";
									if(!BKULUtils.doesDirectoryExist(workspace)) {
										BKULUtils.createDirectory(workspace);
									}
									if(!BKULUtils.doesFileExist(workspace + File.separator + "ServerJoinCallback.java")) {
										BKULUtils.createFile(workspace + File.separator + "ServerJoinCallback.java");
									}
									
									if(!BKULUtils.doesFileExist(workspace + File.separator + "BKULRunner.java")) {
										BKULUtils.createFile(workspace + File.separator + "BKULRunner.java");
									}
									try {
										URL website = new URL(rawGithub + "ServerJoinCallback.java");
										ReadableByteChannel rbc = Channels.newChannel(website.openStream());
										FileOutputStream fos = new FileOutputStream(BKULUtils.getCurrentWorkingDir() +
												File.separator + "workspace" + File.separator + "ServerJoinCallback.java");
										fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
										URL website1 = new URL(rawGithub + "BKULRunner.java");
										ReadableByteChannel rbc1 = Channels.newChannel(website1.openStream());
										FileOutputStream fos1 = new FileOutputStream(BKULUtils.getCurrentWorkingDir() +
												File.separator + "workspace" + File.separator + "BKULRunner.java");
										fos1.getChannel().transferFrom(rbc1, 0, Long.MAX_VALUE);
										String mp = workspace + File.separator + "Main.java";
										BKULUtils.createFile(mp);
										BKULUtils.appendFileNewLn("public class Main {\n"
												+ "	public static void main(String[] args) throws Exception{\n"
												+ "		BKULRunner.run(\"server name\", new ServerJoinCallback() {\n"
												+ "			@Override\n"
												+ "			public void joinServer(String serverName) {\n"
												+ "				// enter code here\n"
												+ "			}\n"
												+ "		});\n"
												+ "	}\n"
												+ "}", mp);
										System.out.println("Successfully copied files! Go to the \"workspace\" folder where your game is.");
										System.out.println("A file called Main.java was created there.");
										System.out.println("Add your code inside public void join.");
										devConsole();
									} catch(Exception e) {
										System.out.println("Couldn't copy files.\n"
												+ "Please check your Internet connection and try again.");
									}
								} else if(workspaceInput.equalsIgnoreCase("b")) {
									System.out.println("Add your code inside public void join, in Main.java in your workspace.");
									devConsole();
								} else {
									System.out.println("Invalid command. Enter either a or b (not case-sensitive).");
								}
							} else {
								System.out.println("Command not found. Type <h> for a list of commands.");
							}
						}
						} else {
							System.out.print("That server does not exist. Please enter another.");
						}
				}
			});
		}
		public static String getGDPath(String fileOrDir) { return gameDataPath + File.separator + fileOrDir; }
	
		public static String getUDPath(String fileOrDir) { return userDataPath + File.separator + fileOrDir; }
	
		public static String getLSPath(String server, String fileOrDir) {
			return getGDPath("servers" + File.separator + server + File.separator + fileOrDir);
		}
	
		public static String getOUPath(String otherUser, String fileOrDir) {
			return basicDataPath + File.separator + otherUser;
		}
		public static void attack(String opponent) throws Exception {
			System.out.print("Loading fighter data... ");
			int wallHealth = Integer.parseInt(BKULUtils.readFile(getUDPath("wall-health.txt")));
			int i3 = 0;
			BufferedReader brForStr3 = new BufferedReader(new FileReader(getUDPath("fighters.txt")));
			for (String line3 = brForStr3.readLine(); line3 != null; line3 = brForStr3.readLine()) {
				String[] splitLines = line3.split("~");
				System.out.println(splitLines[0] + ". " + splitLines[1] + " - Level " + splitLines[2]);
				i3++;
			}
			System.out.print("Done!\n");
			System.out.println("Beginning fight with " + opponent + "...");
			BKULUtils.createFile(getUDPath("yourturn.tmp"));
			boolean fightOn = true;
			while(fightOn) {
				if(BKULUtils.doesFileExist(getUDPath("yourturn.tmp"))) {
					System.out.println("~~Fighters you can Use~~");
					int i = 0;
					BufferedReader brForStr = new BufferedReader(new FileReader(getUDPath("fighters.txt")));
					for (String line = brForStr.readLine(); line != null; line = brForStr.readLine()) {
						i++;
					}
					String[][] splitLinesArray = new String[2][i];
					int i2 = 0;
					BufferedReader brForStr2 = new BufferedReader(new FileReader(getUDPath("fighters.txt")));
					for (String line2 = brForStr2.readLine(); line2 != null; line2 = brForStr.readLine()) {
						String[] splitLines = line2.split("~");
						splitLinesArray[i2] = splitLines;
						System.out.println(splitLines[0] + ". " + splitLines[1] + " - Level " + splitLines[2]);
						i2++;
					}
					System.out.print("Choose a fighter to use, or <s> to skip turn: ");
					String fighterToUse = UserInputGet.nextLine();
					if(!fighterToUse.equalsIgnoreCase("s")) {
						System.out.println("~~Fighters you can Attack~~");
						int i4 = 0;
						String[] splitLinesForAttack;
						BufferedReader brForStr4 = new BufferedReader(new FileReader
								(getOUPath(opponent, "fighters.txt")));
						for (String line4 = brForStr4.readLine(); line4 != null; line4 = brForStr.readLine()) {
							splitLinesForAttack = line4.split("~");
							splitLinesArray[i4] = splitLinesForAttack;
							System.out.println(splitLinesForAttack[0] + ". " +
									splitLinesForAttack[1] + " - Level " + splitLinesForAttack[2]);
							i4++;
						}
						System.out.print("Choose a fighter to attack, or <s> to skip turn: ");
						String fighterToAttack = UserInputGet.nextLine();
						if(!fighterToUse.equalsIgnoreCase("s")) {
							System.out.println("Submitting attack info...");
							
						} else {
							
						}
					} else {
						BKULUtils.deleteFileOrDirectory(getUDPath("yourturn.tmp"));
						BKULUtils.createFile(getOUPath(opponent, "yourturn.tmp"));
					}
				} else {
					System.out.println("Waiting for opponent to attack...");
					boolean going = true;
					int exitCode = 0;
					String fileContent = "";
					while(going) {
						if(BKULUtils.doesFileExist(getUDPath("attackfile.tmp"))) {
							fileContent = BKULUtils.readFile(getUDPath("attackfile.tmp"));
							going = false;
						} else if(BKULUtils.doesFileExist(getUDPath("yourturn.tmp"))) {
							going = false;
							exitCode = 1;
						}
						if(exitCode == 1) {
							System.out.println("Opponent has decided to skip their turn.");
						} else if(exitCode == 0) {
							String[] fileContentParts = fileContent.split("~");
							System.out.println("Opponent has used fighter "
									+ fileContentParts[0] + " to deal " + fileContentParts[1]
											+ " damage to your wall!");
						} else {
							System.out.println("Error: unrecognized exit code.");
						}
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		public static void devConsole() {
			System.out.println("Switching to dev console (<h> for help)...");
			boolean toolOn = true;
			while(toolOn) {
				System.out.print("dev> ");
				String input = new Scanner(System.in).nextLine();
				if(input.equalsIgnoreCase("q")) {
					toolOn = false;
				} else if(input.equalsIgnoreCase("r")){
					try {
						runProcess(BKULUtils.getCurrentWorkingDir() +
			    		  		File.separator + "workspace", "java Main");
					} catch (Exception e) {
		      			e.printStackTrace();
					}
				} else if(input.equalsIgnoreCase("h")) {
					System.out.println("~~DEV CONSOLE HELP~~\n"
							+ "<enter> or <c> to compile\n"
							+ "<r> to test your program (not recommended if it has input)\n"
							+ "<q> to exit the tool");
				} else if(input.equalsIgnoreCase("c")) {
					try {
						runProcess(BKULUtils.getCurrentWorkingDir() +
			    		  		File.separator + "workspace", "javac Main.java");
					} catch (Exception e) {
		      			e.printStackTrace();
					}
				} else if(input.equalsIgnoreCase("")) {
					try {
						runProcess(BKULUtils.getCurrentWorkingDir() +
			    		  		File.separator + "workspace", "javac Main.java");
					} catch (Exception e) {
		      			e.printStackTrace();
					}
				} else if(input.equalsIgnoreCase("p")) {
					System.out.println("~~PORT YOUR CODE TO A SERVER~~");
					System.out.print("Please specify a name for the server: ");
					String serverName = userInputScanner.nextLine();
					displayName = BKULText.getFileFromLocalStorage("display-name.txt");
					userDataPath = getGDPath("servers" + File.separator + serverName + File.separator + "users"
							+ File.separator + displayName);
					BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName));
					BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
					BKULUtils.createFile(getLSPath(serverName, "Marketfile"));
					BKULUtils.writeToFile("Fighter~300", getLSPath(serverName, "Marketfile"));
					ClientUtils.userSetup(serverName);
					System.out.println("Server was created successfully! Adding your code...");
					BKULUtils.createFile(getLSPath(serverName, "custom-server"));
					final Path targetPath = Paths.get(getLSPath(serverName,  serverName + "-customfiles"));
					final Path sourcePath =
							Paths.get(BKULUtils.getCurrentWorkingDir() + File.separator + "workspace");
					BKULUtils.createDirectory(targetPath.toString());
					try {
						Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult preVisitDirectory(final Path dir,
							                final BasicFileAttributes attrs) throws IOException {
											Files.createDirectories(targetPath.resolve(sourcePath
							                    .relativize(dir)));
											return FileVisitResult.CONTINUE;
							    }

							    @Override
							    public FileVisitResult visitFile(final Path file,
							         final BasicFileAttributes attrs) throws IOException {
							            Files.copy(file,
							                    targetPath.resolve(sourcePath.relativize(file)));
							            return FileVisitResult.CONTINUE;
							    }
						});
					} catch (IOException e) {
						System.out.println("Failed to copy files. " + e.getMessage());
					}
					System.out.println("Done!");
				} else {
					System.out.println("Command not found. <h> to list commands.");
				}
			}
		}
		private static void printLines(InputStream ins) throws Exception {
		    String line = null;
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(ins));
		    while ((line = in.readLine()) != null) {
		        System.out.println(line);
		    }
		  }

		  private static void runProcess(String dir, String command) throws Exception {
		    Process pro = Runtime.getRuntime().exec(command, null, new File(dir));
		    printLines(pro.getInputStream());
		    printLines(pro.getErrorStream());
		    pro.waitFor();
		    System.out.println("Process finished with exit code " + pro.exitValue());
		  }
}
