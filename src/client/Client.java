package client;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import global.FinalVariable;
public class Client {
	
	private static String ip = FinalVariable.SERVERHOST;
	
	private long id = 0;
	private long roomId = 0;
	private String userName = "";
	private Socket socket = null;
	
	public Client() throws UnknownHostException, IOException
	{
		socket = new Socket(ip, FinalVariable.PORT);
	}
	
	public long getId()
	{
		return id;
	}

	public long getRoomId()
	{
		return roomId;
	}
	
	public void setRoomId(long roomId)
	{
		this.roomId = roomId;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	

}
