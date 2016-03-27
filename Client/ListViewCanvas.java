

import java.awt.Dimension;
import java.awt.Canvas;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Color;
import java.awt.FontMetrics;
public class ListViewCanvas extends Canvas implements CommonSettings
{
    Dimension offDimension,dimension;
    Graphics offGraphics;  	
    Client chatclient;    
    ArrayList ListArray;
    int G_ILoop,XOffset,YOffset;
    MessageObject messageobject;
    ScrollView scrollview;   
    FontMetrics fontmetrics; 
    int CanvasType,TotalWidth,TotalHeight;
    protected String SelectedUser;
    
	ListViewCanvas(Client Parent,int canvastype)
	{
		chatclient = Parent;		
		dimension = size();
		ListArray = new ArrayList();
		SelectedUser = "";							
		CanvasType = canvastype;	
		setFont(chatclient.getFont());		
		fontmetrics = chatclient.getFontMetrics(chatclient.getFont());	
	}
	
	protected void AddListItemToMessageObject(String ListItem)
	{	
		int m_startY = DEFAULT_LIST_CANVAS_POSITION;	
		if(ListArray.size() > 0)
		{
			messageobject = (MessageObject) ListArray.get(ListArray.size()-1);
			m_startY = 	messageobject.StartY + DEFAULT_LIST_CANVAS_INCREMENT;
		}
		messageobject = new MessageObject();
		messageobject.Message = ListItem;
		messageobject.StartY  = m_startY;
		messageobject.Selected = false;
		messageobject.Width	  = fontmetrics.stringWidth(ListItem)+DEFAULT_LIST_CANVAS_INCREMENT;
		ListArray.add(messageobject);
		TotalWidth = Math.max(TotalWidth,messageobject.Width);
		scrollview.setValues(TotalWidth,m_startY+DEFAULT_LIST_CANVAS_HEIGHT);
		scrollview.setScrollPos(1,1);
		scrollview.setScrollSteps(2,1,DEFAULT_SCROLLING_HEIGHT);
		repaint();					
	}
	
	/****** Function To Clear All the Item From ListArray *********/
	protected void ClearAll()
	{
		ListArray.clear();
		TotalWidth = 0;
		TotalHeight = 0;
		scrollview.setValues(TotalWidth,TotalHeight);	
	}
	
	/*******Function To Get the Index of Give Message from List Array *********/
	private int GetIndexOf(String Message)
	{
		int m_listSize = ListArray.size();
		for(G_ILoop = 0 ; G_ILoop < m_listSize; G_ILoop++)
		{
			messageobject = (MessageObject) ListArray.get(G_ILoop);
			if(messageobject.Message.equals(Message))			
				return G_ILoop;			
		}
		
		return -1;
			
	}
	
	
	/**********Set or Remove Ignore List from Array ********/
	protected void IgnoreUser(boolean IsIgnore)
	{
		if (SelectedUser.equals(""))
		{
			chatclient.messagecanvas.AddMessageToMessageObject("Invalid User Selection!",MESSAGE_TYPE_ADMIN);
			return;
		}
		if (SelectedUser.equals(chatclient.UserName))
		{
			chatclient.messagecanvas.AddMessageToMessageObject("You can not ignored yourself!",MESSAGE_TYPE_ADMIN);	
			return;
		}
				
		
	}
	
	
	/********** Check Whether the User ignored or not *********/
	protected boolean IsIgnoredUser(String UserName)
	{
		int m_listIndex = GetIndexOf(UserName);	
		if (m_listIndex >= 0)
		{
			messageobject = (MessageObject) ListArray.get(m_listIndex);
			return messageobject.IsIgnored;	
		}
		
		/****By Fefault****/
		return false;
		
	}
	
