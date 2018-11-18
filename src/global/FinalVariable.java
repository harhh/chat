package global;

public class FinalVariable {

	public static final String SERVERHOST = "localhost";
	public static final int PORT = 5050;
	
	// region PROTOCOL chat
	// INSTRUCTION|USERID|ROOM|PREVROOM|MESSAGE
	public static final String DELIMITER = "#";

	public static final long FAILEDCREATEUSER = -1;
	public static final long FAILEDLOGINUSER = -1;
	public static final long FAILEDCREATEROOM = -1;
	public static final long FAILEDINSERTROOM = -1;
	
	public static final int CREATEUSER = 1;
	public static final int LOGINUSER = 2;
	public static final int CREATERROOM = 3;
	public static final int INSERTROOM = 4;
	public static final int SENDMESSAGE = 5; 
	public static final int GETROOMLIST = 6;
	public static final int GETROOMHISTORY = 7;
	
	public static final int INSTRUCTIONINDEX = 0;
	public static final int USERIDINDEX = 1;
	public static final int ROOMINDEX = 2;
	public static final int PREVROOMINDEX = 3;
	public static final int ROOMHISTORYPAGEINDEX = 4;
	public static final int MESSAGEINDEX = 5;
	public static final int PROTOCOLLENGH = 6;
	
	public static final String LINEDELEMITER = "-";
	public static final String UTFLINEDELEMITER = System.getProperty("line.separator");
	// server side variables
	public static final int MAXSIZEFILESTREAM = 100;	
	public static final int LINEPERPAGE = 20;
}
