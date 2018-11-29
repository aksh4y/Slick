package edu.northeastern.ccs.im;

import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * The type Test message.
 * @author petermasky
 * @version 1
 */
public class TestMessage {
    private final static Logger LOGGER =
            Logger.getLogger(Logger.class.getName());
    /**
     * The Message.
     */
    Message message;
    private static final String SENDER = "Sender";
    private static final String MY_MESSAGE = "my_message_goes_here";

    /**
     * Make quit message.
     */
    @Test
    public void makeQuitMessage(){
        message= message.makeQuitMessage("myName");
        message.toString();
        assertTrue(message.getName().equals("myName"));
    }

    /**
     * Make no acknowledge message.
     */
    @Test
    public void makeNoAcknowledgeMessage(){
        message= message.makeNoAcknowledgeMessage();
        assertNull( message.getName());
        assertNull( message.getText());
        assertEquals(Message.MessageType.NO_ACKNOWLEDGE, message.getMessageType());
    }

    /**
     * Make acknowledge message.
     */
    @Test
    public void makeAcknowledgeMessage(){
        message= message.makeAcknowledgeMessage(SENDER);
        assertEquals( message.getName(),SENDER);
        assertNull( message.getText());
        assertEquals(Message.MessageType.ACKNOWLEDGE, message.getMessageType());
    }

    /**
     * Make simple login message.
     */
    @Test
    public void makeSimpleLoginMessage(){
        message= message.makeSimpleLoginMessage(SENDER);
        assertEquals( message.getName(),SENDER);
        assertNull( message.getText());
        assertEquals(Message.MessageType.HELLO, message.getMessageType());
    }

    /**
     * Is acknowledge.
     */
    @Test
    public void isAcknowledge(){

        assertTrue(message.makeAcknowledgeMessage(SENDER).isAcknowledge());
        assertFalse(message.makeAcknowledgeMessage(SENDER).isBroadcastMessage());
    }

    /**
     * Is broadcast message.
     */
    @Test
    public void isBroadcastMessage(){

        assertTrue(message.makeBroadcastMessage(SENDER,MY_MESSAGE).isBroadcastMessage());
        assertFalse(message.makeBroadcastMessage(SENDER,MY_MESSAGE).isAcknowledge());
        message.makeBroadcastMessage(SENDER,MY_MESSAGE).toString();
    }

    /**
     * Is display message.
     */
    @Test
    public void isDisplayMessage(){
        assertTrue(message.makeBroadcastMessage(SENDER,MY_MESSAGE).isDisplayMessage());
        assertFalse(message.makeBroadcastMessage(SENDER,MY_MESSAGE).isInitialization());
    }

    /**
     * Is initialization.
     */
    @Test
    public void isInitialization(){
        assertTrue(message.makeSimpleLoginMessage(SENDER).isInitialization());
        assertFalse(message.makeSimpleLoginMessage(SENDER).isDisplayMessage());
    }

    /**
     * Terminate.
     */
    @Test
    public void terminate(){
        assertTrue(message.makeQuitMessage(SENDER).terminate());
        assertFalse(message.makeSimpleLoginMessage(SENDER).terminate());
    }

    /**
     * Make different kind of messages.
     */
    @Test
    public void makeMessage(){
        message = Message.makeMessage(Message.MessageType.QUIT.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.terminate());

        message = Message.makeMessage(Message.MessageType.HELLO.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.HELLO.toString()));

        message = Message.makeMessage(Message.MessageType.BROADCAST.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isBroadcastMessage());

        message = Message.makeMessage(Message.MessageType.ACKNOWLEDGE.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isAcknowledge());

