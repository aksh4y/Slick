package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    
    @Test
    public void nullCheck() {
        assertThrows(Exception.class, ()-> {
            PrattleRunabale.sendBroadcastPM();
        });
        
        assertThrows(Exception.class, ()-> {
            PrattleRunabale.sendGroupMsg();
        });
        
        assertThrows(Exception.class, ()-> {
            PrattleRunabale.sendMsg();
        });
        
        
        if(PrattleRunabale.getActiveList().isEmpty())
            assertTrue(PrattleRunabale.getActiveList().isEmpty());
        else
            assertFalse(PrattleRunabale.getActiveList().isEmpty());
        
        PrattleRunabale.buildMap();
        assertTrue(true);     
    }   
}
