package edu.northeastern.ccs.im.MongoDB.Model;


import java.util.Date;

public class Subpoena {
    private User user1;
    private User user2;
    private Group group;
    private Date startDate;
    private Date endDate;

    public Subpoena(User user1,User user2, Group group, Date fromDate, Date toDate){
        this.user1=user1;
        this.user2=user2;
        this.group= group;
        this.startDate= fromDate;
        this.endDate= toDate;
    }

}
