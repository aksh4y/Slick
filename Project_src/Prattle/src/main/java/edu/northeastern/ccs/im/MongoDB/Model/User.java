package edu.northeastern.ccs.im.MongoDB.Model;
import edu.northeastern.ccs.im.service.UserServicePrattle;

import java.util.ArrayList;
import java.util.List;

/**
 * The type User.
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
    private Boolean parentalControl;


    /**
     * Instantiates a new User.
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
        this.parentalControl=false;
    }

    /**
     * Gets id.
     *
     * @return id of the user
     */
    public int getId() {
        return id;
    }

    /**
     * git
     *
     * @param id int id to set for the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets username.
     *
     * @return username for the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username String username for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return String password for the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password String to set as password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets list of groups.
     *
     * @return list of groups the user belongs to
     */
    public List<String> getListOfGroups() {
        return listOfGroups;
    }

    /**
     * Gets my messages.
     *
     * @return the my messages
     */
    public List<String> getMyMessages() {
        return myMessages;
    }

    /**
     * Sets my messages.
     *
     * @param myMessages the my messages
     */
    public void setMyMessages(List<String> myMessages) {
        this.myMessages = myMessages;
    }

    /**
     * Gets my unread messages.
     *
     * @return the my unread messages
     */
    public List<String> getMyUnreadMessages() {
        return myUnreadMessages;
    }

    /**
     * Sets my unread messages.
     *
     * @param myUnreadMessages the my unread messages
     */
    public void setMyUnreadMessages(List<String> myUnreadMessages) {
        this.myUnreadMessages = myUnreadMessages;
    }

    /**
     * Gets parental control.
     *
     * @return the parental control
     */
    public Boolean getParentalControl() {
        return parentalControl;
    }

    /**
     * Sets parental control.
     *
     * @param parentalControl the parental control
     */
    public void setParentalControl(Boolean parentalControl) {
        this.parentalControl = parentalControl;
    }
}