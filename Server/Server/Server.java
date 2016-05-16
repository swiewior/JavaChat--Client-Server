package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Server extends JFrame implements Serializable, ActionListener, Runnable, CommonSettings
{
	//Zmienne globalne
	Properties DBProperties;
	JButton cmdStart,cmdStop;
	ServerSocket server;
	Socket socket;
	Thread thread;    
	ArrayList userarraylist, messagearraylist;
	  /** File Sharing List **/
	public Vector clientFileSharingUsername;
	public Vector clientFileSharingSocket;
	ChatCommunication chatcommunication;	
	DataOutputStream dataoutputstream;
	int G_ILoop, Port;
	ClientObject clientobject;
	String RoomList;
	protected TextField TxtPort, TxtRooms;
	Properties properties;
	InformationDialog dialog;
	JRadioButtonMenuItem logOff, logSevere, logWarning, logInfo;
	JMenuItem settingitem, quititem; 
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME ); 
	
	public Server () 
	{				

		//Inicjalizacja
		this.setTitle("Java Chat Server");
		this.setResizable(false);
		this.setBackground(Color.white);		
		this.setLayout(new BorderLayout());

		// Panel tytułu
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.black);
		JLabel lblTitle = new JLabel("Wielowątkowy Serwer Chatu");
		lblTitle.setHorizontalAlignment(JLabel.CENTER);
		lblTitle.setForeground(Color.white);
		lblTitle.setFont(new Font("Helvitica",Font.BOLD,20));
		topPanel.add("Center",lblTitle);		
		add("North",topPanel);
		
		//Panel główny
		JPanel centerPanel = new JPanel(null);
		cmdStart = new JButton("START SERVER");
		cmdStart.setBounds(125,10,150,30);
		cmdStart.addActionListener(this);
		centerPanel.add(cmdStart);

		cmdStop = new JButton("STOP SERVER");
		cmdStop.setBounds(125,50,150,30);
		cmdStop.setEnabled(false);
		cmdStop.addActionListener(this);
		centerPanel.add(cmdStop);
		
		// Menu
		JMenuBar menubar = new JMenuBar();
		//Konfiguracja
		JMenu settingmenu = new JMenu("Konfiguracja");		
		settingitem = new JMenuItem("Konfiguracja serwera");
		settingitem.addActionListener(this);
		quititem = new JMenuItem("Wyjście");
		quititem.addActionListener(this);
		settingmenu.add(settingitem);
		settingmenu.add(quititem);		
		//Ustawienia Logów
		JMenu logmenu = new JMenu("Logi");
		ButtonGroup group = new ButtonGroup();
		
		logOff = new JRadioButtonMenuItem("Wyłączone");
		logmenu.add(logOff);
		logOff.addActionListener(this);
		logOff.setSelected(true);
		group.add(logOff);
		
		logSevere = new JRadioButtonMenuItem("Krytyczne");
		logmenu.add(logSevere);
		logSevere.addActionListener(this);
		group.add(logSevere);
		
		logWarning = new JRadioButtonMenuItem("Ostrzeżenia");
		logmenu.add(logWarning);
		group.add(logWarning);
		logWarning.addActionListener(this);
		
		logInfo = new JRadioButtonMenuItem("Informacyjne");
		logmenu.add(logInfo);
		group.add(logInfo);
		logInfo.addActionListener(this);
		
		menubar.add(settingmenu);
		menubar.add(logmenu);
		this.setJMenuBar(menubar);
		
		add("Center", centerPanel);

		setSize(400,180);
		setLocation(100,100);
		
		// Zamknięcie okienka
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {				
				ExitServer();
				dispose();
				System.exit(0);
			}
		});
		
		Configure();
	}
	
	private void Configure()
	{
		dialog = new InformationDialog(this);	
		Port = Integer.parseInt(dialog.TxtPort.getText());
		RoomList = dialog.TxtRooms.getText();
	}
  
	// Obsługa eventów
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getActionCommand().equalsIgnoreCase("Start Server"))
		{	
			DBProperties = GetDBProperties();
			// Inicjalizacja ServerSocket
			try {												
				server = new ServerSocket(Port);
				LOG.log(Level.INFO, "Nasłuchiwanie portu ", server);
			}catch(IOException e) {
				LOG.log(Level.SEVERE, "Server::actionPerformed: ", e);
			}

			// Inicjalizacja listy
			userarraylist = new ArrayList();
			messagearraylist = new ArrayList();
			clientFileSharingUsername = new Vector();
			clientFileSharingSocket = new Vector();

			// Inicjalizacja wątku
			thread = new Thread(this);
			thread.start();
		
			cmdStart.setEnabled(false);
			cmdStop.setEnabled(true);	
		}

		if(evt.getActionCommand().equalsIgnoreCase("Stop Server"))
		{
			LOG.log(Level.INFO, "Stop Server");
			ExitServer();
			cmdStop.setEnabled(false);
			cmdStart.setEnabled(true);	
		}		
		
		if(evt.getSource().equals(settingitem))	
			Configure();
		if(evt.getSource().equals(quititem))	
		{
			ExitServer();
			dispose();
			System.exit(0);
		}
		
		if(evt.getSource().equals(logOff))	
			LOG.setLevel(Level.OFF);	
		if(evt.getSource().equals(logSevere))	
			LOG.setLevel(Level.SEVERE);
		if(evt.getSource().equals(logWarning))	
			LOG.setLevel(Level.WARNING);
		if(evt.getSource().equals(logInfo))
			LOG.setLevel(Level.ALL);
	}    
	
	// Implementacja wątku
	@Override
	public void run()
	{
		// Akceptowanie i tworzenie nowego wątku
		while(thread != null)
		{
			try
			{
				// Akceptowanie połączenia
				socket = server.accept();
				LOG.log(Level.INFO, "Połączono z:  "+ socket);
				// Tworzenie wątku
				chatcommunication = new ChatCommunication(this, socket);

				Thread.sleep(THREAD_SLEEP_TIME);	
			}
			catch(InterruptedException | IOException e) 	{ 
				LOG.log(Level.SEVERE, "Server::run: ", e);
				ExitServer(); 
			}	
		}	
	}

	// Wysyłanie wiadomości do klienta
	private void SendMessageToClient(Socket clientsocket,String message)
	{
		try {
			dataoutputstream = new DataOutputStream(clientsocket.getOutputStream());			
			dataoutputstream.write((message+"\r\n").getBytes());
			LOG.log(Level.INFO, "SendMessageToClient: "+ message);
		} catch(IOException e) {
			LOG.log(Level.WARNING, "Server::SendMessageToClient: ", e);
		}
	}

	// Objekt - nazwa użytkowanika
	private ClientObject GetClientObject(String UserName)
	{
		ClientObject returnClientObject = null;
		ClientObject TempClientObject;
		int m_userListSize = userarraylist.size();
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			TempClientObject = (ClientObject) userarraylist.get(G_ILoop);
			if(TempClientObject.getUserName().equalsIgnoreCase(UserName))
			{
				returnClientObject = TempClientObject;
				break;
			}
		}
		return returnClientObject;
	}

	// Sprawdzanie czy użytkownik istnieje
	private boolean IsUserExists(String UserName) {		
		return GetClientObject(UserName) != null;	
	}

	// Indeks użytkownika
	private int GetIndexOf(String UserName)
	{
		int m_userListSize = userarraylist.size();
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			if(clientobject.getUserName().equalsIgnoreCase(UserName))
				return G_ILoop;					
		}
		return -1;
	}

	// Dodanie użytkownika do listy serwera
	protected void AddUser(Socket ClientSocket,String UserName)
	{
		// Jeśli istnieje, koniec
		if(IsUserExists(UserName))
		{
			SendMessageToClient(ClientSocket,"EXIS");
			return;	
		}

		// Lista pokoi
		SendMessageToClient(ClientSocket,"ROOM "+RoomList);

		// Info o użytkowniku do wszystkich					
		int m_userListSize = userarraylist.size();
		String m_addRFC = "ADD  "+UserName;
		StringBuffer stringbuffer = new StringBuffer("LIST ");
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);				
			// Sprawdzenie nazwy pokojów
			if(clientobject.getRoomName().equals(ROOM_NAME))
			{
				SendMessageToClient(clientobject.getSocket(),m_addRFC);
				stringbuffer.append(clientobject.getUserName());													
				stringbuffer.append(";");									
			}
		}

		// Dodaj użytkownika do listy użytkowników
		clientobject = new ClientObject(ClientSocket,UserName,ROOM_NAME);
		userarraylist.add(clientobject);

		// Wysyłanie listy użytkowników
		stringbuffer.append(UserName);
		stringbuffer.append(";");
		SendMessageToClient(ClientSocket,stringbuffer.toString());	

	}

	// Usuń użytkownika z serwera
	public void RemoveUser(String UserName, String RoomName, int RemoveType)
	{
		ClientObject removeclientobject = GetClientObject(UserName);
		if(removeclientobject != null)
		{
			userarraylist.remove(removeclientobject);	
			userarraylist.trimToSize();
			int m_userListSize = userarraylist.size();
			String m_RemoveRFC=null;
			if(RemoveType == REMOVE_USER)
				m_RemoveRFC = "REMO "+UserName;
			if(RemoveType == KICK_USER)
				m_RemoveRFC = "INKI "+UserName;
			// Wyślij informacje o usunięciu do wszystkich
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = 	(ClientObject) userarraylist.get(G_ILoop);
				if(clientobject.getRoomName().equals(RoomName))
					SendMessageToClient(clientobject.getSocket(),m_RemoveRFC);
			}			
		}			
	}

	// Wywal użytkownika gdy wyskoczy exception
	protected void RemoveUserWhenException(Socket clientsocket)
	{
		int m_userListSize = userarraylist.size();
		ClientObject removeclientobject;
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			removeclientobject = (ClientObject) userarraylist.get(G_ILoop);
			if(removeclientobject.getSocket().equals(clientsocket))
			{
				String m_RemoveUserName = removeclientobject.getUserName();
				String m_RemoveRoomName = removeclientobject.getRoomName();
				userarraylist.remove(removeclientobject);	
				userarraylist.trimToSize();					
				m_userListSize = userarraylist.size();
				String m_RemoveRFC="REMO "+m_RemoveUserName;

				// Wyślij informacje o usunięciu do wszystkich
				for(int m_ILoop = 0; m_ILoop < m_userListSize; m_ILoop++)
				{
					clientobject = (ClientObject) userarraylist.get(m_ILoop);
					if(clientobject.getRoomName().equals(m_RemoveRoomName))
						SendMessageToClient(clientobject.getSocket(),m_RemoveRFC);
				}
				return;	
			}	
		}
	}

	// Zmiana pokoju
	public void ChangeRoom(Socket ClientSocket,String UserName, String NewRoomName)
	{
		int m_clientIndex = GetIndexOf(UserName);		
		if(m_clientIndex >= 0)
		{
			// Wiadomość o zmianie pokoju
			ClientObject TempClientObject = (ClientObject) userarraylist.get(m_clientIndex);
			String m_oldRoomName = TempClientObject.getRoomName();
			TempClientObject.setRoomName(NewRoomName);
			userarraylist.set(m_clientIndex,TempClientObject);
			SendMessageToClient(ClientSocket,"CHRO "+NewRoomName);

			// Wyślij listę użytkowników w pokoju
			int m_userListSize = userarraylist.size();
			StringBuilder stringbuffer = new StringBuilder("LIST ");			
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = (ClientObject) userarraylist.get(G_ILoop);				
				// Sprawdź nazwę pokoju
				if(clientobject.getRoomName().equals(NewRoomName))
				{					
					stringbuffer.append(clientobject.getUserName());
					stringbuffer.append(";");									
				}
			}
			SendMessageToClient(ClientSocket,stringbuffer.toString());


			// Wiadomośc o starym i nowym pokoju do użytkowników			
			String m_OldRoomRFC = "LERO "+UserName+"~"+NewRoomName;
			String m_NewRoomRFC = "JORO "+UserName;
			for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
			{
				clientobject = (ClientObject) userarraylist.get(G_ILoop);
				if(clientobject.getRoomName().equals(m_oldRoomName))
					SendMessageToClient(clientobject.getSocket(),m_OldRoomRFC);
				if((clientobject.getRoomName().equals(NewRoomName)) && (!(clientobject.getUserName().equals(UserName))))
					SendMessageToClient(clientobject.getSocket(),m_NewRoomRFC);
			} 			
		}
	}

	// Wyślij Główną wiadomość
	protected void SendGeneralMessage(Socket ClientSocket, String Message,String UserName,String RoomName)
	{
		boolean m_floodFlag = false;
		messagearraylist.add(UserName);		
		if(messagearraylist.size() > MAX_MESSAGE)
		{
			messagearraylist.remove(0);
			messagearraylist.trimToSize();

			// Sprawdzaj czy użytkownik zatyka serwer
			String m_firstMessage = (String) messagearraylist.get(0);
			int m_messageListSize = messagearraylist.size();			
			for(G_ILoop = 1; G_ILoop < 	m_messageListSize; G_ILoop++)
			{
				if(messagearraylist.get(G_ILoop).equals(m_firstMessage))
					m_floodFlag = true;
				else
				{
					m_floodFlag = false;
					break;
				}	
			}						
		}

		// Wysłanie Głównej wiadomości do wszystkich
		int m_userListSize = userarraylist.size();
		String m_messageRFC = "MESS "+UserName+":"+Message;
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			if((clientobject.getRoomName().equals(RoomName)) && 
				(!(clientobject.getUserName().equals(UserName))))
			{				
				SendMessageToClient(clientobject.getSocket(),m_messageRFC);	
			}	
		}

		// Wywal spammera
		if(m_floodFlag)
		{
			SendMessageToClient(ClientSocket,"KICK ");
			messagearraylist.clear();		
		}
	}
		
		protected void SendFileRequest(String sender, String recipient, String filename)
		{
			clientobject = GetClientObject(recipient);
			if(clientobject != null)
				SendMessageToClient(clientobject.getSocket(), "UPRQ " + sender + "~" + 
								recipient + ":" + filename);

			else
				LOG.log(Level.WARNING, "clientobject == null");
		}
		
		protected void SendFileResponse(String sender, String recipient, String port)
		{
			clientobject = GetClientObject(recipient);
			String IP = clientobject.getSocket().getInetAddress().getHostAddress();
			
			if(clientobject != null)
				SendMessageToClient(clientobject.getSocket(), "UPRS " + IP + "~" + 
								recipient + ":" + port);
			else
				LOG.log(Level.WARNING, "clientobject == null");
		}

	// Wysyłanie prywatnej wiadomości
	protected void SendPrivateMessage(String Message , String ToUserName)
	{		
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
		{
			SendMessageToClient(clientobject.getSocket(),"PRIV "+Message);	
		}

	}

	// Adres użytkownika
	protected void GetRemoteUserAddress(Socket ClientSocket, String ToUserName, 
		String FromUserName)
	{
			clientobject = GetClientObject(ToUserName);
			if(clientobject != null)
			{			
				SendMessageToClient(clientobject.getSocket(),"REIP "+ FromUserName +"~"+
					ClientSocket.getInetAddress().getHostAddress());			
			}

	}

	// Wysyłanie adresu użytkownika
	protected void SendRemoteUserAddress(Socket ClientSocket, String ToUserName, 
		String FromUserName)
	{
			clientobject = GetClientObject(FromUserName);		
			if(clientobject != null)
			{			
					SendMessageToClient(clientobject.getSocket(),"AEIP "+ ToUserName +"~"+
						ClientSocket.getInetAddress().getHostAddress());						
			}			
	}

	// Licznik użytkowników
	protected void GetUserCount(Socket ClientSocket, String RoomName)
	{
		LOG.log(Level.INFO, "RoomName " + RoomName);
		int m_userListSize = userarraylist.size();
		LOG.log(Level.INFO, "m_userListSize = " + m_userListSize);
		int m_userCount = 0;

		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			LOG.log(Level.INFO, "RoomName " + RoomName);
			LOG.log(Level.INFO, "clientobject.getRoomName() " + clientobject.getRoomName());
			if(clientobject.getRoomName().equals(RoomName))
				m_userCount++;
		}

		SendMessageToClient(ClientSocket,"ROCO "+RoomName+"~"+m_userCount);
	}

	// Zamykanie serwera, niszczenie wszystkiego
	void ExitServer()
	{
		if(thread != null)
		{
			LOG.log(Level.INFO, "Zamykanie wątku", server);
			thread.stop();
			thread = null;
		}
		try {
			if(server != null)
			{			
				LOG.log(Level.INFO, "Zamykanie serwera", server);
				server.close();
				server = null;
			}
		}catch(IOException e) { 
			LOG.log(Level.SEVERE, "Server::ExitServer: ", e);
		}		

		userarraylist = null;
		messagearraylist = null;
		cmdStop.setEnabled(false);
		cmdStart.setEnabled(true);
	}

	// Plik z ustawieniami
	private Properties GetDBProperties()
	{
		// Wczytywanie ustawień
		Properties DBProperties = new Properties();	
		try
		{			
			InputStream	inputstream = this.getClass().getClassLoader().
			getResourceAsStream("server.properties");
			DBProperties.load(inputstream);
			inputstream.close();
		}
		catch (IOException e){
			LOG.log(Level.SEVERE, "Server::GetDBProperties: ", e);
		}
		finally
		{
			return (DBProperties);
		}
	}
}