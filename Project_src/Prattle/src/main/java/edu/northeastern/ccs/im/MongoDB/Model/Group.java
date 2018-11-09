package edu.northeastern.ccs.im.MongoDB.Model;

import java.util.List;

public class Group {
	private String _id;
	private String groupName;
	private List<User> userList;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	public void addUser(User user) {
		this.userList.add(user);
	}
	
}
