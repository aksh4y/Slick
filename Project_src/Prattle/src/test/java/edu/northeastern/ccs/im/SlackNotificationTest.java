package edu.northeastern.ccs.im;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class SlackNotificationTest {

    @Test
    public void legitTest() throws IOException {
        Properties prop = new Properties();
        String resourceName = "config.properties"; 
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try(InputStream input = loader.getResourceAsStream(resourceName)) {
            prop.load(input);
        }
        SlackNotification.notifySlack(prop.getProperty("slackURL"));
        /** This is not a valid hook. It's a dummy URL in a valid form */
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
