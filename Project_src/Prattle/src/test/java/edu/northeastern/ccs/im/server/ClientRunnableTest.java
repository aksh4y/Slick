package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.SocketNB;
import edu.northeastern.ccs.im.server.ClientRunnable;

public class ClientRunnableTest {

    ClientRunnable client;

    static PrattleRunabale server;
    @BeforeAll
    public static void setUp(){
        server = new PrattleRunabale();
        server.start();
    }

    @AfterAll
    public static void stopServer() {
        server.terminate();
    }

    @Test
    public void testNameSetup() throws IOException {
        SocketNB socket = new SocketNB("127.0.0.1", 4545);
        ServerSocketChannel channel;
        channel = ServerSocketChannel.open();
        try {
            SocketChannel sChannel = channel.accept();
            client = new ClientRunnable(sChannel);            
            client.setName("Test");
            assertEquals("Test", client.getName());
            assertEquals(true, client.isInitialized());
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void checkInitialization() throws IOException {
        System.out.println("init");
        /*SocketNB socket = new SocketNB("127.0.0.1", 4545);
        ServerSocketChannel serverSocket;
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        //serverSocket.socket().bind(new InetSocketAddress(ServerConstants.PORT));
        Selector selector = SelectorProvider.provider().openSelector();
        // Register to receive any incoming connection messages.
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        // Create our pool of threads on which we will execute.
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(20);*/
        ServerSocketChannel serverSocket = null;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.socket().bind(new InetSocketAddress(ServerConstants.PORT));
            // Create the Selector with which our channel is registered.
            Selector selector = SelectorProvider.provider().openSelector();
            // Register to receive any incoming connection messages.
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            // Create our pool of threads on which we will execute.
            ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(20);

            //serverSocket = ServerSocketChannel.open();
            //try {
            SocketChannel sChannel = serverSocket.accept();
            client = new ClientRunnable(sChannel);            
            client.run();
            assertEquals(true, client instanceof ClientRunnable);
            client.terminateClient();
        }
        catch(Exception e) {

        }
    }
}
