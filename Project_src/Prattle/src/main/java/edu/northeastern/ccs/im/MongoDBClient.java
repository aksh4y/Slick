package edu.northeastern.ccs.im;


import com.fasterxml.jackson.core.JsonProcessingException;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.service.GroupServicePrattle;
import edu.northeastern.ccs.im.service.UserServicePrattle;
import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.MongoDB.Model.User;


public class MongoDBClient {
	public static void main(String[] args) throws JsonProcessingException {
        MongoDatabase db = MongoConnection.createConnection();

        UserServicePrattle userService = new UserServicePrattle(db);
        GroupServicePrattle grpService = new GroupServicePrattle(db);
        userService.createUser("John","JohnPass");


//        grpService.createGroup("MyTestGroup");
        Group grp = grpService.findGroupByName("MyTestGroup");
        User usr = userService.findUserByUsername("John");
        grpService.addUserToGroup(grp, usr);

        grpService.exitGroup(usr.getUsername(),grp.getName());

//        userService.deleteUser("John");
    }

}
