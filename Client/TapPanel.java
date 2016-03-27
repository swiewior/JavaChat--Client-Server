
import java.awt.Panel;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
public class TapPanel extends Panel implements CommonSettings,ActionListener
{
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
	  
	  
	  Panel RoomPanel = new Panel(new BorderLayout());
	  RoomCanvas = new ListViewCanvas(chatclient,ROOM_CANVAS);
	  
	  RoomScrollView = new ScrollView(RoomCanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
	  RoomCanvas.scrollview = RoomScrollView;	  
	  RoomPanel.add("Center",RoomScrollView);	  
	  
	  Panel RoomButtonPanel = new Panel(new BorderLayout());
	  Panel RoomCountPanel = new Panel(new BorderLayout());
	  Label LblCaption = new Label("ROOM COUNT",1);
	  RoomCountPanel.add("North",LblCaption);
	  TxtUserCount = new TextField();
	  TxtUserCount.setEditable(false);
	  RoomCountPanel.add("Center",TxtUserCount);	  	  
	  RoomButtonPanel.add("Center",RoomCountPanel);
	  
	  CmdChangeRoom = new JButton("Change Room");
	  CmdChangeRoom.addActionListener(this);
	  RoomButtonPanel.add("South",CmdChangeRoom);
	  
	  RoomPanel.add("South",RoomButtonPanel);
	  
	
	  MainPanel.add("UserPanel",UserPanel);
	  MainPanel.add("RoomPanel",RoomPanel);
	  cardlayout.show(MainPanel,"UserPanel");
	  BorderPanel borderpanel = new BorderPanel(this,chatclient,cardlayout,MainPanel,TAPPANEL_WIDTH,TAPPANEL_HEIGHT);
	  
	  borderpanel.addTab("USERS","UserPanel");
	  borderpanel.addTab("ROOMS","RoomPanel");
	  
	  Tappanel.add(borderpanel);
	  add("Center",Tappanel);	  		  
	  
	  	    	  
	}
	

	public void actionPerformed(ActionEvent evt)
	{
            //Zmiana pokoju
		if(evt.getSource().equals(CmdChangeRoom))
		{
				
		}
				
	}
		
	Client chatclient;
	protected TextField TxtUserCount;
	ScrollView ImageScrollView,UserScrollView,RoomScrollView;
	protected ListViewCanvas UserCanvas,RoomCanvas;
	JButton CmdChangeRoom;
}