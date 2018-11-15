package edu.northeastern.ccs.im.MongoDB.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Chetan Mahale
 */
public class GroupTest {

    @Test
    public void initializeGroup(){
        Group group1 = new Group("A");
//        Group group2 = new Group("B");
        assertEquals("A", group1.getName());
    }

    @Test
    public void testGroupGetterSetter(){
        Group group1 = new Group("A");
        group1.setName("B");
        assertEquals("B", group1.getName());
    }

    @Test
    public void testGroupUsers(){
        Group group1 = new Group("A");
        User user1 = new User("John", "john");
        User user2 = new User("Jack", "jack");
        group1.addUserTOGroup(user1);
        assertEquals(1, group1.getListOfUsers().size());
        group1.addUserTOGroup(user2);
        assertEquals(2, group1.getListOfUsers().size());
    }
}
