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

public class Upload implements Runnable, CommonSettings{

	public String addr;
	public int port;
	public Socket socket;
	public FileInputStream fileInput;
	public OutputStream Out;
	public File file;
	MessageCanvas messageCanvas;
	ProgressMonitorInputStream pin;
	ProgressMonitor pm;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

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
			byte[] buffer = new byte[1024*1024];
			int count, completed = 0;
			double progress;
			
			pin = new ProgressMonitorInputStream(null, 
				"Wysyłanie " + file.getName(), fileInput);
			pm = pin.getProgressMonitor();
			pm.setMaximum((int) file.length());

			while((count = pin.read(buffer)) >= 0){
				Out.write(buffer, 0, count);
				completed += count;
				progress = (double)completed / (double)(int) file.length();
				pm.setNote("Ukończono " + new DecimalFormat("###.##%").format(progress));
			}

			messageCanvas.AddMessageToMessageObject("Ukończono wysyłanie pliku", MESSAGE_TYPE_ADMIN);

			end();
		} catch (InterruptedIOException e) {
			LOG.log(Level.INFO, "Cancelled", e);
			messageCanvas.AddMessageToMessageObject("Przerwano pobieranie pliku",
				MESSAGE_TYPE_LEAVE);
			end();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Upload::run: ", e);
		}
	}
	
	public void end() {
		try{
			if(Out != null){ Out.flush(); }
			if(pin != null){ pin.close(); }
			if(fileInput != null){ fileInput.close(); }
			if(Out != null){ Out.close(); }
			if(socket != null){ socket.close(); }
		} catch(IOException e) {
			LOG.log(Level.WARNING, "", e);
		}
	}
}