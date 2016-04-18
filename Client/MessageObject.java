public class MessageObject
{

    MessageObject()
    {
        Width   = 0;
        Height  = 0;
        StartX  = 0;
        StartY  = 0;
        Message = null;
        Selected = false;   
        IsIgnored = false;        
    }
    

    String Message;
    int StartX;
    int StartY;
    int Width;
    int Height;
    boolean Selected;
    boolean IsIgnored;
    int MessageType;    
}