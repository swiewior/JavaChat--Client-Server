package server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

	static public void setup() throws IOException {

		// get the global logger to configure it
		Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		LOG.setLevel(Level.SEVERE);
		fileTxt = new FileHandler("Server.log");

		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		LOG.addHandler(fileTxt);
	}
	
	static public void close() throws SecurityException {
		fileTxt.close();
	}
}
 
