package edu.northeastern.ccs.im.service;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
import edu.northeastern.ccs.im.MongoDB.Model.User;

import org.bson.Document;
import com.google.gson.Gson;
import org.bson.types.ObjectId;

import javax.print.attribute.standard.MediaSize;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public Subpoena createSubpoena(String username1, String username2, String groupName, LocalDate fromDate, LocalDate toDate) {
        //TODO send ID back, change to username
        Subpoena subpoena = new Subpoena(username1,username2,groupName,fromDate,toDate);
        insertSubpoena(subpoena);

        return subpoena;
    }

    private void insertSubpoena(Subpoena subpoena) {
        String json =gson.toJson(subpoena);
        scol.insertOne(Document.parse(json));

    }
    public Subpoena querySubpoenaById(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document doc = scol.find(query).first();
        return gson.fromJson(gson.toJson(doc), Subpoena.class);
    }
    public List<Subpoena> getActiveSubpoenas() {
        List<Subpoena> listOfActiveSubpoenas = new ArrayList<Subpoena>();
//        BasicDBObject query = new BasicDBObject();
//        query.put("startDate",
//                new BasicDBObject("$lte", LocalDate.now()));
//        query.put("endDate",
//                new BasicDBObject("gte",LocalDate.now()));

        FindIterable<Document> activeSubpoenas = scol.find();
        for(Document doc: activeSubpoenas) {
            LocalDate startDate = gson.fromJson(gson.toJson(doc.get("startDate")), LocalDate.class);
            LocalDate endDate = gson.fromJson(gson.toJson(doc.get("endDate")), LocalDate.class);
            LocalDate today = LocalDate.now();

            if ((today.isAfter(startDate) || today.isEqual(startDate)) && (today.isBefore(endDate) || today.isEqual(endDate))) {
                listOfActiveSubpoenas.add(gson.fromJson(gson.toJson(doc), Subpoena.class));

            }
        }
        return listOfActiveSubpoenas;
    }
}
