import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JButton;

public class InformationDialog extends Dialog implements ActionListener
{
    Client chatclient;
    protected TextField TxtUserName,TxtServerName,TxtServerPort;
    protected JButton CmdOk,CmdCancel;
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
            properties.load(this.getClass().getClassLoader().getResourceAsStream("data.properties"));		
        } catch(java.io.IOException exc)  { }
        catch(java.lang.NullPointerException NExc)  { }

        addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {setVisible(false);}});

        Panel ButtonPanel = new Panel(new GridLayout(4,2,15,15));				
        ButtonPanel.setBackground(chatclient.ColorMap[0]);

        Label LblUserName = new Label("Nick: ");
        TxtUserName = new TextField(properties.getProperty("TurtleUserName"));		
        ButtonPanel.add(LblUserName);
        ButtonPanel.add(TxtUserName);

        Label LblServerName = new Label("Adres serwera: ");

        TxtServerName = new TextField();
        if (properties.getProperty("TurtleServerName") != null)
            TxtServerName.setText(properties.getProperty("TurtleServerName"));
        else
            TxtServerName.setText("localhost");

        ButtonPanel.add(LblServerName);
        ButtonPanel.add(TxtServerName);

        Label LblServerPort = new Label("Port: ");		
        TxtServerPort = new TextField();
        if (properties.getProperty("TurtleServerPort") != null)
            TxtServerPort.setText(properties.getProperty("TurtleServerPort"));
        else
            TxtServerPort.setText("1436");

        ButtonPanel.add(LblServerPort);
        ButtonPanel.add(TxtServerPort);				


        CmdOk = new JButton("Połącz");
        CmdOk.addActionListener(this);
        CmdCancel = new JButton("Anuluj");
        CmdCancel.addActionListener(this);
        ButtonPanel.add(CmdOk);
        ButtonPanel.add(CmdCancel);

        add("Center",ButtonPanel);

        Panel EmptyNorthPanel = new Panel();
        EmptyNorthPanel.setBackground(chatclient.ColorMap[0]);
        add("North",EmptyNorthPanel);

        Panel EmptySouthPanel = new Panel();
        EmptySouthPanel.setBackground(chatclient.ColorMap[0]);
        add("South",EmptySouthPanel);

        Panel EmptyEastPanel = new Panel();
        EmptyEastPanel.setBackground(chatclient.ColorMap[0]);
        add("East",EmptyEastPanel);

        Panel EmptyWestPanel = new Panel();
        EmptyWestPanel.setBackground(chatclient.ColorMap[0]);
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
            FileOutputStream fout=null;
            try {		
                fout = new FileOutputStream(new File("data.properties"));			
            }catch(java.io.IOException exc) { }
            dispose();
        }	

        if (evt.getSource().equals(CmdCancel))
        {
            IsConnect = false;
            dispose();
        }	
    }
	

}