package edu.northeastern.ccs.im.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test void testCreateDeleteUser() throws JsonProcessingException {
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
        assertEquals(true, userService.deleteUser("test1"));
        assertEquals(true, groupService.deleteGroup("coolgroup"));
        assertEquals(true, groupService.deleteGroup("hotgroup"));
    }

    @Test
    public void testUpdateUser() throws JsonProcessingException {
        User user = userService.createUser("test1", "test1");
        userService.updateUser(user, "testing");
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
        assertEquals(false, userService.checkPassword("john","$2a$"));
    }

//    @Test
//    public void testUserMessages() throws JsonProcessingException {
//        User user = userService.createUser("testmessage","testPass");
//        userService.addToMyMessages(user,"Hello");
//        userService.clearNewMessages(user);
//        assertEquals(true, userService.deleteUser(user.getUsername()));
//    }
}
