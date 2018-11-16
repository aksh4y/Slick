package edu.northeastern.ccs.im;


import com.fasterxml.jackson.core.JsonProcessingException;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.service.GroupServicePrattle;
import edu.northeastern.ccs.im.service.UserServicePrattle;
import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.MongoDB.Model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MongoDBClient {
	public static void main(String[] args) throws JsonProcessingException {
        MongoDatabase db = MongoConnection.createConnection();

        UserServicePrattle userService = new UserServicePrattle(db);
        GroupServicePrattle grpService = new GroupServicePrattle(db);
        cleanDB(db);

//        grpService.deleteGroup("MytestGroup");
//        userService.createUser("pwdHash","test!@#");

//        User usr = userService.findUserByUsername("Peter");

//        userService.createUser("testChetan","testchetan");
//        User usr = userService.findUserByUsername("pwdHash");
//        usr = userService.authenticateUser("pwdHash","test!#");


//        grpService.createGroup("MyTestGroup");
//        Group grp = grpService.findGroupByName("MyTestGroup");
//        User usr = userService.findUserByUsername("Peter");

//
//        grpService.addUserToGroup(grp, usr);
//        usr=userService.findUserByUsername("Peter");
//        grpService.addUserToGroup(grp, usr);

//        grpService.exitGroup(usr.getUsername(),grp.getName());

//        userService.deleteUser("John");
    }

    public static void cleanDB(MongoDatabase db) throws JsonProcessingException{
        UserServicePrattle userService = new UserServicePrattle(db);
        GroupServicePrattle grpService = new GroupServicePrattle(db);
        userService.clearUserTable();
        grpService.clearGroupTable();

        List<String> listOfUsers = new ArrayList<String>(Arrays.asList("Akshay","Chetan","Nipun","Peter","Test","dummy"));
        List<String> listOfGroups = new ArrayList<String>(Arrays.asList("AkshayGroup","ChetanGroup",
                "NipunGroup","PeterGroup","TestGroup","dummyGroup"));


        for(String username: listOfUsers){
            userService.createUser(username,"test");
        }
        User dummyUser = userService.findUserByUsername("dummy");
        int i=0;
        for(String name: listOfGroups){
            //Create group
            Group grp =grpService.createGroup(name);
            //add dummyUser to group
            grpService.addUserToGroup(grp,dummyUser);

            //add yourself to your group
            grpService.addUserToGroup(grp,userService.findUserByUsername(listOfUsers.get(i++)));
        }




//        grpService.createGroup("test");
    }

}
