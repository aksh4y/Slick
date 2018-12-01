package edu.northeastern.ccs.im;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

public class SlackNotification {
    
    private SlackNotification() {}

    private static final String TRANSFER_ERR_MSG = "Something went wrong during data transfer @ Slick";

    private static final String INTEGRATION_ERR_MSG = "Slack integration failed!";
    
    private static final Logger LOGGER = Logger.getLogger(Logger.class.getName());
    /**
     * Notify slack of this error
     */
    public static void notifySlack(String slackURL) {
        Payload payload = Payload.builder().channel("#cs5500-team-203-f18").username("Slick Bot")
                .iconEmoji(":man-facepalming:").text(TRANSFER_ERR_MSG).build();
        Slack slack = Slack.getInstance();
        WebhookResponse response = null;
        try {
            response = slack.send(slackURL, payload);
            if (!response.getMessage().equalsIgnoreCase("OK"))
                LOGGER.log(Level.SEVERE, INTEGRATION_ERR_MSG);
        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, INTEGRATION_ERR_MSG);
        }
    }

}
