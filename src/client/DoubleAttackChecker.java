package client;

import util.BKULUtils;

public class DoubleAttackChecker implements Runnable {
	
	private static String checkFor;
	private static String checkFor2;
	private volatile boolean going = true;
	
	public DoubleAttackChecker(String toCheckFor, String secondToCheckFor) {
		checkFor = toCheckFor;
		checkFor2 = secondToCheckFor;
	}
	
	@Override
	public void run() {
		while(going) {
			if(BKULUtils.doesFileExist(checkFor)) {
				going = false;
			}
			if(BKULUtils.doesFileExist(checkFor2)) {
				going = false;
			}
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			run();
		}
	}
	
}