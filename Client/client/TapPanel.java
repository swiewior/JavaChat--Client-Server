package client;

import gui.BorderPanel;
import gui.HistoryFrame;
import gui.MessageCanvas;
import gui.ScrollView;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TapPanel extends JPanel implements CommonSettings,ActionListener
{
	// Zmienne globalne
	Client chatclient;
	protected TextField TxtUserCount;
	ScrollView UserScrollView,RoomScrollView, RegisterScrollView;
	protected ListViewCanvas UserCanvas,RoomCanvas, RegisterCanvas;
	JButton CmdChangeRoom, CmdIgnoreUser, CmdSendDirect, CmdHistory, CmdSendFile,
		CmdSendDirect2, CmdIgnoreUser2;
	HistoryFrame historyFrame;
	MessageCanvas messagecanvas;
	
	// Konstruktor
	TapPanel(Client parent) {
		chatclient = parent;

		JPanel Tappanel = new JPanel(new BorderLayout());
		CardLayout cardlayout = new CardLayout();
		JPanel MainPanel = new JPanel(cardlayout);

		//Panel Użytkownicy
		JPanel UserPanel = new JPanel(new BorderLayout());
		UserCanvas = new ListViewCanvas(chatclient,USER_CANVAS);
		
		UserScrollView = new ScrollView(UserCanvas,true,true,
			TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
		UserCanvas.scrollview = UserScrollView;	  	 
		UserPanel.add("Center",UserScrollView);
		
		JPanel UserButtonJPanel = new JPanel(new BorderLayout());
		CmdSendDirect = new JButton("Wyślij prywatną wiadomość");
		CmdSendDirect.addActionListener(this);
		UserButtonJPanel.add("North",CmdSendDirect);
		CmdIgnoreUser = new JButton("Ignoruj");
		CmdIgnoreUser.addActionListener(this);
		UserButtonJPanel.add("Center",CmdIgnoreUser);
		JPanel ButtonsJPanel = new JPanel(new BorderLayout());
		
		CmdHistory = new JButton("Historia");
		CmdHistory.addActionListener(this);
		ButtonsJPanel.add("North",CmdHistory);
		CmdSendFile = new JButton("Wyślij plik");
		CmdSendFile.addActionListener(this);
		ButtonsJPanel.add("Center",CmdSendFile);	
		
		UserButtonJPanel.add("South", ButtonsJPanel);
		UserPanel.add("South",UserButtonJPanel);
		
		//Panel Zarejestrowani
		JPanel RegisterPanel = new JPanel(new BorderLayout());
		RegisterCanvas = new ListViewCanvas(chatclient,REGISTER_CANVAS);
		
		RegisterScrollView = new ScrollView(RegisterCanvas,true,true,
			TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
		RegisterCanvas.scrollview = RegisterScrollView;	  	 
		RegisterPanel.add("Center",RegisterScrollView);
		
		JPanel RegisterButtonJPanel = new JPanel(new BorderLayout());
		CmdSendDirect2 = new JButton("Wyślij prywatną wiadomość");
		CmdSendDirect2.addActionListener(this);
		RegisterButtonJPanel.add("North",CmdSendDirect2);
		RegisterPanel.add("South",RegisterButtonJPanel);

		//Panel Pokoje
		JPanel RoomPanel = new JPanel(new BorderLayout());
		RoomCanvas = new ListViewCanvas(chatclient,ROOM_CANVAS);

		RoomScrollView = new ScrollView(RoomCanvas,true,true,
			TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
		RoomCanvas.scrollview = RoomScrollView;	  
		RoomPanel.add("Center",RoomScrollView);	  

		JPanel RoomButtonPanel = new JPanel(new BorderLayout());
		JPanel RoomCountPanel = new JPanel(new BorderLayout());
		Label LblCaption = new Label("Informacje",1);
		RoomCountPanel.add("North",LblCaption);
		TxtUserCount = new TextField();
		TxtUserCount.setEditable(false);
		RoomCountPanel.add("Center",TxtUserCount);	  	  
		RoomButtonPanel.add("Center",RoomCountPanel);

		CmdChangeRoom = new JButton("Zmień pokój");
		CmdChangeRoom.addActionListener(this);
		RoomButtonPanel.add("South",CmdChangeRoom);
		RoomPanel.add("South",RoomButtonPanel);
		
		//Dodaj do panelu
		MainPanel.add("UserPanel",UserPanel);
		MainPanel.add("RegisterPanel",RegisterPanel);
		MainPanel.add("RoomPanel",RoomPanel);
		
		cardlayout.show(MainPanel,"UserPanel");
		BorderPanel borderpanel = new BorderPanel(this,
			chatclient,cardlayout,MainPanel,TAPPANEL_WIDTH,TAPPANEL_HEIGHT);

		borderpanel.addTab("Użytkownicy","UserPanel");
		borderpanel.addTab("Wszyscy", "RegisterPanel");
		borderpanel.addTab("Pokoje","RoomPanel");
		
		Tappanel.add(borderpanel);
		add("Center",Tappanel);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		//Zmiana pokoju
		if(evt.getSource().equals(CmdChangeRoom))
			chatclient.ChangeRoom();
		//Ignorowanie
		if(evt.getSource().equals(CmdIgnoreUser)) {		
			if(evt.getActionCommand().equals("Ignoruj"))
				UserCanvas.IgnoreUser(true);
			else 
				UserCanvas.IgnoreUser(false);
		}
		// Wyślij prywatną wiadomość
		if(evt.getSource().equals(CmdSendDirect))
			UserCanvas.SendDirectMessage();

		if(evt.getSource().equals(CmdSendDirect2))
			RegisterCanvas.SendDirectMessage();
		//Historia
		if(evt.getSource().equals(CmdHistory))
			chatclient.historyFrame.setVisible(true);

		// Wysyłanie pliku
		if(evt.getSource().equals(CmdSendFile))
			UserCanvas.SendFile();
	}
}