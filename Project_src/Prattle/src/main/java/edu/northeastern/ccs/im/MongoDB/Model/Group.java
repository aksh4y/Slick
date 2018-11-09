package edu.northeastern.ccs.im.MongoDB.Model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int id;
    private String name;
    private List<String> listOfUsers;

    public Group(String name){
        this.name = name;
        this.listOfUsers = new ArrayList<>();
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

    public List<String > getListOfUsers() {
        return listOfUsers;
    }

    public void addUserTOGroup(User user) {
        listOfUsers.add(user.getName());
    }

}