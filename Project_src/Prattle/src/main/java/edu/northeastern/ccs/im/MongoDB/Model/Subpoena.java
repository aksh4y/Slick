package edu.northeastern.ccs.im.MongoDB.Model;


import java.time.LocalDate;

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

    public Subpoena(String user1,String user2, String group, LocalDate fromDate, LocalDate toDate){
        this.user1=user1;
        this.user2=user2;
        this.group= group;
        this.startDate= fromDate;
        this.endDate= toDate;
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

}
