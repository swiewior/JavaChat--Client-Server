import Server.Server;

public class main 
{
  static public void main ( String args[] ) throws Exception
  {
     int port = Integer.parseInt ( args[0] );

      Server server = new Server ( port );
  }  
}
