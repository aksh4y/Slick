package edu.northeastern.ccs.im;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
	final static String username = "team203";
	final static String pass = "Oct2018";
	public MongoConnection(){
		//SonarLint requirement and Test coverage
	}
	private static MongoClientURI uri = new MongoClientURI(
			"mongodb://" + username + ":" + pass + "@ds157843.mlab.com:57843/msd");
	private static MongoClient client = new MongoClient(uri);

	public static MongoDatabase createConnection() {
		return client.getDatabase(uri.getDatabase());
	}
}
