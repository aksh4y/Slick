package edu.northeastern.ccs.im;

public class MessagePrinter {    
    public static final String ANSI_RED = "\033[31;1m";
    public static final String ANSI_GREEN= "\033[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void printMessage(String message) {
        if(message.contains("[BROADCASTED]")) {
            message = message.substring(message.indexOf(" ") + 1);
            System.out.println(ANSI_RESET + message);
            return;
        }
        else if(message.contains("[BROADCAST]")) {
            message = message.substring(0, message.lastIndexOf(" "));
            System.out.println(ANSI_RED + message + ANSI_RESET);
            return;
        }
        message = message.substring(0, message.indexOf(" ") + 1) + message.substring(message.indexOf(" ", message.indexOf(" ") + 1) + 1, message.lastIndexOf(" "));
        if(message.contains("[Private Msg]"))
            System.out.println(ANSI_RED + message + ANSI_RESET);
        else if(message.contains("PRIVATE")) {
            System.out.println(ANSI_RESET + message);
        }
        else if(message.contains("GROUP")) {
            message = message.substring(0, message.lastIndexOf("->"));
            System.out.println(ANSI_RESET + message);
        }
        else {   // group incoming	    
            message = message.substring(0, message.lastIndexOf("->"));
            System.out.println(ANSI_RED + message + ANSI_RESET);
        }
    }
}