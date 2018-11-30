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

/**
 *
 * @author Chetan Mahale
 */
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
        if(userService.findUserByUsername("dUser")!=null){
            assertTrue(userService.deleteUser("dUser"));
        }
        if(groupService.findGroupByName("dUserGroup")!=null){
            groupService.deleteGroup("dUserGroup");
        }
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
        if(userService.findUserByUsername("dUser")!=null){
            assertTrue(userService.deleteUser("dUser"));
        }
        User user = userService.createUser("dUser", "dUser");
        assertEquals(0,user.getMyMessages().size());
        userService.addToMyMessages(user,"Hi! Test Message");
        user= userService.findUserByUsername("dUser");
        assertEquals(1,user.getMyMessages().size());
        userService.deleteUser("dUser");
    }
    @Test
    public void testAddToMyUnreadMessages() throws JsonProcessingException {
        if(userService.findUserByUsername("newone")!=null){
            assertTrue(userService.deleteUser("newone"));
        }
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
    public void testGetMessages() throws JsonProcessingException {
        if (userService.findUserByUsername("newestr")!=null){
            assertTrue(userService.deleteUser("newestr"));
        }
        User newestReceiver = userService.createUser("newestr","newestr");
        userService.addToMyMessages(newestReceiver,"[Private Msg] newest: hey wassup");
        userService.addToMyMessages(newestReceiver,"PRIVATE newest: all good man");
        List<String> smessages = userService.getMessages("sender", "newest", "newestr");
        List<String> rmessages = userService.getMessages("receiver", "newest", "newestr");
        assertEquals("[Private Msg] newest: hey wassup",smessages.get(0));
        assertEquals("PRIVATE newest: all good man",rmessages.get(0));
        assertTrue(userService.deleteUser("newestr"));
    }
////    @Test
//    public void testRecallMessages() throws JsonProcessingException {
//        if(userService.findUserByUsername("newsender")!=null){
//            assertTrue(userService.deleteUser("newsender"));
//        }
//        if(userService.findUserByUsername("newreceiver")!=null){
//            assertTrue(userService.deleteUser("newreceiver"));
//        }
//        User newsender = userService.createUser("newsender", "newsenderpass");
//        User newreceiver = userService.createUser("newreceiver", "newreceiverpass");
//        userService.addToMyMessages(newsender,"Private newreceiver hey");
//        userService.addToMyMessages(newsender,"Private newreceiver hey");
//        userService.addToMyMessages(newreceiver,"[Private Msg] newsender: hey");
//        userService.addToUnreadMessages(newreceiver,"[Private Msg] newsender: hey");
//        newreceiver = userService.findUserByUsername("newreceiver");
//        assertEquals(1, newreceiver.getMyMessages().size());
//        assertEquals(1, newreceiver.getMyUnreadMessages().size());
//        userService.recallFromMessages(newreceiver,"newsender");
//        userService.recallFromUnreadMessages(newreceiver, "newsender");
//        newreceiver = userService.findUserByUsername("newreceiver");
//        assertEquals("[Private Msg] newsender: [Message Deleted]", newreceiver.getMyMessages().get(0));
//        assertEquals("[Private Msg] newsender: [Message Deleted]", newreceiver.getMyUnreadMessages().get(0));
//        userService.deleteUser("newreceiver");
//        userService.deleteUser("newsender");
////    }
//    @Test
//    public void testLastSentMessage() throws JsonProcessingException {
//        if(userService.findUserByUsername("newsender")!=null){
//            assertTrue(userService.deleteUser("newsender"));
//        }
//        if(userService.findUserByUsername("rec")!=null){
//            assertTrue(userService.deleteUser("rec"));
//        }
//        User newsender = userService.createUser("newsender", "newsenderpass");
//        User rec = userService.createUser("rec", "recpass");
//        userService.addToMyMessages(newsender,"Private rec hey");
//        userService.addToMyMessages(rec,"[Private Msg] newsender: hey");
//        newsender = userService.findUserByUsername("newsender");
//        rec = userService.findUserByUsername("rec");
//        assertEquals(1, rec.getMyMessages().size());
//        assertEquals(1, newsender.getMyMessages().size());
//        userService.getLastSentMessage("user","newsender","rec");
//        newsender = userService.findUserByUsername("newsender");
//        rec = userService.findUserByUsername("rec");
//        assertEquals("**Recalled**Private rec hey", newsender.getMyMessages().get(0));
//        assertEquals("[Private Msg] newsender: [Message Deleted]",rec.getMyMessages().get(0));
//        userService.addToMyMessages(newsender,"Private rec hey ya");
//        userService.addToUnreadMessages(rec,"[Private Msg] newsender: hey ya");
//        userService.getLastSentMessage("user","newsender","rec");
//        newsender = userService.findUserByUsername("newsender");
//        rec = userService.findUserByUsername("rec");
//        assertEquals("**Recalled**Private rec hey ya", newsender.getMyMessages().get(1));
//        assertEquals("[Private Msg] newsender: [Message Deleted]",rec.getMyUnreadMessages().get(0));
//        userService.deleteUser("rec");
//        userService.deleteUser("newsender");
//    }
//    @Test
//    public void testLastGroupMessage() throws JsonProcessingException {
//        if(userService.findUserByUsername("newsender")!=null){
//            assertTrue(userService.deleteUser("newsender"));
//        }
//        if(userService.findUserByUsername("rec")!=null){
//            assertTrue(userService.deleteUser("rec"));
//        }
//        if(groupService.findGroupByName("newgrouptest")!=null){
//            assertTrue(groupService.deleteGroup("newgrouptest"));
//        }
//        User newsender = userService.createUser("newsender", "newsenderpass");
//        User rec = userService.createUser("rec", "recpass");
//        Group newgrouptest = groupService.createGroup("newgrouptest");
//        newgrouptest.addUserTOGroup(newsender);
//        groupService.addUserToGroup(newgrouptest, newsender);
//        groupService.addUserToGroup(newgrouptest, rec);
//        userService.addToMyMessages(newsender,"GROUP newgrouptest hey everyone");
//        userService.addToMyMessages(rec, "[newsender@newgrouptest] hey everyone");
//        userService.getLastSentMessage("group","newsender","newgrouptest");
//    }

    @Test
    public void testParentalControl() throws JsonProcessingException {
        if(userService.findUserByUsername("newsender")!=null){
            assertTrue(userService.deleteUser("newsender"));
        }
        User user = userService.createUser("newsender", "newpass");
        userService.switchParentalControl("newsender");
        user = userService.findUserByUsername("newsender");
        assertEquals(true, user.getParentalControl());
        userService.switchParentalControl("newsender");
        user = userService.findUserByUsername("newsender");
        assertEquals(false, user.getParentalControl());
        assertFalse(userService.updateMessage("newsender","how are","what are"));
        assertFalse(userService.updateMessage("newsender","what is", "how is"));
        userService.addToMyMessages(user,"how are");
        userService.addToUnreadMessages(user,"what is");
        userService.updateMessage("newsender","how are","what are");
        userService.updateMessage("newsender","what is", "how is");
        user = userService.findUserByUsername("newsender");
        assertEquals("what are",user.getMyMessages().get(0));
        assertEquals("how is", user.getMyUnreadMessages().get(0));
        assertEquals(false, userService.updateMessage("someuserthatshouldnotbeinthedatabase",
                        "anymsg","anyothermsg"));
        assertTrue(userService.deleteUser("newsender"));
    }
}
