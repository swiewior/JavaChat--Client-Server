package gui;

import client.Client;
import client.CommonSettings;
import gui.MessageCanvas;
import gui.ScrollView;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JPanel;

public class MessageBox extends JDialog implements ActionListener,CommonSettings 
{  
	// Zmienne globalne
	Client chatclient;
	JButton CmdOk, CmdCancel;
	MessageCanvas messagecanvas;
	ScrollView MessageScrollView;
	 
	public MessageBox(Client Parent,boolean okcan)
	{
		super(Parent, "Informacje", false);
		chatclient = Parent;
		setBackground(Color.white);
		setLayout(new BorderLayout());	 
		setFont(chatclient.getFont());  
		messagecanvas = new MessageCanvas(chatclient);
		MessageScrollView = new ScrollView(messagecanvas,true,true,200,100,0);
		messagecanvas.scrollview = MessageScrollView;
		messagecanvas.setBackground(Color.white);	   	
		add("Center",MessageScrollView);	   	   
		addOKCancelPanel(okcan);
		createFrame();	  
		pack();
		setVisible(true);
		setSize(200,160);
		setResizable(false);
	}

	public void AddMessage(String message)
	{	
		messagecanvas.AddMessageToMessageObject(message,MESSAGE_TYPE_JOIN);	
	}

	private void addOKCancelPanel( boolean okcan ) 
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		createOKButton( panel);
		if (okcan == true)
		createCancelButton( panel );
		add("South",panel);
	}

	private void createOKButton(JPanel panel) 
	{
		CmdOk = new JButton("OK");   	
		panel.add(CmdOk);
		CmdOk.addActionListener(this); 
	}

	private void createCancelButton(JPanel panel) 
	{
		CmdCancel = new JButton("Anuluj");
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
			dispose();
		else if(ae.getSource() == CmdCancel) 
			dispose();
	}

 }
