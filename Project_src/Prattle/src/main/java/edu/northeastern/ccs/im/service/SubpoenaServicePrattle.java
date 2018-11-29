package edu.northeastern.ccs.im.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;

public class SubpoenaServicePrattle {
    private MongoCollection<Document> scol;
    private Gson gson;

    public SubpoenaServicePrattle(MongoDatabase db) {
        scol = db.getCollection("Subpoenas");
        gson = new Gson();
    }

    public Subpoena createSubpoena(String username1, String username2, String groupName, LocalDate fromDate, LocalDate toDate) {
        Subpoena subpoena = new Subpoena(username1,username2,groupName,fromDate,toDate);
        insertSubpoena(subpoena);

        subpoena.setId(getIdOfSubpoena(subpoena));
        return subpoena;
    }

    private void insertSubpoena(Subpoena subpoena) {
        String json =gson.toJson(subpoena);
        scol.insertOne(Document.parse(json));
    }

    public String getIdOfSubpoena(Subpoena subpoena){
        BasicDBObject query = new BasicDBObject();
        query.put("user1", subpoena.getUser1() );
        query.put("user2", subpoena.getUser2() );
        query.put("group", subpoena.getGroup() );

        String id="";
        FindIterable<Document> rows = scol.find(query);
        for(Document doc: rows) {
            LocalDate startDate = gson.fromJson(gson.toJson(doc.get("startDate")), LocalDate.class);
            LocalDate endDate = gson.fromJson(gson.toJson(doc.get("endDate")), LocalDate.class);
            if(startDate.equals(subpoena.getStartDate()) && endDate.equals(subpoena.getEndDate())){
                id=doc.get("_id").toString();
            }
        }
        return id;
    }
    public Subpoena querySubpoenaById(String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document doc = scol.find(query).first();
            return gson.fromJson(gson.toJson(doc), Subpoena.class);
        }catch(IllegalArgumentException e){
            e.getStackTrace();
            return null;
        }

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
                Subpoena s= gson.fromJson(gson.toJson(doc), Subpoena.class);
                s.setId(getIdOfSubpoena(s));
                listOfActiveSubpoenas.add(s);
            }
        }
        return listOfActiveSubpoenas;
    }
    public void addToSubpoenaMessages(String id, String message){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        BasicDBObject data = new BasicDBObject();
        data.put("listOfMessages", message);

        BasicDBObject command = new BasicDBObject();
        command.put("$addToSet", data);
        scol.updateOne(query, command);
    }

    public boolean deleteSubpoena(String id){
        DeleteResult deleteResult = scol.deleteOne(Filters.eq("_id",new ObjectId(id)));
        return deleteResult.getDeletedCount() == 1;

    }
}
