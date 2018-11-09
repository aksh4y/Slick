package edu.northeastern.ccs.im;

import java.net.UnknownHostException;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.MongoDB.Model.User;

public class MongoDBClient {
	public static void main(String[] args) throws UnknownHostException {

		// final String OUR_DB_NAME = "msd";
		MongoClientURI uri = new MongoClientURI("mongodb://test:test123@ds157493.mlab.com:57493/msd");
		MongoClient mongo = new MongoClient(uri);
		// List<String> db_names = mongo.getDatabaseNames();
		// System.out.println(db_names);

		MongoDatabase db = mongo.getDatabase(uri.getDatabase());
		// Set<String> collections = db.getC();
		// System.out.println(collections);
		MongoCollection<Document> col = db.getCollection("users");
		System.out.println(col.countDocuments());
		User user = createUser("Petere");

		insertUser(user, col);
		findUser("Petere", col);
		// deleteUser("Peter",col);

		mongo.close();

	}

	private static DBObject createDBObject(User user) {
		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		// docBuilder.append("_id", user.getId());
		docBuilder.append("name", user.getUsername());
		return docBuilder.get();
	}

	private static User createUser(String name) {
		User u = new User();
		// u.setId(1);
		u.setUsername(name);
		return u;
	}

	private static void insertUser(User user, MongoCollection<Document> col) {
		// BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();
		// DBObject query = docBuilder.append("name", user.getUsername()).get();
		Document doc = new Document("name", user.getUsername());
		col.insertOne(doc);
	}

	private static void findUser(String name, MongoCollection<Document> col) {
		// DBObject query = BasicDBObjectBuilder.start().add("name", name).get();
		// DBCursor cursor = col.find(query);
		BasicDBObject query = new BasicDBObject("name", name);
		MongoCursor<Document> cursor = col.find(query).iterator();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}
	}

//	private static void updateUser(User user, DBCollection col, String updatedName) {
//		DBObject query = BasicDBObjectBuilder.start().add("name", user.getUsername()).get();
//
//		user.setUsername(updatedName);
//		DBObject doc = createDBObject(user);
//		WriteResult result = col.update(query, doc);
//
//	}
//
//	private static void deleteUser(String name, DBCollection col) {
//		DBObject query = BasicDBObjectBuilder.start().add("name", name).get();
//		WriteResult result = col.remove(query);
//		System.out.println(result.getUpsertedId());
//		System.out.println(result.getN());
//		System.out.println(result.isUpdateOfExisting());
//		// System.out.println(result.getLastConcern());
//
//	}

}
