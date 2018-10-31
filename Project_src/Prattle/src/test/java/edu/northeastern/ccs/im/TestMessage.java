package edu.northeastern.ccs.im;

import edu.northeastern.ccs.im.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMessage {

    Message message;
    private static final String SENDER = "Sender";
//    Message instanceMessage = new Message("","","");
    @Test
    public void makeQuitMessage(){
        message= message.makeQuitMessage("myName");

        assertTrue(message.getName().equals("myName"));


        assert(true);
    }
    @Test
    public void makeNoAcknowledgeMessage(){
        message= message.makeNoAcknowledgeMessage();
        assertNull( message.getName());
        assertNull( message.getText());
    }

    @Test
    public void makeAcknowledgeMessage(){
        message= message.makeAcknowledgeMessage(SENDER);
        assertEquals( message.getName(),SENDER);
        assertNull( message.getText());
    }

    @Test
    public void makeSimpleLoginMessage(){
        message= message.makeSimpleLoginMessage(SENDER);
        assertEquals( message.getName(),SENDER);
        assertNull( message.getText());
    }

    @Test
    public void isAcknowledge(){
//        message.isAcknowledge(message.makeAcknowledgeMessage(SENDER));
    }
}
