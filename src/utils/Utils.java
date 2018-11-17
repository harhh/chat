package utils;

import global.FinalVariable;

public class Utils {
	public static boolean isNumeric(String strNum) {
	    return strNum.matches("-?\\d+(\\.\\d+)?");
	}
	
	public static String formattingProtocol(int instuction, long userId, long prevRoomId, long roomId, int historyPage, String message) {
		StringBuilder stringbuilder = new StringBuilder();
		String[] protocols = new String[FinalVariable.PROTOCOLLENGH];

		protocols[FinalVariable.INSTRUCTIONINDEX] = String.valueOf(instuction);
		protocols[FinalVariable.USERIDINDEX] = String.valueOf(userId);
		protocols[FinalVariable.PREVROOMINDEX] = String.valueOf(prevRoomId);
		protocols[FinalVariable.ROOMINDEX] = String.valueOf(roomId);
		protocols[FinalVariable.ROOMHISTORYPAGEINDEX] = String.valueOf(historyPage);
		protocols[FinalVariable.MESSAGEINDEX] = message;
		
		for(int i=0; i<FinalVariable.PROTOCOLLENGH; i++) {
			stringbuilder.append(protocols[i]);
			if(i < FinalVariable.PROTOCOLLENGH -1)
				stringbuilder.append(FinalVariable.DELIMITER);
		}
		return stringbuilder.toString(); 
	}
	
	public static String formattingUserMessage(int instruction, long userId, String message) {
		String stringUserId = String.valueOf(userId);
		switch (instruction) {
		case FinalVariable.INSERTROOM:
			return stringUserId + "님이 입장하였습니다.";
		case FinalVariable.SENDMESSAGE:
			return stringUserId + " : " + message;
		default:
			break;
		}
		
		return null;
	}
	
	public static String formattingView(String[] protocol) {
		String userId = protocol[FinalVariable.USERIDINDEX];
		String message = protocol[FinalVariable.MESSAGEINDEX];
		
		StringBuilder stringBuilder = new StringBuilder();
		switch (Integer.parseInt(protocol[FinalVariable.INSTRUCTIONINDEX])) {
			case FinalVariable.CREATEUSER:
				break;
			case FinalVariable.LOGINUSER:
				break;
			case FinalVariable.CREATERROOM:
				break;
			case FinalVariable.INSERTROOM:
				break;
			case FinalVariable.SENDMESSAGE:
				stringBuilder.append(userId);
				stringBuilder.append(" : " );
				break;
			case FinalVariable.GETROOMLIST:
				break;
			case FinalVariable.GETROOMHISTORY:
				break;
		}
		stringBuilder.append(message.replaceAll(FinalVariable.LINEDELEMITER, "\n"));
		
		return stringBuilder.toString();
	}
}
