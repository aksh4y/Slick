package edu.northeastern.ccs.im;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    static final Properties prop = new Properties();
    static MongoClientURI uri = null;
    static MongoClient client = null;
    private MongoConnection() {}

    /**
     *
     * @return db instance from connection
     */
    public static MongoDatabase createConnection() {
        try {
            String resourceName = "config.properties"; 
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try(InputStream input = loader.getResourceAsStream(resourceName)) {
                prop.load(input);
            }
            uri = new MongoClientURI(prop.getProperty("mongoURI"));
            client = new MongoClient(uri);
        }
        catch(Exception e) { 
            Logger.getLogger(MongoConnection.class.getSimpleName()).log(Level.SEVERE, "Could not connect to database", e);
        }
        if(client != null)
            return client.getDatabase(uri.getDatabase());
        return null;
    }
}
