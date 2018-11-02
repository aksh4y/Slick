package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.MessageFormat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Class to test ScanNetNB
 * 
 * @author Nipun
 * @version 1.0
 */
class ScanNetNBTest {
	private int PORT = 4545;
	private String HOST = "127.0.0.1";
	private static PrattleRunabale server;
	private static final int BUFFER_SIZE = 64 * 1024;
	private static final String CHARSET_NAME = "us-ascii";
	private static final int MIN_MESSAGE_LENGTH = 7;
	private static final int HANDLE_LENGTH = 3;

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
	 * Test method that tests the ScanNetNB class
	 * 
	 * @throws IOException
	 */
	@Test
	public void hasNextFail() throws IOException {
		SocketNB socketNB = new SocketNB(HOST, PORT);
		ScanNetNB scanNetNB = new ScanNetNB(socketNB);

		assertFalse(scanNetNB.hasNextMessage());
	}

	/*
	 * Test NullPointer Error
	 */
	@Test()
	public void NullPointerTest() {
		SocketChannel sc = null;
		Assertions.assertThrows(NullPointerException.class, () -> {
			new ScanNetNB(sc);
		});
	}

	/*
	 * Test to check close methode
	 * 
	 * @Throws IOException
	 */
	@Test()
	public void IOExceptionTest() throws IOException {
		SocketNB socketNB = new SocketNB(HOST, PORT);
		ScanNetNB scanNetNB = new ScanNetNB(socketNB);
		try {
			scanNetNB.close();
		} catch (Exception e) {
			fail("There was no socket to close");
		}

	}

	/*
	 * Test for testing NextDoesNotExistException from has next method
	 */
	@Test()
	public void NextDoesNotExistExceptionTest() throws IOException {
		SocketNB socketNB = new SocketNB(HOST, PORT);
		ScanNetNB scanNetNB = new ScanNetNB(socketNB);
		Assertions.assertThrows(NextDoesNotExistException.class, () -> {
			scanNetNB.nextMessage();
		});

	}

	/*
	 * Test to check readArgumentMethod
	 * 
	 * @Throws IOException, NoSuchMethodException, SecurityException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException
	 */
	@Test()
	public void ReadArgumentsTest() throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socketNB = new SocketNB(HOST, PORT);
		ScanNetNB scanNetNB = new ScanNetNB(socketNB);
		Class cls = scanNetNB.getClass();
		Method readArguments = cls.getDeclaredMethod("readArgument", CharBuffer.class);
		readArguments.setAccessible(true);
		Message msg = Message.makeBroadcastMessage("TestUser", "Hey");
		String str = msg.toString();
		ByteBuffer wrapper = ByteBuffer.wrap(str.getBytes());
		Charset charset = Charset.forName(CHARSET_NAME);
		CharsetDecoder decoder = charset.newDecoder();
		// Convert the buffer to a format that we can actually use.
		CharBuffer charBuffer = decoder.decode(wrapper);
		// get rid of any extra whitespace at the beginning
		// Start scanning the buffer for any and all messages.
		int start = 0;

		// First read in the handle
		final String handle = charBuffer.subSequence(0, HANDLE_LENGTH).toString();
		// Skip past the handle
		charBuffer.position(start + HANDLE_LENGTH + 1);
		// Read the first argument containing the sender's name
		String sender = (String) readArguments.invoke(scanNetNB, charBuffer);
		assertTrue(sender.equalsIgnoreCase("TestUser"));
	}

}
