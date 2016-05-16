package filetransfer;

import client.CommonSettings;
import gui.MessageCanvas;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Download implements Runnable, CommonSettings {
	
	public ServerSocket server;
	public Socket socket;
	public int port;
	public String saveTo = "";
	public InputStream fileInput;
	public FileOutputStream fileOutput;
	public MessageCanvas messageCanvas;
	//private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME ); 
	private static final Logger LOG = Logger.getLogger( Download.class.getName() );
	
	public Download(String saveTo, MessageCanvas mc){
		
		try {
			server = new ServerSocket(0);
			port = server.getLocalPort();
			this.saveTo = saveTo;
			messageCanvas = mc;
		}
		catch (IOException e) {
					LOG.log(Level.SEVERE, "Download::Download: ", e);
		}
	}

	@Override
	public void run() {
		try {
			socket = server.accept();
			System.out.println("Download : "+socket.getRemoteSocketAddress());
			
			fileInput = socket.getInputStream();
			fileOutput = new FileOutputStream(saveTo);
			
			byte[] buffer = new byte[1024];
			int count;
			
			while((count = fileInput.read(buffer)) >= 0){
				fileOutput.write(buffer, 0, count);
			}
			
			fileOutput.flush();
			
						messageCanvas.AddMessageToMessageObject("Ukończono pobieranie pliku", MESSAGE_TYPE_ADMIN);
			
			if(fileOutput != null){ fileOutput.close(); }
			if(fileInput != null){ fileInput.close(); }
			if(socket != null){ socket.close(); }
		} 
		catch (Exception e) {
					LOG.log(Level.SEVERE, "Download::run: ", e);
		}
	}
}