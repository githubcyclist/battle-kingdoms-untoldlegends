# Battle Kingdoms: Untold Legends
A simple text-based RPG written in Java which uses file syncing software to function.

This means all computers that are playing together have to be running the same file syncing software linked to the same account.

Some examples of file syncing software:
- Dropbox
- Google Drive
- Box

Remember, if you are playing with friends who already use your file syncing software of choice, shared folders can be used.

## How to set up the game

If you want to play the game, here are the instructions:

1. Make sure you have Java installed (just the JRE is fine).

2. Clone or download the repo.

3. Open the "rel" folder.

Now, note that there are two versions of the game: terminal-based and JFrame-based.

Here are the differences:

Terminal-based
- Uses the native terminal or command prompt to run
- No custom title

JFrame-based
- Based on the JFrame and a text box, so there is a custom title
- Uses 100% cross-platform "console" clearing
- Has more bugs, e.g. text in "console" is eraseable...

### Instructions for the terminal-based version
Open a command prompt or terminal and run the command: "java -jar bkul-text-(latest version).jar"

### Instructions for the JFrame-based version
On most systems, you can just double-click the jar file.
If yours doesn't, then we can use a almost identical procedure to the above:
Open a command prompt or terminal and run the command: "java -jar bkul-customconsole-(latest version).jar"

------------------------------------------------------------------------------------------------------------------------------

5. Make sure you create or join a group which is in a folder that is being synced to other computers.

## How to develop the game

To develop the game, first make sure you have Java installed, and not just the JRE, also the JDK (which I'm sure you do).
Then, either fork it or create a pull request.

It is an Eclipse project, so just put it in your Eclipse workspace and you're good to go!

But first, note that there are two different versions of the game (differences discussed above).

The terminal-based version is in the BKULText.java file, and the custom console version is in the BKULCustomConsole.java file.
