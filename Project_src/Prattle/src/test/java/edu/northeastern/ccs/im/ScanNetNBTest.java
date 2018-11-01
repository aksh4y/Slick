package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

/**
 * Class to test ScanNetNB
 * @author Nipun
 * @version 1.0
 */
class ScanNetNBTest {
	private int PORT = 4545;
	private PrattleRunabale server;

	@BeforeClass
	public  void setUp(){
	    System.out.println("Before");
		server = new PrattleRunabale();
		server.start();
	}
	
	@AfterClass
	public void stopServer() {
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
