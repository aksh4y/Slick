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

/**
 *
 * @author Peter
 */
public class SubpoenaServicePrattle {
    private MongoCollection<Document> scol;
    private Gson gson;

    /**
     *
     * @param db instance of the database
     */
    public SubpoenaServicePrattle(MongoDatabase db) {
        scol = db.getCollection("Subpoenas");
        gson = new Gson();
    }

    /**
     *
     * @param username1 name of the first person
     * @param username2 name of the second person
     * @param groupName name of the group
     * @param fromDate start date
     * @param toDate end date
     * @return new subpoena after inserting it in the database
     */
    public Subpoena createSubpoena(String username1, String username2, String groupName, LocalDate fromDate, LocalDate toDate) {
        Subpoena subpoena = new Subpoena(username1,username2,groupName,fromDate,toDate);
        insertSubpoena(subpoena);

        subpoena.setId(getIdOfSubpoena(subpoena));
        return subpoena;
    }

    /**
     *
     * @param subpoena that has to be inserted in the db
     */
    private void insertSubpoena(Subpoena subpoena) {
        String json =gson.toJson(subpoena);
        scol.insertOne(Document.parse(json));
    }

    /**
     *
     * @param subpoena whose id is to be found
     * @return id of the given subpoena
     */
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

    /**
     *
     * @param id id of the subpoena to be retrieved from the db
     * @return the subpoena with given id
     */
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

    /**
     *
     * @return list of active subpoenas
     */
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

    /**
     *
     * @param id or the subpoena
     * @param message message to be added
     */
    public void addToSubpoenaMessages(String id, String message){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        BasicDBObject data = new BasicDBObject();
        data.put("listOfMessages", message);

        BasicDBObject command = new BasicDBObject();
        command.put("$addToSet", data);
        scol.updateOne(query, command);
    }

    /**
     *
     * @param id id of the subpoena
     * @return true if the subpoena was deleted
     */

    public boolean deleteSubpoena(String id){
        DeleteResult deleteResult = scol.deleteOne(Filters.eq("_id",new ObjectId(id)));
        return deleteResult.getDeletedCount() == 1;

    }
}
