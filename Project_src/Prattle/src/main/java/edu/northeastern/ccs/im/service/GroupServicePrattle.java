package edu.northeastern.ccs.im.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import org.bson.Document;

import java.util.List;

/**
 *
 * @author Chetan Mahale
 * @version 1.0
 */
public class GroupServicePrattle {

	/**
	 * Private variables
	 */
	private MongoCollection<Document> gcol;
	private MongoDatabase db;
	private Gson gson;
	private static final String LIST_OF_USERS ="listOfUsers";

	/**
	 *
	 * @param db Database Instance
	 */
	public GroupServicePrattle(MongoDatabase db) {
		this.db = db;
		gcol = db.getCollection("Groups");
		gson = new Gson();
	}

	/**
	 *
	 * @param name name of the group
	 * @return Created Group
	 * @throws JsonProcessingException
	 */
	public Group createGroup(String name) throws JsonProcessingException {
		if (!isGroupnameTaken(name)) {
			Group g = new Group(name.toLowerCase());
			insertGroup(g);
			return g;
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param group Group to be inserted in Database
	 * @throws JsonProcessingException
	 */
	private void insertGroup(Group group) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(group);
		gcol.insertOne(Document.parse(json));
	}

	/**
	 * Delete group boolean.
	 *
	 * @param groupname the groupname
	 * @return the boolean
	 */
	public boolean deleteGroup(String groupname) {
		UserServicePrattle userService= new UserServicePrattle(db);
		Group group= findGroupByName(groupname);
		if(group != null){
			List<String> listOfUsers = group.getListOfUsers();
			gcol.deleteOne(Filters.eq("name", groupname.toLowerCase()));
			boolean removedGroup=false;
			if(listOfUsers.isEmpty()){
				removedGroup=true;
			}else {
				for (String username : listOfUsers) {
					removedGroup = userService.removeGroupFromUser(username, groupname.toLowerCase());
				}
			}

			return (removedGroup);
		}
		return false;
	}

	/**
	 *
	 * @param name Name of the group to be found
	 * @return
	 */
	public Group findGroupByName(String name) {
		Document doc = gcol.find(Filters.eq("name", name.toLowerCase())).first();

		return gson.fromJson(gson.toJson(doc), Group.class);
	}

	/**
	 *
	 * @param name name to Check for group
	 * @return
	 */
	public Boolean isGroupnameTaken(String name) {
		FindIterable<Document> iterable = gcol.find(Filters.eq("name", name.toLowerCase()));
		return iterable.first() != null;
	}

	/**
	 *
	 * @param group Group to be updated
	 * @param user User to be inserted
	 * @return True once updated
	 * @throws JsonProcessingException
	 */
	public Boolean addUserToGroup(Group group, User user) {
		gcol.updateOne(Filters.eq("name", group.getName()), Updates.addToSet(LIST_OF_USERS, user.getUsername()));
		UserServicePrattle userService= new UserServicePrattle(db);
		userService.addGroupToUser(user,group);
		return true;
	}

	/**
	 * Remove user from groups boolean.
	 *
	 * @param listOfGroups the list of groups
	 * @param username     the username
	 * @return the boolean
	 */
	public boolean removeUserFromGroups(List<String> listOfGroups, String username) {
		boolean result=false;
		for( String group : listOfGroups) {
			 result = gcol.updateOne(Filters.eq("name", group),
					 Updates.pull(LIST_OF_USERS, username)).getModifiedCount()==1;
		}
		return result;
	}

	/**
	 * Exit group boolean.
	 *
	 * @param username  the username
	 * @param groupname the groupname
	 * @return the boolean
	 */
	public boolean exitGroup(String username, String groupname){
		//remove user from group
		boolean update=gcol.updateOne(Filters.eq("name", groupname),
				Updates.pull(LIST_OF_USERS, username)).getModifiedCount()==1;

		//remove group from user
		UserServicePrattle userService= new UserServicePrattle(db);
		boolean removeGroup = userService.removeGroupFromUser(username,groupname);
		return (update && removeGroup);
	}
}
