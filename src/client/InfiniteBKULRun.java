package client;

import game.BKULText;
import util.BoolIntOutcome;

public class InfiniteBKULRun implements Runnable {
	// declare recursive counter variable
		public static int recursiveCounter = 0;
		
		// declare variable that confirms if the recursive counter variable is over or not
		public static boolean recCounterOver = false;
		
		// variable that allows the thread to be stopped
		public volatile boolean isRunning = true;
		
		public static BKULClient client;
		
		public InfiniteBKULRun(BKULClient clientArg) { client = clientArg; }
		
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			// do recursive stuff here
			while(isRunning) {
				if(recursiveCounter == 6)  {
					recursiveCounter = 0;
					recCounterOver = true;
				}
				BoolIntOutcome hasGM = BKULText.hasGoldMine();
				if(hasGM.bool) {
					if(hasGM.integer == 1 && recCounterOver == true) {
						client.gold++;
						boolean doesGoldFieldExist = false;
						try { client.goldField.setText("test"); doesGoldFieldExist = true; } catch(Exception e) {}
						if(doesGoldFieldExist) {
							client.goldField.setText(String.valueOf(client.gold) + " gold");
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
