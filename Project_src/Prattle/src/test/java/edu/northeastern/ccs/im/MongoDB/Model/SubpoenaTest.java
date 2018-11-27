package edu.northeastern.ccs.im.MongoDB.Model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }
}
