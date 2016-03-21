package Server;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
    private Server server;
    private Socket socket;

    public ServerThread( Server server, Socket socket ) {
        this.server = server;
        this.socket = socket;
        
        start();
    }
    
    @Override
    public void run() 
    {
        try {
            DataInputStream din = new DataInputStream( socket.getInputStream() );
            while (true) 
            {
                String message = din.readUTF();
                System.out.println( "Wysyłam "+message );

                server.sendToAll( message );
            }
        } catch( EOFException ie ) {
            System.out.println( "Błąd! EOFException" );
        } catch( IOException ie ) {
            System.out.println( "Błąd! IOException" );
            ie.printStackTrace();
        } finally {
            server.removeConnection( socket );
        }
    }
}