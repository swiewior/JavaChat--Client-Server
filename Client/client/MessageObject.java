package client;

public class MessageObject
{
	public MessageObject()
	{
		Width   = 0;
		Height  = 0;
		StartX  = 0;
		StartY  = 0;
		Message = null;
		Selected = false;   
		IsIgnored = false;        
	}
	

	public String Message;
	public int StartX;
	public int StartY;
	public int Width;
	public int Height;
	public boolean Selected;
	public boolean IsIgnored;
	public int MessageType;    
}