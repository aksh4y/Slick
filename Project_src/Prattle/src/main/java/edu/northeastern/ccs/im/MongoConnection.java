package edu.northeastern.ccs.im;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
	static final String username = "team203";// username
	static final String pass = "Oct2018"; // password
	public MongoConnection(){
		//SonarLint requirement and Test coverage
	}

    /**
     *  Mongo client url
     */
	private static MongoClientURI uri = new MongoClientURI(
			"mongodb://" + username + ":" + pass + "@ds157843.mlab.com:57843/msd");

    /**
     * Mongo client
     */
	private static MongoClient client = new MongoClient(uri);

    /**
     *
     * @return db instance from connection
     */
	public static MongoDatabase createConnection() {
		return client.getDatabase(uri.getDatabase());
	}
}