        message = Message.makeMessage(Message.MessageType.NO_ACKNOWLEDGE.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.NO_ACKNOWLEDGE.toString()));

        message = Message.makeHelloMessage(MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.HELLO.toString()));

        message = Message.makeMessage(MY_MESSAGE,SENDER,MY_MESSAGE);
        assertNull(message);

        message = Message.makeMessage(Message.MessageType.HELLO.toString(),null,MY_MESSAGE);
        message.toString();

        message = Message.makeMessage(Message.MessageType.LOGIN_USER.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isUserLogin());

        message = Message.makeMessage(Message.MessageType.LOGIN_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.LOGIN_SUCCESS.toString()));

        message = Message.makeMessage(Message.MessageType.LOGIN_FAIL.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.LOGIN_FAIL.toString()));

        message = Message.makeMessage(Message.MessageType.CREATE_USER.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isUserCreate());

        message = Message.makeMessage(Message.MessageType.CREATE_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.CREATE_SUCCESS.toString()));

        message = Message.makeMessage(Message.MessageType.CREATE_FAIL.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.CREATE_FAIL.toString()));

        message = Message.makeMessage(Message.MessageType.USER_EXIST.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.USER_EXIST.toString()));

        message = Message.makeMessage(Message.MessageType.CREATE_GROUP.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isCreateGroup());

        message = Message.makeMessage(Message.MessageType.GROUP_CREATE_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP_CREATE_SUCCESS.toString()));

        message = Message.makeMessage(Message.MessageType.GROUP_CREATE_FAIL.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP_CREATE_FAIL.toString()));

        message = Message.makeMessage(Message.MessageType.ADD_TO_GROUP.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isAddToGroup());

        message = Message.makeMessage(Message.MessageType.GROUP_NOT_EXIST.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP_NOT_EXIST.toString()));

        message = Message.makeMessage(Message.MessageType.GROUP_ADD_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP_ADD_SUCCESS.toString()));

        message = Message.makeMessage(Message.MessageType.GROUP_ADD_FAIL.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP_ADD_FAIL.toString()));

        message = Message.makeMessage(Message.MessageType.EXIT_FROM_GROUP.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isGroupExit());

        message = Message.makeMessage(Message.MessageType.DELETE_GROUP.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isGroupDelete());

        message = Message.makeMessage(Message.MessageType.DELETE_USER.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isUserDelete());

        message = Message.makeMessage(Message.MessageType.SUCCESS_MESSAGE.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.SUCCESS_MESSAGE.toString()));

        message = Message.makeMessage(Message.MessageType.DELETE_USER_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.DELETE_USER_SUCCESS.toString()));

        message = Message.makeMessage(Message.MessageType.USER_WRONG_PASSWORD.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.USER_WRONG_PASSWORD.toString()));

        message = Message.makeMessage(Message.MessageType.FAIL_MESSAGE.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.FAIL_MESSAGE.toString()));

        message = Message.makeMessage(Message.MessageType.UPDATE_USER.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isUserUpdate());

        message = Message.makeMessage(Message.MessageType.HISTORY_MESSAGE.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.HISTORY_MESSAGE.toString()));

        message = Message.makeMessage(Message.MessageType.GROUP_EXIST.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP_EXIST.toString()));

        message = Message.makeMessage(Message.MessageType.NOTIFY_PENDING.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.NOTIFY_PENDING.toString()));

        message = Message.makeMessage(Message.MessageType.SUBPOENA_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.SUBPOENA_SUCCESS.toString()));

        message = Message.makeMessage(Message.MessageType.SUBPOENA_NO_PRIVILEGE.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.SUBPOENA_NO_PRIVILEGE.toString()));

        message = Message.makeMessage(Message.MessageType.SUBPOENA_LOGIN.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.isSubpoenaLogin());

        message = Message.makeMessage(Message.MessageType.SUBPOENA_LOGIN_SUCCESS.toString(),SENDER,MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.SUBPOENA_LOGIN_SUCCESS.toString()));
    }

    @Test
    public void testMessageTypes(){
        message = message.makePrivateMessage("Joe", "Joey", "Hi");
        message.setMsgRecipient("Ross");
        assertEquals("Ross", message.getMsgRecipient());
        assertEquals(true, message.isPrivateMessage());
        message = message.makeGroupMessage("John", "MSD","Hello");
        assertEquals(true,message.isGroupMessage());
        message = message.makeMessage(Message.MessageType.PRIVATE.toString(),SENDER,"rec",MY_MESSAGE);
        assertEquals(42, message.toString().length());
        message = message.makeMessage(Message.MessageType.GROUP.toString(),SENDER,"rec",MY_MESSAGE);
        assertTrue(message.getMessageType().toString().equals(Message.MessageType.GROUP.toString()));
        message = message.makeMessage(Message.MessageType.MIME.toString(),SENDER,"rec",MY_MESSAGE);
        assertTrue(message.isMIME());
        message = message.makeMessage(Message.MessageType.RECALL.toString(),SENDER,"rec",MY_MESSAGE);
        assertTrue(message.isRecallMessage());
        message = message.makeMessage(Message.MessageType.SEARCH.toString(),SENDER,"rec",MY_MESSAGE);
        assertTrue(message.isSearchMessage());
        message = message.makeMessage(Message.MessageType.GROUP_SUBPOENA_CREATE.toString(),SENDER,"rec",MY_MESSAGE);
        assertTrue(message.isGroupSubpoena());
        message = message.makeMessage(Message.MessageType.USER_SUBPOENA_CREATE.toString(),SENDER,"rec",MY_MESSAGE);
        assertTrue(message.isUserSubpoena());
    }
}
