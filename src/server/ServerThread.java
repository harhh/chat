package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import global.FinalVariable;

public class ServerThread implements Runnable {

	private Socket socket;
	
	private Server server;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	
	boolean isStop = false;
	
	public ServerThread(Socket socket, Server server)
	{
		this.server = server;
		this.socket = socket;
	}
	
	public synchronized void run()
	{
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			String recievedMessage = null;

			while(!isStop)
			{
				if( bufferedReader.ready())
				{
					recievedMessage = bufferedReader.readLine();
					String[] protocol = recievedMessage.split(FinalVariable.DELIMITER);
					doByProtocol(protocol, recievedMessage);
		        }   
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doByProtocol(String[] protocol, String message)
	{
		long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
		long userId = Long.parseLong(protocol[FinalVariable.USERICINDEX]);
		
		try {
			switch (Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX])) {
			case FinalVariable.CREATEUSER:
				System.out.println("CREATEUSER");
				// do
				break;
			case FinalVariable.CREATERROOM:
				System.out.println("CREATERROOM");
				createRoom(roomId, userId);
				break;
			case FinalVariable.SENDMESSAGE:
				System.out.println("SENDMESSAGE");
				broadCastInRoom(roomId, message);
				break;
			case FinalVariable.GETROOMLIST:
				System.out.println("GETROOMLIST");
				// do
				break;
	
			default:
				break;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}
	
	private void createRoom(long roomId, long userId)
	{
		long newRoomId = server.createRoom(roomId, userId, this);
		sendMessage(formatting(FinalVariable.CREATERROOM, "", newRoomId));
	}
	
	private void broadCastInRoom(long roomId, String message)
	{
		server.broadCastingInRoom(roomId, message);
	}
	
	public void sendMessage(String message){
		printWriter.println(message);
	}
	
	public void start()
	{
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws IOException
	{
		isStop = true;
		socket.close();
	}
	
	private String formatting(int instuction, String message, long newRoomId)
	{
		StringBuilder stringbuilder = new StringBuilder();
		String[] protocols = new String[FinalVariable.PROTOCOLLENGH];
		
		switch (instuction) {
		case FinalVariable.CREATERROOM:
			protocols[FinalVariable.ROOMINDEX] = String.valueOf(newRoomId);
			break;
		case FinalVariable.CREATEUSER:
			break;
		case FinalVariable.SENDMESSAGE:
			break;
		case FinalVariable.GETROOMLIST:
			break;
		default:
			break;
		}
		
		protocols[FinalVariable.INSTRUCTIONINDEX] = String.valueOf(instuction);
		protocols[FinalVariable.MESSAGEINDEX] = message;
		
		for(int i=0; i<FinalVariable.PROTOCOLLENGH; i++)
		{
			stringbuilder.append(protocols[i]);
			if(i < FinalVariable.PROTOCOLLENGH -1)
				stringbuilder.append(FinalVariable.DELIMITER);
		}
		
		System.out.println(stringbuilder.toString());
		return stringbuilder.toString(); 
	}
}
