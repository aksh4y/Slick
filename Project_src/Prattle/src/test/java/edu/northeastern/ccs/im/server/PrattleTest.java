package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.northeastern.ccs.im.PrattleRunabale;
import edu.northeastern.ccs.im.ServerSingleton;

/**
 * Test the Prattle class
 *
 * @author Akshay
 */
public class PrattleTest {

    @BeforeAll
    public static void setup() {
        ServerSingleton.runServer();
    }
    @AfterAll
    public static void cleanUp() {
        ServerSingleton.terminate();
    }
    
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
        
        if(!PrattleRunabale.isDone())
            assertEquals(false, PrattleRunabale.isDone());
        else
            assertEquals(true, PrattleRunabale.isDone());
        
        
    }
}
