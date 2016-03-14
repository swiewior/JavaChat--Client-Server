package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client implements Runnable
{
    private TextField tf = new TextField();
    private TextArea ta = new TextArea();

    private Socket socket;
     
    private DataOutputStream dout;
    private DataInputStream din;
    
    public Client ( String host, int port)
    {
        //set window
        
        tf.addActionListener ( new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent e) {
                processMessage (e.getActionCommand() );
            }
        });
        
        try {

            socket = new Socket( host, port );

            System.out.println( "Połączono z "+socket );

            din = new DataInputStream( socket.getInputStream() );
            dout = new DataOutputStream( socket.getOutputStream() );

            new Thread( this ).start();
        } catch( IOException ie ) { 
            System.out.println( ie ); 
        }
    }

    private void processMessage( String message ) 
    {
        try {
            dout.writeUTF( message );
            tf.setText ("");
        } catch( IOException ie ) { 
            System.out.println( ie ); 
        }
    }

    @Override
    public void run() 
    {
        try {
            while (true) 
            {
                String message = din.readUTF();
                ta.append( message + "\n" );
            }
        } catch( IOException ie ) { 
            System.out.println( ie ); 
        }
    }

}
