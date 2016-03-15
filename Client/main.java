import Client.Client;

public class main 
{
  
    static public void main ( String args[] )
    {
        
       String host = args[0];
       int port = Integer.parseInt ( args[1] );
       Client client = new Client(host, port);
    }   
}
