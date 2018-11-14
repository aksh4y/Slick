package edu.northeastern.ccs.im;


import com.fasterxml.jackson.core.JsonProcessingException;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.service.GroupServicePrattle;
import edu.northeastern.ccs.im.service.UserServicePrattle;
import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.MongoDB.Model.User;


public class MongoDBClient {
	public static void main(String[] args) throws JsonProcessingException {
        MongoDatabase db= MongoConnection.createConnection();

        UserServicePrattle userService = new UserServicePrattle(db);
        GroupServicePrattle grpService = new GroupServicePrattle(db);
        User my_user= userService.findUserByUsername("Peter");
//      userService.authenticateUser("","");
//        userService.addToMyMessages(my_user,Message.makeHelloMessage("New Message"));
        Group group = grpService.findGroupByName("204");
        userService.addGroupToUser(my_user,group);
	}

}
