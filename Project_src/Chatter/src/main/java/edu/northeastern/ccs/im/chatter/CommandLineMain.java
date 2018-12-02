package edu.northeastern.ccs.im.chatter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.KeyboardScanner;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessagePrinter;
import edu.northeastern.ccs.im.MessageScanner;

/**
 * Class which can be used as a command-line IM client.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class CommandLineMain {

	public static final String ANSI_RED = "\033[31;1m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static Boolean isSubpoena = false;

	/**
	 * This main method will perform all of the necessary actions for this phase of
	 * the course project.
	 *
	 * @param args
	 *            Command-line arguments which we ignore
	 */
	public static void main(String[] args) throws IOException {
		IMConnection connect;
		do {
			System.out.println("\t\t\t::Welcome to Slick::");
			System.out.println(
					"Welcome! Begin by logging in using LOGIN_USER <username> <password>\nTip: Use the help command to see a list of commands at any point!");
			String user = "DUMMYUSER";
			// Create a Connection to the IM server.
			connect = new IMConnection(args[0], Integer.parseInt(args[1]), user);
		} while (!connect.connect());

		// Create the objects needed to read & write IM messages.
		KeyboardScanner scan = connect.getKeyboardScanner();
		MessageScanner mess = connect.getMessageScanner();

		// Repeat the following loop
		while (connect.connectionActive()) {
			// Check if the user has typed in a line of text to broadcast to the
			// IM server.
			// If there is a line of text to be
			// broadcast: 
			if (scan.hasNext()) {
				// Read in the text they typed
				String line = scan.nextLine();
				// If the line equals "/quit", close the connection to the IM
				// server.
				if (line.equals("/quit")) {
					connect.disconnect();
					break;
				}
				if (!isSubpoena || line.contains("SEARCH")) {
					if (line.equalsIgnoreCase("HELP")) {
						System.out.println(
								"\t\t::Use The Following Commands::\n\nHello\tWTF\tHow are you?\tWhat time is it Mr. Fox?\tWhat is the date?\tWhat time is it?\t"
										+ ANSI_RED + "/quit\n" + ANSI_RESET
										+ "\nCREATE_USER <username> <password>\tLOGIN_USER <username> <password>\n\nUPDATE_PASSWORD <current password> <new password>\t"
										+ ANSI_RED + "DELETE_USER <password>\n\n" + ANSI_RESET
										+ "PRIVATE <username> <message>\tBROADCAST <message>\tGROUP <group name> <message>\tMIME <username> <file path>\n\n"
										+ "CREATE_GROUP <group name>\tJOIN_GROUP <group name>\t" + ANSI_RED
										+ "EXIT_GROUP <group name> \tDELETE_GROUP <group name>\n\n" + ANSI_RESET
										+ "RECALL <message_id> <user/group> <name>\t\tSEARCH <sender/receiver> <name>\n\n"
										+ "SUBPOENA <id>\tSUBPOENA_GROUP <group name> <from_date (mm-dd-yyyy)> <to_date (mm-dd-yyyy)>\n\nSUBPOENA_USER <username> <username/all> <from_date (mm-dd-yyyy)> <to_date (mm-dd-yyyy)>\n\n"
										+ "PARENTAL_CONTROL <on/off>\t\tLOGGER <on/off>\n");
					} else {
						// Else, send the text so that it is broadcast to all users
						// logged in to the IM
						// server.
						try {
							connect.sendMessage(line);
						} catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("Enter valid syntax.");
						}
					}
				} else {
					System.out.println("This functionality is not available");
				}
			}
			// Get any recent messages received from the IM server.
			if (mess.hasNext()) {
				Message message = mess.next();
				if (message.isSubpoenaLoginSuccess()) {
					isSubpoena = true;
					connect.setUsername(message.getSender());
				} else if (message.isPendingMessage()) {
                    System.out.println("\u001B[33m--------YOU HAVE UNREAD MESSAGES--------\u001B[0m");
                } else if (message.isHistoryMessage() && isSubpoena) {
					System.out.println(message.getSender());
				} else if (message.isHistoryMessage()) {
					MessagePrinter.printMessage(message.getSender());
				} 				
				else if (message.isLoginSuccess() || message.isCreateSuccess())
					connect.setUsername(message.getSender());
				else if (!message.getSender().equals(connect.getUserName())) {
					if (message.isBroadcastMessage())
						System.out.println(ANSI_RED + "[Broadcast] " + message.getSender() + ": " + message.getText()
								+ ANSI_RESET);
					else if (message.isPrivateMessage())
						System.out.println(ANSI_RED + "[Private Msg] " + message.getSender() + ": " + message.getText()
								+ ANSI_RESET);
					else if (message.isGroupMessage())
						System.out.println(ANSI_RED + "[" + message.getSender() + "@" + message.getMsgRecipient() + "] "
								+ message.getText() + ANSI_RESET);
					else
						System.out.println(ANSI_RED + message.getSender() + ": " + message.getText() + ANSI_RESET);
				}
			}
		}
		System.out.println("Program complete.");
		System.exit(0);
	}

	public static void decode(String url) {
		url = url.substring(3);
		byte[] imageBytes = Base64.getDecoder().decode(url);
		BufferedImage img;
		File outputfile = new File("image.png");
		try {
			img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			int num = 1;
			while (outputfile.exists()) {
				outputfile = new File("image" + num++ + ".png");
			}
			ImageIO.write(img, "png", outputfile);
			img.flush();
			// System.out.println("FILE RECEIVED " + outputfile.getName() + " FROM " +
			// srcName);

		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
