package game;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import client.ClientUtils;
import menu.InputGetter;
import util.BKULUtils;

public class BKULWindowed {
	
	public static String gameDataPath = "";
	public static String userDataPath = "";
	public static final String LOCAL_CONFIG_PATH = BKULUtils.getCurrentWorkingDir() + File.separator + "local-config";
	public static String displayName = "unnamed";
	
	public static void main(String[] args) throws Exception {
		JFrame bkulFrame = new JFrame("Battle Kingdoms: Untold Legends Pre-Alpha");
		bkulFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		bkulFrame.setSize(500, 500);
		bkulFrame.setVisible(true);
		if(BKULUtils.doesDirectoryExist(LOCAL_CONFIG_PATH)) {
			displayName = getFileFromLocalStorage("display-name.txt");
			JOptionPane.showMessageDialog(null, "Welcome back, " + displayName);
		} else {
			setup(bkulFrame);
		}
	}
	public static void setup(JFrame j) throws Exception {
		gameDataPath = JOptionPane.showInputDialog("Please enter a path to a game data folder, or <q> to quit:")
				+ File.separator + "battlekingdoms-data";
		if(!gameDataPath.equalsIgnoreCase("q" + File.separator + "battlekingdoms-data") && gameDataPath != null) {
		if(BKULUtils.doesDirectoryExist(gameDataPath)) {
			int result = JOptionPane.showConfirmDialog(j, "Yep, found a game data folder there!\nContains the group "
						+ BKULUtils.readFile(getGDPath("group-name.txt")) + "\nJoin this group?", "Join group?",
						JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				if(BKULUtils.doesDirectoryExist(gameDataPath)) {
					displayNamePrompt();
					writeToLocalStorage(gameDataPath, "sync-folder.txt");
					JOptionPane.showMessageDialog(j, "Setup is complete! The game will now close.\nRestart to begin playing!");
					System.exit(0);
				} else {
				}
			} else if(result == JOptionPane.NO_OPTION) {
				setup(j);
			} else {
				throw new Exception("Unrecognized option.");
			}
		} else {
			int result2 = JOptionPane.showConfirmDialog(j, "Could not find a game data folder there.\nWould you like to create it?",
					"Confirmation", JOptionPane.YES_NO_OPTION);
			if(result2 == JOptionPane.YES_OPTION) {
				BKULUtils.createDirectory(gameDataPath);
				BKULUtils.createDirectory(LOCAL_CONFIG_PATH);
				BKULUtils.createFile(getGDPath("group-name.txt"));
				BKULUtils.writeToFile(JOptionPane.showInputDialog(
						"Game data folders contain a group, which must be named.\nEnter your new group's name:"),
						getGDPath("group-name.txt"));
				displayNamePrompt();
				int result3 = JOptionPane.showConfirmDialog(j, "Your new game data folder has no servers in it.\nCreate one now?",
						"Create fresh server?", JOptionPane.YES_NO_OPTION);
				if(result3 == JOptionPane.YES_OPTION) {
					String serverName = JOptionPane.showInputDialog("Please specify a name for the server:");
					userDataPath = getGDPath("servers" + File.separator + serverName + File.separator + "users"
							+ File.separator + displayName);
					System.out.println(userDataPath);
					BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName));
					BKULUtils.createDirectory(getGDPath("servers" + File.separator + serverName + File.separator + "users"));
					BKULUtils.createFile(getLSPath(serverName, "Marketfile"));
					BKULUtils.writeToFile("Fighter~300", getLSPath(serverName, "Marketfile"));
					ClientUtils.userSetup(serverName);
					JOptionPane.showMessageDialog(j, "Server was created successfully!");
				} else {
					
				}
				writeToLocalStorage(gameDataPath, "sync-folder.txt");
				JOptionPane.showMessageDialog(j, "Setup is complete! The game will now close.\nRestart to begin playing!");
				System.exit(0);
			} else if(result2 == JOptionPane.NO_OPTION) {
				setup(j);
			} else {
				throw new Exception("Unrecognized option.");
			}
		}
		} else {
			System.exit(0);
		}
	}
	
public static String getLocalStoragePath(String fileName) { return LOCAL_CONFIG_PATH + File.separator + fileName; }
	
	// method to get file from local-config folder
	public static String getFileFromLocalStorage(String fileName) {
		try {
			return BKULUtils.readFile(LOCAL_CONFIG_PATH + File.separator + fileName);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There was a problem reading this local configuration file: " + fileName + ".\nError details: " + e.getMessage() + "\n"
								+ "If you've tampered with the file, re-create it: " + fileName + ".\n"
								+	"Would you like to delete the local-config folder? WARNING: This will delete display name and sync folder info.\n"
								+ "[y]es or [n]o ", "Could not load local config file", JOptionPane.ERROR_MESSAGE);
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
	
	public static void displayNamePrompt() {
		displayName = JOptionPane.showInputDialog("You need to choose a display name to play.\n" +
				"Please enter it here:");
		writeToLocalStorage(displayName, "display-name.txt");
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
	
	public static String getGDPath(String fileOrDir) { return gameDataPath + File.separator + fileOrDir; }
	
	public static String getUDPath(String fileOrDir) { return userDataPath + File.separator + fileOrDir; }

	public static String getLSPath(String server, String fileOrDir) {
		return getGDPath("servers" + File.separator + server + File.separator + fileOrDir);
	}

	public static String getOUPath(String otherUser, String serverName, String fileOrDir) {
		return getGDPath("servers" + File.separator + serverName + File.separator + "users" + File.separator + otherUser);
	}
}
