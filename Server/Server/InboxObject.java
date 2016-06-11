package server;

import queue.ArrayQueue;

public class InboxObject {
	private String userName;
	private ArrayQueue<String> messageList;
	
	public InboxObject(String userName)
	{
		this.userName = userName;
		messageList = new ArrayQueue(200);
	}

	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
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
