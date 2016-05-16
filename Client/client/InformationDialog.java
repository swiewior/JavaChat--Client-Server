package client;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class InformationDialog extends JDialog implements ActionListener
{
	Client chatclient;
	protected JTextField TxtUserName,TxtServerName,TxtServerPort, TxtHistoryFile,
		txtRegisterNick, txtRegisterPassword;
	protected JButton CmdOk,CmdSave, CmdRegister;
	protected JPasswordField TxtPassword;
	protected Choice roomchoice;
	protected boolean IsConnect;
	protected JLabel lblInfo;
	Properties properties;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
		
	InformationDialog(Client Parent)
	{
		super(Parent,"Java Chat - Login&Register",true);
		chatclient = Parent;				
		setFont(chatclient.TextFont);				
		setLayout(new BorderLayout());
		IsConnect = false;

		properties=new Properties();
		
		File f = new File("client.properties");
		if(f.exists() && !f.isDirectory()) { 
			try {
				properties.load(this.getClass().getClassLoader().
					getResourceAsStream("client.properties"));		
			} catch(java.io.IOException | java.lang.NullPointerException e) {
				LOG.log(Level.WARNING, "InformationDialog::InformationDialog: ", e);
			}
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {setVisible(false);}
		});

		JTabbedPane tabPanel = new JTabbedPane();
		
		//Zakładka Logowania
		JPanel ButtonPanel = new JPanel(new GridLayout(6,2,15,15));				
		ButtonPanel.setBackground(Color.white);

		JLabel LblUserName = new JLabel("Nick: ");
		TxtUserName = new JTextField(properties.getProperty("user"));
		if (properties.getProperty("user") != null)
			TxtUserName.setText(properties.getProperty("user"));
		else
			TxtUserName.setText("user");
		ButtonPanel.add(LblUserName);
		ButtonPanel.add(TxtUserName);
		
		JLabel LblPassword = new JLabel("Hasło: ");
		TxtPassword = new JPasswordField(properties.getProperty("password"));
		ButtonPanel.add(LblPassword);
		ButtonPanel.add(TxtPassword);

		JLabel LblServerName = new JLabel("Adres serwera: ");
		TxtServerName = new JTextField();
		if (properties.getProperty("server") != null)
			TxtServerName.setText(properties.getProperty("server"));
		else
			TxtServerName.setText("localhost");
		ButtonPanel.add(LblServerName);
		ButtonPanel.add(TxtServerName);

		JLabel LblServerPort = new JLabel("Port: ");
		TxtServerPort = new JTextField();
		if (properties.getProperty("port") != null)
			TxtServerPort.setText(properties.getProperty("port"));
		else
			TxtServerPort.setText("80");
		ButtonPanel.add(LblServerPort);
		ButtonPanel.add(TxtServerPort);
				
		JLabel LblHistoryFile = new JLabel("Plik Historii: ");
		TxtHistoryFile = new JTextField();
		if (properties.getProperty("history") != null)
			TxtHistoryFile.setText(properties.getProperty("history"));
		else
			TxtHistoryFile.setText("src/History.xml");
		ButtonPanel.add(LblHistoryFile);
		ButtonPanel.add(TxtHistoryFile);	

		CmdOk = new JButton("Połącz");
		CmdOk.addActionListener(this);
		CmdSave = new JButton("Zapisz");
		CmdSave.addActionListener(this);
		ButtonPanel.add(CmdOk);
		ButtonPanel.add(CmdSave);
		
		tabPanel.add("Logowanie", ButtonPanel);
		
		//Zakładka Rejestracji
		JPanel RegisterPanel = new JPanel(new GridLayout(5,2,15,15));				
		RegisterPanel.setBackground(Color.white);
		
		JLabel lblRegisterNick = new JLabel("Nick: ");
		txtRegisterNick = new JTextField(properties.getProperty("reguser"));
		RegisterPanel.add(lblRegisterNick);
		RegisterPanel.add(txtRegisterNick);
		
		JLabel lblRegisterPassword = new JLabel("Hasło: ");
		txtRegisterPassword = new JPasswordField(properties.getProperty("regpassword"));
		RegisterPanel.add(lblRegisterPassword);
		RegisterPanel.add(txtRegisterPassword);
		
		CmdRegister = new JButton("Rejestracja");
		CmdRegister.addActionListener(this);
		RegisterPanel.add(CmdRegister);
		
		lblInfo = new JLabel();
		RegisterPanel.add(lblInfo);
		
		tabPanel.add("Rejestracja", RegisterPanel);

		add("Center",tabPanel);

		JPanel EmptyNorthPanel = new JPanel();
		EmptyNorthPanel.setBackground(Color.white);
		add("North",EmptyNorthPanel);

		JPanel EmptySouthPanel = new JPanel();
		EmptySouthPanel.setBackground(Color.white);
		add("South",EmptySouthPanel);

		JPanel EmptyEastPanel = new JPanel();
		EmptyEastPanel.setBackground(Color.white);
		add("East",EmptyEastPanel);

		JPanel EmptyWestPanel = new JPanel();
		EmptyWestPanel.setBackground(Color.white);
		add("West",EmptyWestPanel);

		setSize(260,280);
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
			}catch(java.io.IOException e) {
			LOG.log(Level.WARNING, "InformationDialog::InformationDialog: ", e);
			}
			properties.setProperty("user",TxtUserName.getText());
			properties.setProperty("server",TxtServerName.getText());			
			properties.setProperty("port",TxtServerPort.getText());
			properties.setProperty("history",TxtHistoryFile.getText());
			properties.save(fout,"Client Settings");
		}	
		if (evt.getSource().equals(CmdRegister))
		{
			lblInfo.setText("Zarejestrowano!");
			lblInfo.setForeground(Color.GREEN);
			//IsConnect = true;		
		}
	}
	

}