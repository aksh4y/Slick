package edu.northeastern.ccs.im;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Test Cases for the class SocketNB
 *
 * @author Chetan Mahale
 * @version 1.0
 */
public class SocketNbTest {
    
    private static PrattleRunabale server; // Holds the server instance


    SocketNB socket;  // socket instance

    @BeforeAll
    public static void setUp(){
        server = new PrattleRunabale();
        server.start();
    }

    @AfterAll
    public static void stopServer() {
        server.terminate();
    }


    /**
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */
    @Test
    public void socketInitialization() throws IOException {
        socket = new SocketNB("127.0.0.1", 4545);
        assertEquals(true, socket.getSocket().isConnected());
    }
    /**
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */

    @Test
    public void socketTermination() throws IOException{
        socket = new SocketNB("127.0.0.1", 4545);
        socket.close();
        assertEquals(false, socket.getSocket().isConnected());
    }

    /**
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */

    @Test
    public void socketNotConnectibleTest() throws IOException{
        assertThrows(IOException.class,()->socket = new SocketNB("127.0.0.1", 1111));
    }


    /**
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */
    @Test
    public void socketIncorrectInitializationTest() throws IOException{
        try {
            socket = new SocketNB("127.0.0.1", 3306);
            assertEquals(true, socket.getSocket().isConnected());
        }catch (ConnectException e) {
            assertThrows(ConnectException.class, () -> socket = new SocketNB("127.0.0.1", 3306));
        }
    }
}
