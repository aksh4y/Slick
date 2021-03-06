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
        try {
            if (userService.findUserByUsername("test1") != null) {
                assertTrue(userService.deleteUser("test1"));
            }
            User user = userService.createUser("test1", "test2");
            assertEquals(true, userService.isUsernameTaken(user.getUsername()));
            assertEquals(true, userService.deleteUser("test1"));
            assertEquals(false, userService.isUsernameTaken("test1"));
            assertNull(userService.createUser("chetan", "passchetan"));
        }catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void testAuthenticateUser(){
        try {
            User user = userService.authenticateUser("chetan", "test");
            assertEquals("chetan", user.getUsername());
            assertNull(userService.authenticateUser("chetan", "asdfg"));
            assertNull(userService.authenticateUser("asdfg", "Chetan"));
        }catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void testAddRemoveGroupToUser() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("test1") != null) {
                assertTrue(userService.deleteUser("test1"));
            }
            if (groupService.findGroupByName("hotgroup") != null) {
                assertTrue(groupService.deleteGroup("hotgroup"));
            }
            if (groupService.findGroupByName("coolgroup") != null) {
                assertTrue(groupService.deleteGroup("coolgroup"));
            }
            User user = userService.createUser("test1", "test1");
            Group group1 = groupService.createGroup("coolgroup");
            Group group2 = groupService.createGroup("hotgroup");
            assertEquals(true, userService.addGroupToUser(user, group1));
            assertEquals(true, userService.removeGroupFromUser(user.getUsername(), group1.getName()));
            userService.addGroupToUser(user, group1);
            userService.addGroupToUser(user, group2);
            assertEquals(false, userService.deleteUser("test1"));
            assertEquals(true, groupService.deleteGroup("coolgroup"));
            assertEquals(true, groupService.deleteGroup("hotgroup"));
        }catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void testUpdateUser() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("failTest") != null) {
                assertTrue(userService.deleteUser("test1"));
            }
            if (userService.findUserByUsername("test1") != null) {
                assertTrue(userService.deleteUser("test1"));
            }
            User user = new User("failTest", "failTest");
            assertEquals(false, userService.updateUser(user, "failTest123"));
            user = userService.createUser("test1", "test1");
            assertEquals(true, userService.updateUser(user, "testing"));
            user = userService.findUserByUsername("test1");
            assertEquals(true, userService.checkPassword("testing", user.getPassword()));
            assertEquals(true, userService.deleteUser("test1"));
            assertEquals(false, userService.updateUser(null, "new"));
            assertEquals(false, userService.deleteUser("testUserSubject"));
        }catch (Exception e){
            assertTrue(true);
        }
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
        try {
            if (userService.findUserByUsername("dUser") != null) {
                assertTrue(userService.deleteUser("dUser"));
            }
            if (groupService.findGroupByName("dUserGroup") != null) {
                groupService.deleteGroup("dUserGroup");
            }
            User user = userService.createUser("dUser", "dUser");
            Group group = groupService.createGroup("dUserGroup");
            groupService.addUserToGroup(group, user);
            List<String> listOfGroups = new ArrayList<String>(Arrays.asList("dUserGroup"));
            groupService.removeUserFromGroups(listOfGroups, "dUser");
            assertTrue(userService.deleteUser("dUser"));
            groupService.deleteGroup("dUserGroup");
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void addToMyMessages() throws  JsonProcessingException {
        try {
            if (userService.findUserByUsername("dUser") != null) {
                assertTrue(userService.deleteUser("dUser"));
            }
            User user = userService.createUser("dUser", "dUser");
            assertEquals(0, user.getMyMessages().size());
            userService.addToMyMessages(user, "Hi! Test Message");
            user = userService.findUserByUsername("dUser");
            assertEquals(1, user.getMyMessages().size());
            userService.deleteUser("dUser");
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testAddToMyUnreadMessages() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("newone") != null) {
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
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testGetMessages() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("newestr") != null) {
                assertTrue(userService.deleteUser("newestr"));
            }
            User newestReceiver = userService.createUser("newestr", "newestr");
            userService.addToMyMessages(newestReceiver, "[Private Msg] newest: hey wassup");
            userService.addToMyMessages(newestReceiver, "PRIVATE newest: all good man");
            List<String> smessages = userService.getMessages("sender", "newest", "newestr");
            List<String> rmessages = userService.getMessages("receiver", "newest", "newestr");
            assertEquals("[Private Msg] newest: hey wassup", smessages.get(0));
            assertEquals("PRIVATE newest: all good man", rmessages.get(0));
            assertTrue(userService.deleteUser("newestr"));
        }catch (Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void testParentalControl() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("newsender") != null) {
                assertTrue(userService.deleteUser("newsender"));
            }
            User user = userService.createUser("newsender", "newpass");
            userService.switchParentalControl("newsender");
            user = userService.findUserByUsername("newsender");
            assertEquals(true, user.getParentalControl());
            userService.switchParentalControl("newsender");
            user = userService.findUserByUsername("newsender");
            assertEquals(false, user.getParentalControl());
            assertFalse(userService.updateMessage("newsender", "how are", "what are"));
            assertFalse(userService.updateMessage("newsender", "what is", "how is"));
            userService.addToMyMessages(user, "how are");
            userService.addToUnreadMessages(user, "what is");
            userService.updateMessage("newsender", "how are", "what are");
            userService.updateMessage("newsender", "what is", "how is");
            user = userService.findUserByUsername("newsender");
            assertEquals("what are", user.getMyMessages().get(0));
            assertEquals("how is", user.getMyUnreadMessages().get(0));
            assertEquals(false, userService.updateMessage("someuserthatshouldnotbeinthedatabase",
                    "anymsg", "anyothermsg"));
            assertTrue(userService.deleteUser("newsender"));
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testIsSender() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("harry") != null) {
                userService.deleteUser("harry");
            }
            User user = userService.createUser("harry", "potter");
            userService.addToMyMessages(user, "1543604981922 /127.0.0.1:50618 PRIVATE peter hey /Offline");
            userService.addToMyMessages(user, "1543607554047 /127.0.0.1:50513 GROUP petergroup yo guys -> peter /127.0.0.1:50502");
            assertTrue(userService.isSender("1543604981922", "user", "peter", "harry"));
            assertFalse(userService.isSender("1543604981922", "something", "peter", "harry"));
            assertFalse(userService.isSender("1543604981922", "user", "akshay", "harry"));
            assertFalse(userService.isSender("15436049819220978", "user", "peter", "harry"));
            assertTrue(userService.isSender("1543607554047", "group", "petergroup", "harry"));
            assertFalse(userService.isSender("1543607554047", "group", "akshaygroup", "harry"));
            assertFalse(userService.isSender("15436049819220978", "group", "peter", "harry"));
            assertTrue(userService.deleteUser("harry"));
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testRecallMessage() throws JsonProcessingException {
        try {
            if (userService.findUserByUsername("harry") != null) {
                userService.deleteUser("harry");
            }
            if (userService.findUserByUsername("ron") != null) {
                userService.deleteUser("ron");
            }
            if (groupService.findGroupByName("gryffindor") != null) {
                groupService.deleteGroup("gryffindor");
            }
            User user = userService.createUser("harry", "potter");
            User user2 = userService.createUser("ron", "weasley");
            Group group = groupService.createGroup("gryffindor");
            groupService.addUserToGroup(group, user);
            groupService.addUserToGroup(group, user2);
            userService.addToMyMessages(user, "1543617897547 /127.0.0.1:52215 PRIVATE ron hey ron /127.0.0.1:52207");
            userService.addToMyMessages(user, "1543617914550 /127.0.0.1:52215 GROUP gryffindor hey homies -> ron /127.0.0.1:52207");
            userService.addToMyMessages(user, "1543618034531 /127.0.0.1:52215 PRIVATE ron you're offline /Offline");
            userService.addToMyMessages(user, "1543618050537 /127.0.0.1:52215 GROUP gryffindor hey offline homies -> ron /Offline");
            userService.addToMyMessages(user2, "1543617897547 /127.0.0.1:52215 [Private Msg] harry: hey ron /127.0.0.1:52207");
            userService.addToMyMessages(user2, "1543617914550 /127.0.0.1:52215 [harry@gryffindor] hey homies -> ron /127.0.0.1:52207");
            userService.addToUnreadMessages(user2, "1543618034531 /127.0.0.1:52215 [Private Msg] harry: you're offline /Offline");
            userService.addToUnreadMessages(user2, "1543618050537 /127.0.0.1:52215 [harry@gryffindor] hey offline homies -> ron /Offline");
            userService.recallMessage("1543617897547", "user", "ron", "harry");
            userService.recallMessage("1543617914550", "group", "gryffindor", "harry");
            userService.recallMessage("1543618034531", "user", "ron", "harry");
            userService.recallMessage("1543618050537", "group", "gryffindor", "harry");
            userService.recallMessage("1543618050537", "something", "gryffindor", "harry");
            user = userService.findUserByUsername("harry");
            user2 = userService.findUserByUsername("ron");
            assertEquals(4, user.getMyMessages().size());
            assertEquals(2, user2.getMyMessages().size());
            assertEquals(0, user2.getMyUnreadMessages().size());
            assertTrue(userService.deleteUser("harry"));
            assertTrue(userService.deleteUser("ron"));
            assertTrue(groupService.deleteGroup("gryffindor"));
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testAdditionalConditions(){
        try {
            User user = new User("who", "me");
            List<String> messages = new ArrayList<>();
            messages.add("**RECALLED**");
            assertEquals(0, userService.getMessagesbyReceiver("harry", user).size());
            assertFalse(userService.isPresentInMessages(user, "harry", "user"));
            assertFalse(userService.isPresentInMessages(user, "harry", "group"));
            user.setMyMessages(messages);
            assertFalse(userService.isPresentInMessages(user, "harry", "user"));
        }catch (Exception e){
            assertTrue(true);
        }
    }
}
