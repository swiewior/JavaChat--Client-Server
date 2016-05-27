package server;

import queue.ArrayQueue;

public class RoomObject {
	private String roomName;
	private ArrayQueue<String> messageList;
	
	RoomObject(String roomName)
	{
		this.roomName = roomName;
		messageList = new ArrayQueue(200);
	}

	/**
	 * @return the roomName
	 */
	public String getRoomName()
	{
		return roomName;
	}

	/**
	 * @param roomName the roomName to set
	 */
	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}

	/**
	 * @return the messageList
	 */
	public ArrayQueue<String> getMessageList()
	{
		return messageList;
	}

	/**
	 * @param messageList the messageList to set
	 */
	public void setMessageList(ArrayQueue<String> messageList)
	{
		this.messageList = messageList;
	}
}
