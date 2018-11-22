package edu.northeastern.ccs.im.service;

import com.mongodb.BasicDBObject;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.User;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Peter
 * @version 1.0
 */
public class UserServicePrattle {

	private MongoCollection<Document> col;
	private MongoDatabase db;
	private Gson gson;
	private GroupServicePrattle group_service;
	private static int workload = 12;

	/**
	 *
	 * @param db Database Instance
	 */
	public UserServicePrattle(MongoDatabase db) {
		this.db = db;
		col = db.getCollection("Users");
		gson = new Gson();
		group_service= new GroupServicePrattle(db);
	}

	/**
	 *
	 * @param username String username
	 * @param password String password
	 * @return authenticated user if found; else null
	 */
	public User authenticateUser(String username, String password) {
		User user= findUserByUsername(username);
		if(user!= null && checkPassword(password,user.getPassword())){
			return user;
		}
		else
			return null;
	}

	/**
	 *
	 * @param username String username
	 * @param password String password
	 * @return Created User
	 * @throws JsonProcessingException
	 */
	public User createUser(String username, String password) throws JsonProcessingException {
		if (!isUsernameTaken(username)) {
			User u = new User(username.toLowerCase(), password);
			insertUser(u);
			return u;
		}
		return null;
	}

	/**
	 *
	 * @param user User to be inserted in the database
	 * @throws JsonProcessingException
	 */
	private void insertUser(User user) throws JsonProcessingException  {

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		col.insertOne(Document.parse(json));
	}

	/**
	 *
	 * @param username username to be searched
	 * @return
	 */
	public User findUserByUsername(String username) {
		Document doc = col.find(Filters.eq("username", username.toLowerCase())).first();
		return gson.fromJson(gson.toJson(doc), User.class);
	}

	/**
	 *
	 * @param username String name to be checked
	 * @return true is username exists; else false
	 */
	public Boolean isUsernameTaken(String username) {
		FindIterable<Document> iterable = col.find(Filters.eq("username", username.toLowerCase()));
		return iterable.first() != null;
	}

	/**
	 *
	 * @param user User to be updated
	 * @param updatedPassword String password to be updated
	 * @return True after updating
	 */
	public Boolean updateUser(User user, String updatedPassword) {
		if(user!= null) {
			UpdateResult updateResult=col.updateOne(Filters.eq("username", user.getUsername().toLowerCase()),
					new Document("$set", new Document("password", hashPassword(updatedPassword))));
			return (updateResult.getModifiedCount()==1);
		}
		return false;
	}

	public boolean deleteUser(String username) throws JsonProcessingException {
		User user = findUserByUsername(username);
		if (user != null) {
			List<String> listOfGroups = user.getListOfGroups();
			DeleteResult dr = col.deleteOne(Filters.eq("username", username.toLowerCase()));
			boolean removed = listOfGroups.isEmpty() || group_service.removeUserFromGroups(listOfGroups, username.toLowerCase());
			return (dr.getDeletedCount()==1 && removed);
		}
		return false;
	}

	public boolean removeGroupFromUser(String username, String groupName){
		UpdateResult updateResult= col.updateOne(Filters.eq("username", username),
				Updates.pull("listOfGroups", groupName));

		return (updateResult.getModifiedCount()==1);
	}


	/**
	 *
	 * @param user User to be updated
	 * @param group Group to be added
	 * @return True after updating
	 * @throws JsonProcessingException
	 */

	public Boolean addGroupToUser(User user, Group group) {
		UpdateResult updateResult= col.updateOne(Filters.eq("username", user.getUsername()),
				Updates.addToSet("listOfGroups", group.getName()));
		return (updateResult.getModifiedCount()==1);
	}

	public void addToMyMessages(User user, String message){
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.addToSet("myMessages", message));
	}


	public void addToUnreadMessages(User user, String message){
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.addToSet("myUnreadMessages", message));
	}
	public void clearUnreadMessages(User user){
		user = findUserByUsername(user.getUsername());
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.pushEach("myMessages", user.getMyUnreadMessages()));
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.pullAll("myUnreadMessages", user.getMyUnreadMessages()));
	}

	public void getLastSentMessage(String type, String sender, String receiver) {
		if(type.equalsIgnoreCase("user")){
			User user = findUserByUsername(receiver);
			List<String> myMessages = user.getMyMessages();
			String lastSentMessage="";
//			String sentTo="";
			Collections.reverse(myMessages);
			for(String message: myMessages){
				if(message.contains("[")){
					lastSentMessage = message;
//					sentTo= lastSentMessage.split(" ")[1];
					break;
				}
			}

			BasicDBObject query = new BasicDBObject();
			query.put("username", receiver);
			query.put("myMessages", lastSentMessage);
			BasicDBObject data = new BasicDBObject();
			data.put("myMessages.$", "Message deleted");
			BasicDBObject command = new BasicDBObject();
			command.put("$set", data);
			col.updateOne(query, command);

			user = findUserByUsername(sender);
			myMessages = user.getMyMessages();
			lastSentMessage="";
//			String sentTo="";
			Collections.reverse(myMessages);
			for(String message: myMessages){
				if(!message.contains("[")){
					lastSentMessage = message;
//					sentTo= lastSentMessage.split(" ")[1];
					break;
				}
			}

			query = new BasicDBObject();
			query.put("username", sender);
			query.put("myMessages", lastSentMessage);
			data = new BasicDBObject();
			data.put("myMessages.$", "[Recalled] updated message");
			command = new BasicDBObject();
			command.put("$set", data);

			col.updateOne(query, command);


//			UpdateResult updateResult= col.updateOne(Filters.eq("username", username), Updates.("myMessages"));


		}
	}






	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		return BCrypt.hashpw(password_plaintext, salt);
	}

	/**
	 * This method can be used to verify a computed hash from a plaintext (e.g. during a login
	 * request) with that of a stored hash from a database. The password hash from the database
	 * must be passed as the second variable.
	 * @param password_plaintext The account's plaintext password, as provided during a login request
	 * @param stored_hash The account's stored password hash, retrieved from the authorization database
	 * @return boolean - true if the password matches the password of the stored hash, false otherwise
	 */
	public static boolean checkPassword(String password_plaintext, String stored_hash) {
		boolean password_verified = false;

		if(null == stored_hash || !stored_hash.startsWith("$2a$"))
			throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

		password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

		return(password_verified);
	}

	public void clearUserTable(){
		col.deleteMany(new Document());
	}
}
