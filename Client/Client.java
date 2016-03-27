import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.io.*;
import java.net.Socket;
import javax.swing.JButton;

public class Client extends Frame implements Serializable, Runnable, KeyListener, ActionListener, CommonSettings 
{
    // Zmienne globalne
    String UserName,UserRoom,ServerName,AppletStatus,ChatLogo,BannerName,ServerData,RoomList,SplitString;
    int ServerPort,IconCount,TotalUserCount,G_ILoop;
    boolean StartFlag;	
    Socket socket;
    DataInputStream datainputstream;
    DataOutputStream dataoutputstream;
    Color[] ColorMap;
    Dimension dimension;
    MediaTracker tracker;
    Label InformationLabel;
    StringBuffer stringbuffer;
    MessageCanvas messagecanvas;
    ScrollView MessageScrollView;
    Thread thread;
    StringTokenizer Tokenizer;
    TapPanel tappanel;
    TextField TxtMessage;
    JButton CmdSend,CmdExit;
    Font TextFont;
    InformationDialog dialog;
    Toolkit toolkit;
    MenuItem loginitem;
    MenuItem disconnectitem;
    MenuItem seperatoritem;
    MenuItem quititem,aboutitem;
	
    Client() 
    {		
        toolkit = Toolkit.getDefaultToolkit();		
        if(toolkit.getScreenSize().getWidth() > 778)
                setSize(778, 575);
        else
                setSize((int)toolkit.getScreenSize().getWidth(),(int)toolkit.getScreenSize().getHeight() - 20);			
        setResizable(false);
        dimension = getSize();	
        setLayout(new BorderLayout());	

        setTitle("Java Chat Application");		
        addWindowListener(new WindowAdapter() {
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
        UserName 	= "";			
        UserRoom	="";
        RoomList 	= "";

        // Mapa kolorów
        ColorMap 	= new Color[6];
        ColorMap[0] =  Color.white; // Tło 
        ColorMap[1] =  Color.white; // Tło panelu górnego
        ColorMap[2] =  Color.white; // Kolor panelu bocznego
        ColorMap[3] =  Color.black; // kolor wiadomości
        ColorMap[4] =  Color.white; // tło górnego panelu
        ColorMap[5] =  Color.black; // Tło tekstu menu


        SetAppletStatus("");		
        InitializeAppletComponents();	// Inicjalizacja wszystkich komponentów

    }
	
    private void ConnectToServer()
    {
        messagecanvas.ClearAll();
        messagecanvas.AddMessageToMessageObject("Łączę z serwerem... ",MESSAGE_TYPE_ADMIN);								
        try {
                socket = new Socket(ServerName,ServerPort);					

                dataoutputstream = new DataOutputStream(socket.getOutputStream());
                SendMessageToServer("Nowy użytkownik: " +UserName);			
                datainputstream  = new DataInputStream(socket.getInputStream());

                StartFlag = true;

                thread = new Thread(this);
                thread.start();				
                EnableAll();	
                UpdateInformationLabel(); 
        }catch(IOException _IoExc) { QuitConnection();}			
    }

    private void SendMessageToServer(String Message)
    {
        try {
                dataoutputstream.writeUTF(Message+"\r\n");	
        }catch(IOException _IoExc) { QuitConnection();}			
    }

    private void InitializeAppletComponents()
    {
        // Ustawienia okienka
        setBackground(ColorMap[0]);	
        Font font = new Font("Dialog",Font.BOLD,11);
        TextFont = new Font("Dialog",0,11);	
        setFont(font);	

        // Górny panel
        Panel TopPanel = new Panel(new BorderLayout());
        TopPanel.setBackground(ColorMap[4]);	
        add("North",TopPanel);	

        // Panel informacji
        Panel CenterPanel = new Panel(new BorderLayout());
        Panel InformationPanel = new Panel(new BorderLayout());	
        InformationPanel.setBackground(ColorMap[1]);			
        InformationLabel = new Label();		
        InformationLabel.setAlignment(1);

        InformationLabel.setForeground(ColorMap[5]); 
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

    private void SendMessage()
    {
            SendMessageToServer("~"+UserName+": "+TxtMessage.getText());	
            TxtMessage.setText("");
            TxtMessage.requestFocus();	
    }

    // Panel górny
    private void UpdateInformationLabel()
    {
            stringbuffer = new StringBuffer();
            stringbuffer.append("Użytkownik: ");
            stringbuffer.append(UserName);
            stringbuffer.append("       ");
            stringbuffer.append("Pokój: ");
            stringbuffer.append(UserRoom);
            stringbuffer.append("       ");
            stringbuffer.append("Liczba użytkowników: ");
            stringbuffer.append(TotalUserCount);
            stringbuffer.append("       ");	
            InformationLabel.setText(stringbuffer.toString());
    }

    @Override
    public void run()
    {
          try {
        while (true) 
        {
            String message = datainputstream.readUTF();
            messagecanvas.AddMessageToMessageObject(message, MESSAGE_TYPE_DEFAULT);
        }
            } catch( IOException ie ) { 
        System.out.println( ie ); 
    }
    }

    // Zamykanie połączenia
    private void QuitConnection()
    {		
            if(socket != null)
            {
                    try {
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
            SetAppletStatus("ADMIN: Połączenie z serwerem zakończone.");					
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
                            QuitConnection();			
                    }	
    }

    // UStawianie statusu
    private void SetAppletStatus(String Message)
    {
            if (messagecanvas != null)
                    messagecanvas.AddMessageToMessageObject(Message,MESSAGE_TYPE_ADMIN);		
    }

    // Main
    public static void main(String args[]) {		
            Client mainFrame = new Client();				
    }


}