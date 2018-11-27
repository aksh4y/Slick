package edu.northeastern.ccs.im.service;

import com.mongodb.BasicDBObject;
import edu.northeastern.ccs.im.Message;
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
	public List<String> getMessages(String type, String name, String username){
		User user = findUserByUsername(username);
		List<String> listOfMessages = new ArrayList<String>();
		if(type.equalsIgnoreCase("sender")){
			listOfMessages= getMessagesbySender(name,user);
		}
		else if (type.equalsIgnoreCase("receiver")){
			listOfMessages= getMessagesbyReceiver(name,user);
		}

		return listOfMessages;
	}

	public List<String> getMessagesbySender(String name, User user){
		List<String> listOfMessages = new ArrayList<String>();
//		User user = findUserByUsername(name);
		for(String message: user.getMyMessages()){
			if(message.contains("[Private Msg] "+name+":")){
				listOfMessages.add(message);
			}
		}
		return listOfMessages;
	}

	public List<String>  getMessagesbyReceiver(String name,User user){
		List<String> listOfMessages = new ArrayList<String>();
//		User user = findUserByUsername(name);
		for(String message: user.getMyMessages()){

			if(message.contains("PRIVATE "+name)){
				listOfMessages.add(message);
			}
		}
		return listOfMessages;
	}

	public void recallSenderMessage(String  sender, String recepient){
		User user = findUserByUsername(sender);
		List<String>  myMessages = user.getMyMessages();
		String lastSentMessage="";
		Collections.reverse(myMessages);
		for(String message: myMessages){
			if(message.contains(recepient)){ //TODO bad check
				lastSentMessage = message;
				break;
			}
		}

		BasicDBObject query = new BasicDBObject();
		query.put("username", sender);
		query.put("myMessages", lastSentMessage);
		BasicDBObject data = new BasicDBObject();
		if(lastSentMessage.contains("**Recalled**")){
			data.put("myMessages.$", lastSentMessage);
		}else{
			data.put("myMessages.$", "**Recalled**"+ lastSentMessage);
		}
		BasicDBObject command = new BasicDBObject();
		command.put("$set", data);

		col.updateOne(query, command);
	}

	public void recallFromMessages(User user, String sender){
		List<String> myMessages = user.getMyMessages();
		String lastSentMessage="";
		Collections.reverse(myMessages);
		for(String message: myMessages){
			if(message.contains("[") && message.contains(sender)){
				lastSentMessage = message;
				break;
			}
		}
		String[] params=lastSentMessage.split(":");
		BasicDBObject query = new BasicDBObject();
		query.put("username", user.getUsername());
		query.put("myMessages", lastSentMessage);
		BasicDBObject data = new BasicDBObject();
		data.put("myMessages.$", params[0]+": [Message Deleted]");
		BasicDBObject command = new BasicDBObject();
		command.put("$set", data);
		col.updateOne(query, command);
	}

	public void recallFromUnreadMessages(User user){
		List<String> myMessages = user.getMyUnreadMessages();
		String lastSentMessage="";
		Collections.reverse(myMessages);
		for(String message: myMessages){
			if(message.contains("[")){
				lastSentMessage = message;
				break;
			}
		}

		String[] params=lastSentMessage.split(":");
		BasicDBObject query = new BasicDBObject();
		query.put("username", user.getUsername());
		query.put("myUnreadMessages", lastSentMessage);
		BasicDBObject data = new BasicDBObject();
		data.put("myUnreadMessages.$", params[0]+": [Message Deleted]");
		BasicDBObject command = new BasicDBObject();
		command.put("$set", data);
		col.updateOne(query, command);
	}
	public void getLastSentMessage(String type, String sender, String receiver) {
		if(type.equalsIgnoreCase("user")){
			User user = findUserByUsername(receiver);
			if(!user.getMyUnreadMessages().isEmpty()){
				recallFromUnreadMessages(user);
			}else{
				recallFromMessages(user, sender);
			}

			recallSenderMessage(sender,receiver);

		}
		else if (type.equalsIgnoreCase("group")){
			recallSenderMessage(sender,receiver);

			Group group = group_service.findGroupByName(receiver);
			List<String> listOfUsers= group.getListOfUsers();
			listOfUsers.remove(sender);
			for(String username : listOfUsers ){
				User user = findUserByUsername(username);
				if(!user.getMyUnreadMessages().isEmpty()){
					recallFromUnreadMessages(user);
				}else{
					recallFromMessages(user,sender);
				}
			}
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
