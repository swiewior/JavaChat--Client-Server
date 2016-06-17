package client;

import filetransfer.Download;
import filetransfer.Upload;
import gui.HistoryFrame;
import gui.MessageBox;
import gui.MessageCanvas;
import gui.ScrollView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Client extends JFrame implements Serializable, Runnable, 
				KeyListener, ActionListener, CommonSettings, ItemListener 
{
	// Zmienne globalne
	String UserName,UserRoom,ServerName,AppletStatus,ChatLogo,BannerName,
					ServerData,RoomList,SplitString, Password;
	int ServerPort,IconCount,TotalUserCount, TotalRegisterCount, G_ILoop;
	boolean StartFlag;	
	Socket socket;
	DataInputStream datainputstream;
	DataOutputStream dataoutputstream;
	Dimension dimension;
	JLabel InformationLabel;
	StringBuffer stringbuffer;
	MessageCanvas messagecanvas;
	ScrollView MessageScrollView;
	Thread thread, uploadThread;
	StringTokenizer Tokenizer;
	TapPanel tappanel;
	TextField TxtMessage;
	JButton CmdSend,CmdExit;
	public Font TextFont;
	protected PrivateChat[] privatewindow;
	protected int PrivateWindowCount;
	InformationDialog dialog;
	Toolkit toolkit;
	MenuItem loginitem, disconnectitem, seperatoritem, quititem, aboutitem, registeritem;
	CheckboxMenuItem logOff, logSevere, logWarning, logInfo;
	Upload upload;
	
	public History hist;
	public HistoryFrame historyFrame;
	public String HistoryFile;
	public String TransferFile;
	
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	
	public Client() 
	{		
		toolkit = Toolkit.getDefaultToolkit();		
		if(toolkit.getScreenSize().getWidth() > 778)
			setSize(778, 575);
		else
			setSize((int)toolkit.getScreenSize().getWidth(),(int)toolkit.getScreenSize().getHeight() - 20);			
		setResizable(false);
		dimension = getSize();	
		setLayout(new BorderLayout());	

		setTitle("Java Chat Client");	
		addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent evt) { DisconnectChat();System.exit(0);}});

		// Menu
		MenuBar menubar = new MenuBar();
		Menu loginmenu = new Menu("Połączenie");		
		loginitem = new MenuItem("Zaloguj");
		loginitem.addActionListener(this);
		disconnectitem = new MenuItem("Rozłącz");
		disconnectitem.addActionListener(this);
		quititem = new MenuItem("Wyjście");
		quititem.addActionListener(this);
		loginmenu.add(loginitem);
		loginmenu.add(disconnectitem);
		loginmenu.addSeparator();
		loginmenu.add(quititem);

		Menu aboutmenu = new Menu("Pomoc ");
		aboutitem = new MenuItem("O programie ");
		aboutitem.addActionListener(this);
		aboutmenu.add(aboutitem);
		
		//Ustawienia menu Logi
		Menu logmenu = new Menu("Logi");
		ButtonGroup group = new ButtonGroup();
		
		logOff = new CheckboxMenuItem("Wyłączone",  false);
		logmenu.add(logOff);
		logOff.addItemListener(this);
		//group.add(logOff);
		
		logSevere = new CheckboxMenuItem("Krytyczne", true);
		logmenu.add(logSevere);
		logSevere.addItemListener(this);
		//logSevere.setSelected(true);
		//group.add(logSevere);
		
		logWarning = new CheckboxMenuItem("Ostrzeżenia", false);
		logmenu.add(logWarning);
		//group.add(logWarning);
		logWarning.addItemListener(this);
		
		logInfo = new CheckboxMenuItem("Informacyjne", false);
		logmenu.add(logInfo);
		//group.add(logInfo);
		logInfo.addItemListener(this);
		
		menubar.add(loginmenu);
		menubar.add(aboutmenu);
		menubar.add(logmenu);
		this.setMenuBar(menubar);
		
		// Parametry	
		UserName = "";			
		UserRoom = "";
		RoomList = "";
		HistoryFile = "";
		TransferFile = "";
		
		// Okno prywatnych wiadomości
		privatewindow = new PrivateChat[MAX_PRIVATE_WINDOW];
		PrivateWindowCount = 0;
		
		SetAppletStatus("");		
		InitializeAppletComponents();	// Inicjalizacja wszystkich komponentów
	}
	
		private void InitializeAppletComponents()
	{
		// Ustawienia okienka
		Font font = new Font("Dialog",Font.BOLD,11);
		TextFont = new Font("Dialog",0,13);	
		setFont(font);	

		// Panel informacji
		JPanel CenterPanel = new JPanel(new BorderLayout());
		JPanel InformationPanel = new JPanel(new BorderLayout());	
		InformationLabel = new JLabel();
		InformationLabel.setHorizontalAlignment(JLabel.CENTER);

		InformationLabel.setForeground(Color.black); 
		InformationPanel.add("Center",InformationLabel);
		CenterPanel.add("North",InformationPanel);

		// Panel wiadomości
		JPanel MessagePanel = new JPanel(new BorderLayout());
		messagecanvas = new MessageCanvas(this);				
		MessageScrollView = new ScrollView(messagecanvas,true,true,
			TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
		messagecanvas.scrollview = MessageScrollView;	
		MessagePanel.add("Center",MessageScrollView);

		tappanel = new TapPanel(this);

		MessagePanel.add("East",tappanel); 		
		CenterPanel.add("Center",MessagePanel);

		// Panel strumienia wejścia
		JPanel InputPanel = new JPanel(new BorderLayout());
		JPanel TextBoxPanel = new JPanel(new BorderLayout());
		Label LblGeneral = new Label("Wiadomość: ");		
		TxtMessage = new TextField();
		TxtMessage.addKeyListener(this);
		TxtMessage.setFont(TextFont);

		CmdSend = new JButton("Wyślij");
		CmdSend.addActionListener(this);

		TextBoxPanel.add("West",LblGeneral);
		TextBoxPanel.add("Center",TxtMessage);
		TextBoxPanel.add("East",CmdSend);
		InputPanel.add("Center",TextBoxPanel);

		JPanel InputButtonPanel =new JPanel(new BorderLayout());

		CmdExit = new JButton("Zamknij");
		CmdExit.addActionListener(this);
		InputButtonPanel.add("Center",CmdExit);
		InputPanel.add("East",InputButtonPanel);

		JPanel EmptyPanel = new JPanel();
		InputPanel.add("South",EmptyPanel);

		CenterPanel.add("South",InputPanel);

		add("Center",CenterPanel);	
		
		DisableAll();
		LoginToChat();
	}
		
	// Eventy przycisków
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource().equals(CmdSend)) {
			if (!(TxtMessage.getText().trim().equals("")))
				SendMessage();	
		}	
		if ((evt.getSource().equals(CmdExit)) || (evt.getSource().equals(quititem))) {
			DisconnectChat();
			System.exit(0);
		}
		if(evt.getSource().equals(loginitem)) {
			LoginToChat();
		}
		if(evt.getSource().equals(disconnectitem)) {			
			DisconnectChat();						
		}		
		if(evt.getSource().equals(aboutitem)) {			
			MessageBox messagebox = new MessageBox(this,false);					
			messagebox.AddMessage("Java Chat Client");
			messagebox.AddMessage("Sebastian Wiewióra");
			messagebox.AddMessage("Informatyka Stosowana, 2015/2016");

		}

	}
	@Override
	public void itemStateChanged(ItemEvent evt) {
		logOff.setState(false);
		logSevere.setState(false);
		logWarning.setState(false);
		logInfo.setState(false);
		
		if(evt.getItem().equals(logOff.getLabel()))	 {
			logOff.setState(true);
			LOG.setLevel(Level.OFF);	
		}
		if(evt.getItem().equals(logSevere.getLabel()))	{
			logSevere.setState(true);
			LOG.setLevel(Level.SEVERE);
		}
		if(evt.getItem().equals(logWarning.getLabel())) {
			logWarning.setState(true);
			LOG.setLevel(Level.WARNING);
		}		
		if(evt.getItem().equals(logInfo.getLabel())) {
			logInfo.setState(true);
			LOG.setLevel(Level.ALL);
		}
	}

	// Enter wysyła
	@Override
	public void keyPressed(KeyEvent evt)
	{
		if((evt.getKeyCode() == 10) && (!(TxtMessage.getText().trim().equals(""))))		
		{
			SendMessage();
		}
	}
	@Override
	public void keyTyped(KeyEvent e){}
	@Override
	public void keyReleased(KeyEvent e){}
	
	public void LoginToChat() {
		// Panel logowania
		dialog = new InformationDialog(this);	
	}

	
	public void ConnectToServer(InformationDialog d)
	{
		dialog = d;
		messagecanvas.ClearAll();
		LOG.log(Level.INFO, "Łączę z serwerem... ");
		messagecanvas.AddMessageToMessageObject("Łączę z serwerem... ",MESSAGE_TYPE_ADMIN);
		
		ServerName 	= dialog.TxtServerName.getText();
		ServerPort 	= Integer.parseInt(dialog.TxtServerPort.getText());
		
		try {					
			socket = new Socket(ServerName,ServerPort);
			
			dataoutputstream = new DataOutputStream(socket.getOutputStream());
			datainputstream = new DataInputStream(socket.getInputStream());
			StartFlag = true;
			thread = new Thread(this);
			thread.start();
			dialog.setConnectLabel("Połączono!", Color.GREEN);
			
		} catch (IOException e) {
			try {
				LOG.log(Level.SEVERE, "Połączenie nieudane", e);
				dialog.setConnectLabel("Połączenie nieudane!", Color.RED);
				dataoutputstream.close();
				datainputstream.close();
				socket.close();
			} catch (IOException e2) {
				LOG.log(Level.SEVERE, "Błąd zamykania socketa i strumieni", e2);
			}
		}
	}
	
	public void Register(InformationDialog d)
	{
		dialog = d;
		LOG.log(Level.INFO, "Rejestruję...");
		messagecanvas.AddMessageToMessageObject("Rejestruję...",MESSAGE_TYPE_ADMIN);
		dialog.setRegisterLabel("Rejestruję...", Color.BLACK);
		
		UserName 	= dialog.txtRegisterNick.getText();
		Password 	= dialog.txtRegisterPassword.getText();

		String hash = MD5(UserName+Password);
		SendMessageToServer("REGI " + UserName + ":" + hash);
	}
	
	public void Login(InformationDialog d)
	{
		dialog = d;
		LOG.log(Level.INFO, "Loguję...");
		messagecanvas.AddMessageToMessageObject("Loguję...",MESSAGE_TYPE_ADMIN);	
		
		UserName 	= dialog.TxtUserName.getText();
		Password 	= dialog.TxtPassword.getText();
		HistoryFile = dialog.TxtHistoryFile.getText();

		String hash = MD5(UserName+Password);
		SendMessageToServer("HELO " + UserName + ":" + hash);		
	}
		
	public String MD5(String md5) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			LOG.log(Level.SEVERE, null, e);
		}
    return null;
	}

	public void SendMessageToServer(String Message)
	{
		try {
			dataoutputstream.writeUTF(Message);	
		}catch(IOException e) { 
			LOG.log(Level.SEVERE, "Client::SendMessageToServer: ", e);
			QuitConnection(QUIT_TYPE_NULL);}
		catch(NullPointerException e2){
			LOG.log(Level.SEVERE, "Client::SendMessageToServer: ", e2);
			dialog.setRegisterLabel("Sprawdź połączenie", Color.RED);
		}
		LOG.log(Level.INFO, "SendMessageToServer: '"+Message+"'");
	}

	// Wysyłanie wiadomości
	private void SendMessage()
	{
		SendMessageToServer("MESS "+UserRoom+"~"+UserName+": "+TxtMessage.getText());
		messagecanvas.AddMessageToMessageObject(UserName+": "+TxtMessage.getText(),MESSAGE_TYPE_DEFAULT);
		
		//Zapis do Historii
		String msgTime = (new Date()).toString();
		try {
			hist.addMessage(UserName, TxtMessage.getText(), UserRoom, msgTime);							 
			DefaultTableModel table = (DefaultTableModel) historyFrame.jTable1.getModel();
			table.addRow(new Object[]{UserName, TxtMessage.getText(), UserRoom, msgTime});
		} catch(Exception e){
			LOG.log(Level.WARNING, "Client::SendMessage (History): ", e);
		}
		
		TxtMessage.setText("");
		TxtMessage.requestFocus();
	}

	// Panel górny
	private void UpdateInformationLabel()
	{
		stringbuffer = new StringBuffer();
		stringbuffer.append("Użytkownik: ");
		stringbuffer.append(UserName);
		stringbuffer.append("   ");
		stringbuffer.append("Pokój: ");
		stringbuffer.append(UserRoom);
		stringbuffer.append("   ");
		stringbuffer.append("Liczba użytkowników: ");
		stringbuffer.append(TotalUserCount);
		stringbuffer.append("   ");	
		InformationLabel.setText(stringbuffer.toString());
	}
	
	// Wielowątkowość
	@Override
	public void run()
	{
		while(thread != null)
		{
			// Obsługa Wskaźników
			try {
				ServerData = datainputstream.readUTF();	
				LOG.log(Level.INFO, "datainputstream: " + ServerData);

				// Lista użytkowników
				if(ServerData.startsWith("LIST"))
				{
					Tokenizer = new StringTokenizer(ServerData.substring(5),";");						
					// Aktualizacja nagłówka informacyjnego
					TotalUserCount = Tokenizer.countTokens();						
					UpdateInformationLabel();
					// Dodanie użytkownika						
					tappanel.UserCanvas.ClearAll();
					while(Tokenizer.hasMoreTokens())
					{
						String T = Tokenizer.nextToken();
						tappanel.UserCanvas.AddListItemToMessageObject(T);
					}				

					messagecanvas.ClearAll();										
					messagecanvas.AddMessageToMessageObject("Nowy pokój: "+UserRoom, MESSAGE_TYPE_JOIN);		
				}
				
				if(ServerData.startsWith("REGL"))
				{
					Tokenizer = new StringTokenizer(ServerData.substring(5),";");						
					// Aktualizacja nagłówka informacyjnego
					TotalRegisterCount = Tokenizer.countTokens();
					// Dodanie użytkownika						
					tappanel.RegisterCanvas.ClearAll();
					while(Tokenizer.hasMoreTokens())
						tappanel.RegisterCanvas.AddListItemToMessageObject(Tokenizer.nextToken());
				}

				// Aktualizacja listy pokoi
				if( ServerData.startsWith("ROOM"))
				{
					// Wczytywanie listy pokoji
					Tokenizer = new StringTokenizer(ServerData.substring(5),";");
					UserRoom = Tokenizer.nextToken();
					UpdateInformationLabel();
					// Wczytywanie listy użytkowników						
					tappanel.RoomCanvas.ClearAll();
					tappanel.RoomCanvas.AddListItemToMessageObject(UserRoom);
					while(Tokenizer.hasMoreTokens())
					{							
						tappanel.RoomCanvas.AddListItemToMessageObject(Tokenizer.nextToken());							
					}
					
					hist = new History(HistoryFile);
					historyFrame = new HistoryFrame(hist);
					historyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					historyFrame.setVisible(false);
					EnableAll();
					dialog.dispose();
				}

				// Dodanie do pokoju
				if(ServerData.startsWith("ADD")) {
					// Aktualizacja panelu informacyjnego
					TotalUserCount++;
					UpdateInformationLabel();

					// Dodanie do listy użytkowników
					SplitString = ServerData.substring(5);
					EnablePrivateWindow(SplitString);
					tappanel.UserCanvas.AddListItemToMessageObject(SplitString);
					messagecanvas.AddMessageToMessageObject(SplitString + " dołączył do czatu",MESSAGE_TYPE_JOIN);						
				}

				// Użytkownik istnieje
				if (ServerData.startsWith("EXIS"))
				{						
					messagecanvas.AddMessageToMessageObject(" Nazwa użytkownika jest już zajęta.",MESSAGE_TYPE_ADMIN);
					LOG.log(Level.INFO, "Nazwa użytkownika jest już zajęta.");
				}	
				
				// Użytkownik istnieje
				if (ServerData.startsWith("UNRE"))
				{						
					messagecanvas.AddMessageToMessageObject(" Użytkownik nie jest zarejestrowany",MESSAGE_TYPE_ADMIN);
					LOG.log(Level.INFO, "Użytkownik nie jest zarejestrowany.");
				}
				// Nieprawidłowe hasło przy logowaniu
				if (ServerData.startsWith("WRPA"))
				{						
					messagecanvas.AddMessageToMessageObject(" Nieprawidłowe hasło",MESSAGE_TYPE_ADMIN);
					LOG.log(Level.INFO, "Nieprawidłowe hasło.");
				}

				// Usunięcie
				if (ServerData.startsWith("REMO"))
				{						
					SplitString = ServerData.substring(5);	

					tappanel.UserCanvas.RemoveListItem(SplitString);
					RemoveUserFromPrivateChat(SplitString);
					messagecanvas.AddMessageToMessageObject(SplitString+" wylogował się.",MESSAGE_TYPE_LEAVE );

					// Aktualizacja panelu informacyjnego
					TotalUserCount--;
					UpdateInformationLabel();

				}
				// Odbieranie wiadomości
				if( ServerData.startsWith("MESS"))
				{
					// Sprawdź czy użytkownik jest ignorowany
					if(!(tappanel.UserCanvas.IsIgnoredUser(ServerData.substring(5,ServerData.indexOf(":")))))	
					{
						messagecanvas.AddMessageToMessageObject(ServerData.substring(5),MESSAGE_TYPE_DEFAULT);	
						
						//Zapis do Historii
						String msgTime = (new Date()).toString();
						try {
							hist.addMessage(ServerData.substring(5,ServerData.indexOf(":")), ServerData.substring(5), UserName, msgTime);							 
							DefaultTableModel table = (DefaultTableModel) historyFrame.jTable1.getModel();
							table.addRow(new Object[]{ServerData.substring(5,ServerData.indexOf(":")), 
								ServerData.substring(ServerData.indexOf(":")+2), UserRoom, msgTime});
						} catch(Exception e) {
							LOG.log(Level.WARNING, "Client::run::MESS (History): ", e);
						}
					}				
				}

				// Wyrzucanie
				if (ServerData.startsWith("KICK"))
				{
					messagecanvas.AddMessageToMessageObject("Zostałeś wyrzucony za spamowanie",MESSAGE_TYPE_ADMIN);
					LOG.log(Level.INFO, "Zostałeś wyrzucony za spamowanie");
					thread = null;
					QuitConnection(QUIT_TYPE_KICK);	
				}

				// Informacja o wyrzuconym użytkowniku
				if( ServerData.startsWith("INKI"))
				{
					SplitString = ServerData.substring(5);							
					tappanel.UserCanvas.RemoveListItem(SplitString);
					RemoveUserFromPrivateChat(SplitString);
					messagecanvas.AddMessageToMessageObject(SplitString+" został usunięty przez administratora.",MESSAGE_TYPE_ADMIN );

					// Aktualizacja panelu informacyjnego
					TotalUserCount--;
					UpdateInformationLabel();	
				}

				// Zmiana pokoju
				if( ServerData.startsWith("CHRO")) {
					UserRoom = ServerData.substring(5);	
				}

				// Dołączanie do pokoju
				if( ServerData.startsWith("JORO"))
				{
					SplitString = ServerData.substring(5);
					tappanel.UserCanvas.AddListItemToMessageObject(SplitString);
					// Aktualizacja panelu informacyjnego
					TotalUserCount++;
					UpdateInformationLabel();	

					messagecanvas.AddMessageToMessageObject(SplitString + " dołącza do czatu",MESSAGE_TYPE_JOIN);
				}

				// Opuszczenie pokoju
				if( ServerData.startsWith("LERO"))
				{
					SplitString = ServerData.substring(5,ServerData.indexOf("~"));
					tappanel.UserCanvas.RemoveListItem(SplitString);
					messagecanvas.AddMessageToMessageObject(SplitString+" opuścił pokój: "
									+UserRoom+" i dołączył do: "+ServerData.substring(ServerData.indexOf("~")+1),MESSAGE_TYPE_ADMIN);													

					// Aktualizacja panelu informacyjnego
					TotalUserCount--;
					UpdateInformationLabel();	
				}

				// liczba userów w pokoju					
				if( ServerData.startsWith("ROCO"))
				{
					SplitString = ServerData.substring(5,ServerData.indexOf("~"));
					tappanel.TxtUserCount.setText("Liczba użytkowników w "+SplitString+" : "
									+ServerData.substring(ServerData.indexOf("~")+1));
				}
				
				// Odbieranie pliku
				if( ServerData.startsWith("UPRQ"))
				{
					String Sender = ServerData.substring(5,ServerData.indexOf("~"));
					String Filename = ServerData.substring(ServerData.indexOf(":")+1, ServerData.indexOf("|"));
					double Filesize = Double.parseDouble(ServerData.substring(ServerData.indexOf("|")+1));
					double sizeToShow = Filesize / 1024 / 1024;
					if(0 == JOptionPane.showConfirmDialog(null, ("Odebrać '" + Filename +
									"' ("+ new DecimalFormat("###.###").format(sizeToShow) + " MB) od "+Sender+"?"), 
									"Odbieranie pliku", JOptionPane.YES_NO_OPTION)) {
						JFileChooser jf = new JFileChooser();
						jf.setSelectedFile(new File(Filename));
						int returnVal = jf.showSaveDialog(null);
						
						String saveTo = jf.getSelectedFile().getPath();
						if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION)
						{
							Download dwn = new Download(saveTo, messagecanvas, ServerPort, Filesize, this,
								Sender);
							Thread t = new Thread(dwn);
							t.start();
							SendMessageToServer("UPRS "+ UserName + "~" + Sender + ":" + dwn.port);
						}
						else{
							SendMessageToServer("UPRS " + UserName + "~" + Sender + ":" + "NO");
						}
					}
					else
						SendMessageToServer("UPRS " + UserName + "~" + Sender + ":" + "NO");
				}
				
				// Wysyłanie pliku
				if(ServerData.startsWith("UPRS"))
				{
					String sender = ServerData.substring(5,ServerData.indexOf("~"));
					LOG.log(Level.INFO, "UPRS: sender = " + sender);
					String answer = ServerData.substring(ServerData.indexOf(":")+1);
					LOG.log(Level.INFO, "UPRS: answer = " + answer);
					
					if(!answer.equals("NO"))
					{
						int port	= Integer.parseInt(answer);
						String addr = sender;

						upload = new Upload(addr, port, tappanel.UserCanvas.file, messagecanvas);
						uploadThread = new Thread(upload);
						uploadThread.start();
					}
					else
						messagecanvas.AddMessageToMessageObject("Odrzucono prośbę o odebranie pliku",
							MESSAGE_TYPE_LEAVE);
				}		
				
				//Anulowanie pobierania
				if(ServerData.startsWith("UPCL")) {
					upload.end();
					uploadThread.stop();
				}
				
				// Rejestracja
				if(ServerData.startsWith("REGO"))
				{
					String answer = ServerData.substring(5);
					LOG.log(Level.INFO, "REGO: answer = " + answer);
					
					if(answer.equals("EXIST")) {
						dialog.setRegisterLabel("Użytkownik istnieje", Color.RED);
					}
					else if(answer.equals("OK")) {
						dialog.setRegisterLabel("Zarejestrowano!", Color.GREEN);
						messagecanvas.AddMessageToMessageObject("Zarejestrowano", MESSAGE_TYPE_ADMIN);
					}
					else {
						dialog.setRegisterLabel("Błąd rejestracji", Color.RED);
					}
				}

				// Prywatne wiadomości
				if( ServerData.startsWith("PRIV")) {												
					SplitString = ServerData.substring(5,ServerData.indexOf(":"));
					// Sprawdź czy ignorowany
					if(!(tappanel.UserCanvas.IsIgnoredUser(SplitString))) {
						boolean PrivateFlag = false;
						for(G_ILoop = 0; G_ILoop < PrivateWindowCount;G_ILoop++) {								
							if(privatewindow[G_ILoop].UserName.equals(SplitString)) {
								privatewindow[G_ILoop].AddMessageToMessageCanvas(ServerData.substring(5));
								privatewindow[G_ILoop].show();
								privatewindow[G_ILoop].requestFocus();
								PrivateFlag = true;
								
								//Zapis do Historii
								String msgTime = (new Date()).toString();
								try {
									hist.addMessage(SplitString, ServerData.substring(ServerData.indexOf(":")+2), UserName, msgTime);							 
									DefaultTableModel table = (DefaultTableModel) historyFrame.jTable1.getModel();
									table.addRow(new Object[]{SplitString, ServerData.substring(ServerData.indexOf(":")+2), UserName, msgTime});
								} catch(Exception e){
									LOG.log(Level.WARNING, "Client::run::PRIV (History): ", e);
								}
								break;										
							}
						}	

						if(!(PrivateFlag)) {	
							if(PrivateWindowCount >= MAX_PRIVATE_WINDOW)
								messagecanvas.AddMessageToMessageObject("Przekraczasz limit prywatnych wiadomości",MESSAGE_TYPE_ADMIN);	
							else {														
								privatewindow[PrivateWindowCount++] = new PrivateChat(this,SplitString);
								privatewindow[PrivateWindowCount-1].AddMessageToMessageCanvas(ServerData.substring(5));
								privatewindow[PrivateWindowCount-1].show();
								privatewindow[PrivateWindowCount-1].requestFocus();	

								//Zapis do Historii
								String msgTime = (new Date()).toString();
								try {
									hist.addMessage(SplitString, ServerData.substring(ServerData.indexOf(":")+2), UserName, msgTime);							 
									DefaultTableModel table = (DefaultTableModel) historyFrame.jTable1.getModel();
									table.addRow(new Object[]{SplitString, ServerData.substring(ServerData.indexOf(":")+2), UserName, msgTime});
								} catch(Exception e) {
									LOG.log(Level.WARNING, "Client::run::PRIV:PrivateFlag (History): ", e);
								}	
							}
						}
					}						
				}
			} catch(SocketException e) { 
				LOG.log(Level.SEVERE, "", e);
				messagecanvas.AddMessageToMessageObject("Utracono połączenie z serwerem", MESSAGE_TYPE_ADMIN);
				QuitConnection(QUIT_TYPE_NULL);
				return;
			} catch(Exception e) { 
				LOG.log(Level.SEVERE, "Client::run: ", e);
				messagecanvas.AddMessageToMessageObject(e.getMessage(),MESSAGE_TYPE_ADMIN);
				QuitConnection(QUIT_TYPE_DEFAULT); 
				return;
			}	
		}	
	}
	
	// Odblokuj prywatny czat jeżeli użytkownik jest zalogowany
	private void EnablePrivateWindow(String ToUserName)
	{
		for(G_ILoop = 0; G_ILoop < PrivateWindowCount; G_ILoop++)
		{
			if(privatewindow[G_ILoop].UserName.equals(ToUserName))
			{
				privatewindow[G_ILoop].messagecanvas.AddMessageToMessageObject(ToUserName + " jest dostępny",MESSAGE_TYPE_ADMIN);	
				//privatewindow[G_ILoop].EnableAll();			
				return;	
			}
		}	
	}
	
	// Zablokuj prywatny czat jeżeli użytkownik się wyloguje
	private void RemoveUserFromPrivateChat(String ToUserName)
	{
		for(G_ILoop = 0; G_ILoop < PrivateWindowCount; G_ILoop++)
		{
			if(privatewindow[G_ILoop].UserName.equals(ToUserName))
			{
				privatewindow[G_ILoop].messagecanvas.AddMessageToMessageObject(ToUserName + " jest niedostępny",MESSAGE_TYPE_ADMIN);	
				//privatewindow[G_ILoop].DisableAll();			
				return;	
			}
		}	
	}
	
	// Wysyłanie prywatnej wiadomości na serwer
	protected void SentPrivateMessageToServer(String Message, String ToUserName) 
	{
		SendMessageToServer("PRIV "+ToUserName+"~"+UserName+": "+Message);	
		
		//Zapis do Historii
		String msgTime = (new Date()).toString();
		try {
			hist.addMessage(UserName, Message, ToUserName, msgTime);							 
			DefaultTableModel table = (DefaultTableModel) historyFrame.jTable1.getModel();
			table.addRow(new Object[]{UserName, Message, ToUserName, msgTime});
		} catch(Exception e) {
			LOG.log(Level.WARNING, "Client::SentPrivateMessageToServer: ", e);
		}
	}

	// Usuń prywatne okno
	protected void RemovePrivateWindow(String ToUserName)
	{		
		int m_UserIndex = 0;
		for(G_ILoop = 0; G_ILoop < PrivateWindowCount; G_ILoop++) {
			m_UserIndex++;
			if(privatewindow[G_ILoop].UserName.equals(ToUserName)) 
				break;
		}						
		for(int m_iLoop = m_UserIndex;m_iLoop < PrivateWindowCount; m_iLoop++) {
			privatewindow[m_iLoop] = privatewindow[m_iLoop+1];	
		}
		PrivateWindowCount--;		
	}	
	
	// Zmień pokój
	protected void ChangeRoom()
	{
		if(tappanel.RoomCanvas.SelectedUser.equals(""))
		{
			messagecanvas.AddMessageToMessageObject("Nieprawidłowy wybór pokoju",MESSAGE_TYPE_ADMIN);
			return;	
		}

		if(tappanel.RoomCanvas.SelectedUser.equals(UserRoom))
		{
			messagecanvas.AddMessageToMessageObject("Jesteś już w tym pokoju",MESSAGE_TYPE_ADMIN);
			return;	
		}

		SendMessageToServer("CHRO "+UserName+"~"+tappanel.RoomCanvas.SelectedUser);
	}
	
	// Pobierz liczbę użytkowników z serwera
	protected void GetRoomUserCount(String RoomName)
	{
		SendMessageToServer("ROCO "+RoomName);	
	}
	
	// Wysyłanie pliku - wiadomośc na serwer
	public void SendFileMessage(String ToUserName, File filepath)
	{
		SendMessageToServer("UPRQ " + UserName + "~" + ToUserName + ":" + filepath.getName() + "|" + filepath.length());
	}

	// Zamykanie połączenia
	private void QuitConnection(int QuitType) {		
		if(socket != null) {
			try {
				if (QuitType == QUIT_TYPE_DEFAULT)
					SendMessageToServer("QUIT "+UserName+"~"+UserRoom);
				if (QuitType == QUIT_TYPE_KICK)
					SendMessageToServer("KICK "+UserName+"~"+UserRoom);
				socket.close();	
				socket = null;
				tappanel.UserCanvas.ClearAll();					
			} catch(Exception e) {
				LOG.log(Level.WARNING, "Client::QuitConnection: ", e);
			}	
		}
		if(thread != null) {
				thread.stop();
				thread = null;
		}		
		
		//zamykanie logu
		try {
			ClientLogger.close();
		} catch (SecurityException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		
		DisableAll();
		StartFlag = false;
		SetAppletStatus("Połączenie z serwerem zakończone.");		
	}

	// Wyłączenie chatu
	private void DisableAll()
	{		
		TxtMessage.setEnabled(false);
		CmdSend.setEnabled(false);
		tappanel.enable(false);			
		disconnectitem.setEnabled(false);
		loginitem.setEnabled(true);

		UserName = "";
		UserRoom = "";
		TotalUserCount = 0;
	}

	// Włączenie chatu
	private void EnableAll()
	{
		TxtMessage.setEnabled(true);
		CmdSend.setEnabled(true);
		tappanel.enable(true);	
		disconnectitem.setEnabled(true);
		loginitem.setEnabled(false);
	}

	// Wylogowanie
	private void DisconnectChat() 
	{
		if(socket != null) 
		{
			messagecanvas.AddMessageToMessageObject("Wylogowano",MESSAGE_TYPE_ADMIN);				
			QuitConnection(QUIT_TYPE_DEFAULT);			
		}	
	}

	// Ustawianie statusu
	private void SetAppletStatus(String Message)
	{
		if (messagecanvas != null)
			messagecanvas.AddMessageToMessageObject(Message,MESSAGE_TYPE_ADMIN);		
	}
}