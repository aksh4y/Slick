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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter
 * @version 1.0
 */
public class UserServicePrattle {

	private MongoCollection<Document> col;
	private Gson gson;
	private GroupServicePrattle groupService;
	private static int workload = 12;
	private static final String USERNAME="username";
	private static final String MY_MESSAGES="myMessages";
	private static final String MY_UNREAD_MESSAGES="myUnreadMessages";
	/**
	 *
	 * @param db Database Instance
	 */
	public UserServicePrattle(MongoDatabase db) {
		col = db.getCollection("Users");
		gson = new Gson();
		groupService= new GroupServicePrattle(db);
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
		Document doc = col.find(Filters.eq(USERNAME, username.toLowerCase())).first();
		return gson.fromJson(gson.toJson(doc), User.class);
	}

	/**
	 *
	 * @param username String name to be checked
	 * @return true is username exists; else false
	 */
	public Boolean isUsernameTaken(String username) {
		FindIterable<Document> iterable = col.find(Filters.eq(USERNAME, username.toLowerCase()));
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
			UpdateResult updateResult=col.updateOne(Filters.eq(USERNAME, user.getUsername().toLowerCase()),
					new Document("$set", new Document("password", hashPassword(updatedPassword))));
			return (updateResult.getModifiedCount()==1);
		}
		return false;
	}

	/**
	 * Delete user boolean.
	 *
	 * @param username the username
	 * @return the boolean
	 */
	public boolean deleteUser(String username) {
		User user = findUserByUsername(username);
		if (user != null) {
			List<String> listOfGroups = user.getListOfGroups();
			DeleteResult dr = col.deleteOne(Filters.eq(USERNAME, username.toLowerCase()));
			boolean removed = listOfGroups.isEmpty() || groupService.removeUserFromGroups(listOfGroups, username.toLowerCase());
			return (dr.getDeletedCount()==1 && removed);
		}
		return false;
	}

	/**
	 * Remove group from user boolean.
	 *
	 * @param username  the username
	 * @param groupName the group name
	 * @return the boolean
	 */
	public boolean removeGroupFromUser(String username, String groupName){
		UpdateResult updateResult= col.updateOne(Filters.eq(USERNAME, username),
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
		UpdateResult updateResult= col.updateOne(Filters.eq(USERNAME, user.getUsername()),
				Updates.addToSet("listOfGroups", group.getName()));
		return (updateResult.getModifiedCount()==1);
	}

	/**
	 *
	 * @param user user
	 * @param message message to be added
	 */
	public void addToMyMessages(User user, String message){
		col.updateOne(Filters.eq(USERNAME, user.getUsername()), Updates.addToSet(MY_MESSAGES, message));
	}

	/**
	 *
	 * @param user user
	 * @param message message to be added
	 */
	public void addToUnreadMessages(User user, String message){
		col.updateOne(Filters.eq(USERNAME, user.getUsername()), Updates.addToSet(MY_UNREAD_MESSAGES, message));
	}

	/**
	 *
	 * @param user user whose messages are to be cleared
	 */
	public void clearUnreadMessages(User user){
		user = findUserByUsername(user.getUsername());
		col.updateOne(Filters.eq(USERNAME, user.getUsername()), Updates.pushEach(MY_MESSAGES, user.getMyUnreadMessages()));
		col.updateOne(Filters.eq(USERNAME, user.getUsername()), Updates.pullAll(MY_UNREAD_MESSAGES, user.getMyUnreadMessages()));
	}

	/**
	 *
	 * @param type type of the user
	 * @param name name of the user
	 * @param username username of the user
	 * @return
	 */
	public List<String> getMessages(String type, String name, String username){
		User user = findUserByUsername(username);
		List<String> listOfMessages = new ArrayList<>();
		if(type.equalsIgnoreCase("sender")){
			listOfMessages= getMessagesbySender(name,user);
		}
		else if (type.equalsIgnoreCase("receiver")){
			listOfMessages= getMessagesbyReceiver(name,user);
		}

		return listOfMessages;
	}

	/**
	 *
	 * @param name
	 * @param user
	 * @return
	 */
	public List<String> getMessagesbySender(String name, User user){
		List<String> listOfMessages = new ArrayList<>();
		name= name.toLowerCase();
		for(String message: user.getMyMessages()){
			if(message.contains("[Private Msg] "+name+":") || (message.contains("["+name+"@"))){
				listOfMessages.add(message);
			}
		}
		return listOfMessages;
	}

	/**
	 * Get messagesby receiver list.
	 *
	 * @param name the name
	 * @param user the user
	 * @return the list
	 */
	public List<String>  getMessagesbyReceiver(String name,User user){

		List<String> listOfMessages = new ArrayList<>();
		name= name.toLowerCase();
		for(String message: user.getMyMessages()){

			if(message.contains("PRIVATE "+name) || message.contains("-> "+name)){
				listOfMessages.add(message);
			}
		}
		return listOfMessages;
	}

	private Boolean isPresentInUnreadMessages(User user, String key){
		for(String message: user.getMyUnreadMessages()){
			if(message.contains(key)){
				BasicDBObject match = new BasicDBObject(USERNAME, user.getUsername());
				BasicDBObject update = new BasicDBObject(MY_UNREAD_MESSAGES, message);
				UpdateResult updateResult = col.updateOne(match, new BasicDBObject("$pull", update));
				return updateResult.getModifiedCount()==1;
			}
		}
		return false;
	}

	/**
	 * Is present in messages boolean.
	 *
	 * @param user the user
	 * @param key  the key
	 * @return the boolean
	 */
	public Boolean isPresentInMessages(User user, String key,String type){
		for(String message: user.getMyMessages()){
			if(message.contains(key) && !message.contains("**RECALLED**")){

				String[] params=message.split(" ");
				String newMessage="";
				if(type.equalsIgnoreCase("user")) {
					newMessage = params[0] + " " + params[1] + " " + params[2] + " " + params[3] + " " + params[4] + " **RECALLED** " + params[params.length - 1];
				}else if(type.equalsIgnoreCase("group")) {
					newMessage = params[0] + " " + params[1] + " " + params[2] + " **RECALLED** " + params[params.length - 1];
				}
				BasicDBObject query = new BasicDBObject();
				query.put(USERNAME, user.getUsername());
				query.put(MY_MESSAGES, message);
				BasicDBObject data = new BasicDBObject();
				data.put("myMessages.$", newMessage);
				BasicDBObject command = new BasicDBObject();
				command.put("$set", data);
				UpdateResult updateResult = col.updateOne(query, command);
				return updateResult.getModifiedCount()==1;

			}
		}
		return false;
	}

	/**
	 * Is sender boolean.
	 *
	 * @param uid       the uid
	 * @param type      the type
	 * @param recepient the recepient
	 * @param sender    the sender
	 * @return the boolean
	 */
	public Boolean isSender(String uid, String type, String recepient, String sender){ //type user or group
		if(type.equalsIgnoreCase("user")){
			User user = findUserByUsername(sender);
			for(String message: user.getMyMessages()){
				if(message.contains(uid) && message.contains("PRIVATE "+recepient.toLowerCase())){
					return true;
				}
			}
		}else if(type.equalsIgnoreCase("group")){
			User user = findUserByUsername(sender);
			for(String message: user.getMyMessages()){
				if(message.contains(uid) && message.contains("GROUP "+recepient.toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Recall message.
	 *
	 * @param uid       the uid
	 * @param type      the type
	 * @param recepient the recepient
	 * @param sender    the sender
	 */
	public void recallMessage(String uid, String type, String recepient,String sender){ //type user or group
		if(type.equalsIgnoreCase("user")){
			User user = findUserByUsername(recepient);
			if(!isPresentInUnreadMessages(user,uid)){
				isPresentInMessages(user,uid,type);
			}
		}else if(type.equalsIgnoreCase("group")){
			Group group= groupService.findGroupByName(recepient);
			List<String> users = group.getListOfUsers();
			users.remove(sender.toLowerCase());
			for(String username: users){
				User user = findUserByUsername(username);
				if(!isPresentInUnreadMessages(user,uid)){
					isPresentInMessages(user,uid,type);
				}
			}
		}
	}

	/**
	 * Hash password string.
	 *
	 * @param passwordPlainText the password plain text
	 * @return the string
	 */
	public static String hashPassword(String passwordPlainText) {
		String salt = BCrypt.gensalt(workload);
		return BCrypt.hashpw(passwordPlainText, salt);
	}

	/**
	 * This method can be used to verify a computed hash from a plaintext (e.g. during a login
	 * request) with that of a stored hash from a database. The password hash from the database
	 * must be passed as the second variable.
	 * @param passwordPlainText The account's plaintext password, as provided during a login request
	 * @param storedHash The account's stored password hash, retrieved from the authorization database
	 * @return boolean - true if the password matches the password of the stored hash, false otherwise
	 */
	public static boolean checkPassword(String passwordPlainText, String storedHash) {
		boolean passwordVerified = false;

		if(null == storedHash || !storedHash.startsWith("$2a$"))
			throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

		passwordVerified = BCrypt.checkpw(passwordPlainText, storedHash);

		return(passwordVerified);
	}

	/**
	 * Switch parental control boolean.
	 *
	 * @param username the username
	 * @return the boolean
	 */
	public Boolean switchParentalControl(String username) {
		User user = findUserByUsername(username);
		UpdateResult updateResult=col.updateOne(Filters.eq(USERNAME, username), Updates.set("parentalControl", !user.getParentalControl()));
		return updateResult.getModifiedCount()==1;
	}

	/**
	 * Update message boolean.
	 *
	 * @param username the username
	 * @param oldMsg   the old msg
	 * @param newMsg   the new msg
	 * @return the boolean
	 */
	public boolean updateMessage(String username, String oldMsg, String newMsg) {
		User user = findUserByUsername(username);
		if (user != null){
			if(user.getMyUnreadMessages().contains(oldMsg)){
				BasicDBObject query = new BasicDBObject();
				query.put(USERNAME, username);
				query.put(MY_UNREAD_MESSAGES, oldMsg);
				BasicDBObject data = new BasicDBObject();
				data.put("myUnreadMessages.$", newMsg);
				BasicDBObject command = new BasicDBObject();
				command.put("$set", data);
				UpdateResult updateResult = col.updateOne(query, command);
				return updateResult.getModifiedCount()==1;

			}else{
				BasicDBObject query = new BasicDBObject();
				query.put(USERNAME, username);
				query.put(MY_MESSAGES, oldMsg);
				BasicDBObject data = new BasicDBObject();
				data.put("myMessages.$", newMsg);
				BasicDBObject command = new BasicDBObject();
				command.put("$set", data);
				UpdateResult updateResult = col.updateOne(query, command);
				return updateResult.getModifiedCount()==1;
			}
		}
		return false;
	}
}
