package edu.northeastern.ccs.im.MongoDB.Model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Subpoena.
 *
 * @author Peter
 */
public class Subpoena {

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String user1;
    private String user2;
    private String group;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<String> listOfMessages;

    /**
     * Instantiates a new Subpoena.
     *
     * @param user1    the user 1
     * @param user2    the user 2
     * @param group    the group
     * @param fromDate the from date
     * @param toDate   the to date
     */
    public Subpoena(String user1,String user2, String group, LocalDate fromDate, LocalDate toDate){
        this.user1=user1;
        this.user2=user2;
        this.group= group;
        this.startDate= fromDate;
        this.endDate= toDate;
        this.listOfMessages = new ArrayList<>();
    }

    /**
     * Gets user 1.
     *
     * @return the user 1
     */
    public String getUser1() {
        return user1;
    }

    /**
     * Gets user 2.
     *
     * @return the user 2
     */
    public String getUser2() {
        return user2;
    }

    /**
     * Gets group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets list of messages.
     *
     * @return the list of messages
     */
    public List<String> getListOfMessages() {
        return listOfMessages;
    }

    /**
     * Sets list of messages.
     *
     * @param listOfMessages the list of messages
     */
    public void setListOfMessages(List<String> listOfMessages) {
        this.listOfMessages = listOfMessages;
    }

    @Override
    public String toString(){
        return String.format("%s %s %s %s %s", this.user1, this.user2, this.group, this.startDate, this.endDate);
    }
}
