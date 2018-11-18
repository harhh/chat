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
import utils.Utils;

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
					doByProtocol(recievedMessage);
					
					//TODO delete
					System.out.println("client recieved message : " + recievedMessage);
					//do view
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void doByProtocol(String message) {	
		try {
			String[] protocol = message.split(FinalVariable.DELIMITER);
			long userId = Long.parseLong(protocol[FinalVariable.USERIDINDEX]);
			long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
			
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
				sendMessageHandler(Utils.formattingView(protocol));
				break;
			case FinalVariable.GETROOMLIST:
				System.out.println("GETROOMLIST handler");
				getRoomListHandler(Utils.formattingView(protocol));
				break;
			case FinalVariable.GETROOMHISTORY:
				System.out.println("GETROOMHISTORY handler");
				getRoomHistoryHandler(Utils.formattingView(protocol));
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
			ViewControllerSingleton.getInstance().createPopupConfirm("가입에 실패하였습니다.", null, lobby);
		client.setUserId(userId);
		ViewControllerSingleton.getInstance().createPopupConfirm("가입에 성공하였습니다.. (" + userId + ")", null, lobby);
	}
	
	private void loginUserHandler(long userId) {
		if(userId == FinalVariable.FAILEDLOGINUSER) {
			ViewControllerSingleton.getInstance().createPopupConfirm("로그인이 실패하였습니다. 앱이 종료됩니다.", new Delegate() {
				public void doDelegate(Object o) {
					System.exit(0);
				}
			}, lobby);
			return;
		}
		client.setUserId(userId);
		ViewControllerSingleton.getInstance().createPopupConfirm("로그인하였습니다. (" + userId + ")", null, lobby);
		
		
	}
	
	private void createRoomHandler(long roomId) {
		if(roomId == FinalVariable.FAILEDCREATEROOM) {
			ViewControllerSingleton.getInstance().createPopupConfirm("방을 생성하지 못했습니다.", null, lobby);
			return;
		}
		client.setRoomId(roomId);
		ViewControllerSingleton.getInstance().clearTextArea(lobby);
		ViewControllerSingleton.getInstance().createPopupConfirm("방을 생성하였습니다. (" + roomId + ")", null, lobby);
	}
	
	private void insertRoomHandler(long roomId) {
		if(roomId == FinalVariable.FAILEDINSERTROOM) {
			ViewControllerSingleton.getInstance().createPopupConfirm("방이 존재하지 않습니다. (" + roomId + ")", null, lobby);
			return;
		}
		client.setRoomId(roomId);
		client.setHistoryPage(0);
		
		ViewControllerSingleton.getInstance().createPopupConfirm("방에 입장하였습니다..  (" + roomId + ")", null, lobby);
		ViewControllerSingleton.getInstance().clearTextArea(lobby);
		
		getRoomHistory();
	}
	
	private void sendMessageHandler(String message) {
		ViewControllerSingleton.getInstance().appendInTextArea(lobby, message);
	}
	
	public void getRoomListHandler(String message) {
		ViewControllerSingleton.getInstance().refreshRoomList(lobby, message);
	}
	
	public void getRoomHistoryHandler(String message) {
		client.increaseHistoryPage();
		ViewControllerSingleton.getInstance().appendInTextArea(lobby, message);
	}
	
	
	public void login() {
		sendMessage(Utils.formattingProtocol(FinalVariable.LOGINUSER, client.getUserId(), 0, client.getRoomId(), 0, null));
	}
	
	public void createRoom() {
		sendMessage(Utils.formattingProtocol(FinalVariable.CREATERROOM, client.getUserId(), 0, client.getRoomId(), 0, null));
	}
	
	public void insertRoom(long roomId) {
		sendMessage(Utils.formattingProtocol(FinalVariable.INSERTROOM, client.getUserId(), client.getRoomId(), roomId, 0, null));
	}
	
	public void chatMessage(String message) {
		sendMessage(Utils.formattingProtocol(FinalVariable.SENDMESSAGE, client.getUserId(), 0, client.getRoomId(), 0, message));
	}
	
	public void getRoomList() {
		sendMessage(Utils.formattingProtocol(FinalVariable.GETROOMLIST, client.getUserId(), 0, client.getRoomId(), 0, null));
	}
	
	public void getRoomHistory() {
		sendMessage(Utils.formattingProtocol(FinalVariable.GETROOMHISTORY, client.getUserId(), 0, client.getRoomId(), client.getHistoryPage(), null));
	}
	
	
	private void sendMessage(String message) {
		prrintWriter.println(message);
	}
}
