package filetransfer;

import client.CommonSettings;
import gui.MessageCanvas;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

public class Download implements Runnable, CommonSettings {
	
	public ServerSocket server;
	public Socket socket;
	public int port, fileSize, serverPort;
	public String saveTo = "";
	public InputStream fileInput;
	public FileOutputStream fileOutput;
	public MessageCanvas messageCanvas;
	public client.Client parent;
	ProgressMonitorInputStream pin;
	ProgressMonitor pm;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	
	public Download(String saveTo, MessageCanvas mc, int sp, int fs, client.Client parent){
		
		try {
			server = new ServerSocket(0);
			port = server.getLocalPort();
			this.saveTo = saveTo;
			messageCanvas = mc;
			this.parent = parent;
			this.fileSize = fs;
			this.serverPort = sp;
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "Download::Download: ", e);
		}
	}

	@Override
	public void run() {
		try {
			socket = server.accept();
			LOG.log(Level.INFO, "Download : "+socket.getRemoteSocketAddress());
			
			fileInput = socket.getInputStream();
			pin = new ProgressMonitorInputStream(null, 
				"Pobieranie " + saveTo, fileInput);
			pm = pin.getProgressMonitor();
			pm.setMaximum(fileSize);
			
			fileOutput = new FileOutputStream(saveTo);
			
			byte[] buffer = new byte[1024*1024];
			int count, completed = 0;
			double progress;
			
			while((count = pin.read(buffer)) >= 0) {
				fileOutput.write(buffer, 0, count);
				completed += count;
				progress = (double)completed / (double)fileSize;
				pm.setNote("Ukończono " + new DecimalFormat("###.##%").format(progress));
				Thread.sleep(50);
			}
			
			messageCanvas.AddMessageToMessageObject("Ukończono pobieranie pliku", 
				MESSAGE_TYPE_ADMIN);
			
			end();
		} 
		catch (InterruptedIOException e) {
			LOG.log(Level.INFO, "Cancelled", e);
			messageCanvas.AddMessageToMessageObject("Przerwano pobieranie pliku",
				MESSAGE_TYPE_LEAVE);
			end();
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "IOException", e);
		}
		catch (InterruptedException e) {
			LOG.log(Level.WARNING, "Other Exception", e);
		}
	}
	
	public void end() {
		try{
			if(fileOutput != null){ fileOutput.flush(); }
			if(pin != null){ pin.close(); }
			if(fileOutput != null){ fileOutput.close(); }
			if(fileInput != null){ fileInput.close(); }
			if(socket != null){ socket.close(); }
		} catch(IOException e) {
			LOG.log(Level.WARNING, "", e);
		}
	}
}