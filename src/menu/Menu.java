package menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javaConsole.BKULConsole;

/**
 * @brief A menu system for organizing code in the JavaConsole.
 * 
 * @author David MacDermot
 *
 * @date 02-07-2012
 * 
 * @bug
 */
public class Menu {

	private BKULConsole console;
	
	/**
	 * @brief Class Constructor
	 * @param c an instance of the JavaConsole UI
	 */
	public Menu(BKULConsole c) {
		console = c;
	}

	/**
	 * @brief A menu item object.
	 * 
	 * @author David MacDermot
	 *
	 * @date 02-07-2012
	 * 
	 * @bug
	 */
	public class MenuItem {

		private MenuCallback _mc;
		private String _text;

		/**
		 * @brief Class Constructor
		 * @param text The text to display
		 * @param mc an MenuCallback object
		 */
		public MenuItem(String text, MenuCallback mc) {
			_mc = mc;
			_text = text;
		}

		/**
		 * @return the MenuCallback object
		 */
		public MenuCallback get_mc() {
			return _mc;
		}

		/**
		 * @return the display text
		 */
		public String get_text() {
			return _text;
		}

	}

	private ArrayList<MenuItem> Items = new ArrayList<MenuItem>();
	
	/**
	 * @param text The text to display
	 * @param mc an MenuCallback object
	 * @return boolean true if successful.
	 */
	public boolean add(String text, MenuCallback mc) {
		return Items.add(new MenuItem(text, mc));
	}

	/**
	 * @brief Display the list of menu item choices
	 */
	public void show() {
		String chosen = "";
		Scanner in = new Scanner(System.in);

		String[] goodOptions = new String[Items.size()];
		for (int i = 0; i < Items.size(); ++i) {
			MenuItem mi = Items.get(i);
			goodOptions[i] = mi.get_text();
		}

		try {
			chosen = in.nextLine();
		} catch (Exception e1)  {}
		int indexOfChosen = getIndex(Items, chosen);
		MenuItem mi = Items.get(getIndex(Items, chosen));
		MenuCallback mc = mi.get_mc();
		mc.Invoke();
	}
	public int getIndex(ArrayList<MenuItem> list, String toFind) {
		int i = 0;
		for(MenuItem obj : list) {
			if(obj.get_text().equalsIgnoreCase(toFind)) {
				return i;
			}
			i++;
		}
		return 0;
	}
}
