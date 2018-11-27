package edu.northeastern.ccs.im;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import edu.northeastern.ccs.im.Message.MessageType;

/**
 * Each instance of this class represents a single transmission by our IM
 * clients.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.3
 */
public class Message {
	/**
	 * List of the different possible message types.
	 */
	protected enum MessageType {
		/**
		 * Message sent by the user attempting to login using a specified username.
		 */
		HELLO("HLO"),
		/** Message sent by the server acknowledging a successful log in. */
		ACKNOWLEDGE("ACK"),
		/** Message sent by the server rejecting a login attempt. */
		NO_ACKNOWLEDGE("NAK"),
		/**
		 * Message sent by the user to start the logging out process and sent by the
		 * server once the logout process completes.
		 */
		QUIT("BYE"),
		/** Message whose contents is broadcast to all connected users. */
		BROADCAST("BCT"),
		/** Message whose content is sent privately to a user. */
		PRIVATE("PRI"),
		/** Message which is used to login a user. */
		LOGIN_USER("LUS"),
		/** Message if login is successful */
		LOGIN_SUCCESS("LSC"),
		/** Message if login is fails */
		LOGIN_FAIL("LFA"),
		/** Message all members of a group. */
		GROUP("GRP"),
		/** Message to create user **/
		CREATE_USER("CUS"),
		/** Message if create user is successful */
		CREATE_SUCCESS("USC"),
		/** Message if create user is fails */
		CREATE_FAIL("UFA"),
		/** Message if create user is fails */
		USER_EXIST("UEX"),
		/** Message to create group **/
		CREATE_GROUP("CUG"),
		/** Message if create Group is successful */
		GROUP_CREATE_SUCCESS("GSC"),
		/** Message if create user is fails */
		GROUP_CREATE_FAIL("GFA"),
		/** Message if create user is fails */
		GROUP_EXIST("GEX"),
		/** Message to add user to group */
		ADD_TO_GROUP("GAD"),
		/**
		 * Message if group does not exist
		 */
		GROUP_NOT_EXIST("GNE"),
		/** Message if user added to group */
		GROUP_ADD_SUCCESS("GAS"),
		/** Message if user added to group */
		GROUP_ADD_FAIL("GAF"),
		/** Message to update user */
		UPDATE_USER("UUS"),
		/** Message to update user */
		DELETE_USER("DUS"),
		/** Message to send Success Message */
		DELETE_USER_SUCCESS("SUD"),
		/** Message if password is wrong */
		USER_WRONG_PASSWORD("WPD"),
		/** Message to update user */
		DELETE_GROUP("DGR"),
		/** Message to send Success Message */
		SUCCESS_MESSAGE("SUC"),
		/** Message to send Fail Message */
		FAIL_MESSAGE("FAL"),
		/** Message to Exit from group */
		EXIT_FROM_GROUP("EXG"),
		/** Message if user added to group */
		GROUP_EXIT_SUCCESS("GXS"),
		/** Message if user added to group */
		GROUP_EXIT_FAIL("GXF"),
		/** MIME type */
		MIME("MIM"),
		/** Message for history messages */
		HISTORY_MESSAGE("HMG"),
/** Notify pending msgs exist */
    NOTIFY_PENDING("PEN"),
		RECALL("REC"),
		/** Message for user Subpoena create messages */
		USER_SUBPOENA_CREATE("SUN"),
		/** Message for group Subpoena create messages */
		GROUP_SUBPOENA_CREATE("SGN"),
		/** Message for No privilege to add Subpoena */
		SUBPOENA_NO_PRIVILEGE("SNP"),
		/** Message for group Subpoena create messages */
		SUBPOENA_LOGIN("SBN"),
		/** Message for create Subpoena is success */
		SUBPOENA_SUCCESS("SBC"),
		/** Message for create Subpoena is success */
		SUBPOENA_LOGIN_SUCCESS("SLC");
		/** Store the short name of this message type. */
		private String tla;

		/**
		 * Define the message type and specify its short name.
		 * 
		 * @param abbrev
		 *            Short name of this message type, as a String.
		 */
		private MessageType(String abbrev) {
			tla = abbrev;
		}

		/**
		 * Return a representation of this Message as a String.
		 * 
		 * @return Three letter abbreviation for this type of message.
		 */
		@Override
		public String toString() {
			return tla;
		}
	}

