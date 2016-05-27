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
	protected JButton CmdConnect, CmdLogin, CmdSave, CmdRegister;
	protected JPasswordField TxtPassword;
	protected Choice roomchoice;
	protected boolean IsConnect, IsRegister, IsLogin;
	public JLabel lblRegInfo, lblConInfo;
	Properties properties;
	public JTabbedPane tabPanel;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	InputStream fin;
		
	InformationDialog(Client Parent)
	{
		super(Parent,"Java Chat - Login&Register",true);
		chatclient = Parent;
		setFont(chatclient.TextFont);
		setLayout(new BorderLayout());
		IsConnect = false;
		IsRegister = false;
		fin = null;
		properties=new Properties();
		
		try {
			fin = new FileInputStream("client.properties");
			properties.load(fin);
		} catch (FileNotFoundException e) {
			LOG.log(Level.WARNING, "InformationDialog::InformationDialog: ", e);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "InformationDialog::InformationDialog: ", e);
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {setVisible(false);}
		});

		tabPanel = new JTabbedPane();
		
		//Zakładka Połączenia
		JPanel ConnectPanel = new JPanel(new GridLayout(4,2,15,15));				
		ConnectPanel.setBackground(Color.white);

		JLabel LblServerName = new JLabel("Adres serwera: ");
		TxtServerName = new JTextField();
		if (properties.getProperty("server") != null)
			TxtServerName.setText(properties.getProperty("server"));
		else
			TxtServerName.setText("localhost");
		ConnectPanel.add(LblServerName);
		ConnectPanel.add(TxtServerName);

		JLabel LblServerPort = new JLabel("Port: ");
		TxtServerPort = new JTextField();
		if (properties.getProperty("port") != null)
			TxtServerPort.setText(properties.getProperty("port"));
		else
			TxtServerPort.setText("80");
		ConnectPanel.add(LblServerPort);
		ConnectPanel.add(TxtServerPort);

		CmdConnect = new JButton("Połącz");
		CmdConnect.addActionListener(this);
		lblConInfo = new JLabel();
		ConnectPanel.add(CmdConnect);
		ConnectPanel.add(lblConInfo);
		
		tabPanel.add("Połączenie", ConnectPanel);
		
		//Zakładka Rejestracji
		JPanel RegisterPanel = new JPanel(new GridLayout(4,2,15,15));				
		RegisterPanel.setBackground(Color.white);
		
		JLabel lblRegisterNick = new JLabel("Nick: ");
		txtRegisterNick = new JTextField();
		RegisterPanel.add(lblRegisterNick);
		RegisterPanel.add(txtRegisterNick);
		
		JLabel lblRegisterPassword = new JLabel("Hasło: ");
		txtRegisterPassword = new JPasswordField();
		RegisterPanel.add(lblRegisterPassword);
		RegisterPanel.add(txtRegisterPassword);
		
		CmdRegister = new JButton("Rejestracja");
		CmdRegister.addActionListener(this);
		RegisterPanel.add(CmdRegister);
		lblRegInfo = new JLabel();
		RegisterPanel.add(lblRegInfo);
		
		tabPanel.add("Rejestracja", RegisterPanel);
		
		//Zakładka Logowania
		JPanel LoginPanel = new JPanel(new GridLayout(4,2,15,15));				
		LoginPanel.setBackground(Color.white);

		JLabel LblUserName = new JLabel("Nick: ");
		TxtUserName = new JTextField(properties.getProperty("user"));
		if (properties.getProperty("user") != null)
			TxtUserName.setText(properties.getProperty("user"));
		else
			TxtUserName.setText("user");
		LoginPanel.add(LblUserName);
		LoginPanel.add(TxtUserName);
		
		JLabel LblPassword = new JLabel("Hasło: ");
		TxtPassword = new JPasswordField(properties.getProperty("password"));
		LoginPanel.add(LblPassword);
		LoginPanel.add(TxtPassword);
				
		JLabel LblHistoryFile = new JLabel("Plik Historii: ");
		TxtHistoryFile = new JTextField();
		if (properties.getProperty("history") != null)
			TxtHistoryFile.setText(properties.getProperty("history"));
		else
			TxtHistoryFile.setText("History.xml");
		LoginPanel.add(LblHistoryFile);
		LoginPanel.add(TxtHistoryFile);	

		CmdLogin = new JButton("Zaloguj");
		CmdLogin.addActionListener(this);
		CmdSave = new JButton("Zapisz ustawienia");
		CmdSave.addActionListener(this);
		LoginPanel.add(CmdLogin);
		LoginPanel.add(CmdSave);
		
		tabPanel.add("Logowanie", LoginPanel);

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

		try {
			fin.close();
		} catch (IOException | NullPointerException e) {
			LOG.log(Level.WARNING, null, e);
		}
		setSize(350,280);
		chatclient.show();
		show();				
	}	
	
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource().equals(CmdConnect)) {
			chatclient.ConnectToServer(this);
		}
		if (evt.getSource().equals(CmdRegister)) {
			chatclient.Register(this);
		}
		if (evt.getSource().equals(CmdLogin)) {
			chatclient.Login(this);	
		}
		if (evt.getSource().equals(CmdSave)) {
			FileOutputStream fout=null;
			try {		
				fout = new FileOutputStream(new File("client.properties"));			
			}catch(java.io.IOException e) {
			LOG.log(Level.WARNING, "InformationDialog::InformationDialog: ", e);
			}
			properties.setProperty("user",TxtUserName.getText());
			properties.setProperty("server",TxtServerName.getText());			
			properties.setProperty("port",TxtServerPort.getText());
			properties.setProperty("history",TxtHistoryFile.getText());
			properties.save(fout,"Client Settings");
			
			try {
				fout.close();
			} catch (IOException e) {
				LOG.log(Level.WARNING, "InformationDialog::actionPerformed: ", e);
			}
		}	
	}
	
	public void setConnectLabel(String text, Color color) {
		lblConInfo.setText(text);
		lblConInfo.setForeground(color);
	}
	public void setRegisterLabel(String text, Color color) {
		lblRegInfo.setText(text);
		lblRegInfo.setForeground(color);
	}
}