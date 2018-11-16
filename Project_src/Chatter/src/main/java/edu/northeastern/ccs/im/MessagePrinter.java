package edu.northeastern.ccs.im;

public class MessagePrinter {

	public static void printMessage(String msg) {
		String messageArray[] = msg.split("||");
		if (messageArray[0].equals("PIC")) {
			handlePrivateIncoming(msg);
		} else if (messageArray[0].equals("POU")) {
			handlePrivateOutgoing(msg);
		} else if (messageArray[0].equals("GOU")) {
			handleGroupOutgoing(msg);
		} else if (messageArray[0].equals("GIC")) {
			handleGroupIncoming(msg);
		} else if (messageArray[0].equals("BIC")) {
			handleBroadCastIncoming(msg);
		} else if (messageArray[0].equals("BOU")) {
			handleBroadCastOutgoing(msg);
		}
	}

	public static void handlePrivateIncoming(String msg) {

	}

	public static void handleGroupIncoming(String msg) {

	}

	public static void handleBroadCastIncoming(String msg) {

	}

	public static void handlePrivateOutgoing(String msg) {

	}

	public static void handleGroupOutgoing(String msg) {

	}

	public static void handleBroadCastOutgoing(String msg) {

	}

}
