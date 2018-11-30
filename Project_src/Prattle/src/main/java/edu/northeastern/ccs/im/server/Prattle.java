
package edu.northeastern.ccs.im.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;
import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.service.SubpoenaServicePrattle;
import edu.northeastern.ccs.im.service.UserServicePrattle;

/**
 * A network server that communicates with IM clients that connect to it. This
 * version of the server spawns a new thread to handle each client that connects
 * to it. At this point, messages are broadcast to all of the other clients. It
 * does not send a response when the user has gone off-line.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.3
 */

public abstract class Prattle {
    /** Amount of time we should wait for a signal to arrive. */
    private static final int DELAY_IN_MS = 50;

    /** Number of threads available in our thread pool. */
    private static final int THREAD_POOL_SIZE = 20;

    /** Delay between times the thread pool runs the client check. */
    private static final int CLIENT_CHECK_DELAY = 200;

    /** Constant for Offline users */
    private static final String OFFLINE = " /Offline";

    /** Collection of threads that are currently being used. */
    private static ConcurrentLinkedQueue<ClientRunnable> active;

    /** HashMap of threads that are currently being userd */
    private static Map<String, ClientRunnable> activeClients;

    private static boolean done;

    private static MongoDatabase db;

    private static UserServicePrattle userService;

    private static SubpoenaServicePrattle subpoenaService;

    private static Map<String, Subpoena> activeSubpoena;

    private static HashSet<String> vulgar;

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(Logger.class.getName());

    /** Slack WebHook URL */
    private static final String SLACK_URL = "https://hooks.slack.com/services/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";
    /** All of the static initialization occurs in this "method" */
    static {
        // Create the new queue of active threads.
        active = new ConcurrentLinkedQueue<>();
        activeClients = new HashMap<>();
        activeSubpoena = new HashMap<>();
        db = MongoConnection.createConnection();
        userService = new UserServicePrattle(db);
        subpoenaService = new SubpoenaServicePrattle(db);
        vulgar = new HashSet<>();
        createActiveSubpoenaMap();
        prepareVulgarMap();
    }

    /**
     * Broadcast a given message to all the other IM clients currently on the
     * system. This message _will_ be sent to the client who originally sent it.
     * 
     * @param message
     *            Message that the client sent.
     */
    public static void broadcastMessage(Message message) {
        Set<String> sbIds = handleSubpoena(message);
        // Loop through all of our active threads
        User sender = userService.findUserByUsername(message.getName());
        String senderIP = null;
        String msg = null;
        if (sender == null)
            return;
        ClientRunnable cr = activeClients.get(sender.getUsername());
        if (cr == null || !cr.isInitialized()) // Inactive senders can't broadcast
            return;
        senderIP = cr.getIP();
        msg = "[BROADCASTED] " + message.getText();
        if (senderIP == null) // Not CALEA compliant
            return;
        userService.addToMyMessages(sender, senderIP + " " + msg); // sender's copy
        message.setText(message.getText() + " " + senderIP);
        broadcastToAll(message, sbIds);
    }

    /**
     * Send broadcast msgs to all active clients
     * 
     * @param message
     * @param sbIds
     */
    private static void broadcastToAll(Message message, Set<String> sbIds) {
        String msg;
        msg = "[BROADCAST] " + message.getName() + ": " + message.getText();
        for (ClientRunnable tt : active) { // receiver's copy
            // Do not send the message to any clients that are not ready to receive it.
            if (tt.isInitialized() && !tt.getName().equalsIgnoreCase(message.getName()) && !tt.isSubpoena()) {
                User u = userService.findUserByUsername(tt.getName());
                if (u != null) {
                    if (u.getParentalControl())
                        handleParental(message, msg, tt, u);
                    else {
                        userService.addToMyMessages(u, msg);
                        tt.enqueueMessage(message);
                    }
                } else
                    tt.enqueueMessage(message);
            }
        }
        broadcastToSubpoena(msg, sbIds);
    }

    /**
     * Broadcast msgs to active subpoenas
     * 
     * @param message
     * @param sbIds
     * @param tt
     */
    private static void broadcastToSubpoena(String msg, Set<String> sbIds) {
        for (String sID : sbIds) {
            ClientRunnable tt = activeClients.get(sID);
            if ( tt!= null && tt.isInitialized()) {
                tt.enqueueMessage(Message.makeHistoryMessage(msg));
            }
            subpoenaService.addToSubpoenaMessages(sID, msg);
        }

    }

