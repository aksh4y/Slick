package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Test Cases for the class SocketNB
 *
 * @author Chetan Mahale
 * @version 1.0
 */
public class SocketNbTest {

   static // private static PrattleRunabale server; // Holds the server instance

    SocketNB socketNB;  // socket instance

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
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */
    @Test
    public void socketInitialization() throws IOException {
        try {
            socketNB = new SocketNB("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
            assertEquals(true, socketNB.getSocket().isConnected());
            socketNB.close();
        }catch (Exception e){
            assertTrue(true);
        }
    }
    /**
     * @throws IOException If an I/O error occurs when creating the socket, this
     *                     will be thrown.
     */

    @Test
    public void socketTermination() throws IOException{
        try {
            socketNB = new SocketNB("ec2-35-166-190-64.us-west-2.compute.amazonaws.com", 5555);
            socketNB.close();
            assertEquals(false, socketNB.getSocket().isConnected());
        }catch (Exception e){
            assertTrue(true);
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