	/********** Function To Remove the Given Item From the List Array ********/
	protected void RemoveListItem(String ListItem)
	{
		int ListIndex = GetIndexOf(ListItem);
		if( ListIndex >= 0)
		{
			messageobject = (MessageObject) ListArray.get(ListIndex);
			int m_StartY = messageobject.StartY;
			ListArray.remove(ListIndex);		
			int m_listSize = ListArray.size();
			int m_nextStartY;
			for(G_ILoop = ListIndex; G_ILoop < m_listSize; G_ILoop++)
			{
				messageobject = (MessageObject) ListArray.get(G_ILoop);
				m_nextStartY = messageobject.StartY;
				messageobject.StartY = m_StartY;
				m_StartY = m_nextStartY;	
			} 	
			
		}
		repaint();
	}
	
	private void PaintFrame(Graphics graphics)
	{
		int m_listArraySize = ListArray.size();		
		for(G_ILoop = 0; G_ILoop < m_listArraySize; G_ILoop++)
		{			
			messageobject = (MessageObject) ListArray.get(G_ILoop);			
			if((messageobject.StartY + messageobject.Height) >= YOffset)
			{				
				PaintListItemIntoCanvas(graphics,messageobject);	
			}
		}
	}
	
	private void PaintListItemIntoCanvas(Graphics graphics, MessageObject messageObject)
	{
		int m_StartY = messageObject.StartY - YOffset;
			
		if(messageobject.Selected == true)
		{
			graphics.setColor(Color.blue);
			graphics.fillRect(5-XOffset+DEFAULT_LIST_CANVAS_HEIGHT,m_StartY,TotalWidth,DEFAULT_LIST_CANVAS_INCREMENT);
			graphics.setColor(Color.white);		
			graphics.drawString(messageObject.Message,5-XOffset+DEFAULT_LIST_CANVAS_INCREMENT,m_StartY+DEFAULT_LIST_CANVAS_HEIGHT);	
		}
		else
		{
			graphics.setColor(Color.white);
			graphics.fillRect(5-XOffset+DEFAULT_LIST_CANVAS_HEIGHT,m_StartY,TotalWidth,DEFAULT_LIST_CANVAS_INCREMENT);
			graphics.setColor(Color.black);		
			graphics.drawString(messageObject.Message,5-XOffset+DEFAULT_LIST_CANVAS_INCREMENT,m_StartY+DEFAULT_LIST_CANVAS_HEIGHT);		
		}				
	}
	
	public boolean handleEvent(Event event)
    {
        if(event.id == 1001 && event.arg == scrollview)
        {
            if(event.modifiers == 1)
                XOffset = event.key;
            else
                YOffset = event.key;            
            repaint();
            return true;
        } 
        else
        {
            return super.handleEvent(event);
        }			
    }
  
    public boolean 	mouseDown(Event event, int i, int j)
	{		
		int CurrentY = j + YOffset;
		int m_listArraySize = ListArray.size();
		boolean SelectedFlag=false;
		chatclient.tappanel.TxtUserCount.setText("");
		for(G_ILoop = 0; G_ILoop <  m_listArraySize; G_ILoop++)
		{
			messageobject = (MessageObject) ListArray.get(G_ILoop);
			if((CurrentY >= messageobject.StartY) && (CurrentY <= (messageobject.StartY+DEFAULT_LIST_CANVAS_HEIGHT)))
			{				
				messageobject.Selected=true;
				SelectedUser = messageobject.Message;
				SelectedFlag = true;
		
			}
			else
			{
				messageobject.Selected=false;												
			}			
		}		
		repaint();	
		if ((!SelectedFlag))
			SelectedUser="";
		
		
		if((event.clickCount == 2) && (CanvasType == USER_CANVAS) && (!(SelectedUser.equals(""))) && (!(SelectedUser.equals(chatclient.UserName))))
		{

		}
		
		return true;
	}
	

	public void paint(Graphics graphics)
	{			
	
		dimension = size();


		if ((offGraphics == null) || (dimension.width != offDimension.width)|| (dimension.height != offDimension.height)) 
		{			
	    	offDimension = dimension;	    		    		    		    	
		}





		PaintFrame(offGraphics);


	}
	
	public void update(Graphics graphics)	
	{
		paint(graphics);
	}
	


}