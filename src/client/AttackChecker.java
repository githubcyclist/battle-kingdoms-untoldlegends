package client;

import util.BKULUtils;

public class AttackChecker implements Runnable {
	
	private static String checkFor;
	private volatile boolean going = true;
	
	public AttackChecker(String toCheckFor) { checkFor = toCheckFor; }
	
	@Override
	public void run() {
		while(going) {
			if(BKULUtils.doesFileExist(checkFor)) {
				going = false;
				System.out.println("found");
			}
			System.out.println("not found");
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			run();
		}
	}
	
}