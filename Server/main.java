import Server.Server;
import Server.ServerLogger;
import java.io.IOException;

public class main 
{
  static public void main ( String args[] ) throws Exception
  {
		// Zapis logów
		try {    
			ServerLogger.setup();
		} catch (SecurityException | IOException e) {
				e.printStackTrace(System.out);   
		}
		
		Server mainFrame = new Server();				
		mainFrame.setVisible(true);
  }  
}