    /**
     * Handle active parental control msgs
     * 
     * @param message
     * @param msg
     * @param tt
     * @param u
     */
    private static void handleParental(Message message, String msg, ClientRunnable tt, User u) {
        String msgText = message.getText();
        userService.addToMyMessages(u, checkVulgar(msg));
        Message filtred = Message.makeBroadcastMessage(message.getName(), checkVulgar(msgText));
        filtred.setText(checkVulgar(msgText));
        tt.enqueueMessage(filtred);
    }

    /**
     * Broadcast a given private message to all the other given receiver handle
     * system.
     * 
     * @param message
     *            Message that the client sent.
     * @param receiver
     *            the receiver
     */
    public static void broadcastPrivateMessage(User sender, Message message, String receiver, String senderMsg,
            String receiverMsg) {
        Set<String> sbIds = handleSubpoena(message);
        User recipient = userService.findUserByUsername(receiver);
        if (recipient == null) // Valid receiver
            return;
        ClientRunnable cr = activeClients.get(receiver);
        if (cr != null && cr.isInitialized()) {
            String newMsg = receiverMsg.substring(0, receiverMsg.length() - 9);
            if (recipient.getParentalControl()) {
                newMsg = checkVulgar(newMsg);
                String msgText = message.getText();
                msgText = checkVulgar(msgText);
                message.setText(msgText);
            }
            newMsg += " " + cr.getIP();
            userService.addToMyMessages(recipient, newMsg);
            cr.enqueueMessage(message);
            newMsg = senderMsg.substring(0, senderMsg.length() - 9);
            newMsg += " " + cr.getIP();
            userService.addToMyMessages(sender, newMsg);
        } else {
            if (recipient.getParentalControl())
                userService.addToUnreadMessages(recipient, checkVulgar(receiverMsg));
            else
                userService.addToUnreadMessages(recipient, receiverMsg);


            userService.addToMyMessages(sender, senderMsg);
        }
        // Handle active subpoenas
        handleActiveSubpoenas(receiver, receiverMsg, sbIds, cr);
    }

    /**
     * Handle active subpoenas
     * 
     * @param receiver
     * @param receiverMsg
     * @param sbIds
     * @param cr
     */
    private static void handleActiveSubpoenas(String receiver, String receiverMsg, Set<String> sbIds,
            ClientRunnable cr) {
        // Loop through all of our active subpoenas
        for (String sID : sbIds) {
            ClientRunnable tt = activeClients.get(sID);
            String newMsg = receiverMsg.substring(0, receiverMsg.length() - 9);
            if (cr != null && cr.isInitialized()) {
                newMsg += " ->" + receiver + " " + cr.getIP();
                if (tt != null && tt.isInitialized()) {
                    tt.enqueueMessage(Message.makeHistoryMessage(newMsg));
                }
                subpoenaService.addToSubpoenaMessages(sID, newMsg);
            } else {
                newMsg += " -> " + receiver + OFFLINE;
                if (tt != null && tt.isInitialized()) {
                    tt.enqueueMessage(Message.makeHistoryMessage(newMsg));
                }
                subpoenaService.addToSubpoenaMessages(sID, newMsg);
            }
        }
    }

    /**
     * Send group message to all group members
     * 
     * @param msg
     *            the Message object
     * @param listOfUsers
     *            group members
     * @param m
     *            message in string format
     */
    public static void broadcastGroupMessage(User sender, Message msg, List<String> listOfUsers, String senderMsg,
            String receiverMsg) {
        Set<String> sbIds = handleSubpoena(msg);
        for (String user : listOfUsers) {
            if (user.equals(msg.getName())) // Sender
                continue;
            User recipient = userService.findUserByUsername(user);
            if (recipient == null)
                continue;
            ClientRunnable cr = activeClients.get(user);
            if (cr != null && cr.isInitialized()) {
                handleOnlineClient(sender, msg, senderMsg, receiverMsg, user, recipient, cr);
            } else {
                handleOfflineClient(sender, senderMsg, receiverMsg, user, recipient);
            }
            handleActiveSubpoenas(receiverMsg, sbIds, user, cr);
        }
    }

    /**
     * Handle offline clients
     * 
     * @param sender
     * @param senderMsg
     * @param receiverMsg
     * @param user
     * @param recipient
     */
    private static void handleOfflineClient(User sender, String senderMsg, String receiverMsg, String user,
            User recipient) {
        String newMsg = senderMsg;
        newMsg += " -> " + user + OFFLINE;
        userService.addToMyMessages(sender, newMsg);
        newMsg = receiverMsg;
        newMsg += " -> " + user + OFFLINE;
        if (recipient.getParentalControl()) {
            userService.addToUnreadMessages(recipient, checkVulgar(newMsg));
        } else {
            userService.addToUnreadMessages(recipient, newMsg);
        }
    }

