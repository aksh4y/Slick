//	package edu.northeastern.ccs.im;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import edu.northeastern.ccs.im.MongoDB.Model.Group;
//import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
//import edu.northeastern.ccs.im.service.GroupServicePrattle;
//import edu.northeastern.ccs.im.service.SubpoenaServicePrattle;
//import edu.northeastern.ccs.im.service.UserServicePrattle;
//import com.mongodb.client.MongoDatabase;
//
//import edu.northeastern.ccs.im.MongoDB.Model.User;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.*;
//
//
//public class MongoDBClient {
//    public static void main(String[] args) throws JsonProcessingException {
//        MongoDatabase db = MongoConnection.createConnection();
//
//        UserServicePrattle userService = new UserServicePrattle(db);
//        GroupServicePrattle grpService = new GroupServicePrattle(db);
////        cleanDB(db);
////        clearUnreadMessages(db);
////        createSubpoena(db);
////        querySubpoena(db);
////        recallMessage(db);
////        getActiveSubpoenas(db);
////        getMessages(db);
////        createUser(db);
//        addToSubpoenaMessages(db);
//
//
//    }
//    public static void addToSubpoenaMessages(MongoDatabase db) throws JsonProcessingException {
//        SubpoenaServicePrattle subService = new SubpoenaServicePrattle(db);
//        subService.addToSubpoenaMessages("5bfc64560b5f9d34cea6d314","added this message");
//    }
//    public static void createUser(MongoDatabase db) throws JsonProcessingException {
//        UserServicePrattle userService = new UserServicePrattle(db);
//        userService.createUser("Daddy","cool");
//        userService.switchParentalControl("daddy");
//    }
//
//    public static void recallMessage(MongoDatabase db){
//        UserServicePrattle userService = new UserServicePrattle(db);
//        userService.getLastSentMessage("group", "receiver","messgroup");
//    }
//    public static void getMessages(MongoDatabase db){
//        UserServicePrattle userService = new UserServicePrattle(db);
////        List<String> messages=userService.getMessages("sender","peter","akshay");
//
//        List<String> messagesr=userService.getMessages("receiver","akshay","peter");
//    }
//
//    public static void getActiveSubpoenas(MongoDatabase db){
//        SubpoenaServicePrattle subService = new SubpoenaServicePrattle(db);
//        List<Subpoena> s=subService.getActiveSubpoenas();
//
//    }
//
//    public static void querySubpoena(MongoDatabase db){
//        SubpoenaServicePrattle subService = new SubpoenaServicePrattle(db);
//        Subpoena s=subService.querySubpoenaById("5bc64560b5f9d34cea6d313");
//
////        String id=subService.getIdOfSubpoena(s);
//
//    }
//    public static void createSubpoena (MongoDatabase db) throws JsonProcessingException{
//        SubpoenaServicePrattle subService = new SubpoenaServicePrattle(db);
//        Subpoena s1= subService.createSubpoena("peter","chetan","",
//                LocalDate.of(2018,12,7),
//                LocalDate.of(2018,12,9));
//        Subpoena s2= subService.createSubpoena("peter","akshay","",
//                LocalDate.of(2018,11,9),
//                LocalDate.of(2018,12,9));
//
//
//    }
//    public static void clearUnreadMessages(MongoDatabase db) throws JsonProcessingException{
//        UserServicePrattle userService = new UserServicePrattle(db);
//        User user = userService.findUserByUsername("peter");
//        userService.addToUnreadMessages(user,"This message is unread");
//        userService.addToUnreadMessages(user,"This message is also unread");
//        userService.addToMyMessages(user,"This message is read");
//        userService.clearUnreadMessages(user);
//    }
//
//    public static void cleanDB(MongoDatabase db) throws JsonProcessingException{
//        UserServicePrattle userService = new UserServicePrattle(db);
//        GroupServicePrattle grpService = new GroupServicePrattle(db);
//        userService.clearUserTable();
//        grpService.clearGroupTable();
//
//        List<String> listOfUsers = new ArrayList<>(Arrays.asList("Akshay","Chetan","Nipun","Peter","Test","dummy"));
//        List<String> listOfGroups = new ArrayList<>(Arrays.asList("AkshayGroup","ChetanGroup",
//                "NipunGroup","PeterGroup","TestGroup","dummyGroup"));
//
//
//        for(String username: listOfUsers){
//            userService.createUser(username,"test");
//        }
//        User dummyUser = userService.findUserByUsername("dummy");
//        int i=0;
//        for(String name: listOfGroups){
//            //Create group
//            Group grp =grpService.createGroup(name);
//            //add dummyUser to group
//            grpService.addUserToGroup(grp,dummyUser);
//
//            //add yourself to your group
//            grpService.addUserToGroup(grp,userService.findUserByUsername(listOfUsers.get(i++)));
//        }
//    }
//
//
//
//}
//
//
//
//
//
//
//
