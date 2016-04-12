package Server;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JButton;

public class InformationDialog extends Dialog implements ActionListener
{
    Server chatserver;
    protected TextField TxtRooms,TxtPort;
    protected JButton CmdOk,CmdSave;
    protected boolean IsConnect;
    Properties properties;
    Font TextFont;
        
    InformationDialog(Server Parent)
    {		
        super(Parent,"Konfiguracja serwera",true);
        chatserver = Parent;	
        TextFont = new Font("Dialog",0,11);
        setFont(TextFont);				
        setLayout(new BorderLayout());
        IsConnect = false;
        
        addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {setVisible(false);}});

        // Parametry
        properties=new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("server.properties"));		
        } catch(java.io.IOException | java.lang.NullPointerException exc)  { }		
        
        Panel ButtonPanel = new Panel(new GridLayout(4,2,15,15));
        ButtonPanel.setBackground(Color.white);
        
        Label LblPort = new Label("Port: ");
        TxtPort = new TextField();
        if (properties.getProperty("port") != null)
            TxtPort.setText(properties.getProperty("port"));	
        else
            TxtPort.setText("80");
        ButtonPanel.add(LblPort);
        ButtonPanel.add(TxtPort);

        Label LblRooms = new Label("Pokoje: ");
        TxtRooms = new TextField();
        if (properties.getProperty("roomlist") != null)
            TxtRooms.setText(properties.getProperty("roomlist"));
        else
            TxtRooms.setText("Glowny");

        ButtonPanel.add(LblRooms);
        ButtonPanel.add(TxtRooms);


        CmdOk = new JButton("OK");
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

        setSize(350,200);
        chatserver.show();
        show();				
    }	
	
    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getSource().equals(CmdSave))
        {
            FileOutputStream fout=null;
            try {		
                fout = new FileOutputStream(new File("src/server.properties"));			
            }catch(java.io.IOException exc) { }
            
            properties.setProperty("port",TxtPort.getText());			
            properties.setProperty("roomlist",TxtRooms.getText());
            properties.save(fout,"Server Settings");
        }	

        if (evt.getSource().equals(CmdOk))
        {
            dispose();
        }	
    }
	

}