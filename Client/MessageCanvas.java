import java.awt.*;
import java.util.*;

public class MessageCanvas extends Canvas implements CommonSettings
{
    //Zmienne globalne
    Dimension offDimension,dimension;
    Image offImage;
    Graphics offGraphics;  	
    Client chatclient;    
    ArrayList MessageArray;
    int G_ILoop,XOffset,YOffset,HorizantalSpace;
    MessageObject messageobject;
    ScrollView scrollview;   
    FontMetrics fontmetrics; 
    int TotalWidth,MessageCount,TotalHeight;
    Font UserNameFont,TextFont;
    StringTokenizer tokenizer;    
    String TokenString;
    
    // Konstruktor
    MessageCanvas(Client Parent)
    {
        chatclient = Parent;		
        // Czekanie na załadowanie czatu
        dimension = chatclient.getSize();		
        MessageArray = new ArrayList();	
        MessageCount  = 0;			
        TotalWidth = 0;
        HorizantalSpace = 2;
        UserNameFont = chatclient.getFont();		
        TextFont = chatclient.TextFont;						
        setFont(chatclient.getFont());		
        fontmetrics = chatclient.getFontMetrics(chatclient.getFont());		
    }

    // Wyczyść wszystko
    protected void ClearAll()
    {
        MessageArray.clear();
        TotalWidth = 0;
        TotalHeight = 0;
        MessageCount = 0;
        scrollview.setValues(TotalWidth,TotalHeight);	
    }


    protected void AddMessageToMessageObject(String Message, int MessageType)
    {	

        String m_Message="";
        tokenizer = new StringTokenizer(Message," ");		
        while(tokenizer.hasMoreTokens())
        {
            TokenString = tokenizer.nextToken();
            if(fontmetrics.stringWidth(m_Message+TokenString) < dimension.width)				
                m_Message = m_Message + TokenString+" ";
            else
            {
                AddMessage(m_Message,MessageType);
                m_Message="";	
            }
        }

        AddMessage(m_Message,MessageType);				
    }

    private void AddMessage(String Message,int MessageType)
    {			
        int m_startY = DEFAULT_MESSAGE_CANVAS_POSITION;	
        if(MessageArray.size() > 0)
        {
                messageobject = (MessageObject) MessageArray.get(MessageArray.size()-1);
                m_startY = 	messageobject.StartY + messageobject.Height;
        }

        messageobject = new MessageObject();
        messageobject.Message = Message;
        messageobject.StartY = m_startY;
        messageobject.MessageType = MessageType;

        messageobject.IsImage = false;
        messageobject.Width = fontmetrics.stringWidth(Message);
        messageobject.Height =  fontmetrics.getHeight()+fontmetrics.getDescent();		

        MessageArray.add(messageobject);
        MessageCount++;
        TotalWidth = Math.max(TotalWidth,messageobject.Width);
        TotalHeight = m_startY+messageobject.Height;
        scrollview.setValues(TotalWidth,TotalHeight);

        int m_Height = TotalHeight - YOffset;
        if(m_Height > dimension.height)
            YOffset = TotalHeight - dimension.height;	
        
        scrollview.setScrollPos(2,2);					
        scrollview.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();		
    }

    private void PaintFrame(Graphics graphics)
    {
        if(MessageCount < 1) return;
        int m_YPos = YOffset + dimension.height;
        int m_StartPos = 0;
        int m_listArraySize = MessageArray.size();		
        for(G_ILoop = 0; G_ILoop < MessageCount && m_StartPos < m_YPos; G_ILoop++)
        {			
            if(m_listArraySize < G_ILoop) return;
            messageobject = (MessageObject) MessageArray.get(G_ILoop);			
            if(messageobject.StartY >= YOffset)
            {				
                PaintMessageIntoCanvas(graphics,messageobject);	
                m_StartPos = messageobject.StartY;
            }
        }

        if(G_ILoop < MessageCount)
        {
            messageobject = (MessageObject) MessageArray.get(G_ILoop);
            PaintMessageIntoCanvas(graphics,messageobject);	
        }
    }

    private void PaintMessageIntoCanvas(Graphics graphics, MessageObject messageObject)
    {				
        graphics.setColor(Color.black);
        int m_YPos = messageobject.StartY - YOffset;
        int m_XPos = 5 - XOffset;
        int CustomWidth = 0;
        String Message = messageobject.Message;
        // Czcionka nazwy użytkownika	
        if(Message.indexOf(":") >= 0)
        {
            graphics.setFont(UserNameFont);		
            chatclient.getGraphics().setFont(UserNameFont);
            fontmetrics = chatclient.getGraphics().getFontMetrics();
            String m_UserName = Message.substring(0,Message.indexOf(":")+1);
            graphics.drawString(m_UserName,m_XPos+CustomWidth,m_YPos);			
            CustomWidth+=fontmetrics.stringWidth(m_UserName)+HorizantalSpace;
            Message = Message.substring(Message.indexOf(":")+1);
        }
        // Czcionka
        chatclient.getGraphics().setFont(TextFont);
        graphics.setFont(TextFont);		
        fontmetrics =  chatclient.getGraphics().getFontMetrics();
		

        switch (messageobject.MessageType)
        {
            case MESSAGE_TYPE_DEFAULT:
            {
                graphics.setColor(Color.black);
                break;	
            }	
            case MESSAGE_TYPE_JOIN:
            {
                graphics.setColor(Color.blue);
                break;	
            }	
            case MESSAGE_TYPE_LEAVE:
            {
                graphics.setColor(Color.red);
                break;	
            }	
            case MESSAGE_TYPE_ADMIN:
            {
                graphics.setColor(Color.gray);
                break;	
            }	
        }			
        
        graphics.drawString(Message,m_XPos+CustomWidth,m_YPos);

        graphics.setFont(UserNameFont);	
        chatclient.getGraphics().setFont(UserNameFont);
        fontmetrics = chatclient.getGraphics().getFontMetrics();				
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
		return true;
	}
	
	public void paint(Graphics graphics)
	{			
            // Buforowanie		
            dimension = size();

            // Tworzenie graficznego konstekstu
            if ((offGraphics == null) || (dimension.width != offDimension.width)|| (dimension.height != offDimension.height)) 
            {						
            offDimension = dimension;
            offImage = createImage(dimension.width, dimension.height);
            offGraphics = offImage.getGraphics();	    		    		    		    	
            }

            // Usuń poprzednie
            offGraphics.setColor(Color.white);
            offGraphics.fillRect(0, 0, dimension.width, dimension.height);	

            // Rysowanie ramki
            PaintFrame(offGraphics);

            // Rysowanie zawartości na ekran
            graphics.drawImage(offImage, 0, 0, null);						
	}

	public void update(Graphics graphics)	
	{
		paint(graphics);
	}
	
 
}