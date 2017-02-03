package game;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import util.BKULUtils;

public class BKULWindowed {
	
	public static String gameDataPath = "";
	public static String userDataPath = "";
	
	public static void main(String[] args) throws Exception {
		JFrame bkulFrame = new JFrame("Battle Kingdoms: Untold Legends PRE_Pre-Alpha");
		bkulFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		bkulFrame.setSize(500, 500);
		bkulFrame.setVisible(true);
		setup(bkulFrame);
	}
	
	public static void setup(JFrame j) throws Exception {
		gameDataPath = JOptionPane.showInputDialog("Please enter a path to a game data folder, or <q> to quit:")
				+ File.separator + "battlekingdoms-data";
		if(BKULUtils.doesDirectoryExist(gameDataPath)) {
			int result = JOptionPane.showConfirmDialog(j, "Yep, found a game data folder there!\nContains the group "
						+ BKULUtils.readFile(getGDPath("group-name.txt")) + "\nJoin this group?", "Join group?",
						JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				if(BKULUtils.doesDirectoryExist(gameDataPath)) {
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
				BKULUtils.createFile(getGDPath("group-name.txt"));
				BKULUtils.writeToFile(getGDPath("group-name.txt"), getGDPath(JOptionPane.showInputDialog(
						"Game data folders contain a group, which must be named.\nEnter your new group's name:")));
			} else if(result2 == JOptionPane.NO_OPTION) {
				setup(j);
			} else {
				throw new Exception("Unrecognized option.");
			}
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
