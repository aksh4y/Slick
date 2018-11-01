package edu.northeastern.ccs.im;

import edu.northeastern.ccs.im.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMessage {
    /*private final static Logger LOGGER =
            Logger.getLogger(Logger.class.getName());*/
    Message message;
    private static final String SENDER = "Sender";
    private static final String MY_MESSAGE = "my_message_goes_here";
    //    Message instanceMessage = new Message("","","");
    @Test
    public void makeQuitMessage(){
        message= message.makeQuitMessage("myName");
       // LOGGER.log(Level.INFO, message.toString());
        assertTrue(message.getName().equals("myName"));
    }
    @Test
    public void makeNoAcknowledgeMessage(){
        message= message.makeNoAcknowledgeMessage();
        assertNull( message.getName());
        assertNull( message.getText());
        assertEquals(Message.MessageType.NO_ACKNOWLEDGE, message.getMessageType());
    }

    @Test
    public void makeAcknowledgeMessage(){
        message= message.makeAcknowledgeMessage(SENDER);
        assertEquals( message.getName(),SENDER);
        assertNull( message.getText());
        assertEquals(Message.MessageType.ACKNOWLEDGE, message.getMessageType());
    }

    @Test
    public void makeSimpleLoginMessage(){
        message= message.makeSimpleLoginMessage(SENDER);
        assertEquals( message.getName(),SENDER);
        assertNull( message.getText());
        assertEquals(Message.MessageType.HELLO, message.getMessageType());
    }

    @Test
    public void isAcknowledge(){
        assertTrue(message.makeAcknowledgeMessage(SENDER).isAcknowledge());
    }
    @Test
    public void isBroadcastMessage(){

        assertTrue(message.makeBroadcastMessage(SENDER,MY_MESSAGE).isBroadcastMessage());
        message.makeBroadcastMessage(SENDER,MY_MESSAGE).toString();
    }
    @Test
    public void isDisplayMessage(){
        assertTrue(message.makeBroadcastMessage(SENDER,MY_MESSAGE).isDisplayMessage());
    }
    @Test
    public void isInitialization(){
        assertTrue(message.makeSimpleLoginMessage(SENDER).isInitialization());
    }
    @Test
    public void terminate(){
        assertTrue(message.makeQuitMessage(SENDER).terminate());
    }
}
