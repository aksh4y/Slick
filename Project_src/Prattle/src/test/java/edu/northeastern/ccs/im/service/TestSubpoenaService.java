package edu.northeastern.ccs.im.service;

import com.mongodb.client.MongoDatabase;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
import org.junit.jupiter.api.Test;
import java.util.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestSubpoenaService {

    private MongoDatabase db = MongoConnection.createConnection();
    private SubpoenaServicePrattle subpoenaService = new SubpoenaServicePrattle(db);

    @Test
    public void testCreateSubpoena(){
        try {
            Subpoena testSubpoena = subpoenaService.createSubpoena("Tester1", "Tester2", "TestingGroup",
                    LocalDate.of(2019, 12, 23), LocalDate.of(2019, 12, 24));
            Subpoena newSubpoena = subpoenaService.querySubpoenaById(subpoenaService.getIdOfSubpoena(testSubpoena));
            assertEquals(newSubpoena.toString(), testSubpoena.toString());
            assertNull(subpoenaService.querySubpoenaById("somestring"));
            Subpoena testSubpoena2 = new Subpoena("Tester1", "Tester2", "TestGroup",
                    LocalDate.now(), LocalDate.MIN);
            assertEquals("", subpoenaService.getIdOfSubpoena(testSubpoena2));
            subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(testSubpoena));
            assertEquals(false, subpoenaService.deleteSubpoena("6bff384c9b5f1d67b7bb89f1"));
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testActiveSubpoenas(){
        try {
            Subpoena subpoena = subpoenaService.createSubpoena("Tester1", "Tester2", "TestingGroup",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Subpoena subpoena1 = subpoenaService.createSubpoena("Tester1", "Tester2", "TestingGroup",
                    LocalDate.now().minusDays(2), LocalDate.now().minusDays(2));
            Subpoena subpoena2 = subpoenaService.createSubpoena("Tester1", "Tester2", "TestingGroup",
                    LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));
            Subpoena subpoena3 = subpoenaService.createSubpoena("Tester1", "Tester2", "TestingGroup",
                    LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));
            Subpoena subpoena4 = subpoenaService.createSubpoena("Tester1", "Tester2", "TestingGroup",
                    LocalDate.now(), LocalDate.now());
            assertTrue(!subpoenaService.getActiveSubpoenas().isEmpty());
            subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(subpoena), "wassup");
            String id = subpoenaService.getIdOfSubpoena(subpoena);
            Subpoena newSubpoena = subpoenaService.querySubpoenaById(id);
            assertEquals(1, newSubpoena.getListOfMessages().size());
            subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena));
            subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena1));
            subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena2));
            subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena3));
            subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(subpoena4));
        }catch (Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void testSubpoenasMessages(){
        Subpoena s1 = subpoenaService.createSubpoena("cops","robbers","",LocalDate.of(2018,11,7),
                LocalDate.of(2018,12,19));
        Subpoena s2= subpoenaService.createSubpoena("","","jail",LocalDate.of(2018,11,9),
                LocalDate.of(2018,12,19));

        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"1543687707792 /127.0.0.1:57072 [Private Msg] cops: is this tracked? -> robbers /Offline");
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"1543687780193 /127.0.0.1:57072 [Private Msg] cops: gotta check if this is tracked -> robbers /Offline");
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"1543687883049 /127.0.0.1:57111 [Private Msg] robbers: i think they know about this -> cops /127.0.0.1:57072");
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"1543687898449 /127.0.0.1:57111 [Private Msg] robbers: they know what you did last summer -> cops /127.0.0.1:57072");
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"1543687969601 /127.0.0.1:57072 [Private Msg] cops: DAMMIT! -> robbers /127.0.0.1:57111");


        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s2),"1543688445876 /127.0.0.1:57306 [cops@jail] Hi mark zuck,Alexa and Siri -> robbers /Offline");
        subpoenaService.addToSubpoenaMessages(subpoenaService.getIdOfSubpoena(s2),"1543688536693 /127.0.0.1:57332 [robbers@jail] oh oh -> cops /127.0.0.1:57306");

        assertEquals(3,subpoenaService.searchSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"sender","cops").size());
        assertEquals(3,subpoenaService.searchSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"receiver","robbers").size());
        assertEquals(0,subpoenaService.searchSubpoenaMessages(subpoenaService.getIdOfSubpoena(s1),"xyz","cops").size());

        assertEquals(1,subpoenaService.searchSubpoenaMessages(subpoenaService.getIdOfSubpoena(s2),"sender","cops").size());
        assertEquals(1,subpoenaService.searchSubpoenaMessages(subpoenaService.getIdOfSubpoena(s2),"receiver","robbers").size());


        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(s1));
        subpoenaService.deleteSubpoena(subpoenaService.getIdOfSubpoena(s2));
    }
}