    /**
     * Handle online clients
     * 
     * @param sender
     * @param msg
     * @param senderMsg
     * @param receiverMsg
     * @param user
     * @param recipient
     * @param cr
     */
    private static void handleOnlineClient(User sender, Message msg, String senderMsg, String receiverMsg, String user,
            User recipient, ClientRunnable cr) {
        String newMsg = receiverMsg;
        newMsg += " -> " + user + " " + cr.getIP();
        if (recipient.getParentalControl()) {
            userService.addToMyMessages(recipient, checkVulgar(newMsg));
            String msgText = msg.getText();
            Message filtred = Message.makeGroupMessage(msg.getName(), msg.getMsgRecipient(), checkVulgar(msgText));
            cr.enqueueMessage(filtred);
        } else {
            userService.addToMyMessages(recipient, newMsg); // recipient's copy
            cr.enqueueMessage(msg);
        }
        newMsg = senderMsg;
        newMsg += " -> " + user + " " + cr.getIP();
        userService.addToMyMessages(sender, newMsg); // sender's copy
    }

    /**
     * Handle active subpoenas
     * 
     * @param receiverMsg
     * @param sbIds
     * @param user
     * @param cr
     */
    private static void handleActiveSubpoenas(String receiverMsg, Set<String> sbIds, String user, ClientRunnable cr) {
        // Loop through all of our active subpoenas
        for (String sID : sbIds) {
            ClientRunnable tt = activeClients.get(sID);
            if (cr != null && cr.isInitialized()) {
                String newMsg = receiverMsg;
                StringBuilder bld = new StringBuilder();
                bld.append(newMsg).append(" -> ").append(user).append(" ").append(cr.getIP());
                newMsg = bld.toString();
                if (tt != null && tt.isInitialized()) {
                    tt.enqueueMessage(Message.makeHistoryMessage(newMsg));
                }
                subpoenaService.addToSubpoenaMessages(sID, newMsg);
            } else {
                String newMsg = receiverMsg;
                StringBuilder bld = new StringBuilder();
                bld.append(newMsg).append(" -> ").append(user).append(OFFLINE);
                newMsg = bld.toString();
                if (tt != null && tt.isInitialized()) {
                    tt.enqueueMessage(Message.makeHistoryMessage(newMsg));
                }
                subpoenaService.addToSubpoenaMessages(sID, newMsg);
            }
        }
    }

    /*
     * This method will check if there is subpoena related to that message, if yes
     * it return the subpoena id
     */
    private static Set<String> handleSubpoena(Message msg) {
        Subpoena sb;
        Set<String> sbIds = new HashSet<>();
        String user1 = msg.getName();
        String user2 = msg.getMsgRecipient();
        sb = activeSubpoena.get(user1 + "$%$all");
        if (sb != null) {
            sbIds.add(sb.getId());
        }
        sb = activeSubpoena.get(user2 + "$%$all");
        if (sb != null) {
            sbIds.add(sb.getId());
        }
        if (msg.isPrivateMessage()) {
            sb = activeSubpoena.get(user1 + "$%$" + user2);
            if (sb != null) {
                sbIds.add(sb.getId());
            }
            sb = activeSubpoena.get(user2 + "$%$" + user1);
            if (sb != null) {
                sbIds.add(sb.getId());
            }
        }
        if (msg.isGroupMessage()) {
            sb = activeSubpoena.get(msg.getMsgRecipient());
            if (sb != null) {
                sbIds.add(sb.getId());
            }
        }
        return sbIds;
    }

    /**
     * Create a map of active subpoenas
     */
    public static void createActiveSubpoenaMap() {
        List<Subpoena> subpoenaList = subpoenaService.getActiveSubpoenas();
        for (Subpoena subpoena : subpoenaList) {
            if (subpoena.getGroup().isEmpty()) {
                activeSubpoena.put(subpoena.getUser1() + "$%$" + subpoena.getUser2(), subpoena);
            } else {
                activeSubpoena.put(subpoena.getGroup(), subpoena);
            }
        }
    }

