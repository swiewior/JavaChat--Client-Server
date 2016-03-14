package Client;

import java.applet.*;
import java.awt.*;

public class ClientApplet extends Applet
{
    public void init(String host, int port) 
    {
        setLayout( new BorderLayout() );
        add( "Center", new Client( host, port ) );
    }
    

}
