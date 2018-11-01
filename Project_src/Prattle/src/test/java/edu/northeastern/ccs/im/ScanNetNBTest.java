package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Class to test ScanNetNB
 * @author Nipun
 * @version 1.0
 */
class ScanNetNBTest {
	private int PORT = 4545;
	private static PrattleRunabale server;

	@BeforeAll
	public static void setUp(){
	    System.out.println("Before");
		server = new PrattleRunabale();
		server.start();
	}
	
	@AfterAll
	public static void stopServer() {
		server.terminate();
	}
	@Test
	void test() throws IOException {
//		ServerSocketChannel serverSocket = ServerSocketChannel.open();
//		serverSocket.configureBlocking(false);
//		Selector selector = SelectorProvider.provider().openSelector();
//		// Register to receive any incoming connection messages.
//		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
//		SocketChannel socket = serverSocket.accept();
//		ScanNetNB scanNetNB = new ScanNetNB(socket);
//		assertTrue(true);
		SocketNB socketNB = new SocketNB("127.0.0.1", 4545);
		ScanNetNB scanNetNB = new ScanNetNB(socketNB);
		
		assertTrue(socketNB.getSocket().isConnected());
	}
	@Test
	void test1() throws IOException {
//		ServerSocketChannel serverSocket = ServerSocketChannel.open();
//		serverSocket.configureBlocking(false);
//		Selector selector = SelectorProvider.provider().openSelector();
//		// Register to receive any incoming connection messages.
//		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
//		SocketChannel socket = serverSocket.accept();
//		ScanNetNB scanNetNB = new ScanNetNB(socket);
//		assertTrue(true);
		SocketNB socketNB = new SocketNB("127.0.0.1", 4545);
		ScanNetNB scanNetNB = new ScanNetNB(socketNB);
		
		assertTrue(socketNB.getSocket().isConnected());
	}

}
