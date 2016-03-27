import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
public class MessageBox extends Dialog implements ActionListener,CommonSettings 
{  
	MessageBox(Client Parent,boolean okcan)
	{
	   super(Parent, "Information", false);
	   chatclient = Parent;
	   setBackground(chatclient.ColorMap[0]);
	   setLayout(new BorderLayout());	 
	   setFont(chatclient.getFont());  
	   messagecanvas = new MessageCanvas(chatclient);
	   MessageScrollView = new ScrollView(messagecanvas,true,true,200,100,0);
	   messagecanvas.scrollview = MessageScrollView;
	   messagecanvas.setBackground(chatclient.ColorMap[0]);	   	
	   add("Center",MessageScrollView);	   	   
	   addOKCancelPanel(okcan);
	   createFrame();	  
	   pack();
	   setVisible(true);
	   setSize(200,160);
	   setResizable(false);
	}
	
	protected void AddMessage(String message)
	{	
		messagecanvas.AddMessageToMessageObject(message,MESSAGE_TYPE_JOIN);		
	}
	
   	private void addOKCancelPanel( boolean okcan ) 
  	{
	  Panel panel = new Panel();
   	  panel.setLayout(new FlowLayout());
   	  createOKButton( panel);
   	  if (okcan == true)
      	createCancelButton( panel );
   	  add("South",panel);
   	}

  private void createOKButton(Panel panel) 
  {
      
   	CmdOk = new JButton("OK");   	
   	panel.add(CmdOk);
   	CmdOk.addActionListener(this); 
  }

  private void createCancelButton(Panel panel) 
  {
   	CmdCancel = new JButton("Cancel");
   	panel.add(CmdCancel);
   	CmdCancel.addActionListener(this);
  }

  private void createFrame() 
  {
   	Dimension dimension = getToolkit().getScreenSize();
   	setLocation(dimension.width/3,dimension.height/3);
  }

  public void actionPerformed(ActionEvent ae)
  {
   if(ae.getSource() == CmdOk) 
   {     
     dispose();
   }
   else if(ae.getSource() == CmdCancel) 
   {
     dispose();
   }
  }

   Client chatclient;
   JButton CmdOk, CmdCancel;
   MessageCanvas messagecanvas;
   ScrollView MessageScrollView;
 }
