package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.ScanNetNB;
import edu.northeastern.ccs.im.ServerSingleton;
import edu.northeastern.ccs.im.SocketNB;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.service.SubpoenaServicePrattle;

/**
 * Tests for ClientRunnable class
 * 
 * @author Akshay
 *
 */
public class ClientRunnableTest {

	ClientRunnable client;

	/* static PrattleRunabale server; */
	private MongoDatabase db = MongoConnection.createConnection();
	private SubpoenaServicePrattle subpoenaService = new SubpoenaServicePrattle(db);

	static SocketNB socketNB = null;

	@BeforeAll
	public static void setUp() {
		socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
	}

	@AfterAll
	public static void stopServer() {
		try {
			socketNB.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check client initialization
	 * 
	 * @throws IOException
	 */
	@Test
	public void checkInitialization() throws IOException {
		socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		client = new ClientRunnable(socketNB.getSocket());
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
		socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();

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
		 socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();

		client = new ClientRunnable(sChannel);
		client.setName("Test");
		Class cls = client.getClass();
		Method broadcastMessageIsSpecial = cls.getDeclaredMethod("broadcastMessageIsSpecial", Message.class);
		broadcastMessageIsSpecial.setAccessible(true);
		Message msg = Message.makeBroadcastMessage("test user", "How are you?");
		assertTrue((Boolean) broadcastMessageIsSpecial.invoke(client, msg));
	}

	@Test
	public void testSubpoenaCreate() throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate fromDate = LocalDate.parse("11-20-2019", formatter);
		LocalDate toDate = LocalDate.parse("12-20-2019", formatter);
		Message on = Message.makeParentalControlMessage("ON");
		Message off = Message.makeParentalControlMessage("off");
		client = new ClientRunnable(sChannel);
		Class cls = client.getClass();

		Field userField = cls.getDeclaredField("user");
		userField.setAccessible(true);
		User user = (User) userField.get(client);

		Method handleMsgs = cls.getDeclaredMethod("handleMsgs", Message.class);
		handleMsgs.setAccessible(true);
		Method handleOtherMsgs = cls.getDeclaredMethod("handleOtherMsgs", Message.class);
		handleOtherMsgs.setAccessible(true);

		Method createUserSubpoena = cls.getDeclaredMethod("createUserSubpoena", Message.class, LocalDate.class,
				LocalDate.class, Boolean.class);
		createUserSubpoena.setAccessible(true);

		Method createGroupSubpoena = cls.getDeclaredMethod("createGroupSubpoena", Message.class, LocalDate.class,
				LocalDate.class, Boolean.class);
		createGroupSubpoena.setAccessible(true);

		Message msg1 = (Message) createUserSubpoena.invoke(client,
				Message.makeCreateUserSubpoena("akshay$%$peter", "11-20-2019", "12-20-2019"), fromDate, toDate, true);
		handleMsgs.invoke(client, Message.makeSubpoenaLogin("5c02ea2835b9a1209eac7c1e"));
		subpoenaService.deleteSubpoena(msg1.getName());

		handleMsgs.invoke(client, Message.makeCreateUserMessage("crtest4", "crtest"));

		createUserSubpoena.invoke(client, Message.makeCreateUserSubpoena("akshay$%$allki", "11-20-2019", "12-20-2019"),
				fromDate, toDate, true);
		createUserSubpoena.invoke(client, Message.makeCreateUserSubpoena("akshswway$%$all", "11-20-2019", "12-20-2019"),
				fromDate, toDate, true);
		try {handleMsgs.invoke(client, Message.makeLoginMessage("crtest4", "crtest"));}
		catch(NullPointerException ne) { 
		    // Exists 
		}
		handleMsgs.invoke(client, Message.makeCreateGroupMessage("testCRGroup"));
		handleMsgs.invoke(client, Message.makeLoginMessage("crtest4", "crtest"));
		handleMsgs.invoke(client, Message.makeAddUserToGroup("testcrgroup"));
		Message msg = (Message) createGroupSubpoena.invoke(client,
				Message.makeCreateGroupSubpoena("chetangroup", "11-20-2019", "12-20-2019"), fromDate, toDate, true);
		assertTrue(client.isSubpoena());
		subpoenaService.deleteSubpoena(msg.getName());
		createGroupSubpoena.invoke(client, Message.makeCreateGroupSubpoena("nipungroupss", "11-20-2019", "12-20-2019"),
				fromDate, toDate, true);
		
		handleMsgs.invoke(client, Message.makeCreateUserMessage("crtest5", "crtest"));
		handleOtherMsgs.invoke(client, on);
		handleMsgs.invoke(client, Message.makeAddUserToGroup("testcrgroup"));

		client.getActiveList().remove("crtest5");
		Message privateMsg = Message.makePrivateMessage("crtest4", "crtest5", "fuck test");
		Message groupMsg = Message.makeGroupMessage("crtest4", "testCRGroup", "fuck group");
		Message broad = Message.makeBroadcastMessage("crtest4", "fuck all");

		client.setIP("/192.104.0.0:45435");
		handleMsgs.invoke(client, privateMsg);
		handleMsgs.invoke(client, groupMsg);
		client.setIP("/192.104.1.1:34324");

		handleMsgs.invoke(client, Message.makeLoginMessage("crtest5", "crtest"));
		handleMsgs.invoke(client, Message.makeLoginMessage("crtest4", "crtest"));
		handleMsgs.invoke(client, broad);
		handleMsgs.invoke(client, Message.makeLoginMessage("crtest4", "crtest"));
		handleMsgs.invoke(client, Message.makeDeleteUserMessage("crtest"));
		handleMsgs.invoke(client, Message.makeLoginMessage("crtest5", "crtest"));
		handleMsgs.invoke(client, Message.makeDeleteUserMessage("crtest"));

		handleMsgs.invoke(client, Message.makeDeleteGroupMessage("testcrGroup"));

		client.setName("DUMMYUSER");
		handleMsgs.invoke(client, Message.makeSubpoenaLogin(msg.getName()));
		handleMsgs.invoke(client, Message.makeLoginMessage("nipun", "test"));
		handleOtherMsgs.invoke(client, off);
		handleOtherMsgs.invoke(client, on);
		handleOtherMsgs.invoke(client, on);
		handleOtherMsgs.invoke(client, off);
		
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("all"));
		handleMsgs.invoke(client, Message.makeLoginMessage("admin", "test"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("all"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("debug"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("warn"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("off"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("fatal"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("info"));
		handleOtherMsgs.invoke(client, Message.makeLoggerMessage("all"));
		handleMsgs.invoke(client, Message.makeMIMEMessage("crtest3", "crtest4", "C:\\Users\\Admin\\Desktop\\Bg.jpg"));

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
		 socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();
		Message msg = Message.makeBroadcastMessage("Test", "How are you?");
		Message nullMsg = Message.makeBroadcastMessage(null, "How are you?");
		if (!sChannel.isOpen())
			SocketChannel.open();
		client = new ClientRunnable(sChannel);
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
		 socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();

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
		// socket.close();
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
		 socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();

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
		 socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();
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
		Message userDeleteMessage2 = Message.makeDeleteUserMessage("crtest");
		Message userDeleteWrongPasswordMessage = Message.makeDeleteUserMessage("crtest");
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

		// Exit Group

		queue.add(userExitWrongGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		queue.add(userExitGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		queue.add(userExitGroupMessage);
		user = new User("crtest", "crtest");
		client.run();
		// Group Delete
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
		user = new User("crtest2", "crtest");
		queue.add(userDeleteMessage2);
		client.run();
		queue.add(correctLoginMessage);
		client.run();

		//testSubpoenaCreate();

		assertTrue(client.isInitialized());
		queue.add(terminate);
		try {
			client.run();
		} catch (Exception e) {
			assertTrue(e == e);
		}
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
		socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();
		client = new ClientRunnable(sChannel);
		Class cls = client.getClass();
		Method setUserName = cls.getDeclaredMethod("setUserName", String.class);
		setUserName.setAccessible(true);
		String userName = "NewUser";
		setUserName.invoke(client, userName);
		client.enqueueMessage(msg);
		assertTrue(userName.equalsIgnoreCase(client.getName()));
		assertFalse(client.isInitialized());

		Method handleMsgsMethod = cls.getDeclaredMethod("handleMsgs", Message.class);
		handleMsgsMethod.setAccessible(true);
		Method handleOtherMsgsMethod = cls.getDeclaredMethod("handleOtherMsgs", Message.class);
		handleOtherMsgsMethod.setAccessible(true);

		handleMsgsMethod.invoke(client, Message.makeLoginMessage("nipun", "test"));
		handleOtherMsgsMethod.invoke(client, Message.makeCreateGroupSubpoena("petergroup", "11-20-2019", "12-20-2019"));
		// handleMsgsMethod.invoke(client, Message.makeSubpoenaLogin(id));

		handleMsgsMethod.invoke(client, Message.makeLoginMessage("admin", "test"));
		handleOtherMsgsMethod.invoke(client, Message.makeCreateGroupSubpoena("petergroup", "11-20-2021", "12-20-2019"));
		handleOtherMsgsMethod.invoke(client, Message.makeSearchMessage("nipun", "peter", "sender"));
		handleMsgsMethod.invoke(client, Message.makeLoginMessage("peter", "test"));
		handleMsgsMethod.invoke(client, Message.makeRecallMessage("1543550328118", "user", "akshay"));
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
		 socketNB = createClientSocket("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
		SocketChannel sChannel;
		sChannel = socketNB.getSocket();
		if (sChannel.isOpen()) {
			client = new ClientRunnable(sChannel);
			assertEquals(0, client.getUserId());
		} else {
			SocketChannel.open();
			client = new ClientRunnable(sChannel);
			assertEquals(0, client.getUserId());
		}

	}

	private static SocketNB createClientSocket(String clientName, int port) {
		boolean scanning = true;
		SocketNB socket = null;
		int numberOfTry = 0;
		while (scanning && numberOfTry < 10) {
			numberOfTry++;
			try {
				socket = new SocketNB(clientName, port);
				scanning = false;
			} catch (IOException e) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
		return socket;
	}
}