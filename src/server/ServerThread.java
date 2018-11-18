package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import global.FinalVariable;
import utils.Utils;

public class ServerThread implements Runnable {

	private Socket socket;
	private Server server;
	
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	
	boolean isStop = false;
	
	public ServerThread(Socket socket, Server server) {
		this.server = server;
		this.socket = socket;
	}
	
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws IOException {
		isStop = true;
		socket.close();
	}
	
	
	public synchronized void run() {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			String meesage = null;

			while(!isStop) {
				if( bufferedReader.ready()) {
					meesage = bufferedReader.readLine();
					doByProtocol(meesage);
		        }   
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doByProtocol(String message) {	
		try {
			System.out.println(message);
			
			String[] protocol = message.split(FinalVariable.DELIMITER);
			long userId = Long.parseLong(protocol[FinalVariable.USERIDINDEX]);
			long prevRoomId = Long.parseLong(protocol[FinalVariable.PREVROOMINDEX]);
			long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
		
			switch (Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX])) {
			case FinalVariable.CREATEUSER:
				System.out.println("CREATEUSER");
				createUser();
				break;
			case FinalVariable.LOGINUSER:
				System.out.println("LOGINUSER");
				loginUser(this);
				break;
			case FinalVariable.CREATERROOM:
				System.out.println("CREATERROOM");
				createRoom(roomId, userId, message);
				break;
			case FinalVariable.INSERTROOM:
				System.out.println("INSERTROOM");
				insertRoom(prevRoomId, roomId, userId, message);
				break;
			case FinalVariable.SENDMESSAGE:
				System.out.println("SENDMESSAGE");
				broadCastInRoom(roomId, message);
				break;
			case FinalVariable.GETROOMLIST:
				System.out.println("GETROOMLIST");
				getRoomList(message);
				break;
			case FinalVariable.GETROOMHISTORY:
				System.out.println("GETROOMHISTORY");
				getRoomHistory(message);
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
	
	private void createUser() {
		long userId = server.createUser();
		sendMessage(Utils.formattingProtocol(FinalVariable.LOGINUSER, userId, 0, 0, 0, null));
	}
	
	private void loginUser(ServerThread serverThread) {
		long userId = server.loginUser(serverThread);
		sendMessage(Utils.formattingProtocol(FinalVariable.LOGINUSER, userId, 0, 0, 0, null));
	}
	 
	private void createRoom(long roomId, long userId, String message) {
		long newRoomId = server.createRoom(roomId, userId, this);
		String newProtocol = Utils.formattingProtocol(FinalVariable.CREATERROOM, userId, 0, newRoomId, 0, null);
		sendMessage(newProtocol);
		fileTaskRequest(newProtocol);
	}
	
	private void insertRoom(long prevRoomId, long roomId, long userId, String message) {
		long newRoomId = server.insertRoom(prevRoomId, roomId, userId, this, message);
		sendMessage(Utils.formattingProtocol(FinalVariable.INSERTROOM, userId, prevRoomId, newRoomId, 0, null));
	}
	
	private void broadCastInRoom(long roomId, String message) {
		server.writeFile(message, this);
		server.broadCastingInRoom(roomId, message);
	}
	
	private void getRoomList(String message) {
		sendMessage(Utils.formattingProtocol(FinalVariable.GETROOMLIST, 0, 0, 0, 0, String.valueOf(server.autoIncreasedRoomId)));
	}
	
	private void getRoomHistory(String message) {
		fileTaskRequest(message);
	}
	
	public void fileTaskRequest(String message) {
		server.fileTastQueue.add(message);
	}
	
	public void sendMessage(String message) {
		printWriter.println(message);
	}
	
	
}
