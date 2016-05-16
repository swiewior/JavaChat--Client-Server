


import client.Client;
import client.ClientLogger;
import java.io.IOException;
import java.io.RandomAccessFile;

public class main {
		
		public static void main(String args[]) {
			/*RandomAccessFile file = new RandomAccessFile(new File("Historia.dat"), "rw");
			
			file.seek(0);
			file.writeInt(0);
			
			
			
			
			// count = 4B il wpisow
			// adresy wpisow = 4b * count
			// 
			
			
			
			String text = "ąć";
			file.readInt();
			file.seek(100);
			file.writeUTF(text);
			*/
		// Zapis logów
		try {		
			ClientLogger.setup();
		} catch (SecurityException | IOException	e) {	
				e.printStackTrace(System.out);	 
		}	
			
			Client mainFrame = new Client();	

			
		}
}

/* KONWENCJA NAZEWNICTWA
Klasy - z dużej, 
Metody - z małej
Zmienne i obiekty klas - z małej (kolejne człony juz z duzej), 
Pakiety - z małej, 
Interfejsy - tak jak klasy,
Stałe - wszystkie wielkie litery i każdy człon oddzielony przez _
*/