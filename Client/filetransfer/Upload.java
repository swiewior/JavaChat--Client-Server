package filetransfer;

import client.CommonSettings;
import gui.MessageCanvas;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Upload implements Runnable, CommonSettings{

	public String addr;
	public int port;
	public Socket socket;
	public FileInputStream fileInput;
	public OutputStream Out;
	public File file;
	MessageCanvas messageCanvas;
	//private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	private static final Logger LOG = Logger.getLogger( Upload.class.getName() );

	public Upload(String addr, int port, File filepath, MessageCanvas mc){
			super();		

			try {
				file = filepath;
				messageCanvas = mc;
				socket = new Socket(InetAddress.getByName(addr), port);
				Out = socket.getOutputStream();
				fileInput = new FileInputStream(filepath);
			} 
			catch (Exception e) {
				LOG.log(Level.SEVERE, "Upload::Upload: ", e);
			}
		}
		
	@Override
	public void run()
	{
		try {       
			byte[] buffer = new byte[1024];
			int count;

			while((count = fileInput.read(buffer)) >= 0){
					Out.write(buffer, 0, count);
			}
			Out.flush();

			//ui.jTextArea1.append("[Applcation > Me] : File upload complete\n");
			messageCanvas.AddMessageToMessageObject("Ukończono wysyłanie pliku", MESSAGE_TYPE_ADMIN);
			//ui.jButton5.setEnabled(true); ui.jButton6.setEnabled(true);
			//ui.jTextField5.setVisible(true);

			if(fileInput != null){ fileInput.close(); }
			if(Out != null){ Out.close(); }
			if(socket != null){ socket.close(); }
		}
		catch (Exception e) {
			LOG.log(Level.SEVERE, "Upload::run: ", e);
		}
	}
}