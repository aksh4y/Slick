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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.ScanNetNB;
import edu.northeastern.ccs.im.SocketNB;

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

	// @Test
	// public void testNameSetup() throws IOException {
	// SocketNB socket = new SocketNB("127.0.0.1", 4545);
	// SocketChannel sChannel;
	// sChannel = socket.getSocket();
	// try {
	// client = new ClientRunnable(sChannel);
	// client.setName("Test");
	// assertEquals("Test", client.getName());
	// assertEquals(true, client.isInitialized());
	// }
	// catch(Exception e) {
	// System.out.println(e.getMessage());
	// }
	// }

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
	}

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
		client.setName("Test");
		Class cls = client.getClass();
		Field input = cls.getDeclaredField("input");
		input.setAccessible(true);
		ScanNetNB scanNetNB = (ScanNetNB) input.get(client);
		Class scanNet = scanNetNB.getClass();
		Field messages = scanNet.getDeclaredField("messages");
		messages.setAccessible(true);
		Queue<Message> queue = (Queue<Message>) messages.get(scanNetNB);
		queue.add(nonSpeacialBroadMsg);
		Method checkForInitialization = cls.getDeclaredMethod("checkForInitialization");
		checkForInitialization.setAccessible(true);
		checkForInitialization.invoke(client);
		// //Make a broadcast
		// Method broadcastMessageIsSpecial =
		// cls.getDeclaredMethod("broadcastMessageIsSpecial", Message.class);
		// broadcastMessageIsSpecial.setAccessible(true);
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
		queue.add(msg);
		queue.add(terminate);
		checkForInitialization.invoke(client);
		client.run();
		Field specialResponse = cls.getDeclaredField("specialResponse");
		Queue<Message> sResps = (Queue<Message>) specialResponse.get(client);
		sResps.add(msg);
		Field waitingList = cls.getDeclaredField("specialResponse");
		Queue<Message> wait = (Queue<Message>) waitingList.get(client);
		wait.add(msg);
		queue.add(msg);
		checkForInitialization.invoke(client);
		client.run();
	}

	// @Test
	// public void TestForRunIntialized2() throws IOException, NoSuchFieldException,
	// SecurityException,
	// IllegalArgumentException, IllegalAccessException, NoSuchMethodException,
	// InvocationTargetException {
	// SocketNB socket = new SocketNB("127.0.0.1", 4545);
	// SocketChannel sChannel;
	// sChannel = socket.getSocket();
	// Message msg = Message.makeBroadcastMessage("Test", "How are you?");
	// Message nonSpeacialBroadMsg = Message.makeBroadcastMessage("Test", null);
	// client = new ClientRunnable(sChannel);
	// client.setName("Test");
	// Class cls = client.getClass();
	// Field input = cls.getDeclaredField("input");
	// input.setAccessible(true);
	// ScanNetNB scanNetNB = (ScanNetNB) input.get(client);
	// Class scanNet = scanNetNB.getClass();
	// Field messages = scanNet.getDeclaredField("messages");
	// messages.setAccessible(true);
	// Queue<Message> queue = (Queue<Message>) messages.get(scanNetNB);
	// queue.add(msg);
	// queue.add(nonSpeacialBroadMsg);
	// Method checkForInitialization =
	// cls.getDeclaredMethod("checkForInitialization");
	// checkForInitialization.setAccessible(true);
	// checkForInitialization.invoke(client);
	//// //Make a broadcast
	//// Method broadcastMessageIsSpecial =
	// cls.getDeclaredMethod("broadcastMessageIsSpecial", Message.class);
	//// broadcastMessageIsSpecial.setAccessible(true);
	// client.run();
	//
	// }

	// @Test
	// public void TestForRunIntialized3() throws IOException, NoSuchFieldException,
	// SecurityException,
	// IllegalArgumentException, IllegalAccessException, NoSuchMethodException,
	// InvocationTargetException {
	// SocketNB socket = new SocketNB("127.0.0.1", 4545);
	// SocketChannel sChannel;
	// sChannel = socket.getSocket();
	// Message msg = Message.makeBroadcastMessage("Test", "How are you?");
	// Message nonSpeacialBroadMsg = Message.makeBroadcastMessage("Test", "kk");
	// client = new ClientRunnable(sChannel);
	// client.setName("Test");
	// Class cls = client.getClass();
	// Field input = cls.getDeclaredField("input");
	// input.setAccessible(true);
	// ScanNetNB scanNetNB = (ScanNetNB) input.get(client);
	// Class scanNet = scanNetNB.getClass();
	// Field messages = scanNet.getDeclaredField("messages");
	// messages.setAccessible(true);
	// Queue<Message> queue = (Queue<Message>) messages.get(scanNetNB);
	// queue.add(msg);
	// queue.add(nonSpeacialBroadMsg);
	// Method checkForInitialization =
	// cls.getDeclaredMethod("checkForInitialization");
	// checkForInitialization.setAccessible(true);
	// checkForInitialization.invoke(client);
	//// //Make a broadcast
	//// Method broadcastMessageIsSpecial =
	// cls.getDeclaredMethod("broadcastMessageIsSpecial", Message.class);
	//// broadcastMessageIsSpecial.setAccessible(true);
	// client.run();
	//
	// }
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