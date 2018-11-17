package edu.northeastern.ccs.im;

public class MessagePrinter {    
    public static final String ANSI_RED = "\033[31;1m";
    public static final String ANSI_GREEN= "\033[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void printMessage(String message) {
        if(message.contains("[BROADCASTED]") || message.contains("GROUP "))
            System.out.println(ANSI_RESET + message);
        else if(message.contains("[BROADCAST]") || message.contains("[PRIVATE]"))
            System.out.println(ANSI_RED + message + ANSI_RESET);
        else if(message.contains("PRIVATE"))
            System.out.println(ANSI_RESET + message);
        else   // group incoming
            System.out.println(ANSI_RED + message + ANSI_RESET);	    
    }
}
