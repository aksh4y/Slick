package edu.northeastern.ccs.im.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 * @author Chetan Mahale
 */
public class TestGroupService {
    private MongoDatabase db = MongoConnection.createConnection();
    private GroupServicePrattle groupService = new GroupServicePrattle(db);
    private UserServicePrattle userService = new UserServicePrattle(db);

    @Test
    public void testCreateAndDeleteGroup() throws JsonProcessingException{
        Group g = groupService.createGroup("Rock");
        assertEquals("rock", g.getName());
        boolean check = groupService.deleteGroup("rock");
        assertEquals(true,check);
        Group g1 = groupService.createGroup("chetangroup");
        assertNull(g1);
        Group g2 = groupService.createGroup("dummy");
        groupService.addUserToGroup(g2, userService.findUserByUsername("chetan"));
        groupService.addUserToGroup(g2, userService.findUserByUsername("peter"));
        assertEquals(true, groupService.deleteGroup(g2.getName()));
        assertEquals(false, groupService.deleteGroup("asdf"));
    }

    @Test
    public void checkIfGroupNameTaken() throws JsonProcessingException {
        if(groupService.isGroupnameTaken("chetangroup")){
            groupService.createGroup("chetangroup");
        }
        assertEquals(true, groupService.isGroupnameTaken("Chetangroup"));
        assertEquals(false, groupService.isGroupnameTaken("Abed"));
    }

    @Test
    public void testFindGroupByName() throws JsonProcessingException {
        if(groupService.isGroupnameTaken("chetangroup")){
            groupService.createGroup("chetangroup");
        }
        Group g = groupService.findGroupByName("Chetangroup");
        assertEquals("chetangroup", g.getName());
    }

    @Test void testAddExitUserToGroup() throws JsonProcessingException {
        assertEquals(true, userService.isUsernameTaken("chetan"));
        User chetan = userService.findUserByUsername("chetan");
        Group group1 = groupService.findGroupByName("petergroup");
        assertEquals(true, groupService.addUserToGroup(group1, chetan));
        assertEquals(true, groupService.exitGroup(chetan.getUsername(),group1.getName()));
        assertEquals(false, groupService.exitGroup("asdf", "dfkh"));
        assertEquals(false, groupService.exitGroup("asdf",group1.getName()));
        assertEquals(false, groupService.exitGroup("akshay",group1.getName()));
        assertEquals(false, groupService.exitGroup(chetan.getUsername(), "dfkh"));
    }

    @Test
    public void testExitGroupBadData() throws JsonProcessingException {
        User user = userService.createUser("BadOne", "BadOnePass");
        Group group = groupService.createGroup("BadGroup");
        groupService.addUserToGroup(group,user);
        userService.removeGroupFromUser(user.getUsername(),group.getName());
        assertEquals(false, groupService.exitGroup(user.getUsername(),group.getName()));
        assertEquals(true, userService.deleteUser(user.getUsername()));
        assertEquals(true, groupService.deleteGroup(group.getName()));
    }

    @Test
    public void testRemoveAbsentUserFromGroup() throws JsonProcessingException {
        List<String> lgroup = new ArrayList<>(Arrays.asList("petergroup"));
        assertEquals(false, groupService.removeUserFromGroups(lgroup, "chetan"));
        groupService.addUserToGroup(groupService.findGroupByName("petergroup"),userService.findUserByUsername("dummy"));
        assertEquals(true, groupService.removeUserFromGroups(lgroup, "dummy"));
    }
    @Test
    public void testDeleteGroupBadData() throws JsonProcessingException {
        User user1 = userService.createUser("BadTwo", "BadPass");
        Group group = groupService.createGroup("badgrouptwo");
        groupService.addUserToGroup(group,user1);
        userService.removeGroupFromUser(user1.getUsername(),group.getName());
        assertEquals(false, groupService.deleteGroup(group.getName()));
        assertEquals(true, userService.deleteUser(user1.getUsername()));
    }
}
