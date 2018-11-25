package edu.northeastern.ccs.im.MongoDB.Model;


import java.time.LocalDate;

public class Subpoena {

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

}
