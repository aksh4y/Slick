package edu.northeastern.ccs.im.MongoDB.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.service.UserServicePrattle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Chetan Mahale
 */
public class UserTest {

    MongoDatabase db = MongoConnection.createConnection();
//    GroupServicePrattle groupService = new GroupServicePrattle(db);
    UserServicePrattle userService = new UserServicePrattle(db);

//    @Test
//    public void initializeUserTest(){
//        User user = new User("John", "john");
//        assertEquals("John", user.getUsername());
//        assertEquals(true, userService.checkPassword("john",user.getPassword()));
//    }
//
//    @Test
//    public void testUserGetterSetter(){
//        User user = new User("John", "john");
//        user.setId(1);
//        assertEquals(1, user.getId());
//        user.setUsername("Jack");
//        assertEquals("Jack", user.getUsername());
//        user.setPassword("Doe");
//        assertEquals("Doe", user.getPassword());
//    }

    @Test
    public void testUserGroups() throws JsonProcessingException {
        User user = userService.createUser("John", "john");
        Group group1 = new Group("A");
        Group group2 = new Group("B");
        userService.addGroupToUser(user, group1);
        user = userService.findUserByUsername("John");
        assertEquals(1, user.getListOfGroups().size());
        userService.addGroupToUser(user, group2);
        user = userService.findUserByUsername("John");
        assertEquals(2, user.getListOfGroups().size());
        assertEquals(true, userService.deleteUser("John"));
    }
}
