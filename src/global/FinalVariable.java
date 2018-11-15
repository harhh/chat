package global;

public class FinalVariable {

	public static final String SERVERHOST = "localhost";
	public static final int PORT = 5050;
	
	//region PROTOCOL
	//e.g. Send|RoomId|UserId|Message
	public static final String DELIMITER = "#";

	public static final int CREATEUSER = 1;
	public static final int CREATERROOM = 2; 
	public static final int SENDMESSAGE = 3; 
	public static final int GETROOMLIST = 4;
	
	
	public static final int INSTRUCTIONINDEX = 0;
	public static final int ROOMINDEX = 1;
	public static final int USERICINDEX = 2;
	public static final int MESSAGEINDEX = 3;
	public static final int PROTOCOLLENGH=4;
	//endregion
}
