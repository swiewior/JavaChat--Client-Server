package Server;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
    private Server server;
    private Socket socket;

    public ServerThread( Server server, Socket socket ) {
        this.server = server;
    }
    
    @Override
    public void run() 
    {
        try {
            DataInputStream din = new DataInputStream( socket.getInputStream() );

            while (true) 
            {
                String message = din.readUTF();
                System.out.println( "Sending "+message );

                server.sendToAll( message );
            }
        } catch( EOFException ie ) {
        } catch( IOException ie ) {
            ie.printStackTrace();
        } finally {
            server.removeConnection( socket );
        }
    }
}