	/** The string sent when a field is null. */
	private static final String NULL_OUTPUT = "--";

	/** The handle of the message. */
	private MessageType msgType;

	/**
	 * The first argument used in the message. This will be the sender's identifier.
	 */
	private String msgSender;

	/** The second argument used in the message. */
	private String msgText;

	/**
	 * The third argument (optional)
	 */
	private String msgRecipient;

	/**
	 * Create a new message that contains actual IM text. The type of distribution
	 * is defined by the handle and we must also set the name of the message sender,
	 * message recipient, and the text to send.
	 * 
	 * @param handle
	 *            Handle for the type of message being created.
	 * @param srcName
	 *            Name of the individual sending this message
	 * @param text
	 *            Text of the instant message
	 */
	private Message(MessageType handle, String srcName, String text) {
		msgType = handle;
		// Save the properly formatted identifier for the user sending the
		// message.
		msgSender = srcName;
		// Save the text of the message.
		msgText = text;
	}

	private Message(MessageType handle, String srcName, String recipient, String text) {
		msgType = handle;
		// Save the properly formatted identifier for the user sending the
		// message.
		msgSender = srcName;

		msgRecipient = recipient;
		// Save the text of the message.
		msgText = text;
	}

	/**
	 * Create simple command type message that does not include any data.
	 * 
	 * @param handle
	 *            Handle for the type of message being created.
	 */
	private Message(MessageType handle) {
		this(handle, null, null);
	}

	/**
	 * Create a new message that contains a command sent the server that requires a
	 * single argument. This message contains the given handle and the single
	 * argument.
	 * 
	 * @param handle
	 *            Handle for the type of message being created.
	 * @param srcName
	 *            Argument for the message; at present this is the name used to
	 *            log-in to the IM server.
	 */
	private Message(MessageType handle, String srcName) {
		this(handle, srcName, null);
	}

	/**
	 * Create a new message to continue the logout process.
	 * 
	 * @return Instance of Message that specifies the process is logging out.
	 */
	public static Message makeQuitMessage(String myName) {
		return new Message(MessageType.QUIT, myName, null);
	}

	/**
	 * Create a private message
	 * 
	 * @param srcName
	 * @param recipient
	 * @param text
	 * @return
	 */
	public static Message makePrivateMessage(String srcName, String recipient, String text) {
		return new Message(MessageType.PRIVATE, srcName, recipient, text);
	}

	public static Message makeRecallMessage(String srcName, String recipient, String text) {
		return new Message(MessageType.RECALL, srcName, recipient, text);
	}

