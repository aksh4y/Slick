package edu.northeastern.ccs.im.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


//import edu.northeastern.ccs.im.MongoDB.Model.User;

public class TestUserService {

    MongoDatabase db = MongoConnection.createConnection();
    GroupServicePrattle groupService = new GroupServicePrattle(db);
    UserServicePrattle userService = new UserServicePrattle(db);

    @Test
    public void checkIfUserExists(){
        assertEquals(true, userService.isUsernameTaken("chetan"));
    }

    @Test
    public void testFindUserByUsername(){
        User user = userService.findUserByUsername("chetan");
        assertEquals("chetan", user.getUsername());
    }

    @Test
    public void testCreateDeleteUser() throws JsonProcessingException {
        User user = userService.createUser("test1","test2");
        assertEquals(true, userService.isUsernameTaken(user.getUsername()));
        assertEquals(true, userService.deleteUser("test1"));
        assertEquals(false, userService.isUsernameTaken("test1"));
        assertNull(userService.createUser("chetan", "passchetan"));
    }

    @Test
    public void testAuthenticateUser(){
        User user = userService.authenticateUser("chetan","test");
        assertEquals("chetan", user.getUsername());
        assertNull(userService.authenticateUser("chetan", "asdfg"));
        assertNull(userService.authenticateUser("asdfg", "Chetan"));
    }

    @Test
    public void testAddRemoveGroupToUser() throws JsonProcessingException {
        User user = userService.createUser("test1", "test1");
        Group group1 = groupService.createGroup("coolgroup");
        Group group2 = groupService.createGroup("hotgroup");
        assertEquals(true, userService.addGroupToUser(user, group1));
        assertEquals(true, userService.removeGroupFromUser(user.getUsername(),group1.getName()));
        userService.addGroupToUser(user,group1);
        userService.addGroupToUser(user,group2);
        assertEquals(false, userService.deleteUser("test1"));
        assertEquals(true, groupService.deleteGroup("coolgroup"));
        assertEquals(true, groupService.deleteGroup("hotgroup"));
    }

    @Test
    public void testUpdateUser() throws JsonProcessingException {

        User user = new User("failTest", "failTest");
        assertEquals(false,userService.updateUser(user, "failTest123"));
        user = userService.createUser("test1", "test1");
        assertEquals(true,userService.updateUser(user, "testing"));
        user = userService.findUserByUsername("test1");
        assertEquals(true, userService.checkPassword("testing",user.getPassword()));
        assertEquals(true, userService.deleteUser("test1"));
        assertEquals(false, userService.updateUser(null, "new"));
        assertEquals(false, userService.deleteUser("testUserSubject"));

    }

    @Test
    public void testCheckPassword(){
        try {
            userService.checkPassword("check",null);
            Assertions.fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try {
            userService.checkPassword("check","$@!#");
            Assertions.fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try {
           assertFalse(userService.checkPassword("check","$2a$12$NJ2.h9oZK7XhSftYXDslGeB6C25r.i8s7Ux8ajwIw3Vw7T7tJzMNa"));
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void deleteUserMoreTest() throws  JsonProcessingException{
        User user = userService.createUser("dUser", "dUser");
        Group group = groupService.createGroup("dUserGroup");
        groupService.addUserToGroup(group,user);
        List<String>listOfGroups = new ArrayList<String>(Arrays.asList("dUserGroup"));
        groupService.removeUserFromGroups(listOfGroups,"dUser");
        assertTrue(userService.deleteUser("dUser"));
        groupService.deleteGroup("dUserGroup");
    }
    @Test
    public void addToMyMessages() throws  JsonProcessingException {
        User user = userService.createUser("dUser", "dUser");
        assertEquals(0,user.getMyMessages().size());
        userService.addToMyMessages(user,"Hi! Test Message");
        user= userService.findUserByUsername("dUser");
        assertEquals(1,user.getMyMessages().size());
        userService.deleteUser("dUser");
    }
    @Test
    public void testAddToMyUnreadMessages() throws JsonProcessingException {
        User user = userService.createUser("newone", "newpass");
        userService.addToUnreadMessages(user, "Hello");
        user = userService.findUserByUsername("newone");
        assertEquals("Hello", user.getMyUnreadMessages().get(0));
        userService.clearUnreadMessages(user);
        user = userService.findUserByUsername("newone");
        assertEquals(0, user.getMyUnreadMessages().size());
        userService.deleteUser("newone");
    }
    @Test
    public void testGetMessages(){
        List<String> smessages = userService.getMessages("sender", "Akshay");
        List<String> rmessages = userService.getMessages("receiver", "Akshay");
        System.out.println(smessages);
        System.out.println(rmessages);
    }
    @Test
    public void testRecallMessages() throws JsonProcessingException {
        User sender = userService.createUser("sender", "senderpass");
        User receiver = userService.createUser("receiver", "receiverpass");
        userService.addToMyMessages(sender,"Private receiver hey");
        userService.addToMyMessages(sender,"Private receiver hey");
        userService.addToMyMessages(receiver,"[Private Msg] sender: hey");
        userService.addToUnreadMessages(receiver,"[Private Msg] sender: hey");
        receiver = userService.findUserByUsername("receiver");
        assertEquals(1, receiver.getMyMessages().size());
        assertEquals(1, receiver.getMyUnreadMessages().size());
        userService.recallFromMessages(receiver,"sender");
        userService.recallFromUnreadMessages(receiver);
        receiver = userService.findUserByUsername("receiver");
        assertEquals("[Private Msg] sender: [Message Deleted]", receiver.getMyMessages().get(0));
        assertEquals("[Private Msg] sender: [Message Deleted]", receiver.getMyUnreadMessages().get(0));
        userService.deleteUser("receiver");
        userService.deleteUser("sender");
    }
    @Test
    public void testLastSentMessage() throws JsonProcessingException {
        User sender = userService.createUser("sender", "senderpass");
        User rec = userService.createUser("rec", "recpass");
        userService.addToMyMessages(sender,"Private rec hey");
        userService.addToMyMessages(rec,"[Private Msg] sender: hey");
        sender = userService.findUserByUsername("sender");
        rec = userService.findUserByUsername("rec");
        assertEquals(1, rec.getMyMessages().size());
        assertEquals(1, sender.getMyMessages().size());
        userService.getLastSentMessage("user","sender","rec");
        sender = userService.findUserByUsername("sender");
        rec = userService.findUserByUsername("rec");
        assertEquals("**Recalled**Private rec hey", sender.getMyMessages().get(0));
        assertEquals("[Private Msg] sender: [Message Deleted]",rec.getMyMessages().get(0));
        userService.addToMyMessages(sender,"Private rec hey ya");
        userService.addToUnreadMessages(rec,"[Private Msg] sender: hey ya");
        userService.getLastSentMessage("user","sender","rec");
        sender = userService.findUserByUsername("sender");
        rec = userService.findUserByUsername("rec");
        assertEquals("**Recalled**Private rec hey ya", sender.getMyMessages().get(1));
        assertEquals("[Private Msg] sender: [Message Deleted]",rec.getMyUnreadMessages().get(0));
        userService.deleteUser("rec");
        userService.deleteUser("sender");
    }
//    @Test
//    public void testLastGroupMessage() throws JsonProcessingException {
//        User sender = userService.createUser("sender", "senderpass");
//        User rec = userService.createUser("rec", "recpass");
//        userService.addToMyMessages(sender,"[sender@sendergroup] hey");
//    }
}
