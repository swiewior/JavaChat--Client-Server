import Client.Client;
import javax.swing.JFrame;

public class main extends JFrame
{
  
    static public void main ( String args[] )
    {

        String host = args[0];
        int port = Integer.parseInt ( args[1] );

        try {
            Client client = new Client(host, port);
            client.setVisible(true);        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
          
      