package client;

import gui.MessageCanvas;
import gui.ScrollView;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;

public class PrivateChat extends Frame implements CommonSettings,KeyListener,ActionListener
{
// Deklaracja zmiennych
  Client client;
  protected String UserName;
  MessageCanvas messagecanvas;
  ScrollView MessageScrollView;
  TextField TxtMessage;
  JButton CmdSend,CmdClose,CmdIgnore,CmdClear;
  
  // Konstruktor
  PrivateChat(Client Parent, String ToUserName)
  {
    client = Parent;
    UserName = ToUserName;
    setTitle("Prywatna rozmowa z "+UserName); 	
    setBackground(Color.white);
    setFont(client.getFont());
    InitializeComponents();
    
    // Zamykanie
    addWindowListener(new WindowAdapter(){
    public void windowClosing(WindowEvent evt) { ExitPrivateWindow(); }});	
  }	

  // Inicjalizacja komponentów
  private void InitializeComponents()
  {
    setLayout(null);
    Label LblConversation = new Label("Prywatna rozmowa z "+UserName);
    LblConversation.setForeground(Color.black);
    LblConversation.setBounds(5, 30, 400, 20);
    add(LblConversation);

    Panel MessagePanel = new Panel(new BorderLayout());
    messagecanvas = new MessageCanvas(client);				
    MessageScrollView = new ScrollView(messagecanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
    messagecanvas.scrollview = MessageScrollView;	
    MessagePanel.add("Center",MessageScrollView);
    MessagePanel.setBounds(5, 50, 400, 200);
    add(MessagePanel);

    TxtMessage = new TextField();
    TxtMessage.addKeyListener(this);
    TxtMessage.setFont(client.TextFont);
    TxtMessage.setBounds(5, 260, 320, 20);
    add(TxtMessage);

    CmdSend = new JButton("Wyślij");
    CmdSend.addActionListener(this);
    CmdSend.setBounds(335, 260, 70, 20);
    add(CmdSend);

    CmdClear = new JButton("Wyczyść");
    CmdClear.addActionListener(this);
    CmdClear.setBounds(5, 290, 100, 20);

    CmdIgnore = new JButton("Ignoruj");
    CmdIgnore.addActionListener(this);
    CmdIgnore.setBounds(105, 290, 100, 20);

    CmdClose = new JButton("Zamknij");
    CmdClose.addActionListener(this);
    CmdClose.setBounds(205, 290, 80, 20);

    add(CmdClear);
    add(CmdIgnore);
    add(CmdClose);

    setSize(PRIVATE_WINDOW_WIDTH,PRIVATE_WINDOW_HEIGHT);
    setResizable(false);
    show();
    this.requestFocus();
  }

  //Obsługa przycisków
  public void actionPerformed(ActionEvent evt)
  {
    if(evt.getSource().equals(CmdSend))
    {
      // Wyślij wiadomość
      if (!(TxtMessage.getText().trim().equals("")))
        SendMessage();
    }

    // Zamknij
    if(evt.getSource().equals(CmdClose))
    {
      ExitPrivateWindow();	
    }

    // Wyczyść
    if(evt.getSource().equals(CmdClear))
    {
      messagecanvas.ClearAll();
    }

    // Ignorowanie
    if(evt.getSource().equals(CmdIgnore))
    {			
      if(evt.getActionCommand().equals("Ignoruj"))
      {
        client.tappanel.UserCanvas.IgnoreUser(true,UserName);
        messagecanvas.AddMessageToMessageObject(UserName +" jest ignorowany",MESSAGE_TYPE_ADMIN);
        CmdIgnore.setLabel("Odblokuj");				
      }
      else
      {
        messagecanvas.AddMessageToMessageObject(UserName +" przestał być ignorowany",MESSAGE_TYPE_ADMIN);
        client.tappanel.UserCanvas.IgnoreUser(false,UserName);
        CmdIgnore.setLabel("Ignoruj");					
      }
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

  public void keyTyped(KeyEvent e){}
  public void keyReleased(KeyEvent e){}

  private void SendMessage()
  {
      messagecanvas.AddMessageToMessageObject(client.UserName+": "+TxtMessage.getText(),MESSAGE_TYPE_DEFAULT);
      client.SentPrivateMessageToServer(TxtMessage.getText(),UserName);			
      TxtMessage.setText("");
      TxtMessage.requestFocus();
						
  }

  // Dodaj wiadomość do panelu
  protected void AddMessageToMessageCanvas(String Message)
  {		
      messagecanvas.AddMessageToMessageObject(Message,MESSAGE_TYPE_DEFAULT);			
  }

  protected void DisableAll()
  {
      TxtMessage.setEnabled(false);
      CmdSend.setEnabled(false);	
  }

  protected void EnableAll()
  {
      TxtMessage.setEnabled(true);
      CmdSend.setEnabled(true);	
  }

  // Wyjście z prywatnego czatu
  private void ExitPrivateWindow() {
    client.RemovePrivateWindow(UserName);    
    setVisible(false);    
  }

}