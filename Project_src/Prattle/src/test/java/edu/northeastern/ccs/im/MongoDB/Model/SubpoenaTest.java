package edu.northeastern.ccs.im.MongoDB.Model;

import edu.northeastern.ccs.im.Message;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Chetan Mahale
 */
public class SubpoenaTest {
    @Test
    public void testSubpoenaObject(){
        Subpoena subpoena = new Subpoena("sender","receiver","abc", LocalDate.MIN,LocalDate.MAX);
        assertEquals("sender",subpoena.getUser1());
        assertEquals("receiver",subpoena.getUser2());
        assertEquals("abc",subpoena.getGroup());
        assertEquals(LocalDate.MIN,subpoena.getStartDate());
        assertEquals(LocalDate.MAX,subpoena.getEndDate());
        subpoena.setId("1");
        assertEquals("1", subpoena.getId());
        List<String> list = new ArrayList<>();
        list.add("A");
        subpoena.setListOfMessages(list);
        assertEquals(1, subpoena.getListOfMessages().size());
        assertTrue(!subpoena.toString().isEmpty());
    }
}
