package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.SocketNB;
import edu.northeastern.ccs.im.server.ServerConstants;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Chetan Mahale
 * @version 1.0
 */
public class ServerConstantsTest {


    /** Command to say hello. */
    private static final String HELLO_COMMAND = "Hello";

    /** Command to ask about how things are going. */
    private static final String QUERY_COMMAND = "How are you?";

    /** Command that showing the professor is hip (or is that hep?). */
    private static final String COOL_COMMAND = "WTF";

    /** Command for impatient users */
    private static final String IMPATIENT_COMMAND = "What time is it Mr. Fox?";

    /** Message to find the date. */
    private static final String DATE_COMMAND = "What is the date?";

    /** Message to find the time. */
    private static final String TIME_COMMAND = "What time is it?";

    /** Random string to test Random input */
    private static final String MY_MESSAGE = "my_message_goes_here"; // static message


    @Test
    public void initializationTest() {
        ServerConstants serverConstants = new ServerConstants();
        assertEquals(true, serverConstants instanceof ServerConstants);
    }

    @Test
    public void helloMessageTest() {
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(HELLO_COMMAND);
        assertEquals(2, messages.size());
    }
    @Test
    public void queryMessageTest() {
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(QUERY_COMMAND);
        assertEquals(2, messages.size());
    }

    @Test
    public void coolMessageTest() {
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(COOL_COMMAND);
        assertEquals(1, messages.size());
    }

    @Test
    public void impatientMessageCommandTest() {
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(IMPATIENT_COMMAND);
        assertEquals(2, messages.size());
    }

    @Test
    public void dateMessageTest() {
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(DATE_COMMAND);
        assertEquals(1, messages.size());
    }

    @Test
    public void timeMessageTest(){
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(TIME_COMMAND);
        assertEquals(1, messages.size());
    }

    @Test
    public void randomMessageTest(){
        List<Message> messages;
        messages = ServerConstants.getBroadcastResponses(MY_MESSAGE);
        assertNull(messages);
    }
}
