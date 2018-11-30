package edu.northeastern.ccs.im;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

/**
 * This class is similar to the java.io.PrintWriter class, but this class's
 * methods work with our non-blocking Socket classes. This class could easily be
 * made to wait for network output (e.g., be made &quot;non-blocking&quot; in
 * technical parlance), but I have not worried about it yet.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.3
 */
public class PrintNetNB {
	/** Channel over which we will write out any messages. */
	private final SocketChannel channel;
	
	/** Logger */
    private static final Logger LOGGER = Logger.getLogger(Logger.class.getName());

	/**
	 * Number of times to try sending a message before we give up in frustration.
	 */
	private static final int MAXIMUM_TRIES_SENDING = 100;
	
	private static final String TRANSFER_ERR_MSG = "Something went wrong during data transfer @ Slick";
	
	private static final String INTEGRATION_ERR_MSG = "Slack integration failed!";
	
	Properties prop = new Properties();
	InputStream input;
	/** Slack WebHook URL */
	private String slackURL;

	/**
	 * Creates a new instance of this class. Since, by definition, this class sends
	 * output over the network, we need to supply the non-blocking Socket instance
	 * to which we will write.
	 * 
	 * @param sockChan Non-blocking SocketChannel instance to which we will send all
	 *                 communication.
	 */
	public PrintNetNB(SocketChannel sockChan) {
		// Remember the channel that we will be using.
		channel = sockChan;
		try {
            input = new FileInputStream("config.properties");
            prop.load(input);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load config file", e);
        }
        slackURL = prop.getProperty("slackURL");
	}

	/**
	 * Creates a new instance of this class. Since, by definition, this class sends
	 * output over the network, we need to supply the non-blocking Socket instance
	 * to which we will write.
	 * 
	 * @param connection Non-blocking Socket instance to which we will send all
	 *                   communication.
	 */
	public PrintNetNB(SocketNB connection) {
		// Remember the channel that we will be using.
		channel = connection.getSocket();
	}

	/**
	 * Send a Message over the network. This method performs its actions by printing
	 * the given Message over the SocketNB instance with which the PrintNetNB was
	 * instantiated. This returns whether our attempt to send the message was
	 * successful.
	 * 
	 * @param msg Message to be sent out over the network.
	 * @return True if we successfully send this message; false otherwise.
	 */
	public boolean print(Message msg) {
		String str = msg.toString();
		ByteBuffer wrapper = ByteBuffer.wrap(str.getBytes());        
		int bytesWritten = 0;
		int attemptsRemaining = MAXIMUM_TRIES_SENDING;
		while (wrapper.hasRemaining() && (attemptsRemaining > 0)) {
			try {
				attemptsRemaining--;
				bytesWritten += channel.write(wrapper);
			} catch (IOException e) {
				// Show that this was unsuccessful
			    Payload payload = Payload.builder()
                        .channel("#cs5500-team-203-f18")
                        .username("Slick Bot")
                        .iconEmoji(":man-facepalming:")
                        .text(TRANSFER_ERR_MSG)
                        .build();

                Slack slack = Slack.getInstance();
                WebhookResponse response = null;
                try {
                    response = slack.send(slackURL, payload);
                    if(!response.getMessage().equalsIgnoreCase("OK"))
                        LOGGER.log(Level.SEVERE, INTEGRATION_ERR_MSG);
                } catch (IOException e1) {
                    LOGGER.log(Level.SEVERE, INTEGRATION_ERR_MSG);
                }
                LOGGER.log(Level.SEVERE, TRANSFER_ERR_MSG);
				return false;
			}
		}
		// Check to see if we were successful in our attempt to write the message
		if (wrapper.hasRemaining()) {
			LOGGER.log(Level.SEVERE, "Something went wrong: {0} out of {1} bytes -- dropping this user", new Object[]{bytesWritten, wrapper.limit()}); 
			 Payload payload = Payload.builder()
                     .channel("#cs5500-team-203-f18")
                     .username("Slick Bot")
                     .iconEmoji(":man-facepalming:")
                     .text(TRANSFER_ERR_MSG)
                     .build();

             Slack slack = Slack.getInstance();
             WebhookResponse response = null;
             try {
                 response = slack.send(slackURL, payload);
                 if(!response.getMessage().equalsIgnoreCase("OK"))
                     LOGGER.log(Level.SEVERE, INTEGRATION_ERR_MSG);
             } catch (IOException e1) {
                 LOGGER.log(Level.SEVERE, INTEGRATION_ERR_MSG);
             }
             LOGGER.log(Level.SEVERE, TRANSFER_ERR_MSG);
             return false;
         }
		return true;
	}
}
