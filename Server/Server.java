package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server 
{
    ServerSocket server;
    
    Map StreamsTable;
    
    public Server ( int port ) throws IOException
    {
        this.StreamsTable = Collections.synchronizedMap (new HashMap());
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

}

