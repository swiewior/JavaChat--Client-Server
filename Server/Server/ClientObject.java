package server;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientObject {
	Socket ClientSocket;
	String ClientUserName,ClientRoomName;
	Connection conn;
	int userId;
	static int id = 0;
	Statement st;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

	ClientObject(Socket socket, Connection con, String UserName,
		String RoomName) {
		userId = ++id;
		ClientSocket = socket;
		conn = con;
		
		// do usunięcia
		ClientUserName = UserName;
		ClientRoomName = RoomName;
		
		push();
	}

	public void setSocket(Socket socket) {
		ClientSocket =  socket;
	}

	public void setUserName(String UserName) {
		ClientUserName = UserName;
		update();
	}

	public void setRoomName(String RoomName) {
		ClientRoomName = RoomName;
		update();
	}

	public Socket getSocket() {
		return ClientSocket;
	}

	public String getUserName() {
		return pull("username");
	}

	public String getRoomName() {
		return pull("roomname");
	}
	
	public int getUserId() {
		return userId;
	}
	
	private void push() {
		try {
			String sql = "insert into logged (id_logged, username, roomname)"
				+ "values (?, ?, ?)";
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt (1, userId);
			st.setString (2, ClientUserName);
			st.setString (3, ClientRoomName);
			st.execute();
		} catch (SQLException ex) {
			LOG.log(Level.WARNING, null, ex);
		}
	}
	
	private String pull(String what) {
		try {
			String sql = "Select * from logged where id_logged = ?";
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt (1, userId);
			ResultSet rst = st.executeQuery();
			rst.next();
			String username = rst.getString("username");
			String roomname = rst.getString("roomname");
			LOG.log(Level.INFO, "pull: " + username + ", " + roomname);
			
			if("username".equals(what))
				return username;
			else if("roomname".equals(what))
				return roomname;
			
		} catch (SQLException ex) {
			LOG.log(Level.WARNING, null, ex);
		}
		
		return null;
	}
	
	private void update() {
		try {
			String sql = "update logged "
				+ "set username = ?, roomname = ? "
				+ "where id_logged = ?";
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString (1, ClientUserName);
			st.setString (2, ClientRoomName);
			st.setInt (3, userId);
			st.executeUpdate();
		} catch (SQLException ex) {
			LOG.log(Level.WARNING, null, ex);
		}
	}
		
	public void delete() throws SQLException {
		String query = "delete from logged where id_logged = ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.setInt(1, userId);
		st.execute();
		id--;
	}

}