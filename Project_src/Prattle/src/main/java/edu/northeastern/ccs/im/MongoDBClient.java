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
//        cleanDB(db);
        clearUnreadMessages(db);

//        userService.getLastSentMessage("user", "peter", "akshay");

//        userService.getLastSentMessage("user","akshay");


//        userService.addToMyMessages(user, "confirm last message");
//        user = userService.findUserByUsername("peter");
//        System.out.println(user.getMyMessages().get(user.getMyMessages().size()-1));



    }
    public static void clearUnreadMessages(MongoDatabase db) throws JsonProcessingException{
        UserServicePrattle userService = new UserServicePrattle(db);
        User user = userService.findUserByUsername("peter");
        userService.addToUnreadMessages(user,"This message is unread");
        userService.addToUnreadMessages(user,"This message is also unread");
        userService.addToMyMessages(user,"This message is read");
        userService.clearUnreadMessages(user);
    }

    public static void cleanDB(MongoDatabase db) throws JsonProcessingException{
        UserServicePrattle userService = new UserServicePrattle(db);
        GroupServicePrattle grpService = new GroupServicePrattle(db);
        userService.clearUserTable();
        grpService.clearGroupTable();

        List<String> listOfUsers = new ArrayList<>(Arrays.asList("Akshay","Chetan","Nipun","Peter","Test","dummy"));
        List<String> listOfGroups = new ArrayList<>(Arrays.asList("AkshayGroup","ChetanGroup",
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
    }



}







