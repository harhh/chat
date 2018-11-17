package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import controller.ViewControllerSingleton;
import global.FinalVariable;
import ui.Lobby;
import utils.Delegate;

public class ClientThread extends Thread{

	private Client client;
	private Lobby lobby;
	
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter prrintWriter;
	
	public ClientThread(Client client, Lobby lobby) {
		this.client = client;
		this.lobby = lobby;
	}
	
	public void run() {
		try {
			socket = client.getSocket();
			bufferedReader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
			prrintWriter= new PrintWriter(client.getSocket().getOutputStream(), true);
			
			String recievedMessage = null;
			boolean isStop = false;
			while(!isStop) {
				if(bufferedReader.ready()) {
					recievedMessage = bufferedReader.readLine();
					String[] protocol = recievedMessage.split(FinalVariable.DELIMITER);
					doByProtocol(protocol, recievedMessage);
					
					//TODO delete
					System.out.println("client recieved message : " + recievedMessage);
					//do view
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private void doByProtocol(String[] protocol, String message) {
		long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
		long userId = Long.parseLong(protocol[FinalVariable.USERIDINDEX]);
		
		try {
			switch (Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX])) {
			case FinalVariable.CREATEUSER:
				System.out.println("CREATEUSER handler");
				createUserHandler(userId);
				break;
			case FinalVariable.LOGINUSER:
				System.out.println("LOGINUSER handler");
				loginUserHandler(userId);
				break;
			case FinalVariable.CREATERROOM:
				System.out.println("CREATERROOM handler");
				createRoomHandler(roomId);
				break;
			case FinalVariable.INSERTROOM:
				System.out.println("INSERTROOM handler");
				insertRoomHandler(roomId);
				break;
			case FinalVariable.SENDMESSAGE:
				System.out.println("SENDMESSAGE handler");
				sendMessageHandler(message);
				break;
			case FinalVariable.GETROOMLIST:
				System.out.println("GETROOMLIST handler");
				break;
	
			default:
				break;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	private void createUserHandler(long userId) {
		if(userId == FinalVariable.FAILEDLOGINUSER) 
			ViewControllerSingleton.getInstance().createPopupConfirm("가입에 실패하였습니다.", null);
		client.setUserId(userId);
		ViewControllerSingleton.getInstance().createPopupConfirm("가입에 성공하였습니다.. (" + userId + ")", null);
	}
	
	private void loginUserHandler(long userId) {
		if(userId == FinalVariable.FAILEDLOGINUSER) {
			ViewControllerSingleton.getInstance().createPopupConfirm("로그인이 실패하였습니다. 앱이 종료됩니다.", new Delegate() {
				public void doDelegate(Object o) {
					System.exit(0);
				}
			});
			return;
		}
		client.setUserId(userId);
		ViewControllerSingleton.getInstance().createPopupConfirm("로그인하였습니다. (" + userId + ")", null);
	}
	
	private void createRoomHandler(long roomId) {
		if(roomId == FinalVariable.FAILEDCREATEROOM) {
			ViewControllerSingleton.getInstance().createPopupConfirm("방을 생성하지 못했습니다.", null);
			return;
		}
		client.setRoomId(roomId);
		ViewControllerSingleton.getInstance().createPopupConfirm("방을 생성하였습니다. (" + roomId + ")", null);
	}
	
	private void insertRoomHandler(long roomId) {
		if(roomId == FinalVariable.FAILEDINSERTROOM) {
			ViewControllerSingleton.getInstance().createPopupConfirm("방이 존재하지 않습니다. (" + roomId + ")", null);
			return;
		}
		client.setRoomId(roomId);
		ViewControllerSingleton.getInstance().createPopupConfirm("방에 입장하였습니다..  (" + roomId + ")", null);
	}
	
	private void sendMessageHandler(String message) {
		ViewControllerSingleton.getInstance().appendInTextArea(lobby.GetTextArea(), message);
	}
	
	public void login() {
		sendMessage(formatting(FinalVariable.LOGINUSER, 0, 0, ""));
	}
	
	public void createRoom() {
		sendMessage(formatting(FinalVariable.CREATERROOM, 0, 0, ""));
	}
	
	public void insertRoom(long roomId) {
		sendMessage(formatting(FinalVariable.INSERTROOM, 0, roomId, ""));
	}
	
	public void chatMessage(String message) {
		sendMessage(formatting(FinalVariable.SENDMESSAGE, 0, 0, message));
	}
	
	private void sendMessage(String message) {
		prrintWriter.println(message);
	}
	
	private String formatting(int instuction, long prevRoomId, long roomId, String message) {
		StringBuilder stringbuilder = new StringBuilder();
		String[] protocols = new String[FinalVariable.PROTOCOLLENGH];

		protocols[FinalVariable.USERIDINDEX] = String.valueOf(client.getUserId());
		protocols[FinalVariable.INSTRUCTIONINDEX] = String.valueOf(instuction);
		protocols[FinalVariable.PREVROOMINDEX] = String.valueOf(client.getRoomId());
		protocols[FinalVariable.ROOMINDEX] = String.valueOf(client.getRoomId());
		protocols[FinalVariable.MESSAGEINDEX] = message;
		
		switch (instuction) {
		case FinalVariable.CREATEUSER:
			break;
		case FinalVariable.LOGINUSER:
			break;
		case FinalVariable.CREATERROOM:
			break;
		case FinalVariable.INSERTROOM:
			protocols[FinalVariable.ROOMINDEX] = String.valueOf(roomId);
			break;
		case FinalVariable.SENDMESSAGE:
			break;
		case FinalVariable.GETROOMLIST:
			break;
		default:
			break;
		}
		
		for(int i=0; i<FinalVariable.PROTOCOLLENGH; i++) {
			stringbuilder.append(protocols[i]);
			if(i < FinalVariable.PROTOCOLLENGH -1)
				stringbuilder.append(FinalVariable.DELIMITER);
		}
		return stringbuilder.toString(); 
	}
}
