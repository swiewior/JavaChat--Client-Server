package server;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InformationDialog extends Dialog implements ActionListener
{
	Server chatserver;
	protected JTextField TxtRooms,TxtPort;
	protected JButton CmdOk,CmdSave;
	protected boolean IsConnect;
	Properties properties;
	Font TextFont;
	InputStream fin;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME ); 
		
	InformationDialog(Server Parent)
	{		
		super(Parent,"Konfiguracja serwera",true);
		
		chatserver = Parent;	
		TextFont = new Font("Dialog",0,11);
		setFont(TextFont);				
		setLayout(new BorderLayout());
		IsConnect = false;
		fin = null;
		properties = new Properties();
		
		addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {setVisible(false);}});
		
		JPanel ButtonPanel = new JPanel(new GridLayout(3,2,20,20));
		ButtonPanel.setBackground(Color.white);
		
		JLabel LblPort = new JLabel("Port: ");
		TxtPort = new JTextField();
		if (properties.getProperty("port") != null)
			TxtPort.setText(properties.getProperty("port"));	
		else
			TxtPort.setText("80");
		ButtonPanel.add(LblPort);
		ButtonPanel.add(TxtPort);

		Label LblRooms = new Label("Pokoje: ");
		TxtRooms = new JTextField();
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
		
		//ładowanie properties
		try {
			fin = new FileInputStream("server.properties");
			properties.load(fin);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "nie znaleziono server.properties", e);
		}
		
		if (properties.getProperty("roomlist") != null)
			TxtRooms.setText(properties.getProperty("roomlist"));
		if (properties.getProperty("port") != null)
			TxtPort.setText(properties.getProperty("port"));	

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

		try {
			fin.close();
		} catch (IOException | NullPointerException e) {
			LOG.log(Level.WARNING, null, e);
		}
		setSize(350,180);
		chatserver.show();
		show();				
	}	
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource().equals(CmdSave))
		{
			FileOutputStream fout=null;
			try {		
				fout = new FileOutputStream(new File("server.properties"));			
			}catch(java.io.IOException e) {
				LOG.log(Level.WARNING, "InformationDialog::actionPerformed: ", e);
			}
			
			properties.setProperty("port",TxtPort.getText());			
			properties.setProperty("roomlist",TxtRooms.getText());
			properties.save(fout,"Server Settings");
			try {
				fout.close();
			} catch (IOException e) {
				LOG.log(Level.WARNING, "InformationDialog::actionPerformed: ", e);
			}
		}	

		if (evt.getSource().equals(CmdOk))
			dispose();	
	}
}