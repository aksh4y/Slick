package edu.northeastern.ccs.im.service;

import org.bson.Document;

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
			Group g = new Group(name);
			g.setName(name);

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
	 *
	 * @param name Name of the group to be found
	 * @return
	 */
	public Group findGroupByName(String name) {
		Document doc = gcol.find(Filters.eq("name", name)).first();

		return gson.fromJson(gson.toJson(doc), Group.class);
	}

	/**
	 *
	 * @param name name to Check for group
	 * @return
	 */
	public Boolean isGroupnameTaken(String name) {
		FindIterable<Document> iterable = gcol.find(Filters.eq("name", name));
		return iterable.first() != null;
	}

	/**
	 *
	 * @param group Group to be updated
	 * @param user User to be inserted
	 * @return True once updated
	 * @throws JsonProcessingException
	 */
	public Boolean addUserToGroup(Group group, User user) throws JsonProcessingException {

		gcol.updateOne(Filters.eq("name", group.getName()), Updates.addToSet("listOfUsers", user.getUsername()));

		UserServicePrattle user_service= new UserServicePrattle(db);
		user_service.addGroupToUser(user,group);
		return true;
	}

	public void removeUserFromGroups(List<Group> listOfGroups, String username) throws JsonProcessingException {
		for( Group group : listOfGroups) {
			gcol.updateOne(Filters.eq("name", group.getName()), Updates.pull("listOfUsers", username));
		}
	}

}
