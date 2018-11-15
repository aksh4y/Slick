package edu.northeastern.ccs.im.MongoDB.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Chetan Mahale
 */
public class UserTest {
    @Test
    public void initializeUserTest(){
        User user = new User("John", "john");
        assertEquals("John", user.getUsername());
        assertEquals("john",user.getPassword());
    }

    @Test
    public void testUserGetterSetter(){
        User user = new User("John", "john");
        user.setId(1);
        assertEquals(1, user.getId());
        user.setUsername("Jack");
        assertEquals("Jack", user.getUsername());
        user.setPassword("Doe");
        assertEquals("Doe", user.getPassword());
    }

    @Test
    public void testUserGroups(){
        User user = new User("John", "john");
        Group group1 = new Group("A");
        Group group2 = new Group("B");
        user.addGroupToUser(group1);
        assertEquals(1, user.getListOfGroups().size());
        user.addGroupToUser(group2);
        assertEquals(2, user.getListOfGroups().size());
    }
}
