package edu.northeastern.ccs.im;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        SocketNB socket = new SocketNB("127.0.0.1", 4545);
        ServerSocketChannel channel;
        channel = ServerSocketChannel.open();
        try {
            SocketChannel sChannel = channel.accept();
            client = new ClientRunnable(sChannel);            
            client.run();
            client.terminateClient();
        }
        catch(Exception e) {

        }
    }
}