	/**
	 * 
	 * @param srcName
	 * @param recipient
	 * @param url
	 * @return
	 */
	public static Message makeMIMEMessage(String srcName, String recipient, String url) {
		if (!url.substring(0, 3).equals("REC")) {
			try {
				File f = new File(url);
				String encodedstring = "REC" + encodeFileToBase64Binary(f);
				return new Message(MessageType.MIME, srcName, recipient, encodedstring);
			} catch (Exception e) {
				System.err.println("No such file found");
				return null;
			}
		} else {
			// decode
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
				System.out.println("FILE RECEIVED " + outputfile.getName() + " FROM " + srcName);
				return new Message(MessageType.MIME, srcName, recipient, url);
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	private static String encodeFileToBase64Binary(File file) {
		String encodedfile = null;
		try {
			FileInputStream fileInputStreamReader = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fileInputStreamReader.read(bytes);
			encodedfile = new String(Base64.getEncoder().encode(bytes), "UTF-8");
			fileInputStreamReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encodedfile;
	}

	/**
	 * Create a group message
	 * 
	 * @param srcName
	 * @param group
	 *            name
	 * @param text
	 * @return
	 */
	public static Message makeGroupMessage(String srcName, String groupName, String text) {
		return new Message(MessageType.GROUP, srcName, groupName, text);
	}

	/**
	 * Create a new message broadcasting an announcement to the world.
	 * 
	 * @param myName
	 *            Name of the sender of this very important missive.
	 * @param text
	 *            Text of the message that will be sent to all users
	 * @return Instance of Message that transmits text to all logged in users.
	 */
	public static Message makeBroadcastMessage(String myName, String text) {
		return new Message(MessageType.BROADCAST, myName, text);
	}

	/**
	 * Create a new user login message.
	 * 
	 * @param username
	 *            .Text
	 * @param password
	 *            Text
	 * @return Instance of User Login Message.
	 */
	public static Message makeUserLoginMessage(String username, String password) {
		return new Message(MessageType.LOGIN_USER, username, password);
	}

	/**
	 * Create a new message stating the name with which the user would like to
	 * login.
	 * 
	 * @param text
	 *            Name the user wishes to use as their screen name.
	 * @return Instance of Message that can be sent to the server to try and login.
	 */
	protected static Message makeHelloMessage(String text) {
		return new Message(MessageType.HELLO, null, text);
	}

	/**
	 * Given a handle, name and text, return the appropriate message instance or an
	 * instance from a subclass of message.
	 * 
	 * @param handle
	 *            Handle of the message to be generated.
	 * @param srcName
	 *            Name of the originator of the message (may be null)
	 * @param text
	 *            Text sent in this message (may be null)
	 * @return Instance of Message (or its subclasses) representing the handle,
	 *         name, & text.
	 */
	protected static Message makeMessage(String handle, String srcName, String text) {
		Message result = null;
		if (handle.compareTo(MessageType.QUIT.toString()) == 0) {
			result = makeQuitMessage(srcName);
		} else if (handle.compareTo(MessageType.HELLO.toString()) == 0) {
			result = makeLoginMessage(srcName);
		} else if (handle.compareTo(MessageType.BROADCAST.toString()) == 0) {
			result = makeBroadcastMessage(srcName, text);
		} else if (handle.compareTo(MessageType.ACKNOWLEDGE.toString()) == 0) {
			result = makeAcknowledgeMessage(srcName);
		} else if (handle.compareTo(MessageType.NO_ACKNOWLEDGE.toString()) == 0) {
			result = makeNoAcknowledgeMessage();
		} else if (handle.compareTo(MessageType.LOGIN_USER.toString()) == 0) {
			result = makeUserLoginMessage(srcName, text);
		} else if (handle.compareTo(MessageType.LOGIN_SUCCESS.toString()) == 0) {
			result = makeLoginSuccess(srcName);
		} else if (handle.compareTo(MessageType.LOGIN_FAIL.toString()) == 0) {
			result = makeLoginFaill();
		} else if (handle.compareTo(MessageType.CREATE_USER.toString()) == 0) {
			result = makeCreateUserMessage(srcName, text);
		} else if (handle.compareTo(MessageType.CREATE_SUCCESS.toString()) == 0) {
			result = makeCreateUserSuccess(srcName);
		} else if (handle.compareTo(MessageType.CREATE_FAIL.toString()) == 0) {
			result = makeCreateUserFail();
		} else if (handle.compareTo(MessageType.USER_EXIST.toString()) == 0) {
			result = makeUserIdExist();
		} else if (handle.compareTo(MessageType.CREATE_GROUP.toString()) == 0) {
			result = makeCreateGroupMessage(srcName);
		} else if (handle.compareTo(MessageType.GROUP_CREATE_SUCCESS.toString()) == 0) {
			result = makeCreateGroupSuccess();
		} else if (handle.compareTo(MessageType.GROUP_CREATE_FAIL.toString()) == 0) {
			result = makeCreateGroupFail();
		} else if (handle.compareTo(MessageType.GROUP_EXIST.toString()) == 0) {
			result = makeGroupExist();
		} else if (handle.compareTo(MessageType.GROUP_NOT_EXIST.toString()) == 0) {
			result = makeGroupNotExist();
		} else if (handle.compareTo(MessageType.GROUP_ADD_FAIL.toString()) == 0) {
			result = makeGroupAddFail();
		} else if (handle.compareTo(MessageType.GROUP_ADD_SUCCESS.toString()) == 0) {
			result = makeGroupAddSuc();
		} else if (handle.compareTo(MessageType.ADD_TO_GROUP.toString()) == 0) {
			result = makeAddUserToGroup(srcName);
		} else if (handle.compareTo(MessageType.EXIT_FROM_GROUP.toString()) == 0) {
			result = makeUserExitGroup(srcName);
		} else if (handle.compareTo(MessageType.DELETE_GROUP.toString()) == 0) {
			result = makeDeleteGroupMessage(srcName);
		} else if (handle.compareTo(MessageType.DELETE_USER.toString()) == 0) {
			result = makeDeleteUserMessage(srcName);
		} else if (handle.compareTo(MessageType.SUCCESS_MESSAGE.toString()) == 0) {
			result = makeSuccessMsg();
		} else if (handle.compareTo(MessageType.DELETE_USER_SUCCESS.toString()) == 0) {
			result = makeDeleteUserSuccessMsg();
		} else if (handle.compareTo(MessageType.USER_WRONG_PASSWORD.toString()) == 0) {
			result = makeUserWrongPasswordMsg();
		} else if (handle.compareTo(MessageType.FAIL_MESSAGE.toString()) == 0) {
			result = makeFailMsg();
		} else if (handle.compareTo(MessageType.UPDATE_USER.toString()) == 0) {
			result = makeUpdateUserMessage(srcName, text);
		} else if (handle.compareTo(MessageType.HISTORY_MESSAGE.toString()) == 0) {
			result = makeHistoryMessage(srcName);
		}else if (handle.compareTo(MessageType.NOTIFY_PENDING.toString()) == 0) {		
      result = makePendingMsgNotif();		
   } else if (handle.compareTo(MessageType.SUBPOENA_NO_PRIVILEGE.toString()) == 0) {
			result = makeCreateNoPrivilegeMessage();
		} else if (handle.compareTo(MessageType.SUBPOENA_LOGIN.toString()) == 0) {
			result = makeSubpoenaLogin(srcName);
		} else if (handle.compareTo(MessageType.SUBPOENA_SUCCESS.toString()) == 0) {
			result = makeSubpoenaSuccess(srcName);
		} else if (handle.compareTo(MessageType.SUBPOENA_LOGIN_SUCCESS.toString()) == 0) {
			result = makeSubpoenaLoginSuccess();
		}
		return result;
	}

	/**
	 * Create a new message Subpoena Login is success.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeSubpoenaLoginSuccess() {
		return new Message(MessageType.SUBPOENA_LOGIN_SUCCESS);
	}

	/**
	 * Create a new message Subpoena Create is success.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeSubpoenaSuccess(String id) {
		return new Message(MessageType.SUBPOENA_SUCCESS, id);
	}

	/**
	 * Create a new message to make Subpoena Login request.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeSubpoenaLogin(String id) {
		return new Message(MessageType.SUBPOENA_LOGIN, id);
	}

	/**
	 * Create a new message to make User Subpoena create request.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeCreateUserSubpoena(String users, String fromDate, String toDate) {
		return new Message(MessageType.USER_SUBPOENA_CREATE, users, fromDate, toDate);
	}

	/**
	 * Create a new message to make Group Subpoena create request.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeCreateGroupSubpoena(String groupName, String fromDate, String toDate) {
		return new Message(MessageType.GROUP_SUBPOENA_CREATE, groupName, fromDate, toDate);
	}

	/**
	 * Create a new message to make no PRIVILEGE message
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeCreateNoPrivilegeMessage() {
		return new Message(MessageType.SUBPOENA_NO_PRIVILEGE);
	}

	/**
	 * Create a new message to make history message.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeHistoryMessage(String message) {
		return new Message(MessageType.HISTORY_MESSAGE, message);
	}

	/**
	 * Create a new message to delete a group.
	 * 
	 * @return Instance of Message.
	 */

	public static Message makeDeleteGroupMessage(String groupName) {
		return new Message(MessageType.DELETE_GROUP, groupName);
	}

	/**
	 * Create a new message to delete a user.
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeDeleteUserMessage(String password) {
		return new Message(MessageType.DELETE_USER, password);
	}

	/**
	 * Create a new message to delete a user.
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeUpdateUserMessage(String password, String newPassword) {
		return new Message(MessageType.UPDATE_USER, password, newPassword);
	}

	/**
	 * Create a new message to send success message
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeDeleteUserSuccessMsg() {
		return new Message(MessageType.DELETE_USER_SUCCESS);
	}

	/**
	 * Create a new message to delete user wrong password message
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeUserWrongPasswordMsg() {
		return new Message(MessageType.USER_WRONG_PASSWORD);
	}
  
  /**		
  * Create a new message if pending messages exist		
  * 		
  * @return Instance of Message.		
  */		
  public static Message makePendingMsgNotif() {		
    return new Message(MessageType.NOTIFY_PENDING);		
  }

	/**
	 * Create a new message to send success message
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeSuccessMsg() {
		return new Message(MessageType.SUCCESS_MESSAGE);
	}

	/**
	 * Create a new message to send fail message
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeFailMsg() {
		return new Message(MessageType.FAIL_MESSAGE);
	}

	/**
	 * Create a new message to exit user from group
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeUserExitGroup(String groupName) {
		return new Message(MessageType.EXIT_FROM_GROUP, groupName);
	}

	/**
	 * Create a new message to Create a Group.
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeCreateGroupMessage(String groupName) {
		return new Message(MessageType.CREATE_GROUP, groupName);
	}

	/**
	 * Create a new message to if Create Group is successful
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeCreateGroupSuccess() {
		return new Message(MessageType.GROUP_CREATE_SUCCESS);
	}

	/**
	 * Create a new message to if Create Group fails
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeCreateGroupFail() {
		return new Message(MessageType.GROUP_CREATE_FAIL);
	}

	/**
	 * Create a new message to if Group exist
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeGroupExist() {
		return new Message(MessageType.GROUP_EXIST);
	}

	/**
	 * Create a new message to add user to group
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeAddUserToGroup(String groupName) {
		return new Message(MessageType.ADD_TO_GROUP, groupName);
	}

	/**
	 * Create a new message to if Login is successful
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeLoginSuccess(String name) {
		return new Message(MessageType.LOGIN_SUCCESS, name);
	}

	/**
	 * Create a new message to if Login fails
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeLoginFaill() {
		return new Message(MessageType.LOGIN_FAIL);
	}

	/**
	 * Create a new message to Create a user.
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeCreateUserMessage(String userName, String password) {
		return new Message(MessageType.CREATE_USER, userName, password);
	}

	/**
	 * Create a new message to if Create User is successful
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeCreateUserSuccess(String name) {
		return new Message(MessageType.CREATE_SUCCESS, name);
	}

	/**
	 * Create a new message to if Create User fails
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeCreateUserFail() {
		return new Message(MessageType.CREATE_FAIL);
	}

	/**
	 * Create a new message to send if group does not exist
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeGroupNotExist() {
		return new Message(MessageType.GROUP_NOT_EXIST);
	}

	/**
	 * Create a new message to send if group add fails
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeGroupAddFail() {
		return new Message(MessageType.GROUP_ADD_FAIL);
	}

	/**
	 * Create a new message to send if group add Success
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeGroupAddSuc() {
		return new Message(MessageType.GROUP_ADD_SUCCESS);
	}

	/**
	 * Create a new message to if UserId exist
	 * 
	 * @return Instance of Message.
	 */
	public static Message makeUserIdExist() {
		return new Message(MessageType.USER_EXIST);
	}

	/**
	 * Given a handle, name and text, return the appropriate message instance or an
	 * instance from a subclass of message.
	 * 
	 * @param handle
	 *            Handle of the message to be generated.
	 * @param srcName
	 *            Name of the originator of the message (may be null)
	 * @param text
	 *            Text sent in this message (may be null)
	 * @param recipient
	 *            Name of the message recipient or group name
	 * @return Instance of Message (or its subclasses) representing the handle,
	 *         name, & text.
	 */
	protected static Message makeMessage(String handle, String srcName, String recipient, String text) {
		Message result = null;
		if (handle.compareTo(MessageType.PRIVATE.toString()) == 0)
			result = makePrivateMessage(srcName, recipient, text);
		else if (handle.compareTo(MessageType.GROUP.toString()) == 0)
			result = makeGroupMessage(srcName, recipient, text);
		else if (handle.compareTo(MessageType.MIME.toString()) == 0)
			result = makeMIMEMessage(srcName, recipient, text);
		return result;
	}

	/**
	 * Create a new message to reject the bad login attempt.
	 * 
	 * @return Instance of Message that rejects the bad login attempt.
	 */
	public static Message makeNoAcknowledgeMessage() {
		return new Message(MessageType.NO_ACKNOWLEDGE);
	}

	/**
	 * Create a new message to acknowledge that the user successfully logged as the
	 * name <code>srcName</code>.
	 * 
	 * @param srcName
	 *            Name the user was able to use to log in.
	 * @return Instance of Message that acknowledges the successful login.
	 */
	public static Message makeAcknowledgeMessage(String srcName) {
		return new Message(MessageType.ACKNOWLEDGE, srcName);
	}

	/**
	 * Create a new message for the early stages when the user logs in without all
	 * the special stuff.
	 * 
	 * @param myName
	 *            Name of the user who has just logged in.
	 * @return Instance of Message specifying a new friend has just logged in.
	 */
	public static Message makeLoginMessage(String myName) {
		return new Message(MessageType.HELLO, myName);
	}

	/**
	 * Return the type of this message.
	 * 
	 * @return MessageType for this message.
	 */
	public MessageType getType() {
		return msgType;
	}

	/**
	 * Return the name of the sender of this message.
	 * 
	 * @return String specifying the name of the message originator.
	 */
	public String getSender() {
		return msgSender;
	}

	/**
	 * Return the text of this message.
	 * 
	 * @return String equal to the text sent by this message.
	 */
	public String getText() {
		return msgText;
	}

	/**
	 * Determine if this message is an acknowledgement message.
	 * 
	 * @return True if the message is an acknowledgement message; false otherwise.
	 */
	public boolean isAcknowledge() {
		return (msgType == MessageType.ACKNOWLEDGE);
	}

	/**
	 * Determine if this message is private to recipient.
	 * 
	 * @return True if the message is a private message; false otherwise.
	 */
	public boolean isPrivateMessage() {
		return (msgType == MessageType.PRIVATE);
	}

	/**
	 * Determine if this message is a group message.
	 * 
	 * @return True if the message is a group message; false otherwise.
	 */
	public boolean isGroupMessage() {
		return (msgType == MessageType.GROUP);
	}

	/**
	 * Determine if this message is broadcasting text to everyone.
	 * 
	 * @return True if the message is a broadcast message; false otherwise.
	 */
	public boolean isBroadcastMessage() {
		return (msgType == MessageType.BROADCAST);
	}

	/**
	 * Determine if this message is a MIME msg.
	 * 
	 * @return True if the message is a MIME message; false otherwise.
	 */
	public boolean isMIME() {
		return (msgType == MessageType.MIME);
	}

	/**
	 * Determine if this message contains text which the recipient should display.
	 * 
	 * @return True if the message is an actual instant message; false if the
	 *         message contains data
	 */
	public boolean isDisplayMessage() {
		return (msgType == MessageType.BROADCAST);
	}

	/**
	 * Determine if this message is of type Subpoena login success.
	 * 
	 * @return True if the message is of type SUBPOENA_LOGIN_SUCCESS; false otherwise
	 */
	public boolean isSubpoenaLoginSuccess() {
		return (msgType == MessageType.SUBPOENA_LOGIN_SUCCESS);
	}

	
	/**
	 * Determine if this message is of type login success.
	 * 
	 * @return True if the message is of type login_success; false otherwise
	 */
	public boolean isLoginSuccess() {
		return (msgType == MessageType.LOGIN_SUCCESS);
	}

	/**
	 * Determine if this message is of type create success.
	 * 
	 * @return True if the message is of type create_success; false otherwise
	 */
	public boolean isCreateSuccess() {
		return (msgType == MessageType.CREATE_SUCCESS);
	}

	/**
	 * Determine if this message is sent by a new client to log-in to the server.
	 * 
	 * @return True if the message is an initialization message; false otherwise
	 */
	public boolean isInitialization() {
		return (msgType == MessageType.HELLO);
	}

	/**
	 * Determine if this message is a message signing off from the IM server.
	 * 
	 * @return True if the message is sent when signing off; false otherwise
	 */
	public boolean terminate() {
		return (msgType == MessageType.QUIT);
	}

	public String getMsgRecipient() {
		return msgRecipient;
	}

	public void setMsgRecipient(String msgRecipient) {
		this.msgRecipient = msgRecipient;
	}

	/**
	 * Representation of this message as a String. This begins with the message
	 * handle and then contains the length (as an integer) and the value of the next
	 * two arguments.
	 * 
	 * @return Representation of this message as a String.
	 */
	@Override
	public String toString() {
		String result = msgType.toString();
		if (msgSender != null) {
			result += " " + msgSender.length() + " " + msgSender;
		} else {
			result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
		}
		if (msgRecipient != null) {
			result += " " + msgRecipient.length() + " " + msgRecipient;
		}
		if (msgText != null) {
			result += " " + msgText.length() + " " + msgText;
		} else {
			result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
		}
		return result;
	}
}