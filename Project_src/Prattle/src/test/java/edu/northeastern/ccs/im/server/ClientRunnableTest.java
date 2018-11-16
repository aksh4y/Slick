package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.ScanNetNB;
import edu.northeastern.ccs.im.SocketNB;
import edu.northeastern.ccs.im.MongoDB.Model.User;

/**
 * Tests for ClientRunnable class
 * 
 * @author Akshay
 *
 */
public class ClientRunnableTest {

	ClientRunnable client;
	ServerSocketChannel serverSocket;

	static PrattleRunabale server;

	@BeforeAll
	public static void setUp() {
		server = new PrattleRunabale();
		server.start();
	}

	@AfterAll
	public static void stopServer() {
		server.terminate();
	}

	/**
	 * Check client initialization
	 * 
	 * @throws IOException
	 */
	@Test
	public void checkInitialization() throws IOException {
		SocketNB s = new SocketNB("127.0.0.1", 4545);
		client = new ClientRunnable(s.getSocket());
		try {
			try {
				client.run();
				assertEquals(true, client instanceof ClientRunnable);
				client.terminateClient();
			} catch (Exception e) {

			}
		} catch (Exception e) {
		}
	}

	/*
	 * Test to check when the broadcastMessageIsSpecial fails
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void BroadCastMessageFalseTest() throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();

		client = new ClientRunnable(sChannel);
		client.setName("Test");
		Class cls = client.getClass();
		Method broadcastMessageIsSpecial = cls.getDeclaredMethod("broadcastMessageIsSpecial", Message.class);
		broadcastMessageIsSpecial.setAccessible(true);
		Message msg = Message.makeBroadcastMessage("test user", null);
		assertFalse((Boolean) broadcastMessageIsSpecial.invoke(client, msg));
		Message msg1 = Message.makeBroadcastMessage("test user", "jj");
		assertFalse((Boolean) broadcastMessageIsSpecial.invoke(client, msg1));

	}

	/*
	 * Test to check the broadcastMessageIsSpecial
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void BroadCastMessageTest() throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();

		client = new ClientRunnable(sChannel);
		client.setName("Test");
		Class cls = client.getClass();
		Method broadcastMessageIsSpecial = cls.getDeclaredMethod("broadcastMessageIsSpecial", Message.class);
		broadcastMessageIsSpecial.setAccessible(true);
		Message msg = Message.makeBroadcastMessage("test user", "How are you?");
		assertTrue((Boolean) broadcastMessageIsSpecial.invoke(client, msg));
	}

	/*
	 * Test to check the checkMessage
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void checkMessageTest() throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();
		Message msg = Message.makeBroadcastMessage("Test", "How are you?");
		Message nullMsg = Message.makeBroadcastMessage(null, "How are you?");

		client = new ClientRunnable(sChannel);
		client.setName("Test");
		Class cls = client.getClass();
		Field input = cls.getDeclaredField("input");
		input.setAccessible(true);
		ScanNetNB scanNetNB = (ScanNetNB) input.get(client);
		Class scanNet = scanNetNB.getClass();
		Field messages = scanNet.getDeclaredField("messages");
		messages.setAccessible(true);
		Queue<Message> queue = (Queue<Message>) messages.get(scanNetNB);
		queue.add(nullMsg);
		Method checkForInitialization = cls.getDeclaredMethod("checkForInitialization");
		checkForInitialization.setAccessible(true);
		checkForInitialization.invoke(client);
		Method messageChecks = cls.getDeclaredMethod("messageChecks", Message.class);
		messageChecks.setAccessible(true);
		Method sendMessage = cls.getDeclaredMethod("sendMessage", Message.class);
		sendMessage.setAccessible(true);
		sendMessage.invoke(client, msg);
		assertTrue((Boolean) messageChecks.invoke(client, msg));
	}

	/*
	 * Test to check when the checkMessage fails
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void checkMessageTestFail() throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();

		client = new ClientRunnable(sChannel);
		client.setName("Test");
		Class cls = client.getClass();
		Method messageChecks = cls.getDeclaredMethod("messageChecks", Message.class);
		messageChecks.setAccessible(true);
		Method checkForInitialization = cls.getDeclaredMethod("checkForInitialization");
		checkForInitialization.setAccessible(true);
		checkForInitialization.invoke(client);
		Message msg = Message.makeBroadcastMessage("", "");
		assertFalse((Boolean) messageChecks.invoke(client, msg));
		Message msg1 = Message.makeBroadcastMessage(null, "");
		assertFalse((Boolean) messageChecks.invoke(client, msg1));
		socket.close();
	}

	/*
	 * Test to check the setUserName
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void setUserNameTest() throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();

		client = new ClientRunnable(sChannel);
		client.setName("Test");
		Class cls = client.getClass();
		Method setUserName = cls.getDeclaredMethod("setUserName", String.class);
		setUserName.setAccessible(true);
		String userName = null;
		assertFalse((Boolean) setUserName.invoke(client, userName));
		userName = "NewUser";
		assertTrue((Boolean) setUserName.invoke(client, userName));
	}

	/*
	 * Test to check when the run
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void TestForRunIntialized() throws IOException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();
		Message msg = Message.makeBroadcastMessage("Test", "How are you?");
		Message nonSpeacialBroadMsg = Message.makeBroadcastMessage("Test", null);
		Message nonNameMessage = Message.makeBroadcastMessage(null, null);
		Message nonBroad = Message.makeAcknowledgeMessage("");
		Message terminate = Message.makeQuitMessage("Test");
		client = new ClientRunnable(sChannel);
		Class cls = client.getClass();
		Field input = cls.getDeclaredField("input");
		input.setAccessible(true);
		Field userField = cls.getDeclaredField("user");
		userField.setAccessible(true);
		User user = (User) userField.get(client);
		ScanNetNB scanNetNB = (ScanNetNB) input.get(client);
		Class scanNet = scanNetNB.getClass();
		Field messages = scanNet.getDeclaredField("messages");
		messages.setAccessible(true);
		Queue<Message> queue = (Queue<Message>) messages.get(scanNetNB);
		queue.add(nonSpeacialBroadMsg);
		Method checkForInitialization = cls.getDeclaredMethod("checkForInitialization");
		checkForInitialization.setAccessible(true);
		checkForInitialization.invoke(client);
		client.run();

		queue.add(msg);
		queue.add(nonSpeacialBroadMsg);
		checkForInitialization.invoke(client);
		client.run();

		Message nonSpeacialBroadMsgNotNull = Message.makeBroadcastMessage("Test", "kk");
		queue.add(msg);
		queue.add(nonSpeacialBroadMsgNotNull);
		checkForInitialization.invoke(client);
		client.run();

		Message bombOff = Message.makeBroadcastMessage("Test", "Prattle says everyone log off");
		queue.add(msg);
		queue.add(bombOff);
		checkForInitialization.invoke(client);
		client.run();

		queue.add(msg);
		queue.add(nonNameMessage);
		checkForInitialization.invoke(client);
		client.run();

		queue.add(msg);
		queue.add(nonBroad);
		checkForInitialization.invoke(client);
		client.run();

		Field specialResponse = cls.getDeclaredField("specialResponse");
		specialResponse.setAccessible(true);
		Queue<Message> sResps = (Queue<Message>) specialResponse.get(client);
		sResps.add(msg);
		Field waitingList = cls.getDeclaredField("waitingList");
		waitingList.setAccessible(true);
		Queue<Message> wait = (Queue<Message>) waitingList.get(client);
		wait.add(msg);
		queue.add(msg);
		checkForInitialization.invoke(client);
		client.run();

		Field immediateResponse = cls.getDeclaredField("immediateResponse");
		immediateResponse.setAccessible(true);
		Queue<Message> imi = (Queue<Message>) immediateResponse.get(client);
		queue.add(msg);
		imi.add(msg);
		checkForInitialization.invoke(client);
		client.run();

		Message correctCreateUserMessage = Message.makeCreateUserMessage("crtest", "crtest");
		Message correctCreateUser2Message = Message.makeCreateUserMessage("crtest2", "crtest");
		Message correctCreateGroupMessage = Message.makeCreateGroupMessage("crGroupTest");
		Message correctLoginMessage = Message.makeLoginMessage("crtest", "crtest");
		// Message incorrectLoginMessage = Message.makeLoginMessage("crtest1", "tewst");
		Message groupDeleteMessage = Message.makeDeleteGroupMessage("crGroupTest");
		Message userDeleteMessage = Message.makeDeleteUserMessage("crtest");
		Message userDeleteMessage2 = Message.makeDeleteUserMessage("crtest2");
		Message userDeleteWrongPasswordMessage = Message.makeDeleteUserMessage("crtest1");
		Message userUpdateMessage = Message.makeUpdateUserMessage("crtest", "crtest");
		Message userUpdateWrongPasswordMessage = Message.makeUpdateUserMessage("crtest1", "crtest");
		Message userAddToWrongGroupMessage = Message.makeAddUserToGroup("crGroupTest1");
		Message userAddToGroupMessage = Message.makeAddUserToGroup("crGroupTest");
		Message userExitWrongGroupMessage = Message.makeUserExitGroup("crGroupTest1");
		Message userExitGroupMessage = Message.makeUserExitGroup("crGroupTest");
		Message privateMsg = Message.makePrivateMessage("crtest", "crtest2", "private test");
		Message privateWrongMsg = Message.makePrivateMessage("crtest", "crtest12", "private test");
		Message groupMsg = Message.makeGroupMessage("crtest", "crGroupTest", "Hey hello group");
		Message wrongGroupMsg = Message.makeGroupMessage("crtest1", "crGroupTest", "Hey hello group");
		Message wrongGroupMsg2 = Message.makeGroupMessage("crtest", "crGroupTest22", "Hey hello group");
		
		
		queue.add(correctCreateUserMessage);
		client.run();
		queue.add(correctCreateUserMessage);
		client.run();
		
		queue.add(correctCreateUser2Message);
		client.run();

		
		queue.add(privateMsg);
		user = new User("crtest", "crtest");
		client.run();
		
		queue.add(privateWrongMsg);
		user = new User("crtest", "crtest");
		client.run();
		
		queue.add(correctCreateGroupMessage);
		user = new User("crtest", "crtest");
		client.run();

		queue.add(correctCreateGroupMessage);
		user = new User("crtest", "crtest");
		client.run();

		queue.add(groupMsg);
		user = new User("crtest", "crtest");
		client.run();
		
		queue.add(wrongGroupMsg);
		user = new User("crtest", "crtest");
		client.run();
		
		queue.add(wrongGroupMsg2);
		user = new User("crtest", "crtest");
		client.run();
		
		queue.add(correctLoginMessage);
		client.run();

		// Update password
		queue.add(userUpdateWrongPasswordMessage);
		client.run();
		queue.add(userUpdateMessage);
		client.run();

		// Add to Group
		queue.add(userAddToWrongGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		queue.add(userAddToGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		
		//Exit Group

		queue.add(userExitWrongGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		queue.add(userExitGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		queue.add(userExitGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		//Group Delete
		queue.add(groupDeleteMessage);
		user = new User("crtest", "crtest");
		client.run();

		queue.add(groupDeleteMessage);
		client.run();

		queue.add(userDeleteWrongPasswordMessage);
		user = new User("crtest", "crtest");
		client.run();

		queue.add(userDeleteMessage);
		user = new User("crtest", "crtest");
		client.run();

		queue.add(userDeleteMessage);
		user = new User("crtest", "crtest");
		client.run();
		queue.add(userDeleteMessage2);
		user = new User("crtest2", "crtest");
		client.run();
		queue.add(correctLoginMessage);
		client.run();
		
		 assertTrue(client.isInitialized());
		 queue.add(terminate);
		 Assertions.assertThrows(NullPointerException.class, () -> {
			client.run();
		});

	}

	/*
	 * Test to check multiple public methods
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void testPublicMethods() throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Message msg = Message.makeBroadcastMessage("Test", "How are you?");
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();
		client = new ClientRunnable(sChannel);
		Class cls = client.getClass();
		Method setUserName = cls.getDeclaredMethod("setUserName", String.class);
		setUserName.setAccessible(true);
		String userName = "NewUser";
		setUserName.invoke(client, userName);
		client.enqueueMessage(msg);
		assertTrue(userName.equalsIgnoreCase(client.getName()));
		assertFalse(client.isInitialized());
	}

	/*
	 * Test to check when the GuestUserId method
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test
	public void testGetUserId() throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		Message msg = Message.makeBroadcastMessage("Test", "How are you?");
		SocketNB socket = new SocketNB("127.0.0.1", 4545);
		SocketChannel sChannel;
		sChannel = socket.getSocket();
		client = new ClientRunnable(sChannel);
		assertEquals(0, client.getUserId());
	}
}