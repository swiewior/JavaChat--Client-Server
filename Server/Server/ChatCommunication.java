package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatCommunication implements Runnable,CommonSettings
{
	// Zmienne globalne
	Thread thread;
	Socket socket;
	DataInputStream inputstream;
	String RFC, filesharing_username;
	Server Parent;	
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME ); 
	
	// Inicjalizacja socketa do klienta
	ChatCommunication(Server chatserver, Socket clientsocket)
	{				

		Parent = chatserver;
		socket = clientsocket;	
		try {	
			inputstream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));		
		}catch(IOException e) { 
			LOG.log(Level.WARNING, "ChatCommunication::ChatCommunication: ", e);
		}
		thread = new Thread(this);
		thread.start();	
	}

	// Implementacja interfejsu wątków
	@Override
	public void run()
	{
		while(thread != null)
		{
			try {				
				RFC = inputstream.readUTF();
				LOG.log(Level.INFO, "RFC: " + RFC);
				// Obsługa wskaźników
				if(RFC.startsWith("REGI")){
					LOG.log(Level.INFO, "REGI");
					Parent.RegisterRequest(socket, RFC.substring(5, RFC.indexOf(":")), 
						RFC.substring(RFC.indexOf(":")+1));
				}

				if(RFC.startsWith("HELO")) {	
					LOG.log(Level.INFO, "HELO");
					Parent.AddUser(socket,RFC.substring(5, RFC.indexOf(":")), RFC.substring(RFC.indexOf(":")+1));														
				}

				if(RFC.startsWith("QUIT")) {
					Parent.RemoveUser(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1),REMOVE_USER);
					LOG.log(Level.INFO, "QUIT");
					QuitConnection();	
				}
								
				if(RFC.startsWith("KICK")) {
					Parent.RemoveUser(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1),KICK_USER);
					LOG.log(Level.INFO, "KICK");
					QuitConnection();
				}
				
				if(RFC.startsWith("CHRO")) {
					Parent.ChangeRoom(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
					LOG.log(Level.INFO, "CHRO");
				}

				if(RFC.startsWith("MESS")) {
					Parent.SendGeneralMessage(socket,RFC.substring(RFC.indexOf(":")+1),
									RFC.substring(RFC.indexOf("~")+1,RFC.indexOf(":")),RFC.substring(5,RFC.indexOf("~")));
					LOG.log(Level.INFO, "MESS");
				}

				if(RFC.startsWith("PRIV")) {
					Parent.SendPrivateMessage(RFC.substring(RFC.indexOf("~")+1),RFC.substring(5,RFC.indexOf("~")));	
					LOG.log(Level.INFO, "PRIV");
				}

				if(RFC.startsWith("ROCO")) {
					Parent.GetUserCount(socket,RFC.substring(5));	
					LOG.log(Level.INFO, "ROCO");
				}

				if(RFC.startsWith("UPRQ")){
					LOG.log(Level.INFO, "Upload Request");
					Parent.SendFileRequest(RFC.substring(5, RFC.indexOf("~")), 
									RFC.substring(RFC.indexOf("~")+1, RFC.indexOf(":")), RFC.substring(RFC.indexOf(":")+1));
				}

				if(RFC.startsWith("UPRS")){
					LOG.log(Level.INFO, "Upload Response");
					Parent.SendFileResponse(RFC.substring(5, RFC.indexOf("~")), 
									RFC.substring(RFC.indexOf("~")+1, RFC.indexOf(":")), RFC.substring(RFC.indexOf(":")+1));
				}
				if(RFC.startsWith("UPCL")){
					LOG.log(Level.INFO, "Upload Cancel");
					Parent.SendFileCancel(RFC.substring(5));
				}
				if(RFC.startsWith("IGNO")){
					LOG.log(Level.INFO, "Ignore/UnIgnore");
					Parent.Ignore(RFC.substring(5, RFC.indexOf("~")), 
									(RFC.substring(RFC.indexOf("~")+1)));
				}
				
				

			} catch(Exception e) { 
					LOG.log(Level.SEVERE, "ChatCommunication::run: ", e);
					Parent.RemoveUserWhenException(socket);
					QuitConnection(); 
			}	
		}
	}

	private void QuitConnection()
	{
		thread.stop();
		thread = null;		
		try {
		socket.close();
		}catch(IOException e) {
			LOG.log(Level.WARNING, "ChatCommunication::QuitConnection: ", e);
		}
		socket = null;	
	}
}