package edu.northeastern.ccs.im.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestGroupService {
    MongoConnection mongoConnection = new MongoConnection();
    private MongoDatabase db = mongoConnection.createConnection();
    private GroupServicePrattle groupService = new GroupServicePrattle(db);
    private UserServicePrattle userService = new UserServicePrattle(db);

    @Test
    public void testCreateAndDeleteGroup() throws JsonProcessingException{
//        Group g = groupService.createGroup("Rock");
//        assertEquals("rock", g.getName());
//        boolean check = groupService.deleteGroup("rock");
//        assertEquals(true,check);
//        Group g1 = groupService.createGroup("chetangroup");
//        assertNull(g1);
        Group g2 = groupService.createGroup("dummy");
        groupService.addUserToGroup(g2, userService.findUserByUsername("chetan"));
        groupService.addUserToGroup(g2, userService.findUserByUsername("peter"));
        assertEquals(true, groupService.deleteGroup(g2.getName()));
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
        Group group1 = groupService.findGroupByName("chetangroup");
        assertEquals(true, groupService.addUserToGroup(group1, chetan));
        assertEquals(true, groupService.exitGroup(chetan.getUsername(),group1.getName()));
    }

//    @Test void testRemoveUserFromGroup() throws JsonProcessingException{
//        User chetan = userService.findUserByUsername("chetan");
//        Group g1 = groupService.findGroupByName("chetangroup");
//        Group g2 = groupService.findGroupByName("petergroup");
//        groupService.addUserToGroup(g1, chetan);
//        groupService.addUserToGroup(g2, chetan);
//        List<String> groups = new ArrayList<>();
//        groups.add(g1.getName());
//        groups.add(g2.getName());
//        System.out.println(groupService.removeUserFromGroups(groups, chetan.getUsername()));
//    }
}
