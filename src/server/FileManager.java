package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedHashSet;

import global.FinalVariable;
import utils.Utils;

public class FileManager implements Runnable {
	
	private String filePath = null; 
	private Server server;
	
	LinkedHashSet<Long> leastRecentlyUsedSet = new LinkedHashSet<Long>();
	HashMap<Long, RandomAccessFile> randomAccessFileMap = new HashMap<Long, RandomAccessFile>();
	
	public FileManager(Server server) {

		this.server = server;
		this.leastRecentlyUsedSet = new LinkedHashSet<Long>();
		this.filePath = System.getProperty("user.dir");
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws IOException {
		
	}
	
	public synchronized void run() {
		try {
			//road File
			System.out.println("file task start");
			while(true)
			{
				if(!server.fileTastQueue.isEmpty())
				{
					String task = server.fileTastQueue.poll();
					System.out.println("file task" + task);
					String[] protocol = task.split(FinalVariable.DELIMITER); 
					doByProtocol(protocol);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doByProtocol(String[] protocol) {
		try {
			int instruction = Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX]);
			long userId = Long.parseLong(protocol[FinalVariable.USERIDINDEX]);
//			long prevRoomId = Long.parseLong(protocol[FinalVariable.PREVROOMINDEX]);
			long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
			int roomHistoryPage = Integer.parseInt(protocol[FinalVariable.ROOMHISTORYPAGEINDEX]);
			String userSendedMessage = protocol[FinalVariable.MESSAGEINDEX];
			
			switch (instruction) {
			case FinalVariable.CREATEUSER:
				break;
			case FinalVariable.LOGINUSER:
				break;
			case FinalVariable.CREATERROOM:
				break;
			case FinalVariable.INSERTROOM:
				break;
			case FinalVariable.SENDMESSAGE:
				fileWrite(roomId, Utils.formattingUserMessage(instruction, userId, userSendedMessage));
				break;
			case FinalVariable.GETROOMLIST:
				break;
			case FinalVariable.GETROOMHISTORY:
				fileRead(userId, roomId, roomHistoryPage);
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

	private void keepStream(long roomId, RandomAccessFile randomAccessFile) {
		randomAccessFileMap.put(roomId, randomAccessFile);
	}
	
	private void closeStream(long roomId) {
		try {
			if(randomAccessFileMap.containsKey(roomId)) {
				randomAccessFileMap.get(roomId).close();
				randomAccessFileMap.remove(roomId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkUseFileStream(long roomId) {
		if(leastRecentlyUsedSet.contains(roomId)) {
			leastRecentlyUsedSet.remove(roomId);
		}
		leastRecentlyUsedSet.add(roomId);
	}

	private void insertFileStream(long roomId, RandomAccessFile randomAccessFile) {
		if(leastRecentlyUsedSet.size() >= FinalVariable.MAXSIZEFILESTREAM) {
			long leastRecentlyUsedRoodId = leastRecentlyUsedSet.iterator().next();
			leastRecentlyUsedSet.remove(leastRecentlyUsedRoodId);
			closeStream(leastRecentlyUsedRoodId);
		}
		
		checkUseFileStream(roomId);
		keepStream(roomId, randomAccessFile);
	}
	
	private void fileRead(long userId, long roomId, int page) {
		RandomAccessFile randomAccessFile = null;
		try {
			if(!leastRecentlyUsedSet.contains(roomId)) 
			{
				randomAccessFile = new RandomAccessFile(getFilePath(roomId), "rw");
				insertFileStream(roomId, randomAccessFile);	
			}
			
			randomAccessFile = randomAccessFileMap.get(roomId);
			
			long pos = randomAccessFile.length() - FinalVariable.BYTESPERPAGE * (page + 1);
			randomAccessFile.seek(Math.max(pos, 0));
			StringBuilder stringBuilder = new StringBuilder();

			randomAccessFile.readLine();
			while(true) {
				String readLine = randomAccessFile.readLine();
				if(readLine ==null)
					break;
				stringBuilder.append(readLine);
				stringBuilder.append(FinalVariable.LINEDELEMITER);
			}
			if(stringBuilder.length() <= 0)
				return;
			
			stringBuilder.setLength(stringBuilder.length() - 1);
			server.sendRoomHistory(userId, roomId, stringBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void fileWrite(long roomId, String message) {
		RandomAccessFile randomAccessFile = null;
		try {
			if(!leastRecentlyUsedSet.contains(roomId)) {
				randomAccessFile = new RandomAccessFile(getFilePath(roomId), "rw");
				insertFileStream(roomId, randomAccessFile);		
			}
			randomAccessFile = randomAccessFileMap.get(roomId);
			
			//append
			randomAccessFile.seek(randomAccessFile.length());
			randomAccessFile.writeBytes(message + "\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public long getRoomCount()
//	{
//		String filePath = getManageFilePath();
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
//		insertFileStream(roomId, bufferedReader, printWriter);
//	}
	
//	private String getManageFilePath()
//	{
//		return filePath + "/manage.txt";
//	}
//	
	private String getFilePath(long roomId) {
		return filePath + "/" + String.valueOf(roomId) + ".txt";	
	}
}