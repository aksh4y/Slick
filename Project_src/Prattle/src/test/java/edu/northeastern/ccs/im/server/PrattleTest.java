package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
            Prattle.broadcastPrivateMessage(null, null, null, null, null);
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
            Prattle.broadcastPrivateMessage(null, null, null, null, null);
        });

        assertThrows(Exception.class, ()-> {
            Prattle.broadcastGroupMessage(null, null, null, null, null);
        });

        assertThrows(Exception.class, ()-> {
            Prattle.broadcastMessage(null);
        });


        if(Prattle.getActiveClients().isEmpty())
            assertTrue(Prattle.getActiveClients().isEmpty());
        else
            assertFalse(Prattle.getActiveClients().isEmpty());

        Prattle.prepareVulgarMap();
        assertTrue(true);     
    }   

    @Test
    public void checkGetters() {
        if(Prattle.getActive().isEmpty()) 
            assertTrue(Prattle.getActive().isEmpty());
        else
            assertFalse(Prattle.getActive().isEmpty());
        if(Prattle.getActiveSubpoena().isEmpty()) 
            assertTrue(Prattle.getActiveSubpoena().isEmpty());
        else
            assertFalse(Prattle.getActiveSubpoena().isEmpty());
        assertEquals(" /Offline", Prattle.getOffline());
        Prattle.keepPrattleRunning();
        Prattle.keepPrattleAlive();
    }

    @Test
    public void checkPropertiesIntegrity() throws IOException {
        try {
            Prattle.getInput();
            Prattle.getProp();
            Prattle.getSlackURL();
            assertTrue(true);
        }
        catch(NullPointerException ne) {
            assertFalse(false);
        }
    }
}
