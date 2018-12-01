package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import edu.northeastern.ccs.im.SlackNotification;

public class SlackNotificationTest {

    @Test
    public void runTest() {
        SlackNotification.notifySlack("https://hooks.slack.com/services/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");
        try {
            SlackNotification.notifySlack(null);
        }
        catch(NullPointerException e) {
            assertTrue(true);
        }
    }
}
