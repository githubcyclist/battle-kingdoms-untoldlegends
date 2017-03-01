package menu;

import java.util.Scanner;

public class InputGetter {
	public static String nextLine() {
		String choosen = "";
		Scanner in = new Scanner(System.in);

		try {
			choosen = in.nextLine();
		} catch (Exception e1)  {}
		return choosen;
	}
}
