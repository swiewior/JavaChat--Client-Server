import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JButton;

public class InformationDialog extends Dialog implements ActionListener
{
    Client chatclient;
    protected TextField TxtUserName,TxtServerName,TxtServerPort;
    protected JButton CmdOk,CmdSave;
    protected Choice roomchoice;
    protected boolean IsConnect;
    Properties properties;
        
    InformationDialog(Client Parent)
    {		
        super(Parent,"Java Chat - Login",true);
        chatclient = Parent;				
        setFont(chatclient.TextFont);				
        setLayout(new BorderLayout());
        IsConnect = false;

        properties=new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("client.properties"));		
        } catch(java.io.IOException | java.lang.NullPointerException exc)  { }

        addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {setVisible(false);}});

        Panel ButtonPanel = new Panel(new GridLayout(4,2,15,15));				
        ButtonPanel.setBackground(chatclient.ColorMap[0]);

        Label LblUserName = new Label("Nick: ");
        TxtUserName = new TextField(properties.getProperty("user"));
        if (properties.getProperty("user") != null)
            TxtUserName.setText(properties.getProperty("user"));
        else
            TxtUserName.setText("user");
        ButtonPanel.add(LblUserName);
        ButtonPanel.add(TxtUserName);

        Label LblServerName = new Label("Adres serwera: ");
        TxtServerName = new TextField();
        if (properties.getProperty("server") != null)
            TxtServerName.setText(properties.getProperty("server"));
        else
            TxtServerName.setText("localhost");
        ButtonPanel.add(LblServerName);
        ButtonPanel.add(TxtServerName);

        Label LblServerPort = new Label("Port: ");
        TxtServerPort = new TextField();
        if (properties.getProperty("port") != null)
            TxtServerPort.setText(properties.getProperty("port"));
        else
            TxtServerPort.setText("80");
        ButtonPanel.add(LblServerPort);
        ButtonPanel.add(TxtServerPort);


        CmdOk = new JButton("Połącz");
        CmdOk.addActionListener(this);
        CmdSave = new JButton("Zapisz");
        CmdSave.addActionListener(this);
        ButtonPanel.add(CmdOk);
        ButtonPanel.add(CmdSave);

        add("Center",ButtonPanel);

        Panel EmptyNorthPanel = new Panel();
        EmptyNorthPanel.setBackground(Color.white);
        add("North",EmptyNorthPanel);

        Panel EmptySouthPanel = new Panel();
        EmptySouthPanel.setBackground(Color.white);
        add("South",EmptySouthPanel);

        Panel EmptyEastPanel = new Panel();
        EmptyEastPanel.setBackground(Color.white);
        add("East",EmptyEastPanel);

        Panel EmptyWestPanel = new Panel();
        EmptyWestPanel.setBackground(Color.white);
        add("West",EmptyWestPanel);

        setSize(250,200);
        chatclient.show();
        show();				
    }	
	
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getSource().equals(CmdOk))
        {
            IsConnect = true;
            dispose();
        }	

        if (evt.getSource().equals(CmdSave))
        {
            FileOutputStream fout=null;
            try {		
                fout = new FileOutputStream(new File("src/client.properties"));			
            }catch(java.io.IOException exc) { }
            properties.setProperty("user",TxtUserName.getText());
            properties.setProperty("server",TxtServerName.getText());			
            properties.setProperty("port",TxtServerPort.getText());
            properties.save(fout,"Client Settings");
        }	
    }
	

}