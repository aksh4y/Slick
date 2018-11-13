package edu.northeastern.ccs.im.service;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.User;

public class UserServicePrattle {

	private MongoCollection<Document> col;
	private MongoDatabase db;
	private Gson gson;

	public UserServicePrattle(MongoDatabase db) {
		this.db = db;
		col = db.getCollection("Users");
		gson = new Gson();
	}

	public User authenticateUser(String username, String password) {
		Document doc = col.find(Filters.and(Filters.eq("name", username), Filters.eq("password", password))).first();

		return gson.fromJson(gson.toJson(doc), User.class);
	}

	public User createUser(String username, String password) throws JsonProcessingException {
		if (!isUsernameTaken(username)) {
			User u = new User();
			u.setName(username);
			u.setPassword(password);
			insertUser(u);
			return u;
		}
		return null;
	}

	private void insertUser(User user) throws JsonProcessingException  {

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		col.insertOne(Document.parse(json));
	}

	public User findUserByUsername(String username) {
		Document doc = col.find(Filters.and(Filters.eq("name", username))).first();

		User user = gson.fromJson(gson.toJson(doc), User.class);
		return user;
	}

	public Boolean isUsernameTaken(String name) {
		FindIterable<Document> iterable = col.find(Filters.eq("name", name));
		return iterable.first() != null;
	}

	public Boolean updateUser(User user, String updatedPassword) {

		col.updateOne(Filters.eq("name", user.getName()),
				new Document("$set", new Document("password", updatedPassword)));
		return true;
	}

	public Boolean addGroupToUser(User user, Group group) throws JsonProcessingException {
		// col.updateOne(Filters.eq("name", user.getName()), new Document("$push",
		// {"listOfGroups", group}));
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(group);

		col.updateOne(Filters.eq("name", user.getName()), Updates.addToSet("listOfGroups", Document.parse(json)));

		return true;
	}
}
