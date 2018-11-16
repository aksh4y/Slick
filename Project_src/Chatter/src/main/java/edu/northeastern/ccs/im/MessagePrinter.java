package edu.northeastern.ccs.im;

public class MessagePrinter {

	/*public static void printMessage(String msg) {
		String messageArray[] = msg.split("&&");
		if (messageArray[0].equals("PIC")) {
			handlePrivateIncoming(msg);
		} else if (messageArray[0].equals("POU")) {
			handlePrivateOutgoing(msg);
		} else if (messageArray[0].equals("GOU")) {
			handleGroupOutgoing(msg);
		} else if (messageArray[0].equals("GIC")) {
			handleGroupIncoming(msg);
		} else if (messageArray[0].equals("BIC")) {
			handleBroadcastIncoming(msg);
		} else if (messageArray[0].equals("BOU")) {
			handleBroadcastOutgoing(msg);
		}
	}*/
    
    public static final String ANSI_RED = "\033[31;1m";
    public static final String ANSI_GREEN= "\033[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    
	public static void printMessage(String message) {
	    //System.out.println(msg.toString());
	    
	    if(message.contains("[BROADCASTED]") || message.contains("GROUP "))
	        System.out.println(ANSI_RESET + message);
	    else if(message.contains("[BROADCAST]") || message.contains("[PRIVATE]"))
	        System.out.println(ANSI_RED + message + ANSI_RESET);
	    else if(message.contains("PRIVATE"))
	        System.out.println(ANSI_RESET + message);
	    else   // group incoming
	        System.out.println(ANSI_RED + message + ANSI_RESET);
	        
	    
	   /* Message msg = null;
	    if(message.contains("[BROADCAST]")) {
	        String name = message.substring(message.indexOf("] " + 1), message.indexOf(":") - 1);
	        int totalLen = Integer.parseInt(message.substring(4, message.indexOf("[") - 1));
	        String text = message.substring(message.indexOf(":") + 1, totalLen);
	        msg = Message.makeBroadcastMessage(name, text);
	    }
	    else if(message.contains("[PRIVATE]")) {
	        String name = message.substring(message.indexOf("] " + 1), message.indexOf(":") - 1);
            int totalLen = Integer.parseInt(message.substring(4, message.indexOf("[") - 1));
            String text = message.substring(message.indexOf(":") + 1, totalLen);
           // msg = Message.makePrivateMessage(name, text);
	    }
	    
	    
	    System.out.println(msg.getText());
	    if(msg.isBroadcastMessage())
	        handleBroadcastMessage(msg);
	    else if(msg.isPrivateMessage())
	        handlePrivateMessage(msg);
	    else if(msg.isGroupMessage())
	        handleGroupMessage(msg);*/
	}
	
	private static void handleGroupMessage(Message msg) {
	    if(msg.getText().contains("["))
            handleGroupIncoming(msg);
        else
            handleGroupOutgoing(msg);
    }

    public static void handlePrivateMessage(Message msg) {
	    if(msg.getText().contains("["))
            handlePrivateIncoming(msg);
        else
            handlePrivateOutgoing(msg);
	}
	
	public static void handleBroadcastMessage(Message msg) {
        if(msg.getText().contains("["))
            handleBroadcastIncoming(msg);
        else
            handleBroadcastOutgoing(msg);
    }

	public static void handlePrivateIncoming(Message msg) {
	    System.out.println(ANSI_RED + "[Private Msg] " + msg.getSender() + ": " + msg.getText() + ANSI_RESET);
	}
	
	public static void handlePrivateOutgoing(Message msg) {
        System.out.println(ANSI_GREEN + "PRIVATE " + msg.getMsgRecipient() +" " + msg.getText() + ANSI_RESET);
    }

	public static void handleBroadcastIncoming(Message msg) {
	    System.out.println(ANSI_RED + "[Broadcast] " + msg.getSender() + ": " + msg.getText() + ANSI_RESET);
	}
	
	public static void handleBroadcastOutgoing(Message msg) {
	    System.out.println(ANSI_GREEN + msg.getText() + ANSI_RESET);
    }
	
    public static void handleGroupIncoming(Message msg) {
        System.out.println(ANSI_RED + "[" + msg.getSender() + "@" + msg.getMsgRecipient() + "] " + msg.getText() + ANSI_RESET);
    }

	public static void handleGroupOutgoing(Message msg) {
	    System.out.println(ANSI_GREEN + "GROUP " + msg.getMsgRecipient() +" " + msg.getText() + ANSI_RESET);
	}
}
