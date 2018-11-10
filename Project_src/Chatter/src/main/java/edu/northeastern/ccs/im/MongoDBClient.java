package edu.northeastern.ccs.im;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import edu.northeastern.ccs.im.MongoDB.Model.User;

public class MongoDBClient {
    public static void main(String[] args) throws UnknownHostException {

        final String OUR_DB_NAME = "MSD";
        MongoClient mongo = new MongoClient("localhost", 27017);

        List<String> db_names = mongo.getDatabaseNames();
        System.out.println(db_names);

        DB db = mongo.getDB(OUR_DB_NAME);
        Set<String> collections = db.getCollectionNames();
        System.out.println(collections);

        DBCollection col = db.getCollection("users");

        User user = createUser("Peter");

        insertUser(user,col);
        findUser("Peter", col);
//        deleteUser("Peter",col);

        mongo.close();

    }

    private static DBObject createDBObject(User user) {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

//        docBuilder.append("_id", user.getId());
        docBuilder.append("name", user.getName());
        return docBuilder.get();
    }

    private static User createUser(String name) {
        User u = new User();
//        u.setId(1);
        u.setName(name);
        return u;
    }

    private static void insertUser(User user, DBCollection col) {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();
        DBObject query = docBuilder.append("name", user.getName()).get();

        WriteResult result = col.insert(query);
    }
    private static void findUser(String name, DBCollection col) {
        DBObject query = BasicDBObjectBuilder.start().add("name", name).get();
        DBCursor cursor = col.find(query);
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
    }

    private static void updateUser(User user, DBCollection col, String updatedName) {
        DBObject query = BasicDBObjectBuilder.start().add("name", user.getName()).get();

        user.setName(updatedName);
        DBObject doc = createDBObject(user);
        WriteResult result = col.update(query, doc);

    }

    private static void deleteUser(String name, DBCollection col) {
        DBObject query = BasicDBObjectBuilder.start().add("name", name).get();
        WriteResult result = col.remove(query);
        System.out.println(result.getUpsertedId());
        System.out.println(result.getN());
        System.out.println(result.isUpdateOfExisting());
        System.out.println(result.getLastConcern());



    }




}
