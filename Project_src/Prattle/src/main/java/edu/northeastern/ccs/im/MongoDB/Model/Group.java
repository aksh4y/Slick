package edu.northeastern.ccs.im.MongoDB.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chetan Mahale
 * @version 1.0
 */
public class Group {

    /**
     * Private variables id, name and list of users for Group
     */
    private int id;
    private String name;
    private List<String> listOfUsers;

    /**
     *
     * @param name String representing name of the group
     */
    public Group(String name){
        this.name = name;
        this.listOfUsers = new ArrayList<>();
    }

    /**
     *
     * @return id of the Group
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id int id to set for the group
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return name of the group
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name String to set as name of the group
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return list of names of users
     */
    public List<String > getListOfUsers() {
        return listOfUsers;
    }

    /**
     *
     * @param user User to add to the groups
     */
    public void addUserTOGroup(User user) {
        listOfUsers.add(user.getUsername());
    }

}