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

	/**
	 *
	 * @param user user
	 * @param message message to be added
	 */
	public void addToMyMessages(User user, String message){
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.addToSet("myMessages", message));
	}

	/**
	 *
	 * @param user user
	 * @param message message to be added
	 */
	public void addToUnreadMessages(User user, String message){
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.addToSet("myUnreadMessages", message));
	}

	/**
	 *
	 * @param user user whose messages are to be cleared
	 */
	public void clearUnreadMessages(User user){
		user = findUserByUsername(user.getUsername());
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.pushEach("myMessages", user.getMyUnreadMessages()));
		col.updateOne(Filters.eq("username", user.getUsername()), Updates.pullAll("myUnreadMessages", user.getMyUnreadMessages()));
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
		List<String> listOfMessages = new ArrayList<String>();
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
		List<String> listOfMessages = new ArrayList<String>();
		name= name.toLowerCase();
		for(String message: user.getMyMessages()){
			if(message.contains("[Private Msg] "+name+":")){
				listOfMessages.add(message);
			}
		}
		return listOfMessages;
	}

	public List<String>  getMessagesbyReceiver(String name,User user){

		List<String> listOfMessages = new ArrayList<String>();
		name= name.toLowerCase();
		for(String message: user.getMyMessages()){

			if(message.contains("PRIVATE "+name)){
				listOfMessages.add(message);
			}
		}
		return listOfMessages;
	}

	private Boolean isPresentInUnreadMessages(User user, String key){
		for(String message: user.getMyUnreadMessages()){
			if(message.contains(key)){
				BasicDBObject match = new BasicDBObject("username", user.getUsername());
				BasicDBObject update = new BasicDBObject("myUnreadMessages", message);
				UpdateResult updateResult = col.updateOne(match, new BasicDBObject("$pull", update));
				return updateResult.getModifiedCount()==1;
			}
		}
		return false;
	}
	public Boolean isPresentInMessages(User user, String key){
		for(String message: user.getMyMessages()){
			if(message.contains(key) && !message.contains("**RECALLED**")){

				String[] params=message.split(" ");
				String newMessage= params[0]+" "+params[1]+" **RECALLED** "+params[params.length-1];
				BasicDBObject query = new BasicDBObject();
				query.put("username", user.getUsername());
				query.put("myMessages", message);
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
	public Boolean isSender(String UID, String type, String recepient, String sender){ //type user or group
		if(type.equalsIgnoreCase("user")){
			User user = findUserByUsername(sender);
			for(String message: user.getMyMessages()){
				if(message.contains(UID) && message.contains("PRIVATE "+recepient.toLowerCase())){
					return true;
				}
			}
		}else if(type.equalsIgnoreCase("group")){
			User user = findUserByUsername(sender);
			for(String message: user.getMyMessages()){
				if(message.contains(UID) && message.contains("GROUP "+recepient.toLowerCase())){
					return true;
				}
			}
		}
		else{
			return false;
		}
		return false;
	}

	public void recallMessage(String UID, String type, String recepient,String sender){ //type user or group
		if(type.equalsIgnoreCase("user")){
			User user = findUserByUsername(recepient);
			if(!isPresentInUnreadMessages(user,UID)){
				isPresentInMessages(user,UID);
			}
		}else if(type.equalsIgnoreCase("group")){
			Group group= group_service.findGroupByName(recepient);
			List<String> users = group.getListOfUsers();
			users.remove(sender.toLowerCase());
			for(String username: users){
				User user = findUserByUsername(username);
				if(!isPresentInUnreadMessages(user,UID)){
					isPresentInMessages(user,UID);
				}
			}
		}
		else{

		}
	}


//	public void recallSenderMessage(String  sender, String recepient){
//		User user = findUserByUsername(sender);
//		List<String>  myMessages = user.getMyMessages();
//		String lastSentMessage="";
//		Collections.reverse(myMessages);
//		for(String message: myMessages){
//			if(message.contains(recepient)){ //TODO bad check
//				lastSentMessage = message;
//				break;
//			}
//		}
//
//		BasicDBObject query = new BasicDBObject();
//		query.put("username", sender);
//		query.put("myMessages", lastSentMessage);
//		BasicDBObject data = new BasicDBObject();
//		if(lastSentMessage.contains("**Recalled**")){
//			data.put("myMessages.$", lastSentMessage);
//		}else{
//			data.put("myMessages.$", "**Recalled**"+ lastSentMessage);
//		}
//		BasicDBObject command = new BasicDBObject();
//		command.put("$set", data);
//
//		col.updateOne(query, command);
//	}

//	public void recallFromMessages(User user, String sender){
//		List<String> myMessages = user.getMyMessages();
//		String lastSentMessage="";
//		Collections.reverse(myMessages);
//		for(String message: myMessages){
//			if(message.contains("[") && message.contains(sender)){
//				lastSentMessage = message;
//				break;
//			}
//		}
//		String[] params=lastSentMessage.split(":");
//		BasicDBObject query = new BasicDBObject();
//		query.put("username", user.getUsername());
//		query.put("myMessages", lastSentMessage);
//		BasicDBObject data = new BasicDBObject();
//		data.put("myMessages.$", params[0]+": [Message Deleted]");
//		BasicDBObject command = new BasicDBObject();
//		command.put("$set", data);
//		col.updateOne(query, command);
//	}

//	public void recallFromUnreadMessages(User user, String sender){
//		List<String> myMessages = user.getMyUnreadMessages();
//		String lastSentMessage="";
//		Collections.reverse(myMessages);
//		for(String message: myMessages){
//			if(message.contains("[") && message.contains(sender)){
//				lastSentMessage = message;
//				break;
//			}
//		}
//
//		String[] params=lastSentMessage.split(":");
//		BasicDBObject query = new BasicDBObject();
//		query.put("username", user.getUsername());
//		query.put("myUnreadMessages", lastSentMessage);
//		BasicDBObject data = new BasicDBObject();
//		data.put("myUnreadMessages.$", params[0]+": [Message Deleted]");
//		BasicDBObject command = new BasicDBObject();
//		command.put("$set", data);
//		col.updateOne(query, command);
//	}
//	public void getLastSentMessage(String type, String sender, String receiver) {
//		if(type.equalsIgnoreCase("user")){
//			User user = findUserByUsername(receiver);
//			if(!user.getMyUnreadMessages().isEmpty()){
//				recallFromUnreadMessages(user,sender);
//			}else{
//				recallFromMessages(user, sender);
//			}
//
//			recallSenderMessage(sender,receiver);
//
//		}
//		else if (type.equalsIgnoreCase("group")){
//			recallSenderMessage(sender,receiver);
//
//			Group group = group_service.findGroupByName(receiver);
//			List<String> listOfUsers= group.getListOfUsers();
//			listOfUsers.remove(sender);
//			for(String username : listOfUsers ){
//				User user = findUserByUsername(username);
//				if(!user.getMyUnreadMessages().isEmpty()){
//					recallFromUnreadMessages(user,sender);
//				}else{
//					recallFromMessages(user,sender);
//				}
//			}
//		}
//	}


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

	public Boolean switchParentalControl(String username) {
		User user = findUserByUsername(username);
		UpdateResult updateResult=col.updateOne(Filters.eq("username", username), Updates.set("parentalControl", !user.getParentalControl()));
		return updateResult.getModifiedCount()==1;
	}

	public boolean updateMessage(String username, String oldMsg, String newMsg) {
		User user = findUserByUsername(username);
		if (user != null){
			if(user.getMyUnreadMessages().contains(oldMsg)){
				BasicDBObject query = new BasicDBObject();
				query.put("username", username);
				query.put("myUnreadMessages", oldMsg);
				BasicDBObject data = new BasicDBObject();
				data.put("myUnreadMessages.$", newMsg);
				BasicDBObject command = new BasicDBObject();
				command.put("$set", data);
				UpdateResult updateResult = col.updateOne(query, command);
				return updateResult.getModifiedCount()==1;

			}else{
				BasicDBObject query = new BasicDBObject();
				query.put("username", username);
				query.put("myMessages", oldMsg);
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
