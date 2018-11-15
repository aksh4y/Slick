package edu.northeastern.ccs.im.service;

import com.mongodb.WriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
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

//	private String getHashedPassword(String username){
//		Document doc = col.find(Filters.eq("username", username.toLowerCase())).first();
//		if(doc != null)
//			return  gson.fromJson(gson.toJson(doc), User.class).getPassword();
//		else
//			return "";
//	}
	/**
	 *
	 * @param username String username
	 * @param password String password
	 * @return authenticated user if found; else null
	 */
	public User authenticateUser(String username, String password) {
		User user= findUserByUsername(username);
		if(checkPassword(password,user.getPassword())){
			return user;
		}
		else
			return null;
//		Document doc = col.find(Filters.eq("username", username.toLowerCase())).first();
//		,
//				Filters.eq("password", checkPassword(password,getHashedPassword(username))))).first();

//		return gson.fromJson(gson.toJson(doc), User.class);
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
		col.updateOne(Filters.eq("username", user.getUsername().toLowerCase()),
				new Document("$set", new Document("password", updatedPassword)));
		return true;
	}

	public boolean deleteUser(String username) throws JsonProcessingException{
		User user=findUserByUsername(username);
		List<String> listOfGroups = user.getListOfGroups();
		DeleteResult dr = col.deleteOne(Filters.eq("username", username.toLowerCase()));
		boolean removed=group_service.removeUserFromGroups(listOfGroups,username.toLowerCase());
		return (dr.wasAcknowledged() && removed);
	}

	public boolean removeGroupFromUser(String username, String groupName){
		UpdateResult updateResult= col.updateOne(Filters.eq("username", username),
				Updates.pull("listOfGroups", groupName));

		return updateResult.wasAcknowledged();
	}


	/**
	 *
	 * @param user User to be updated
	 * @param group Group to be added
	 * @return True after updating
	 * @throws JsonProcessingException
	 */
	public Boolean addGroupToUser(User user, Group group) throws JsonProcessingException {
		// col.updateOne(Filters.eq("name", user.getName()), new Document("$push",
		// {"listOfGroups", group}));
//		ObjectMapper mapper = new ObjectMapper();
//		String json = mapper.writeValueAsString(group);
//		String json =gson.toJson(group);
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.addToSet("listOfGroups", group.getName()));
		return true;
	}


	public void clearNewMessages(User user){
		col.updateOne(Filters.eq("name", user.getUsername()), Updates.set("myNewMessages", ""));

	}

	public void addToMyMessages(User user, String message) throws JsonProcessingException{
//		ObjectMapper mapper = new ObjectMapper();
//		String json = mapper.writeValueAsString(message);
//		JSONObject jsonObj = new JSONObject(message);
		//String json =gson.toJson(message);
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.addToSet("myMessages", message));
	}

	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return(hashed_password);
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

}
