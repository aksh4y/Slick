package edu.northeastern.ccs.im.MongoDB.Model;

import edu.northeastern.ccs.im.Message;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Subpoena {

    public String getId() {
        return id;
    }

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

    public Subpoena(String user1,String user2, String group, LocalDate fromDate, LocalDate toDate){
        this.user1=user1;
        this.user2=user2;
        this.group= group;
        this.startDate= fromDate;
        this.endDate= toDate;
        this.listOfMessages = new ArrayList<>();
    }
    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public String getGroup() {
        return group;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<String> getListOfMessages() {
        return listOfMessages;
    }

    public void setListOfMessages(List<String> listOfMessages) {
        this.listOfMessages = listOfMessages;
    }
    @Override
    public String toString(){
        return String.format("%s %s %s %s %s", this.user1, this.user2, this.group, this.startDate, this.endDate);
    }
}
