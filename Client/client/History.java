package client;

import gui.HistoryFrame;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public class History 
{
  
  public String filePath;
  
  public History(String filePath)
	{
    this.filePath = filePath;
  }
  
	//Dodanie elementu do pliku historii
  public void addMessage(String UserName, String TextMessage, String UserRoom, String Time){

    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(filePath);
			
      Node data = doc.getFirstChild();
      
      Element message = doc.createElement("message");
      Element _sender = doc.createElement("sender"); _sender.setTextContent(UserName);
      Element _content = doc.createElement("content"); _content.setTextContent(TextMessage);
      Element _recipient = doc.createElement("recipient"); _recipient.setTextContent(UserRoom);
      Element _time = doc.createElement("time"); _time.setTextContent(Time);
      
      message.appendChild(_sender); 
			message.appendChild(_content); 
			message.appendChild(_recipient); 
			message.appendChild(_time);
      data.appendChild(message);
      
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(filePath));
      transformer.transform(source, result);
 
	  } 
      catch(Exception ex){
		System.out.println("Exceptionmodify xml");
	  }
	}
	
	
  
	// Wypełnianie tablicy z pliku historii na początku programu
  public void FillTable(HistoryFrame frame){
   
    DefaultTableModel model = (DefaultTableModel) frame.jTable1.getModel();
  
    try{
      File fXmlFile = new File(filePath);
			
			if(fXmlFile.exists() && !fXmlFile.isDirectory())
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("message");

				for (int temp = 0; temp < nList.getLength(); temp++) 
				{
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) 
					{
						Element eElement =( Element) nNode;
						model.addRow(new Object[]
						{
							getTagValue("sender", eElement), 
							getTagValue("content", eElement), 
							getTagValue("recipient", eElement), 
							getTagValue("time", eElement)
						});
					}
				}
			}
			else
			{
				//Tworzenie nowego pliku ze strukturą XML
				fXmlFile.createNewFile();
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("data");
				doc.appendChild(rootElement);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(filePath));
				transformer.transform(source, result);
			}
				
    }
    catch(Exception ex){
      System.out.println("Filling Exception");
    }

  }

	public static String getTagValue(String sTag, Element eElement) 
	{
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}
	
	// Czyszczenie pliku
	public void ClearFile()
	{
		try (PrintWriter writer = new PrintWriter(filePath)) {
			writer.print("");
			writer.close();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("data");
			doc.appendChild(rootElement);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
		} catch (FileNotFoundException | ParserConfigurationException ex) {
			Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
		} catch (TransformerConfigurationException ex) {
			Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
		} catch (TransformerException ex) {
			Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}