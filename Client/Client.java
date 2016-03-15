package Client;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame implements Runnable
{
    JPanel panel;
    private JTextField tf = new JTextField();
    private JTextArea ta = new JTextArea();

    private Socket socket;
     
    private DataOutputStream dout;
    private DataInputStream din;
    
    public Client ( String host, int port)
    {
        //ustawianie okna
        panel = new JPanel();
        this.setSize(500, 500);
        this.setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel.setLayout(null);
        this.add(panel);
        tf.setBounds(20, 400, 340, 30);
	panel.add(tf);
        ta.setBounds(20, 20, 450, 360);
	panel.add(ta);
        
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
