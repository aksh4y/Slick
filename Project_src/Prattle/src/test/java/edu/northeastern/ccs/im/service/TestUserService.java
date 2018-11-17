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
}
