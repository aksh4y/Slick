package edu.northeastern.ccs.im;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

	public MongoDatabase createConnection() {
		MongoClientURI uri = new MongoClientURI("mongodb://test:test123@ds157493.mlab.com:57493/msd");
		MongoClient mongo = new MongoClient(uri);
		return mongo.getDatabase(uri.getDatabase());
	}
}
