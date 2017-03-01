package api;

public class BKULRunner {
	public static void run(String server, ServerJoinCallback sjc) throws Exception {
		sjc.join(server);
	}
}
