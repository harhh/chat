package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.ArrayBlockingQueue;

import global.FinalVariable;
import utils.Utils;

public class FileManager implements Runnable {
	
	private String filePath = null; 
	private Server server;
	
	private boolean isLoadedRoomCount  = false;
	
	LinkedHashSet<Long> leastRecentlyUsedSet = new LinkedHashSet<Long>();
	HashMap<Long, PrintWriter> printWriterMap = new HashMap<Long, PrintWriter>();
	RandomAccessFile randomAccessManageFile = null;
	
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
			System.out.println("file task start");
			while(true)
			{
				if(!server.fileTastQueue.isEmpty())
				{
					String task = server.fileTastQueue.poll();
					System.out.println("file task" + task);
					doByProtocol(task);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doByProtocol(String task) {
		try {
			String[] protocol = task.split(FinalVariable.DELIMITER); 
			
			int instruction = Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX]);
			long userId = Long.parseLong(protocol[FinalVariable.USERIDINDEX]);
//			long prevRoomId = Long.parseLong(protocol[FinalVariable.PREVROOMINDEX]);
			long roomId = Long.parseLong(protocol[FinalVariable.ROOMINDEX]);
			long roomHistoryPage = Integer.parseInt(protocol[FinalVariable.ROOMHISTORYPAGEINDEX]);
			String userSendedMessage = protocol[FinalVariable.MESSAGEINDEX];
			
			switch (instruction) {
				case FinalVariable.CREATEUSER:
					break;
				case FinalVariable.LOGINUSER:
					break;
				case FinalVariable.CREATERROOM:
					writeRoomCount(roomId);
					break;
				case FinalVariable.INSERTROOM:
					break;
				case FinalVariable.SENDMESSAGE:
					writeSendMessage(roomId, Utils.formattingUserMessage(instruction, userId, userSendedMessage));
					break;
				case FinalVariable.GETROOMLIST:
					break;
				case FinalVariable.GETROOMHISTORY:
					readRoomHistory(userId, roomId, roomHistoryPage);
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

	private void closeStream(long roomId) {
		if(printWriterMap.containsKey(roomId)) {
			printWriterMap.get(roomId).close();
			printWriterMap.remove(roomId);
		}
	}

	private void checkUseFileStream(long roomId) {
		if(leastRecentlyUsedSet.contains(roomId)) {
			leastRecentlyUsedSet.remove(roomId);
		}
		leastRecentlyUsedSet.add(roomId);
	}

	private void insertFileStream(long roomId, PrintWriter printWriter) {
		if(leastRecentlyUsedSet.size() >= FinalVariable.MAXSIZEFILESTREAM) {
			long leastRecentlyUsedRoodId = leastRecentlyUsedSet.iterator().next();
			leastRecentlyUsedSet.remove(leastRecentlyUsedRoodId);
			closeStream(leastRecentlyUsedRoodId);
		}
		
		keepStream(roomId, printWriter);
	}
	
	private void keepStream(long roomId, PrintWriter printWriter) {
		printWriterMap.put(roomId, printWriter);
	}
	
	private void readRoomHistory(long userId, long roomId, long seekLinePoint) {
		BufferedReader bufferedReader = null;
		try {
			if(!leastRecentlyUsedSet.contains(roomId)) {
				File file = new File(getFilePath(roomId));
				if(!file.exists()) {
					System.err.println("create file");
					file.createNewFile();
				}
				PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
				insertFileStream(roomId, printWriter);	
			}
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(getFilePath(roomId)), "utf-8"));
			StringBuilder stringBuilder = new StringBuilder();
			
			long nextSeekLineEndPoint = -1;
			long end = seekLinePoint;
			if(seekLinePoint == 0) {
				seekLinePoint = Long.MAX_VALUE;
				end = seekLinePoint;
			}
			
			int lineCount = 0;
			ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(FinalVariable.LINEPERPAGE);
			while(true) {
				String line = bufferedReader.readLine();
				if(line == null)
					break;
				if(queue.size() >= FinalVariable.LINEPERPAGE)
					queue.poll();
				queue.add(line);
				
				if(++lineCount == end)
					break;
			}
			
			nextSeekLineEndPoint = Math.max(lineCount - FinalVariable.LINEPERPAGE, -1);
			
			for (String string : queue) {
				stringBuilder.append(string);
				stringBuilder.append(FinalVariable.LINEDELEMITER);
			}
			
			bufferedReader.close();
			if(stringBuilder.length() <= 0)
				return;
			
			stringBuilder.setLength(stringBuilder.length() - 1);
			server.sendRoomHistory(userId, roomId, nextSeekLineEndPoint, stringBuilder.toString());
			checkUseFileStream(roomId);		
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void writeSendMessage(long roomId, String message) {
		PrintWriter printWriter = null;
		try {
			if(!leastRecentlyUsedSet.contains(roomId)) {
				File file = new File(getFilePath(roomId));
				if(!file.exists()) {
					file.createNewFile();
				}
				printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
				insertFileStream(roomId, printWriter);
			}
			printWriter = printWriterMap.get(roomId);
			printWriter.append(message + FinalVariable.UTFLINEDELEMITER);
			printWriter.flush();
			checkUseFileStream(roomId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setRoomCount() {
		server.autoIncreaseRoomId(getRoomCount());
	}
	
	private long getRoomCount() {
		long roomCount = 0;
		if(!isLoadedRoomCount) {
			try {
				File manageFile = new File(getManageFilePath());
				if(!manageFile.exists()) {
					manageFile.createNewFile();
					writeRoomCount(0);
				} else {
					roomCount = Long.parseLong(readRoomCount());
				}
			} catch (IOException e) {
			}			
		}
		
		isLoadedRoomCount = true;
		return roomCount;
	}
	
	private String readRoomCount() {
		try {
			if(randomAccessManageFile == null) {
				randomAccessManageFile = new RandomAccessFile(getManageFilePath(), "rw");
			}
			randomAccessManageFile.seek(0);
			return randomAccessManageFile.readUTF();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "0";
	}
	
	private void writeRoomCount(long roomCount) {
		try {
			if(randomAccessManageFile == null) {
				randomAccessManageFile = new RandomAccessFile(getManageFilePath(), "rw");
			}
			randomAccessManageFile.seek(0);
			randomAccessManageFile.writeUTF(String.valueOf(roomCount));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getManageFilePath() {
		return filePath + "/manage.txt";
	}
	
	private String getFilePath(long roomId) {
		return filePath + "/" + String.valueOf(roomId) + ".txt";	
	}
}