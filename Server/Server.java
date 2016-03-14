package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server 
{
    private ServerSocket server;
    
    private Hashtable StreamsTable;
    
    public Server ( int port ) throws IOException
    {
        this.StreamsTable = new Hashtable();
        listen ( port );
    }
    
    private void listen ( int port ) throws IOException
    {
        server = new ServerSocket ( port );
        
        System.out.println ( "Nasłuchiwanie portu " +server );
        
        while ( true )
        {
            Socket newconnection = server.accept();
            System.out.println ( "Połączono z " +newconnection);
            
            DataOutputStream dataout;
            dataout = new DataOutputStream ( newconnection.getOutputStream () );
            
            StreamsTable.put ( newconnection, dataout );
        }
    }

    Enumeration getStreams() {
        return StreamsTable.elements();
    }

    void sendToAll ( String message )
    {
        synchronized ( StreamsTable )
        {
            for ( Enumeration e = getStreams(); e.hasMoreElements(); )
            {
                DataOutputStream dataout = (DataOutputStream)e.nextElement();
                try {
                    dataout.writeUTF ( message );
                } catch ( IOException ie ) {
                    System.out.println ( ie );
                }
            }
        }
    }

    void removeConnection ( Socket s )
    {
        synchronized ( StreamsTable )
        {
            System.out.println ( "Zamykanie połączenia " +s );
            StreamsTable.remove ( s );
            
            try {
                s.close ();
            } catch ( IOException ie ) {
                System.out.println ( "Błąd podczas zamykania " +s );
                ie.printStackTrace ();
            }
        }
    }
}