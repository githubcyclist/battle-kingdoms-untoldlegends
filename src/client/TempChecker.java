package client;

import game.BKULText;
import menu.InputGetter;
import util.BKULUtils;

public class TempChecker implements Runnable {
	
	// variable that allows the thread to be stopped
	public volatile boolean isRunning = true;
	
	// name of server
	public String nameOfServer = "noname";
	
	public TempChecker(String serverName) { nameOfServer = serverName; }
	
	@Override
	public void run() {
		while(isRunning) {
			try {
				if(BKULUtils.doesFileExist(BKULText.getUDPath("warfile.tmp"))) {
					System.out.print("\n" + BKULUtils.readFile(BKULClient.getUDPath("warfile.tmp")) + " has declared war on you!");
					BKULUtils.deleteFileOrDirectory(BKULClient.getUDPath("warfile.tmp"));
					System.out.print("Join fight? [y]es or [n]o ");
					if(InputGetter.nextLine().equalsIgnoreCase("y")) {
						BKULClient.attack(BKULUtils.readFile(BKULClient.getUDPath("warfile.tmp")));
					}
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
