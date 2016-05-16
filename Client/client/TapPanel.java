package client;

import gui.ScrollView;
import gui.BorderPanel;
import gui.HistoryFrame;
import gui.MessageCanvas;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TapPanel extends JPanel implements CommonSettings,ActionListener
{
	// Zmienne globalne
	Client chatclient;
	protected TextField TxtUserCount;
	ScrollView UserScrollView,RoomScrollView;
	protected ListViewCanvas UserCanvas,RoomCanvas;
	JButton CmdChangeRoom, CmdIgnoreUser, CmdSendDirect, CmdHistory, CmdSendFile;
	HistoryFrame historyFrame;
	MessageCanvas messagecanvas;
	
	// Konstruktor
	TapPanel(Client parent) {
		chatclient = parent;

		JPanel Tappanel = new JPanel(new BorderLayout());
		CardLayout cardlayout = new CardLayout();
		JPanel MainJPanel = new JPanel(cardlayout);

		JPanel UserJPanel = new JPanel(new BorderLayout());
		UserCanvas = new ListViewCanvas(chatclient,USER_CANVAS);

		UserScrollView = new ScrollView(UserCanvas,true,true,
			TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
		UserCanvas.scrollview = UserScrollView;	  	 
		UserJPanel.add("Center",UserScrollView);
		
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
		UserJPanel.add("South",UserButtonJPanel);

		JPanel RoomJPanel = new JPanel(new BorderLayout());
		RoomCanvas = new ListViewCanvas(chatclient,ROOM_CANVAS);

		RoomScrollView = new ScrollView(RoomCanvas,true,true,
			TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
		RoomCanvas.scrollview = RoomScrollView;	  
		RoomJPanel.add("Center",RoomScrollView);	  

		JPanel RoomButtonJPanel = new JPanel(new BorderLayout());
		JPanel RoomCountJPanel = new JPanel(new BorderLayout());
		Label LblCaption = new Label("Informacje",1);
		RoomCountJPanel.add("North",LblCaption);
		TxtUserCount = new TextField();
		TxtUserCount.setEditable(false);
		RoomCountJPanel.add("Center",TxtUserCount);	  	  
		RoomButtonJPanel.add("Center",RoomCountJPanel);

		CmdChangeRoom = new JButton("Zmień pokój");
		CmdChangeRoom.addActionListener(this);
		RoomButtonJPanel.add("South",CmdChangeRoom);

		RoomJPanel.add("South",RoomButtonJPanel);

		MainJPanel.add("UserJPanel",UserJPanel);
		MainJPanel.add("RoomJPanel",RoomJPanel);
		cardlayout.show(MainJPanel,"UserJPanel");
		BorderPanel borderpanel = new BorderPanel(this,
			chatclient,cardlayout,MainJPanel,TAPPANEL_WIDTH,TAPPANEL_HEIGHT);

		borderpanel.addTab("Użytkownicy","UserJPanel");
		borderpanel.addTab("Pokoje","RoomJPanel");

		Tappanel.add(borderpanel);
		add("Center",Tappanel);	  	  
	}


	public void actionPerformed(ActionEvent evt) {
		//Zmiana pokoju
		if(evt.getSource().equals(CmdChangeRoom)) {
			chatclient.ChangeRoom();
		}
		//Ignorowanie
		if(evt.getSource().equals(CmdIgnoreUser)) {		
			if(evt.getActionCommand().equals("Ignoruj")) {
				UserCanvas.IgnoreUser(true);		
			}
			else {
				UserCanvas.IgnoreUser(false);			
			}
		}

		if(evt.getSource().equals(CmdSendDirect)) {
				UserCanvas.SendDirectMessage();	
		}
		//Historia
		if(evt.getSource().equals(CmdHistory))
			chatclient.historyFrame.setVisible(true);

		// Wysyłanie pliku
		if(evt.getSource().equals(CmdSendFile))
			UserCanvas.SendFile();
	}
}