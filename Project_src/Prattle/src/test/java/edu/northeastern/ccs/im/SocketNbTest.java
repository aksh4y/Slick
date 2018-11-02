package edu.northeastern.ccs.im;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.IOException;
import java.net.BindException;


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
//        server.terminate();
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
        try {
            socket = new SocketNB("127.0.0.1", 4545);
            assertEquals(true, socket.getSocket().isConnected());
            socket.close();
        }catch (Exception e){
            assertThrows(BindException.class, ()->socket = new SocketNB("127.0.0.1", 4545));
        }
    }
    /**
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */

    @Test
    public void socketTermination() throws IOException{
        try {
            socket = new SocketNB("127.0.0.1", 4545);
            socket.close();
            assertEquals(false, socket.getSocket().isConnected());
        }catch (Exception e){
            assertThrows(BindException.class, ()->socket = new SocketNB("127.0.0.1", 4545));
        }
    }
}
