
package spam;

import javax.swing.table.DefaultTableModel;
import server.*;
import static server.CommonSettings.KICK_USER;

public class Ignore {
	public SpamPanel frame;

	public void Ignore (Server Parent, ClientObject IgnoreUserObject, boolean IsIgnore) {
		frame = Parent.spamPanel;
		if(IsIgnore)
			IgnoreUserObject.setUserIgnored(IgnoreUserObject.getUserIgnored() + 1);
		else
			IgnoreUserObject.setUserIgnored(IgnoreUserObject.getUserIgnored() - 1);
		
		UpdateIgnore(Parent, IgnoreUserObject);
	}
	
	public void UpdateIgnore (Server parent, ClientObject IgnoreUserObject) {
		frame = parent.spamPanel;
		DefaultTableModel table = (DefaultTableModel) frame.ignoredTable.getModel();
		int ignores = IgnoreUserObject.getUserIgnored();
		String name = IgnoreUserObject.getUserName();
		String ip = IgnoreUserObject.getSocket().getInetAddress().getHostAddress().toString();
		
		for (int i = 0; i < table.getRowCount(); i++) {
			
			if (table.getValueAt(i, 0).equals(name)) {
				table.setValueAt(ignores, i, 2);
				table.fireTableRowsUpdated(0, table.getRowCount()-1);
				return;
			}
		}
		
		table.addRow(new Object[]{name, ip, ignores});	
	}
	
	public void RemoveSelected(Server parent) {
		frame = parent.spamPanel;
		
		int selectedRow = frame.ignoredTable.getSelectedRow();
		DefaultTableModel table = (DefaultTableModel) frame.ignoredTable.getModel();
		String userName = table.getValueAt(selectedRow, 0).toString();
		ClientObject client = parent.GetClientObject(userName);
		parent.SendMessageToClient(client.getSocket(),"KICK ");
		parent.RemoveUser(userName, client.getRoomName(), KICK_USER);
	}
	
	public void RemoveDisconnected(Server parent, ClientObject removeclientobject) {
		frame = parent.spamPanel;
		DefaultTableModel table = (DefaultTableModel) frame.ignoredTable.getModel();
		String userName = removeclientobject.getUserName().toString();
		
		for (int i = 0; i < table.getRowCount(); i++) {
			
			if (table.getValueAt(i, 0).equals(userName)) {
				table.removeRow(i);
				return;
			}
		}
		
	}
}
