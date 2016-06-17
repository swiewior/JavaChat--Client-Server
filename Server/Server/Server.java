package server;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import spam.*;

public class Server extends JFrame implements Serializable, ActionListener, Runnable, CommonSettings
{
	//Zmienne globalne
	Properties DBProperties;
	JButton cmdStart,cmdStop;
	ServerSocket server;
	Socket socket;
	Thread thread;    
	ArrayList<ClientObject> userarraylist;
	ArrayList messagearraylist;
	ArrayList<RoomObject> roomArrayList;
	ArrayList<InboxObject> inboxArrayList;
	public Vector clientFileSharingUsername;
	public Vector clientFileSharingSocket;
	ChatCommunication chatcommunication;	
	DataOutputStream dataoutputstream;
	int G_ILoop, Port;
	ClientObject clientobject;
	RoomObject roomobject;
	InboxObject inboxobject;
	String RoomList;
	protected TextField TxtPort, TxtRooms;
	Properties properties;
	InformationDialog dialog;
	JRadioButtonMenuItem logOff, logSevere, logWarning, logInfo;
	JMenuItem settingitem, quititem; 
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME ); 
	Connection conn;
	public SpamPanel spamPanel;
	
	public Server () 
	{
		//Inicjalizacja
		this.setTitle("Java Chat Server");
		this.setResizable(true);
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
		cmdStart.setBounds(175,10,150,30);
		cmdStart.addActionListener(this);
		centerPanel.add("Center", cmdStart);

		cmdStop = new JButton("STOP SERVER");
		cmdStop.setBounds(175,50,150,30);
		cmdStop.setEnabled(false);
		cmdStop.addActionListener(this);
		centerPanel.add("Center", cmdStop);
		
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
		group.add(logOff);
		
		logSevere = new JRadioButtonMenuItem("Krytyczne");
		logmenu.add(logSevere);
		logSevere.addActionListener(this);
		logSevere.setSelected(true);
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
		
		spamPanel = new SpamPanel(this);
		add("South", spamPanel);

		setSize(500,420);
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
		StringBuilder rooms = new StringBuilder();
		rooms.append("Główny;");
		rooms.append(dialog.TxtRooms.getText());
		RoomList = rooms.toString();
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

			// Inicjalizacja list
			userarraylist = new ArrayList();
			messagearraylist = new ArrayList();
			roomArrayList = new ArrayList();
			inboxArrayList = new ArrayList();
			clientFileSharingUsername = new Vector();
			clientFileSharingSocket = new Vector();
			
			//Inicjalizacja Bazy Danych
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException ex) {
				LOG.log(Level.WARNING, null, ex);
			}
			String url = "jdbc:mysql://localhost:3306/userdatabase";
			try {
				conn = DriverManager.getConnection(url, "root", "56321");
			} catch (SQLException ex) {
				LOG.log(Level.SEVERE, "Inicjalizacja Bazy Danych", ex);
				ExitServer();
				return;
			}
			
			//Tworzenie roomArrayList
			String[] rooms = RoomList.split(";");
			for (G_ILoop = 0; G_ILoop < rooms.length; G_ILoop++) {
				roomobject = new RoomObject(rooms[G_ILoop]);
				roomArrayList.add(roomobject);
			}
			
			//Tworzenie inboxArrayList			
			try {
				final String query = "SELECT username FROM registered";
				try (Statement st = conn.createStatement()) {
					ResultSet rs = st.executeQuery(query);
					while (rs.next())
					{
						String registeredName = rs.getString("username");
						inboxobject = new InboxObject(registeredName);
						inboxArrayList.add(inboxobject);
					}
				}
			} catch (SQLException e) {
				LOG.log(Level.WARNING, "", e);
			}

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
	public synchronized void SendMessageToClient(Socket clientsocket,String message)
	{
		try {
			dataoutputstream = new DataOutputStream(clientsocket.getOutputStream());			
			dataoutputstream.writeUTF(message);
			LOG.log(Level.INFO, "SendMessageToClient: "+ message);
		} catch(IOException e) {
			LOG.log(Level.WARNING, "Server::SendMessageToClient: ", e);
		}
	}

	// Obiekt - nazwa użytkowanika
	public ClientObject GetClientObject(String UserName)
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
	protected synchronized void AddUser(Socket ClientSocket,String UserName, String hash)
	{
		int count = 0;
		try {
			final String queryCheck = "SELECT count(*) from registered WHERE username = ?";
			final PreparedStatement ps = conn.prepareStatement(queryCheck);
			ps.setString(1, UserName);
			final ResultSet resultSet = ps.executeQuery();
			if(resultSet.next()) {
				count = resultSet.getInt(1);
			}
			
		} catch (SQLException e) {
			LOG.log(Level.WARNING, null, e);
		}
		
		LOG.log(Level.INFO, "Liczba zarejestrowanych: {0}", Integer.toString(count));
		if (count == 0) {
			SendMessageToClient(ClientSocket,"UNRE");
			return;
		}
		
		count = 0;
		try {
			final String queryCheck = "SELECT count(*) from registered WHERE hash = ?";
			final PreparedStatement ps = conn.prepareStatement(queryCheck);
			ps.setString(1, hash);
			final ResultSet resultSet = ps.executeQuery();
			if(resultSet.next()) {
				count = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, null, e);
		}
		
		LOG.log(Level.INFO, "Hash: {0}", Integer.toString(count));
		if (count == 0) {
			SendMessageToClient(ClientSocket,"WRPA");
			return;
		}
		
		// Jeśli istnieje, koniec
		if(IsUserExists(UserName)) {
			SendMessageToClient(ClientSocket,"EXIS");
			return;	
		}

		// Lista pokoi
		SendMessageToClient(ClientSocket,"ROOM "+RoomList);

		// Info o użytkowniku do wszystkich					
		int m_userListSize = userarraylist.size();
		String m_addRFC = "ADD  "+UserName;
		// Tworzenie listy użytkowników
		StringBuffer stringbuffer = new StringBuffer("LIST ");
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);				
			// Sprawdzenie nazwy pokoi
			if(clientobject.getRoomName().equals(ROOM_NAME)) {
				SendMessageToClient(clientobject.getSocket(),m_addRFC);
				stringbuffer.append(clientobject.getUserName());													
				stringbuffer.append(";");									
			}
		}
	
		// Dodaj użytkownika do listy
		clientobject = new ClientObject(ClientSocket, conn, UserName,ROOM_NAME);
		userarraylist.add(clientobject);
		
		// Wysyłanie listy użytkowników z uwzględnieniem nowego usera
		stringbuffer.append(UserName);
		stringbuffer.append(";");
		SendMessageToClient(ClientSocket,stringbuffer.toString());
		
		// Lista zarejestrowanych osób
		try {
			stringbuffer = new StringBuffer("REGL ");
			final String query = "SELECT username FROM registered";
			try (Statement st = conn.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				
				while (rs.next()) {
					String registeredName = rs.getString("username");
					stringbuffer.append(registeredName);
					stringbuffer.append(";");
				}
			}
			} catch (SQLException e) {
			LOG.log(Level.WARNING, "", e);
		}
		SendMessageToClient(ClientSocket,stringbuffer.toString());
		
		// Wysyłanie listy ostatnich wiadomości
		String[] messages;
		int m_roomListSize = roomArrayList.size();
		for(G_ILoop = 0; G_ILoop < m_roomListSize; G_ILoop++)
		{
			roomobject = (RoomObject) roomArrayList.get(G_ILoop);
			if(roomobject.getRoomName().equals(ROOM_NAME))
			{
				messages = roomobject.getMessageList().toString().split("\\r?\\n");
				for (G_ILoop = 0; G_ILoop < messages.length; G_ILoop++)
				{
					LOG.log(Level.INFO, "Ostatnia wiad: {0}", messages[G_ILoop]);
					SendMessageToClient(ClientSocket, messages[G_ILoop]);
				}
				break;
			}			
		}
		
		//Wysyłanie inbox
		int m_inboxListSize = roomArrayList.size();
		for(G_ILoop = 0; G_ILoop < m_inboxListSize; G_ILoop++) {	
		inboxobject = (InboxObject) inboxArrayList.get(G_ILoop);
			if(inboxobject.getUserName().equals(UserName)) {
				while(!inboxobject.getMessageList().isEmpty()) {
					String message = inboxobject.getMessageList().dequeue();
					LOG.log(Level.INFO, "inbox: {0}", message);
					SendMessageToClient(ClientSocket, message);
				}
				break;
			}
		}
		
		new Ignore().UpdateIgnore(this, clientobject);
	}

	// Usuń użytkownika z serwera
	public synchronized void RemoveUser(String UserName, String RoomName, int RemoveType)
	{
		ClientObject removeclientobject = GetClientObject(UserName);
		if(removeclientobject != null)
		{
			// Usuń użytkownika z listy ignorowanych
			new spam.Ignore().RemoveDisconnected(this, removeclientobject);
			
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
			
			// Usuń użytkownika z bazy danych
			try {
				removeclientobject.delete();
			} catch (SQLException e) {
				LOG.log(Level.WARNING, null, e);
			}
		}
	}

	// Wywal użytkownika gdy wyskoczy exception
	protected synchronized void RemoveUserWhenException(Socket clientsocket)
	{
		int m_userListSize = userarraylist.size();
		ClientObject removeclientobject;
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			removeclientobject = (ClientObject) userarraylist.get(G_ILoop);
			if(removeclientobject.getSocket().equals(clientsocket))
			{
				// Usuń użytkownika z listy ignorowanych
				new spam.Ignore().RemoveDisconnected(this, removeclientobject);
				
				String m_RemoveUserName = removeclientobject.getUserName();
				String m_RemoveRoomName = removeclientobject.getRoomName();
				userarraylist.remove(removeclientobject);	
				userarraylist.trimToSize();					
				m_userListSize = userarraylist.size();
				
				String m_RemoveRFC="INKI "+m_RemoveUserName;
				// Wyślij informacje o usunięciu do wszystkich
				for(int m_ILoop = 0; m_ILoop < m_userListSize; m_ILoop++)
				{
					clientobject = (ClientObject) userarraylist.get(m_ILoop);
					if(clientobject.getRoomName().equals(m_RemoveRoomName))
						SendMessageToClient(clientobject.getSocket(),m_RemoveRFC);
				}
				
				// Usuń użytkownika z bazy danych
				try {
					removeclientobject.delete();
				} catch (SQLException e) {
					LOG.log(Level.WARNING, null, e);
				}
				return;	
			}	
		}
	}

	// Zmiana pokoju
	public void ChangeRoom(Socket ClientSocket,String UserName, String NewRoomName)
	{
		//TempClientObject.update(conn);
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
				if((clientobject.getRoomName().equals(NewRoomName)) && 
					(!(clientobject.getUserName().equals(UserName))) )
					SendMessageToClient(clientobject.getSocket(),m_NewRoomRFC);
			} 			
		}
		
		// Wysyłąnie listy ostatnich wiadomości
		String[] messages;
		int m_roomListSize = roomArrayList.size();
		for(G_ILoop = 0; G_ILoop < m_roomListSize; G_ILoop++)
		{
			roomobject = (RoomObject) roomArrayList.get(G_ILoop);
			if(roomobject.getRoomName().equals(NewRoomName))
			{
				messages = roomobject.getMessageList().toString().split("\\r?\\n");
				for (G_ILoop = 0; G_ILoop < messages.length; G_ILoop++)
				{
					LOG.log(Level.INFO, "Ostatnia wiad: {0}", messages[G_ILoop]);
					SendMessageToClient(ClientSocket, messages[G_ILoop]);
				}
				return;
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

			// Sprawdzaj czy użytkownik przeciąża serwer
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
				SendMessageToClient(clientobject.getSocket(),m_messageRFC);
		}
		
		// Umieszczanie wiadomości w kolejce messageList
		int m_roomListSize = roomArrayList.size();
		m_messageRFC = "MESS "+UserName+":"+Message;
		for(G_ILoop = 0; G_ILoop < m_roomListSize; G_ILoop++)
		{
			roomobject = (RoomObject) roomArrayList.get(G_ILoop);
			if(roomobject.getRoomName().equals(RoomName))
			{
				LOG.log(Level.INFO, "Dodaję do kolejki: {0}", m_messageRFC);
				roomobject.getMessageList().enqueue(m_messageRFC);
				return;
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
	protected void SendFileCancel(String recipient) {
		clientobject = GetClientObject(recipient);
		SendMessageToClient(clientobject.getSocket(), "UPCL");
	}
	

	// Wysyłanie prywatnej wiadomości
	protected void SendPrivateMessage(String Message , String ToUserName)
	{		
		clientobject = GetClientObject(ToUserName);
		if(clientobject != null)
			SendMessageToClient(clientobject.getSocket(),"PRIV "+Message);
		else
		{
			// Umieszczanie wiadomości w kolejce messageList
			int m_inboxListSize = inboxArrayList.size();
			String m_messageRFC = "PRIV "+Message;
			for(G_ILoop = 0; G_ILoop < m_inboxListSize; G_ILoop++)
			{
				inboxobject = (InboxObject) inboxArrayList.get(G_ILoop);
				if(inboxobject.getUserName().equals(ToUserName)) {
					LOG.log(Level.INFO, "Dodaję do inbox: {0}", m_messageRFC);
					inboxobject.getMessageList().enqueue(m_messageRFC);
					return;
				}			
			}
		}
	}

	// Licznik użytkowników
	protected void GetUserCount(Socket ClientSocket, String RoomName)
	{
		LOG.log(Level.INFO, "RoomName {0}", RoomName);
		int m_userListSize = userarraylist.size();
		LOG.log(Level.INFO, "m_userListSize = {0}", m_userListSize);
		int m_userCount = 0;

		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			LOG.log(Level.INFO, "RoomName {0}", RoomName);
			LOG.log(Level.INFO, "clientobject.getRoomName() {0}", clientobject.getRoomName());
			if(clientobject.getRoomName().equals(RoomName))
				m_userCount++;
		}
		SendMessageToClient(ClientSocket,"ROCO "+RoomName+"~"+m_userCount);
	}
		
	protected void RegisterRequest(Socket ClientSocket, String userName, String hash)
	{
		LOG.log(Level.INFO, "hash to register: {0}", hash);
		int count = 0;
		try {
			String queryCheck = "SELECT count(*) from registered WHERE username = ?";
			PreparedStatement ps = conn.prepareStatement(queryCheck);
			ps.setString(1, userName);
			LOG.log(Level.INFO, ps.toString());
			ResultSet resultSet = ps.executeQuery();
			if(resultSet.next()) {
				count = resultSet.getInt(1);
				LOG.log(Level.INFO, "Liczba zarejestrowanych: {0}", count);
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, null, e);
		}
		
		if (count != 0) {
			LOG.log(Level.INFO, "Użytkownik jest już zarejestrowany");
			SendMessageToClient(ClientSocket,"REGO " + "EXIST");
			return;
		}
		
		try {
			String sql = "insert into registered (hash, username) "
				+ "values (?, ?)";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString (1, hash);
			st.setString (2, userName);
			st.execute();
			SendMessageToClient(ClientSocket,"REGO " + "OK");
		} catch (SQLException e) {
			LOG.log(Level.WARNING, null, e);
			SendMessageToClient(ClientSocket,"REGO " + "OTHER ERROR");
			return;
		}
		
		// Wysyłanie na nowo listy zarejestrowanych użytkowników do wszystkich
		// Tworzenie listy zarejestrowanych
		StringBuilder stringbuffer = new StringBuilder("REGL ");
		try {
			final String query = "SELECT username FROM registered";
			try (Statement st = conn.createStatement()) {
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					String registeredName = rs.getString("username");
					stringbuffer.append(registeredName);
					stringbuffer.append(";");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "", e);
			return;
		}
		// Wysyłanie lsity do wszystkich
		int m_userListSize = userarraylist.size();
		for(G_ILoop = 0; G_ILoop < m_userListSize; G_ILoop++)
		{
			clientobject = (ClientObject) userarraylist.get(G_ILoop);
			SendMessageToClient(clientobject.getSocket(), stringbuffer.toString());
		}
		
		//Dodawanie świerzaka do inbox
		inboxobject = new InboxObject(userName);
		inboxArrayList.add(inboxobject);
	}
	
	void Ignore(String IgnoredUserName, String IsIgnored) {
		boolean boolIgnored = Boolean.parseBoolean(IsIgnored);
		ClientObject IgnoredClient = GetClientObject(IgnoredUserName);
		
		new spam.Ignore().Ignore(this, IgnoredClient, boolIgnored);
	}

	public void RemoveSelected() {
		new spam.Ignore().RemoveSelected(this);
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
		
		//usuwam listę zalogowanych
		try {
			Statement st = conn.createStatement();
			String sql = "Truncate table logged";
			st.executeUpdate(sql);
		} catch (SQLException | NullPointerException e) {
			LOG.log(Level.SEVERE, "Czyszczenie tablicy logged", e);
		}
		
		//zamykanie bazy danych
		try {
			conn.close();
		} catch (SQLException | NullPointerException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		
		//zamykanie logu
		try {
			ServerLogger.close();
		} catch (SecurityException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		cmdStop.setEnabled(false);
		cmdStart.setEnabled(true);
	}

	// Plik z ustawieniami
	private Properties GetDBProperties()
	{
		// Wczytywanie ustawień
		Properties dbprop = new Properties();	
		try
		(InputStream inputstream = this.getClass().getClassLoader().
			getResourceAsStream("server.properties")) {
			dbprop.load(inputstream);
		}
		catch (IOException e){
			LOG.log(Level.SEVERE, "Server::GetDBProperties: ", e);
		}
		finally
		{
			return (dbprop);
		}
	}
}