    /**
     * Start up the threaded talk server. This class accepts incoming connections on
     * a specific port specified on the command-line. Whenever it receives a new
     * connection, it will spawn a thread to perform all of the I/O with that
     * client. This class relies on the server not receiving too many requests -- it
     * does not include any code to limit the number of extant threads.
     * 
     * @param args
     *            String arguments to the server from the command line. At present
     *            the only legal (and required) argument is the port on which this
     *            server should list.
     * @throws IOException
     *             Exception thrown if the server cannot connect to the port to
     *             which it is supposed to listen.
     */
    public static void main(String[] args) throws IOException {
        // Connect to the socket on the appropriate port to which this server connects.
        ServerSocketChannel serverSocket = null;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.socket().bind(new InetSocketAddress(ServerConstants.PORT));
            // Create the Selector with which our channel is registered.
            Selector selector = SelectorProvider.provider().openSelector();
            // Register to receive any incoming connection messages.
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            // Create our pool of threads on which we will execute.
            ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
            // Listen on this port until ...
            done = false;
            while (!done) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime midnight = LocalDate.now().atTime(0, 0);
                if (now.isEqual(midnight)) {
                    createActiveSubpoenaMap();
                }
                // Check if we have a valid incoming request, but limit the time we may wait.
                while (selector.select(DELAY_IN_MS) != 0) {
                    // Get the list of keys that have arrived since our last check
                    Set<SelectionKey> acceptKeys = selector.selectedKeys();
                    // Now iterate through all of the keys
                    Iterator<SelectionKey> it = acceptKeys.iterator();
                    while (it.hasNext()) {
                        // Get the next key; it had better be from a new incoming connection
                        SelectionKey key = it.next();
                        it.remove();
                        // Assert certain things I really hope is true
                        assert key.isAcceptable();
                        assert key.channel() == serverSocket;
                        // Create a new thread to handle the client for which we just received a
                        // request.
                        acceptClientConnection(serverSocket, threadPool);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while trying to open socket: " + e.toString(), e);
            Payload payload = Payload.builder().channel("#cs5500-team-203-f18").username("Slick Bot")
                    .iconEmoji(":man-facepalming:").text("Something went wrong during socket connection @ Slick")
                    .build();

            Slack slack = Slack.getInstance();
            WebhookResponse response = null;
            try {
                response = slack.send(SLACK_URL, payload);
                if (!response.getMessage().equalsIgnoreCase("OK"))
                    LOGGER.log(Level.SEVERE, "Slack integration failed!");
            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, "Slack integration failed!");
            }
        } finally {
            if (serverSocket != null)
                serverSocket.close();
        }
    }

    /**
     * Accept client connection
     * 
     * @param serverSocket
     * @param threadPool
     */
    @SuppressWarnings("unchecked")
    private static void acceptClientConnection(ServerSocketChannel serverSocket, ScheduledExecutorService threadPool) {
        try {
            // Accept the connection and create a new thread to handle this client.
            SocketChannel socket = serverSocket.accept();
            // Make sure we have a connection to work with.
            if (socket != null) {
                ClientRunnable tt = new ClientRunnable(socket);
                // Add the thread to the queue of active threads
                String ip = socket.getRemoteAddress().toString();
                if (ip == null) { // Not CALEA compliant
                    tt.enqueueMessage(Message.makeFailMsg());
                    return;
                }
                tt.setIP(ip);
                active.add(tt);
                activeClients.put(tt.getName(), tt);
                // Have the client executed by our pool of threads.
                @SuppressWarnings("rawtypes")
                ScheduledFuture clientFuture = threadPool.scheduleAtFixedRate(tt, CLIENT_CHECK_DELAY,
                        CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS);
                tt.setFuture(clientFuture);
            }
        } catch (AssertionError ae) {
            LOGGER.log(Level.WARNING, "Caught Assetion: " + ae.toString(), ae);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Caught Exception: " + e.toString(), e);
        }
    }

    /**
     * 
     * @return the done flag
     */
    public static boolean isDone() {
        return done;
    }

    /**
     * Sets the done flag
     * 
     * @param done
     */
    public static void setDone(boolean done) {
        Prattle.done = done;
    }

    /**
     * Remove the given IM client from the list of active threads.
     * 
     * @param dead
     *            Thread which had been handling all the I/O for a client who has
     *            since quit.
     */
    public static void removeClient(ClientRunnable dead) {
        // Test and see if the thread was in our list of active clients so that we
        // can remove it.
        if (!active.remove(dead) || !activeClients.remove(dead.getName(), dead)) {
            LOGGER.log(Level.SEVERE, "Could not find the thread expected to be removed");
        }
    }

    public static void addToActiveClients(String name, ClientRunnable clientRunnable) {
        activeClients.put(name, clientRunnable);
    }

    /** Check each message for flagging */
    private static String checkVulgar(String line) {
        String l = line;
        for (String s : l.split(" ")) {
            if (vulgar.contains(s)) {
                l = line.replaceAll(s, "*****");
            }
        }
        return l;
    }

    public static void prepareVulgarMap() {
        try (BufferedReader file = new BufferedReader(new FileReader("Pc.txt"))) {
            String word = file.readLine();
            while (word != null) {
                vulgar.add(word);
                word = file.readLine();
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found for parental control", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception when trying to read file for parental control", e);
        }
    }
}
