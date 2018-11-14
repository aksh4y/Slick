package edu.northeastern.ccs.im.MongoDB.Model;

import edu.northeastern.ccs.im.Message;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String password;
    private List<Group> listOfGroups;
    private List<Message> myMessages;

    public User(){
        this.listOfGroups = new ArrayList<Group>();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public List<Group> getListOfGroups() {
        return listOfGroups;
    }

    public void addGroupToUser(Group newGroup) {
        listOfGroups.add(newGroup);
    }

}