package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Queue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Class to test ScanNetNB
 * 
 * @author Nipun
 * @version 1.0
 */
class ScanNetNBTest {
    private static final int PORT = 4545;
    private static final String HOST = "127.0.0.1";
    //private static PrattleRunabale server;
    private static final int BUFFER_SIZE = 64 * 1024;
    private static final String CHARSET_NAME = "us-ascii";
    private static final int MIN_MESSAGE_LENGTH = 7;
    private static final int HANDLE_LENGTH = 3;
    private static SocketNB socketNB;

    @BeforeAll
    public static void setUp() {
        /*server = new PrattleRunabale();
		server.start();*/
        ServerSingleton.runServer();
        socketNB = createClientSocket(HOST, PORT);
    }

    @AfterAll
    public static void stopServer() {
        try {
            socketNB.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ServerSingleton.terminate();
    }

    /**
     * Test method that tests the ScanNetNB class
     * 
     * @throws IOException
     */
    @Test
    public void hasNextFail() throws IOException {
        //SocketNB socketNB = new SocketNB("127.0.0.200", PORT);
        ScanNetNB scanNetNB = new ScanNetNB(socketNB);

        assertFalse(scanNetNB.hasNextMessage());
        //socketNB.close();
        scanNetNB.close();
    }

    /*
     * Test NullPointer Error
     */
    @Test()
    public void NullPointerTest() {
        SocketChannel sc = null;
        Assertions.assertThrows(NullPointerException.class, () -> {
            new ScanNetNB(sc);
        });

    }

    /*
     * Test to check close methode
     * 
     * @Throws IOException
     */
    @Test()
    public void IOExceptionTest() throws IOException {
        //SocketNB socketNB = new SocketNB("127.0.0.203", PORT);
        ScanNetNB scanNetNB = new ScanNetNB(socketNB);
        //socketNB.close();
        try {
            scanNetNB.close();
        } catch (Exception e) {
            fail("There was no socket to close");
        }

    }

    /*
     * Test for testing NextDoesNotExistException from has next method
     */
    @Test()
    public void NextDoesNotExistExceptionTest() throws IOException {
        //SocketNB socketNB = new SocketNB("127.0.0.204", PORT);
        ScanNetNB scanNetNB = new ScanNetNB(socketNB);
        Assertions.assertThrows(NextDoesNotExistException.class, () -> {
            scanNetNB.nextMessage();
        });
        //socketNB.close();
        scanNetNB.close();
    }

    /*
     * Test to check readArgumentMethod
     * 
     * @Throws IOException, NoSuchMethodException, SecurityException,
     * IllegalAccessException, IllegalArgumentException, InvocationTargetException
     */
    @Test()
    public void ReadArgumentsTest() throws IOException, NoSuchMethodException, SecurityException,
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //SocketNB socketNB = new SocketNB("127.0.0.205", PORT);
        ScanNetNB scanNetNB = new ScanNetNB(socketNB);
        Class cls = scanNetNB.getClass();
        Method readArguments = cls.getDeclaredMethod("readArgument", CharBuffer.class);
        readArguments.setAccessible(true);
        Message msg = Message.makeBroadcastMessage("TestUser", "Hey");
        String str = msg.toString();
        ByteBuffer wrapper = ByteBuffer.wrap(str.getBytes());
        Charset charset = Charset.forName(CHARSET_NAME);
        CharsetDecoder decoder = charset.newDecoder();
        // Convert the buffer to a format that we can actually use.
        CharBuffer charBuffer = decoder.decode(wrapper);
        // get rid of any extra whitespace at the beginning
        // Start scanning the buffer for any and all messages.
        int start = 0;

        // First read in the handle
        final String handle = charBuffer.subSequence(0, HANDLE_LENGTH).toString();
        // Skip past the handle
        charBuffer.position(start + HANDLE_LENGTH + 1);
        // Read the first argument containing the sender's name
        String sender = (String) readArguments.invoke(scanNetNB, charBuffer);
        assertTrue(sender.equalsIgnoreCase("TestUser"));
        //socketNB.close();
        scanNetNB.close();
    }

    /**
     * Has next message test.
     *
     * @throws InterruptedException   the interrupted exception
     * @throws IllegalAccessException the illegal access exception
     * @throws IOException            the io exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws SecurityException      the security exception
     */
    @Test()
    public void hasNextMessageTest()
            throws InterruptedException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
        //SocketNB socketNB = new SocketNB("127.0.0.206", PORT);
        ScanNetNB scanNetNB = new ScanNetNB(socketNB);
        Message msg = Message.makeBroadcastMessage("TestUser", "Hey");
        Class cls = scanNetNB.getClass();
        Field messages = cls.getDeclaredField("messages");
        messages.setAccessible(true);
        Queue<Message> queue = (Queue<Message>) messages.get(scanNetNB);
        queue.add(msg);
        assertTrue(scanNetNB.hasNextMessage());
        assertEquals(msg, scanNetNB.nextMessage());
        //socketNB.close();
        scanNetNB.close();
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
