package server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import global.FinalVariable;
import utils.Utils;

public class Server {

	private Socket socket;
	private ServerSocket serverSocket;
	
	private FileManager fileManager;
	
	public LinkedBlockingQueue<String> fileTastQueue = new LinkedBlockingQueue<String>();

	// using CAS
	// if not support in process, JVM implements by spin lock
	// can be db auto increase
	public AtomicLong autoIncreasedRoomId = new AtomicLong(0);
	public AtomicLong autoIncreasedUserId = new AtomicLong(0);

	// Warning. use in thread safe
	public HashMap<Long, HashMap<Long, ServerThread>> serverThreadMaps = null;

	boolean isStop = false;

	public Server(int port) throws IOException {
		this.serverThreadMaps = new HashMap<Long, HashMap<Long, ServerThread>>();
		this.serverSocket = new ServerSocket(port);
	}

	public void start() {
		fileManager = new FileManager(this);
		fileManager.start();
		fileManager.setRoomCount();
		
		while (!isStop) {
			// server ready
			System.out.println("server ready...");
			try {
				socket = serverSocket.accept();

				// accept client
				System.out.println("accept client..." + socket.getInetAddress());
				ServerThread serverThread = new ServerThread(socket, this);
				serverThread.start();

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

		// dispose
//		for(ServerThread serverThread : list)
//		{
//			try {
//				serverThread.stop();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void autoIncreaseRoomId(long roomCount) {
		autoIncreasedRoomId.set(roomCount);
	}
	
	// synchronized
	// can use ReadWriteLock etc..
	public synchronized void broadCastingInRoom(long roomId, String message) {
		if (serverThreadMaps.containsKey(roomId)) {
			HashMap<Long, ServerThread> serverThreadMap = serverThreadMaps.get(roomId);
			serverThreadMap.forEach((k, v) -> {
				v.sendMessage(message);
			});
		}
	}

	public synchronized void unRegisterServerThread(long roomId, long userId) {
		if (serverThreadMaps.containsKey(roomId) && serverThreadMaps.get(roomId).containsKey(userId)) 
			serverThreadMaps.get(roomId).remove(userId);
	}
	
	public synchronized void registerServerThread(long roomId, long userId, ServerThread serverThread) {
		if(!serverThreadMaps.containsKey(roomId)) 
			serverThreadMaps.put(roomId, new HashMap<Long, ServerThread>());

		serverThreadMaps.get(roomId).put(userId, serverThread);
	}
	
	/*
	 * Thread Safe
	 */
	public long createUser() {
		long newUserId = autoIncreasedUserId.incrementAndGet();
		if(newUserId == Long.MAX_VALUE) 
			return FinalVariable.FAILEDCREATEUSER;
		
		return newUserId;
	}
	
	public long createRoom(long prevRoomId, long userId, ServerThread serverThread) {
		long newRoomId = autoIncreasedRoomId.incrementAndGet();
		if(newRoomId == Long.MAX_VALUE) 
			return FinalVariable.FAILEDCREATEROOM;
		
		unRegisterServerThread(prevRoomId, userId);
		registerServerThread(newRoomId, userId, serverThread);
		return newRoomId;
	}
	
	public synchronized long insertRoom(long prevRoomId, long roomId, long userId, ServerThread serverThread, String message) {
		if(roomId <= autoIncreasedRoomId.get()) {
			unRegisterServerThread(prevRoomId, userId);
			registerServerThread(roomId, userId, serverThread);
			return roomId;
		}
		return FinalVariable.FAILEDINSERTROOM;
	}
	
	public synchronized void sendRoomHistory(long userId, long roomId, long seekPointer, String message) {
		if (serverThreadMaps.containsKey(roomId) && serverThreadMaps.get(roomId).containsKey(userId)) {
			ServerThread serverThead = serverThreadMaps.get(roomId).get(userId);
			serverThead.sendMessage(Utils.formattingProtocol(FinalVariable.GETROOMHISTORY, userId, 0, roomId, seekPointer, message));
		}
	}
	
	public synchronized void sendRoomCount(long userId, long roomId, String message) {
		if (serverThreadMaps.containsKey(roomId) && serverThreadMaps.get(roomId).containsKey(userId)) {
			ServerThread serverThead = serverThreadMaps.get(roomId).get(userId);
			serverThead.sendMessage(Utils.formattingProtocol(FinalVariable.GETROOMLIST, userId, roomId, 0, 0, message));
		}
	}
	
	public synchronized long loginUser(ServerThread serverThread) {
		long userId = createUser();
		registerServerThread(0, userId, serverThread);
		return userId;
	}

	public static void main(String[] args) throws IOException {
		new Server(FinalVariable.PORT).start();
	}

}

