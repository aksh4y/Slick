package edu.northeastern.ccs.im.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


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
        assertEquals(true, userService.isUsernameTaken("test1"));
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
    public void testAddRemoveGroupToUser(){

    }
}
