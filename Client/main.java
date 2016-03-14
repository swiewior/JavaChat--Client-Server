
import Client.Client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Seba
 */

public class main {

    /**
     * @param args the command line arguments
     */
    static public void main ( String args[] )
    {
        
       String host = args[0];
       int port = Integer.parseInt ( args[1] );
       Client client = new Client(host, port);
    }  
    
}
