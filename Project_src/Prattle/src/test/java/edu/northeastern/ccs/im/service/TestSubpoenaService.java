package edu.northeastern.ccs.im.service;

import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
import org.junit.jupiter.api.Test;
import java.util.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestSubpoenaService {

    MongoConnection mongoConnection = new MongoConnection();
    private MongoDatabase db = mongoConnection.createConnection();
    private GroupServicePrattle groupService = new GroupServicePrattle(db);
    private UserServicePrattle userService = new UserServicePrattle(db);
    private SubpoenaServicePrattle subpoenaService = new SubpoenaServicePrattle(db);

    @Test
    public void testCreateSubpoena(){
        Subpoena testSubpoena = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                                        LocalDate.of(2019,12,23),LocalDate.of(2019, 12, 24));
        Subpoena newSubpoena = subpoenaService.querySubpoenaById(subpoenaService.getIdOfSubpoena(testSubpoena));
        assertEquals(newSubpoena.toString(),testSubpoena.toString());
        assertNull(subpoenaService.querySubpoenaById("somestring"));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(testSubpoena));
    }
    @Test
    public void testActiveSubpoenas(){
        Subpoena subpoena = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                                                            LocalDate.now(),LocalDate.now().plusDays(1));
        assertTrue(!subpoenaService.getActiveSubpoenas().isEmpty());
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(subpoena),"wassup");
        String id = subpoenaService.getIdOfSubpoena(subpoena);
        Subpoena newSubpoena = subpoenaService.querySubpoenaById(id);
        assertEquals(1,newSubpoena.getListOfMessages().size());
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena));
//        System.out.println(userService.updateMessage("ipwrap1", "/127.0.0.1:64465 PRIVATE ipwrap2 hey abcdefg", "/127.0.0.1:64465 [Private Msg] ipwrap1: hey /127.0.0.1:64456"));
    }
}
