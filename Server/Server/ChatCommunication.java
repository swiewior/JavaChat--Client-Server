package Server;

import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class ChatCommunication implements Runnable,CommonSettings
{
  // Zmienne globalne
  Thread thread;
  Socket socket;
  DataInputStream inputstream;
  String RFC, filesharing_username;
  Server Parent;	
  
  // Inicjalizacja socketa do klienta
  ChatCommunication(Server chatserver, Socket clientsocket)
  {				
    Parent = chatserver;
    socket = clientsocket;	
    try {	
      inputstream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));		
    }catch(IOException _IOExc) { System.out.println ( "ChatCommunictaion exception inputstream: " + _IOExc);}
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
        RFC = inputstream.readLine();											
        // Obsługa tokenów
        if(RFC.startsWith("HELO")) {		
          System.out.println ( "HELO");
          Parent.AddUser(socket,RFC.substring(5));														
        }

        if(RFC.startsWith("QUIT")) {
          Parent.RemoveUser(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1),REMOVE_USER);
           System.out.println ( "QUIT");
          QuitConnection();	
        }
								
        if(RFC.startsWith("KICK")) {
          Parent.RemoveUser(RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1),KICK_USER);
           System.out.println ( "KICK");
          QuitConnection();
        }

        if(RFC.startsWith("CHRO")) {
          Parent.ChangeRoom(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
        }

        if(RFC.startsWith("MESS")) {
          Parent.SendGeneralMessage(socket,RFC.substring(RFC.indexOf(":")+1),RFC.substring(RFC.indexOf("~")+1,RFC.indexOf(":")),RFC.substring(5,RFC.indexOf("~")));	
        }

        if(RFC.startsWith("PRIV")) {
            Parent.SendPrivateMessage(RFC.substring(RFC.indexOf("~")+1),RFC.substring(5,RFC.indexOf("~")));	
        }

        if(RFC.startsWith("ROCO")) {
          Parent.GetUserCount(socket,RFC.substring(5));	
        }								


        if(RFC.startsWith("REIP")) {
          Parent.GetRemoteUserAddress(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
        }

        if(RFC.startsWith("AEIP")) {
          Parent.SendRemoteUserAddress(socket,RFC.substring(5,RFC.indexOf("~")),RFC.substring(RFC.indexOf("~")+1));	
        }	


      } catch(Exception _Exc) { Parent.RemoveUserWhenException(socket);QuitConnection();}	
    }
  }

  private void QuitConnection()
  {
    thread.stop();
    thread = null;		
    try {
    socket.close();
    }catch(IOException _IOExc) { }
    socket = null;	
  }
}

