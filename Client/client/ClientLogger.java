package client;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientLogger 
{
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

	static public void setup() throws IOException 
	{
		// get the global logger to configure it
		Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		LOG.setLevel(Level.OFF);
		fileTxt = new FileHandler("Client.log");

		// create a TXT formatter
		LOG.addHandler(fileTxt);
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
	}
}