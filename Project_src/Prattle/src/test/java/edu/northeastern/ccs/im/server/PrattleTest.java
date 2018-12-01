package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.ServerSingleton;

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
        //PrattleRunabale p = new PrattleRunabale();
        //     p.start();
        if(PrattleRunabale.isDone()) {
            assertEquals(true, PrattleRunabale.isDone());
            ServerSingleton.runServer();
        }
        else
            assertEquals(false, PrattleRunabale.isDone());
        try {
            PrattleRunabale.sendMsg();
        }
        catch(NullPointerException e) { 
            assertFalse(false);
        }
        ClientRunnable client = null;
        PrattleRunabale.removeClient(client);
        ServerSingleton.terminate();
        assertEquals(true, PrattleRunabale.isDone());
    }
}
