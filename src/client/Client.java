package client;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import global.FinalVariable;

public class Client {
	private static String ip = FinalVariable.SERVERHOST;
	
	private long userId = 0;
	private long roomId= 0;
	
	private long historySeekPointer = 0;
	
	private Socket socket = null;
	
	public Client() throws UnknownHostException, IOException {
		socket = new Socket(ip, FinalVariable.PORT);
	}
	
	public long getUserId() {
		return userId;
	}

	public long getRoomId() {
		return roomId;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public long getHistorySeekPointer() {
		return this.historySeekPointer;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}
	
	public void setHistorySeekPointer(long historySeekPointer) {
		this.historySeekPointer = historySeekPointer;
	}
}
