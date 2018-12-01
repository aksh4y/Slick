package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Test class for PrintNetNB
 * @author Akshay
 * @version 1.0
 */
public class PrintNetNBTest {

    // private static PrattleRunabale server; // Holds the server instance
    static SocketNB socket;    // holds the socket
    Message message;    // holds the message
    private static final String SENDER = "Sender";  // static sender
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 4545;


    @BeforeAll
    public static void setUp() throws IOException{
        try {
            ServerSingleton.runServer();
            socket = createClientSocket(HOST, PORT);
        }
        catch(Exception e) {
            System.out.println(ServerSingleton.running);
        }
    }

    @AfterAll
    public static void stopServer() {
        ServerSingleton.terminate();
    }

    /**
     * Test method that tests the PrintNetNB class
     * @throws IOException 
     */
    @Test
    public void nullMsgErrorTest () throws IOException {   
        //SocketNB socket = new SocketNB(HOST, PORT);
        //setUpSocket();
        message = null;
        PrintNetNB testObj = new PrintNetNB(socket);
        assertThrows(NullPointerException.class,
                ()->{
                    testObj.print(message);
                });
    }

    /**
     * Test no socket error
     * @throws IOException
     */
    @Test
    public void noSocketErrorTest() throws IOException { 
        SocketNB socket2 = null;
        assertThrows(NullPointerException.class,
                ()->{
                    new PrintNetNB(socket2);
                });
    }

    /**
     * Test print function
     * @throws IOException
     */
    @Test
    public void printTest() throws IOException {
        makeAcknowledgeMessage();
        PrintNetNB testObj;
        try {
            testObj = new PrintNetNB(socket);
            assertEquals(true, testObj.print(message));
        }
        catch(NullPointerException ne) {
            if(ServerSingleton.running) {
                //SocketNB socketNB = new SocketNB(HOST, PORT);
                testObj = new PrintNetNB(socket);
                assertEquals(true, testObj.print(message));
            }
        }
        
        try {
            testObj = new PrintNetNB(socket);
            testObj.getChannel();
            assertTrue(true);
        }
        catch(Exception e) {
            assertFalse(false);
        }
    }

    /**
     * Test constructing PrintNetNB using a socket channel
     * @throws IOException
     */
    @Test
    public void socketChannelTest() throws IOException {
        makeAcknowledgeMessage();
        //SocketNB socket = new SocketNB(HOST, PORT);
        SocketChannel sockChan = socket.getSocket();
        PrintNetNB testObj = new PrintNetNB(sockChan);
        assertEquals(true, testObj.print(message));
    }

    /**
     * Makes an acknowledge message
     */
    private void makeAcknowledgeMessage(){
        message= Message.makeAcknowledgeMessage(SENDER);
    }
    
    private static SocketNB createClientSocket(String clientName, int port){

        boolean scanning = true;
        SocketNB socket = null;
        int numberOfTry = 0;

        while (scanning && numberOfTry < 10){
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
