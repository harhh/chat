package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import controller.ViewControllerSingleton;
import global.FinalVariable;
import ui.Lobby;

public class ClientThread extends Thread{

	private Client client;
	private Lobby lobby;
	
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter prrintWriter;
	
	public ClientThread(Client client, Lobby lobby)
	{
		this.client = client;
		this.lobby = lobby;
	}
	
	public void run()
	{
		try {
			socket = client.getSocket();
			bufferedReader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
			prrintWriter= new PrintWriter(client.getSocket().getOutputStream(), true);
			
			String recievedMessage = null;
			boolean isStop = false;
			while(!isStop)
			{
				if(bufferedReader.ready())
				{
					recievedMessage = bufferedReader.readLine();
					String[] protocol = recievedMessage.split(FinalVariable.DELIMITER);
					doByProtocol(protocol, recievedMessage);
					
					//TODO delete
					System.out.println(recievedMessage);
					//do view
				}
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private void doByProtocol(String[] protocol, String message)
	{
		long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
//		long userId = Long.parseLong(protocol[FinalVariable.USERICINDEX]);
		
		try {
			switch (Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX])) {
			case FinalVariable.CREATEUSER:
				System.out.println("CREATEUSER");
				
				break;
			case FinalVariable.CREATERROOM:
				//TODO method
				client.setRoomId(roomId);
				break;
			case FinalVariable.SENDMESSAGE:
				//TODO method
				ViewControllerSingleton.getInstance().AppendInTextArea(lobby.GetTextArea(), message);
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
	
	public void createRoom()
	{
		sendMessage(formatting(FinalVariable.CREATERROOM, ""));
	}
	
	public void chatMessage(String message)
	{
		sendMessage(formatting(FinalVariable.SENDMESSAGE, message));
	}
	
	private void sendMessage(String message){
		prrintWriter.println(message);
	}
	
	private String formatting(int instuction, String message)
	{
		StringBuilder stringbuilder = new StringBuilder();
		String[] protocols = new String[FinalVariable.PROTOCOLLENGH];
		
		switch (instuction) {
		case FinalVariable.CREATERROOM:
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
		protocols[FinalVariable.ROOMINDEX] = String.valueOf(client.getRoomId());
		protocols[FinalVariable.USERICINDEX] = String.valueOf(client.getId());
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
