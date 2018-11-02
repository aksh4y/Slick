package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.SocketNB;

/**
 * Tests for ClientRunnable class
 * @author Akshay
 *
 */
public class ClientRunnableTest {

    ClientRunnable client = null;
    ServerSocketChannel serverSocket;
    Message msg = null;

    static PrattleRunabale server;

    @BeforeAll
    public static void setUp() throws IOException{
        server = new PrattleRunabale();
        server.start();
    }

    @AfterAll
    public static void stopServer() {
        server.terminate();
    }

    /**
     * Check client initialization
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
            }
            catch(Exception e) {

            }
        }
        catch(Exception e) {}
    }

    @Test
    public void testClientRunnable() throws IOException {
        SocketNB socket = new SocketNB("127.0.0.1", 4545);
        client = new ClientRunnable(socket.getSocket());

        msg= msg.makeAcknowledgeMessage("AKI");

        Message message = Message.makeBroadcastMessage("THIS_GUY","text");
        client.checkForInitialization();
        assertEquals(false, client.broadcastMessageIsSpecial(msg));
        client.checkForInitialization();

        assertEquals(null, client.getName());
        client.setFuture(null);
        
        assertEquals(true, client.sendMessage(msg));

        assertEquals(true, client.sendMessage(message));

        client.enqueueMessage(msg);

        client.enqueueMessage(message);

        assertEquals(false, client.broadcastMessageIsSpecial(msg));
        assertEquals(false, client.broadcastMessageIsSpecial(message));

        assertEquals(0, client.getUserId());

        client.handleSpecial(msg);

        client.handleSpecial(message);

        assertEquals(false, client.isInitialized());

        assertThrows(NullPointerException.class,
                ()->{
                    client.terminateClient();
                });

    }


}