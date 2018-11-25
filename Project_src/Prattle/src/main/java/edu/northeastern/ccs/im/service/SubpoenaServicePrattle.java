package edu.northeastern.ccs.im.service;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
import edu.northeastern.ccs.im.MongoDB.Model.User;

import org.bson.Document;
import com.google.gson.Gson;
import java.util.Date;

public class SubpoenaServicePrattle {
    private MongoCollection<Document> scol;
    private MongoDatabase db;
    private Gson gson;
    private GroupServicePrattle groupService;
    private UserServicePrattle userService;

    public SubpoenaServicePrattle(MongoDatabase db) {
        this.db = db;
        scol = db.getCollection("Subpoenas");
        gson = new Gson();
        userService = new UserServicePrattle(db);
        groupService = new GroupServicePrattle(db);
    }

    public Subpoena createSubpoena(String username1, String username2, String groupName, Date fromDate, Date toDate) {
        //TODO send ID back, change to username
//        User user1 =  userService.findUserByUsername(username1);
//        User user2 =  userService.findUserByUsername(username2);
//        Group group = groupService.findGroupByName(groupName);
        Subpoena subpoena = new Subpoena(username1,username2,groupName,fromDate,toDate);
        insertSubpoena(subpoena);
        return subpoena;
    }

    private void insertSubpoena(Subpoena subpoena) {
        String json =gson.toJson(subpoena);
        scol.insertOne(Document.parse(json));
    }

    //TODO  methods 1.find based on ID login [return object] 2.check active subpoena [return object] 3.
}
