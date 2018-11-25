package edu.northeastern.ccs.im.MongoDB.Model;


import java.time.LocalDate;
import java.util.Date;

public class Subpoena {


    //    private User user1;
//    private User user2;
//    private Group group;
//    private Date startDate;
//    private Date endDate;
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
