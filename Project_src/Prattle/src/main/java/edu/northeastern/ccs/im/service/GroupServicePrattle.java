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

public class GroupServicePrattle {

	private MongoCollection<Document> gcol;
	private MongoDatabase db;
	private Gson gson;

	public GroupServicePrattle(MongoDatabase db) {
		this.db = db;
		gcol = db.getCollection("Groups");
		gson = new Gson();
	}

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

	private void insertGroup(Group group) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(group);
		gcol.insertOne(Document.parse(json));
	}

	public Group findGroupByName(String name) {
		Document doc = gcol.find(Filters.and(Filters.eq("name", name))).first();

		Group group = gson.fromJson(gson.toJson(doc), Group.class);
		return group;
	}

	private Boolean isGroupnameTaken(String name) {
		FindIterable<Document> iterable = gcol.find(Filters.eq("name", name));
		return iterable.first() != null;
	}

	public Boolean addUserToGroup(Group group, User user) throws JsonProcessingException {

		gcol.updateOne(Filters.eq("name", group.getName()), Updates.addToSet("listOfUsers", user.getName()));

		return true;
	}

}
