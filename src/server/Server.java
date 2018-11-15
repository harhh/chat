package server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import global.FinalVariable;

public class Server {

	private Socket socket;
	private ServerSocket serverSocket;

	// using CAS
	// if not support in process, JVM implements by spin lock
	// can be db auto increase
	public AtomicLong autoIncreasedRoomId = new AtomicLong(0);
	public AtomicLong autoIncreasedUserId = new AtomicLong(0);

	// Warning. only use in thread safe
	public HashMap<Long, HashMap<Long, ServerThread>> serverThreadMaps = null;

	boolean isStop = false;

	public Server(int port) throws IOException {
		serverThreadMaps = new HashMap<Long, HashMap<Long, ServerThread>>();
		serverSocket = new ServerSocket(port);
	}

	public void start() {
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

	// synchronized
	// can use ReadWriteLock etc..
	public synchronized void broadCastingInRoom(Long roomId, String message) {
		if (serverThreadMaps.containsKey(roomId)) {
			HashMap<Long, ServerThread> serverThreadMap = serverThreadMaps.get(roomId);
			serverThreadMap.forEach((k, v) -> {
				System.out.println(message + roomId + "broad");
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
	public long createRoom(long prevRoomId, long userId, ServerThread serverThread) {
		long newRoomId = autoIncreasedRoomId.getAndIncrement();
		if(newRoomId == Long.MAX_VALUE) 
			return FinalVariable.FAILEDCREATEROOM;
		
		unRegisterServerThread(prevRoomId, userId);
		registerServerThread(newRoomId, userId, serverThread);
		System.out.println("create room : " + newRoomId + "by user id(" + userId + ")");
		return newRoomId;
	}
	
	public synchronized long insertRoom(long prevRoomId, long roomId, long userId, ServerThread serverThread) {
		if(serverThreadMaps.containsKey(roomId)) {
			unRegisterServerThread(prevRoomId, userId);
			registerServerThread(roomId, userId, serverThread);
			return roomId;
		}
		return FinalVariable.FAILEDINSERTROOM;
	}
	
	public long createUser() {
		long newUserId = autoIncreasedUserId.getAndIncrement();
		if(newUserId == Long.MAX_VALUE) 
			return FinalVariable.FAILEDCREATEUSER;
		
		return newUserId;
	}

	public long loginUser() {
		return createUser();
	}
	
	public static void main(String[] args) throws IOException {
		new Server(FinalVariable.PORT).start();
	}

}
