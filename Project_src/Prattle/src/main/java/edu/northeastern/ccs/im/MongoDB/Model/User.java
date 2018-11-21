package edu.northeastern.ccs.im.MongoDB.Model;
import edu.northeastern.ccs.im.service.UserServicePrattle;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter
 * @version 1.0
 */
public class User {

    /**
     * Private variables id, name password and list of groups for User
     */
    private int id;
    private String username;
    private String password;
    private List<String> listOfGroups;
    private List<String> myMessages;



    private List<String> myUnreadMessages;

    /**
     *
     * @param username username for the user
     * @param password password for the user
     */
    public User(String username, String password){
        this.username = username;
        this.password = UserServicePrattle.hashPassword(password);
        this.listOfGroups = new ArrayList<>();
        this.myMessages = new ArrayList<>();
        this.myUnreadMessages = new ArrayList<>();
    }

    /**
     *
     * @return id of the user
     */
    public int getId() {
        return id;
    }

    /**git
     *
     * @param id int id to set for the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return username for the user
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username String username for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return String password for the user
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password String to set as password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return list of groups the user belongs to
     */
    public List<String> getListOfGroups() {
        return listOfGroups;
    }

    public List<String> getMyMessages() {
        return myMessages;
    }

    public void setMyMessages(List<String> myMessages) {
        this.myMessages = myMessages;
    }

    public List<String> getMyUnreadMessages() {
        return myUnreadMessages;
    }

    public void setMyUnreadMessages(List<String> myUnreadMessages) {
        this.myUnreadMessages = myUnreadMessages;
    }
}