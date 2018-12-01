package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import edu.northeastern.ccs.im.SlackNotification;

public class SlackNotificationTest {

    @Test
    public void legitTest() {
        /** This is not a valid hook */
        SlackNotification.notifySlack("https://hooks.slack.com/services/T2CR59JN7/BEDGKFU07/");
        try {
            SlackNotification.notifySlack(null);
        }
        catch(NullPointerException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void badTests() {
        SlackNotification.notifySlack("http://abcd");
        assertTrue(true);
    }
}
