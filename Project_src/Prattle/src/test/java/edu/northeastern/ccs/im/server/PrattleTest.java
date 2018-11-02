package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.PrattleRunabale;

/**
 * Test the Prattle class
 * @author Akshay
 *
 */
public class PrattleTest {

    @Test
    public void prattleTest() {
        PrattleRunabale p = new PrattleRunabale();
        p.start();
        p.sendMsg();
        assertEquals(false, p.isDone());
        ClientRunnable client = null;
        p.removeClient(client);
        p.terminate();
        assertEquals(true, p.isDone());
    }
}
