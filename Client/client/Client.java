package client;

import filetransfer.Download;
import filetransfer.Upload;
import gui.MessageBox;
import gui.MessageCanvas;
import gui.ScrollView;
import gui.HistoryFrame;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.io.*;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Client extends Frame implements Serializable, Runnable, 
				KeyListener, ActionListener, CommonSettings 
{
  // Zmienne globalne
  String UserName,UserRoom,ServerName,AppletStatus,ChatLogo,BannerName,
					ServerData,RoomList,SplitString;
  int ServerPort,IconCount,TotalUserCount,G_ILoop;
  boolean StartFlag;	
  Socket socket;
  DataInputStream datainputstream;
  DataOutputStream dataoutputstream;
  Color[] ColorMap;
  Dimension dimension;
  Label InformationLabel;
  StringBuffer stringbuffer;
  MessageCanvas messagecanvas;
  ScrollView MessageScrollView;
  Thread thread;
  StringTokenizer Tokenizer;
  TapPanel tappanel;
  TextField TxtMessage;
  JButton CmdSend,CmdExit;
  public Font TextFont;
  protected PrivateChat[] privatewindow;
  protected int PrivateWindowCount;
  InformationDialog dialog;
  Toolkit toolkit;
  MenuItem loginitem;
  MenuItem disconnectitem;
  MenuItem seperatoritem;
  MenuItem quititem,aboutitem;
	
	public History hist;
	public HistoryFrame historyFrame;
	public String HistoryFile;
	public String TransferFile;
	
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
    seperatoritem = new MenuItem("-");
    quititem = new MenuItem("Wyjście");
    quititem.addActionListener(this);
    loginmenu.add(loginitem);
    loginmenu.add(disconnectitem);
    loginmenu.add(seperatoritem);
    loginmenu.add(quititem);

    Menu aboutmenu = new Menu("Pomoc ");
    aboutitem = new MenuItem("O programie ");
    aboutitem.addActionListener(this);
    aboutmenu.add(aboutitem);

    menubar.add(loginmenu);
    menubar.add(aboutmenu);
    setMenuBar(menubar);

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
	
  private void ConnectToServer()
  {
    messagecanvas.ClearAll();
    messagecanvas.AddMessageToMessageObject("Łączę z serwerem... ",MESSAGE_TYPE_ADMIN);								
    try {
			hist = new History(HistoryFile);
			historyFrame = new HistoryFrame(hist);
			historyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			historyFrame.setVisible(false);

			socket = new Socket(ServerName,ServerPort);					

			dataoutputstream = new DataOutputStream(socket.getOutputStream());
			SendMessageToServer("HELO " +UserName);			
			datainputstream = new DataInputStream(socket.getInputStream());

			StartFlag = true;
			thread = new Thread(this);
			thread.start();				
			EnableAll();	
    } catch(IOException _IoExc) { QuitConnection(QUIT_TYPE_NULL);}			
  }

  private void SendMessageToServer(String Message)
  {
    try {
      dataoutputstream.writeBytes(Message+"\r\n");	
    }catch(IOException _IoExc) { QuitConnection(QUIT_TYPE_DEFAULT);}		
		System.out.print("SendMessageToServer: '"+Message+"'" + "\n");
  }
    
  private void InitializeAppletComponents()
  {
    // Ustawienia okienka
    setBackground(Color.white);	
    Font font = new Font("Dialog",Font.BOLD,11);
    TextFont = new Font("Dialog",0,11);	
    setFont(font);	

    // Górny panel
    Panel TopPanel = new Panel(new BorderLayout());
    TopPanel.setBackground(Color.white);	
    add("North",TopPanel);	

    // Panel informacji
    Panel CenterPanel = new Panel(new BorderLayout());
    Panel InformationPanel = new Panel(new BorderLayout());	
    InformationPanel.setBackground(Color.white);			
    InformationLabel = new Label();		
    InformationLabel.setAlignment(1);

    InformationLabel.setForeground(Color.black); 
    InformationPanel.add("Center",InformationLabel);
    CenterPanel.add("North",InformationPanel);

    // Panel wiadomości
    Panel MessagePanel = new Panel(new BorderLayout());
    messagecanvas = new MessageCanvas(this);				
    MessageScrollView = new ScrollView(messagecanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
    messagecanvas.scrollview = MessageScrollView;	
    MessagePanel.add("Center",MessageScrollView);

    tappanel = new TapPanel(this);

    MessagePanel.add("East",tappanel); 	  
    CenterPanel.add("Center",MessagePanel);

    // Panel strumienia wejścia
    Panel InputPanel = new Panel(new BorderLayout());
    Panel TextBoxPanel = new Panel(new BorderLayout());
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

    Panel InputButtonPanel =new Panel(new BorderLayout());

    CmdExit = new JButton("Zamknij");
    CmdExit.addActionListener(this);
    InputButtonPanel.add("Center",CmdExit);
    InputPanel.add("East",InputButtonPanel);

    Panel EmptyPanel = new Panel();
    InputPanel.add("South",EmptyPanel);

    CenterPanel.add("South",InputPanel);

    add("Center",CenterPanel);	

    DisableAll();
    LoginToChat();
  }

  private void LoginToChat()
  {
    // Panel logowania
    dialog = new InformationDialog(this);	
    if (dialog.IsConnect == true)
    {
      UserName 	= dialog.TxtUserName.getText();
      ServerName 	= dialog.TxtServerName.getText();
      ServerPort 	= Integer.parseInt(dialog.TxtServerPort.getText());
			HistoryFile = dialog.TxtHistoryFile.getText();
      
      ConnectToServer();				
    }		
  }

  // Eventy przycisków
  @Override
  public void actionPerformed(ActionEvent evt)
  {
      if(evt.getSource().equals(CmdSend))
      {
        if (!(TxtMessage.getText().trim().equals("")))
          SendMessage();	
      }	

      if ((evt.getSource().equals(CmdExit)) || (evt.getSource().equals(quititem)))
      {
        DisconnectChat();
        System.exit(0);
      }

      if(evt.getSource().equals(loginitem)) {
        LoginToChat();				
      }

      if(evt.getSource().equals(disconnectitem)) {			
        DisconnectChat();						
      }		
      if(evt.getSource().equals(aboutitem))
      {			
        MessageBox messagebox = new MessageBox(this,false);					
        messagebox.AddMessage("Java Chat Client");
        messagebox.AddMessage("Sebastian Wiewióra");
        messagebox.AddMessage("Informatyka Stosowana, 2015/2016");

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
		} catch(Exception ex){}
		
		TxtMessage.setText("");
    TxtMessage.requestFocus();
	}

  // Panel górny
  private void UpdateInformationLabel()
  {
      stringbuffer = new StringBuffer();
      stringbuffer.append("Użytkownik: ");
      stringbuffer.append(UserName);
      stringbuffer.append("    ");
      stringbuffer.append("Pokój: ");
      stringbuffer.append(UserRoom);
      stringbuffer.append("    ");
      stringbuffer.append("Liczba użytkowników: ");
      stringbuffer.append(TotalUserCount);
      stringbuffer.append("    ");	
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
        ServerData = datainputstream.readLine();	
				System.out.print("datainputstream: " + ServerData + "\n");
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
            tappanel.UserCanvas.AddListItemToMessageObject(Tokenizer.nextToken());							
          }	

          messagecanvas.ClearAll();										
          messagecanvas.AddMessageToMessageObject("Nowy pokój: "+UserRoom, MESSAGE_TYPE_JOIN);		
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
        }

        // Dodanie do pokoju
        if(ServerData.startsWith("ADD"))
        {
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
          thread = null;
          QuitConnection(QUIT_TYPE_NULL);
        }					 

        // Usunięcie
        if (ServerData.startsWith("REMO"))
        {						
          SplitString = ServerData.substring(5);	

          tappanel.UserCanvas.RemoveListItem(SplitString);
          RemoveUserFromPrivateChat(SplitString);
          messagecanvas.AddMessageToMessageObject(SplitString+" został wylogowany z czatu.",MESSAGE_TYPE_LEAVE );

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
						} catch(Exception ex){}
					}				
        }

        // Wyrzucanie
        if (ServerData.startsWith("KICK"))
        {
          messagecanvas.AddMessageToMessageObject("Zostałeś wyrzucony za zbyt dużo wiadomości",MESSAGE_TYPE_ADMIN);
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
        if( ServerData.startsWith("CHRO"))
        {
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
					String Filename = ServerData.substring(ServerData.indexOf(":")+1);
					if(0 == JOptionPane.showConfirmDialog(null, ("Odebrać '" + Filename + "' od "+SplitString+"?"), 
									"Odbieranie pliku", JOptionPane.YES_NO_OPTION))
					{
						JFileChooser jf = new JFileChooser();
						jf.setSelectedFile(new File(Filename));
						int returnVal = jf.showSaveDialog(null);
						
						String saveTo = jf.getSelectedFile().getPath();
						if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION)
						{
							Download dwn = new Download(saveTo, messagecanvas);
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
					System.out.print("UPRS: sender = " + sender + "\n");
					String answer = ServerData.substring(ServerData.indexOf(":")+1);
					System.out.print("UPRS: answer = " + answer + "\n");
					
					if(!answer.equals("NO"))
					{
						int port  = Integer.parseInt(answer);
						String addr = sender;

						//ui.jButton5.setEnabled(false); ui.jButton6.setEnabled(false);

							Upload upl = new Upload(addr, port, tappanel.UserCanvas.file, messagecanvas);
							Thread t = new Thread(upl);
							t.start();
					}
					else
						messagecanvas.AddMessageToMessageObject("Odrzucono prośbę o odebranie pliku od" 
										+ sender, MESSAGE_TYPE_ADMIN);
				}				

        // Prywatne wiadomości
        if( ServerData.startsWith("PRIV"))
        {												
          SplitString = ServerData.substring(5,ServerData.indexOf(":"));
          // Sprawdź czy ignorowany
          if(!(tappanel.UserCanvas.IsIgnoredUser(SplitString)))	
          {
            boolean PrivateFlag = false;
            for(G_ILoop = 0; G_ILoop < PrivateWindowCount;G_ILoop++)
            {								
              if(privatewindow[G_ILoop].UserName.equals(SplitString))
              {
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
								} catch(Exception ex){}
                break;										
              }
            }	

          if(!(PrivateFlag))
          {	
            if(PrivateWindowCount >= MAX_PRIVATE_WINDOW)
            {
              messagecanvas.AddMessageToMessageObject("Przekraczasz limit prywatnych wiadomości",MESSAGE_TYPE_ADMIN);	
            }
            else
            {														
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
							} catch(Exception ex){}
							break;			
            }
          }

          }						
        }
      }catch(Exception _Exc) { messagecanvas.AddMessageToMessageObject(_Exc.getMessage(),MESSAGE_TYPE_ADMIN);QuitConnection(QUIT_TYPE_DEFAULT); }	
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
        privatewindow[G_ILoop].EnableAll();			
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
        privatewindow[G_ILoop].DisableAll();			
        return;	
      }
    }	
  }
  
  // Wysyłanie prywatnej wiadomości na serwer
  protected void SentPrivateMessageToServer(String Message, String ToUserName) {
		SendMessageToServer("PRIV "+ToUserName+"~"+UserName+": "+Message);	
		
		//Zapis do Historii
		String msgTime = (new Date()).toString();
		try {
			hist.addMessage(UserName, Message, ToUserName, msgTime);               
			DefaultTableModel table = (DefaultTableModel) historyFrame.jTable1.getModel();
			table.addRow(new Object[]{UserName, Message, ToUserName, msgTime});
		} catch(Exception ex){}
  }

  // Usuń prywatne okno
  protected void RemovePrivateWindow(String ToUserName)
  {		
    int m_UserIndex = 0;
    for(G_ILoop = 0; G_ILoop < PrivateWindowCount; G_ILoop++) {
      m_UserIndex++;
      if(privatewindow[G_ILoop].UserName.equals(ToUserName)) break;
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
		SendMessageToServer("UPRQ " + UserName + "~" + ToUserName + ":" + filepath.getName());
	}

  // Zamykanie połączenia
  private void QuitConnection(int QuitType)
  {		
    if(socket != null)
    {
      try {
        if (QuitType == QUIT_TYPE_DEFAULT)
          SendMessageToServer("QUIT "+UserName+"~"+UserRoom);
        if (QuitType == QUIT_TYPE_KICK)
          SendMessageToServer("KICK "+UserName+"~"+UserRoom);
        socket.close();	
        socket = null;
        tappanel.UserCanvas.ClearAll();					
      }catch(IOException _IoExc) { }				
    }
    if(thread != null)
    {
        thread.stop();
        thread = null;
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
        messagecanvas.AddMessageToMessageObject("Połączenie z serwerem zakończone",MESSAGE_TYPE_ADMIN);				
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