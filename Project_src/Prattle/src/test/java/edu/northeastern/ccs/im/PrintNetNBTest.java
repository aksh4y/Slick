package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    //private static PrattleRunabale server; // Holds the server instance
    SocketNB socket;    // holds the socket
    Message message;    // holds the message
    private static final String SENDER = "Sender";  // static sender
    private static final String MY_MESSAGE = "my_message_goes_here"; // static message
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 4545;


    @BeforeAll
    public static void setUp(){
       ServerSingleton.runServer();
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
        setUpSocket();
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
        SocketNB socket = null;
        assertThrows(NullPointerException.class,
                ()->{
                    PrintNetNB testObj = new PrintNetNB(socket);
                });
    }

    /**
     * Test print function
     * @throws IOException
     */
    @Test
    public void printTest() throws IOException {
        setUpSocket();
        makeAcknowledgeMessage();
        PrintNetNB testObj = new PrintNetNB(socket);
        assertEquals(true, testObj.print(message));
    }

    /**
     * Test constructing PrintNetNB using a socket channel
     * @throws IOException
     */
    @Test
    public void socketChannelTest() throws IOException {
        setUpSocket();
        makeAcknowledgeMessage();
        SocketChannel sockChan = socket.getSocket();
        PrintNetNB testObj = new PrintNetNB(sockChan);
        assertEquals(true, testObj.print(message));
    }

    /**
     * Makes an acknowledge message
     */
    private void makeAcknowledgeMessage(){
        message= message.makeAcknowledgeMessage(SENDER);
    }

    /**
     * Sets up a socket
     * @throws IOException
     */
    private void setUpSocket() throws IOException {
        socket = new SocketNB(HOST, PORT);
    }
}
