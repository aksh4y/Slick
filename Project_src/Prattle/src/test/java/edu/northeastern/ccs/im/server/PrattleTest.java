package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.PrattleRunabale;

/**
 * Test the Prattle class
 *
 * @author Akshay
 */
public class PrattleTest {

    /**
     * Prattle test.
     */
    @Test
    public void prattleTest() {
        PrattleRunabale p = new PrattleRunabale();
   //     p.start();
        if(p.isDone()) {
            assertEquals(true, p.isDone());
            p.start();
        }
        else
            assertEquals(false, p.isDone());
        p.sendMsg();
        ClientRunnable client = null;
        p.removeClient(client);
        p.terminate();
        assertEquals(true, p.isDone());
    }
}
