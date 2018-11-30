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
        Subpoena testSubpoena2 = new Subpoena("Tester1","Tester2","TestGroup",
                                                LocalDate.now(),LocalDate.MIN);
        assertEquals("", subpoenaService.getIdOfSubpoena(testSubpoena2));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(testSubpoena));
        assertEquals(false, subpoenaService.deleteSubpoena("6bff384c9b5f1d67b7bb89f1"));
    }
    @Test
    public void testActiveSubpoenas(){
        Subpoena subpoena = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                                                            LocalDate.now(),LocalDate.now().plusDays(1));
        Subpoena subpoena1 = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                LocalDate.now().minusDays(2),LocalDate.now().minusDays(2));
        Subpoena subpoena2 = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                LocalDate.now().plusDays(2),LocalDate.now().plusDays(2));
        Subpoena subpoena3 = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                LocalDate.now().minusDays(2),LocalDate.now().plusDays(2));
        Subpoena subpoena4 = subpoenaService.createSubpoena("Tester1","Tester2","TestingGroup",
                LocalDate.now(),LocalDate.now());
        assertTrue(!subpoenaService.getActiveSubpoenas().isEmpty());
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(subpoena),"wassup");
        String id = subpoenaService.getIdOfSubpoena(subpoena);
        Subpoena newSubpoena = subpoenaService.querySubpoenaById(id);
        assertEquals(1,newSubpoena.getListOfMessages().size());
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena1));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena2));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena3));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena4));
    }
}
