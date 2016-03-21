package Client;

import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.io.IOException;
import java.net.Socket;


public class Client extends JFrame implements Runnable
{
    private final JPanel contentPane;
    private JTextField txtOutMsg;
    private final JTextArea txtadisplay;
    private Socket socket;
    private DataOutputStream osw;
    private DataInputStream din;
    
    public Client(String host, int port) {
 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 636, 160);
    
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    
    txtOutMsg = new JTextField();
    txtOutMsg.setText("Wpisz coś");
    txtOutMsg.setBounds(10, 21, 480, 20);
    contentPane.add(txtOutMsg);
    txtOutMsg.setColumns(10);
    
    txtadisplay = new JTextArea();
    txtadisplay.setBounds(10, 42, 480, 20);
    contentPane.add(txtadisplay);
    
    // Przycisk "Wyślij"
    JButton btnSend = new JButton("Wyślij");
    btnSend.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
 
            try {
                    osw.writeUTF( txtOutMsg.getText() + "\n" );
                    txtOutMsg.setText( "" );
            } catch (IOException e1) {
                 e1.printStackTrace();
     }}});
 
     btnSend.setBounds(10, 52, 131, 45);
     contentPane.add(btnSend);
     btnSend.setEnabled(false);
     
    //Przycisk "Połącz"
    final JButton btnConnect = new JButton("Połącz");
    btnConnect.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                socket = new Socket( host, port );

                System.out.println( "Połączono z "+socket );

                din = new DataInputStream( socket.getInputStream() );
                osw = new DataOutputStream( socket.getOutputStream() );
                btnConnect.setEnabled(false);
                btnSend.setEnabled(true);
                new Thread( Client.this ).start();
            } catch( IOException ie ) { 
                System.out.println( ie ); 
            }
        }
    });
            
    btnConnect.setBounds(508, 20, 89, 23);
    contentPane.add(btnConnect);
    }
    

    @Override
    public void run() 
    {
        //new Thread(this).start();
        try {
            while (true) 
            {
                String message = din.readUTF();
                System.out.println( message );
                //UWAGA USUNĄĆ TO!!!
                txtadisplay.setText(null);
                txtadisplay.append( message );
            }
        } catch( IOException ie ) { 
            System.out.println( ie ); 
        }
    }
}


