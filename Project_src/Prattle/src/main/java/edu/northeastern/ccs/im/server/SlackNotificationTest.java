package edu.northeastern.ccs.im.server;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import edu.northeastern.ccs.im.SlackNotification;

public class SlackNotificationTest {

    @Test
    public void runTest() {
        SlackNotification.notifySlack("https://hooks.slack.com/services/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");
        assertThrows(NullPointerException.class, ()->{
            SlackNotification.notifySlack(null);
        }); 
    }
}
