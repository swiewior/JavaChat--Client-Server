import java.awt.Panel;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;

public class TapPanel extends Panel implements CommonSettings,ActionListener
{
    // Zmienne globalne
    Client chatclient;
    protected TextField TxtUserCount;
    ScrollView UserScrollView,RoomScrollView;
    protected ListViewCanvas UserCanvas,RoomCanvas;
    JButton CmdChangeRoom, CmdIgnoreUser, CmdSendDirect ;
    
    // Konstruktor
    TapPanel(Client parent)
    {

        chatclient = parent;

        Panel Tappanel = new Panel(new BorderLayout());
        CardLayout cardlayout = new CardLayout();
        Panel MainPanel = new Panel(cardlayout);

        Panel UserPanel = new Panel(new BorderLayout());
        UserCanvas = new ListViewCanvas(chatclient,USER_CANVAS);

        UserScrollView = new ScrollView(UserCanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
        UserCanvas.scrollview = UserScrollView;	  	 
        UserPanel.add("Center",UserScrollView);
        
        Panel UserButtonPanel = new Panel(new BorderLayout());
        CmdSendDirect = new JButton("Wyślij prywatną wiadomość");
        CmdSendDirect.addActionListener(this);
        UserButtonPanel.add("North",CmdSendDirect);
        CmdIgnoreUser = new JButton("Ignoruj");
        CmdIgnoreUser.addActionListener(this);
        UserButtonPanel.add("Center",CmdIgnoreUser);
        UserPanel.add("South",UserButtonPanel);

        Panel RoomPanel = new Panel(new BorderLayout());
        RoomCanvas = new ListViewCanvas(chatclient,ROOM_CANVAS);

        RoomScrollView = new ScrollView(RoomCanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
        RoomCanvas.scrollview = RoomScrollView;	  
        RoomPanel.add("Center",RoomScrollView);	  

        Panel RoomButtonPanel = new Panel(new BorderLayout());
        Panel RoomCountPanel = new Panel(new BorderLayout());
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

        MainPanel.add("UserPanel",UserPanel);
        MainPanel.add("RoomPanel",RoomPanel);
        cardlayout.show(MainPanel,"UserPanel");
        BorderPanel borderpanel = new BorderPanel(this,chatclient,cardlayout,MainPanel,TAPPANEL_WIDTH,TAPPANEL_HEIGHT);

        borderpanel.addTab("Użytkownicy","UserPanel");
        borderpanel.addTab("Pokoje","RoomPanel");

        Tappanel.add(borderpanel);
        add("Center",Tappanel);	  		  


    }


    public void actionPerformed(ActionEvent evt)
    {
        //Zmiana pokoju
        if(evt.getSource().equals(CmdChangeRoom))
        {
            chatclient.ChangeRoom();
        }
        //Ignorowanie
        if(evt.getSource().equals(CmdIgnoreUser))
        {			
            if(evt.getActionCommand().equals("Ignoruj")) {
                UserCanvas.IgnoreUser(true);				
            }
            else {
                UserCanvas.IgnoreUser(false);					
            }
        }

        if(evt.getSource().equals(CmdSendDirect))
        {
                UserCanvas.SendDirectMessage();	
        }
    }


}