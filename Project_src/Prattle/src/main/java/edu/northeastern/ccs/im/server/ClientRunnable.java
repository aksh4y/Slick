
package edu.northeastern.ccs.im.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoDatabase;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MongoConnection;
import edu.northeastern.ccs.im.PrintNetNB;
import edu.northeastern.ccs.im.ScanNetNB;
import edu.northeastern.ccs.im.MongoDB.Model.Group;
import edu.northeastern.ccs.im.MongoDB.Model.Subpoena;
import edu.northeastern.ccs.im.MongoDB.Model.User;
import edu.northeastern.ccs.im.service.GroupServicePrattle;
import edu.northeastern.ccs.im.service.SubpoenaServicePrattle;
import edu.northeastern.ccs.im.service.UserServicePrattle;

/**
 * Instances of this class handle all of the incoming communication from a
 * single IM client. Instances are created when the client signs-on with the
 * server. After instantiation, it is executed periodically on one of the
 * threads from the thread pool and will stop being run only when the client
 * signs off.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.3
 */
public class ClientRunnable implements Runnable {
    /**
     * Number of milliseconds that special responses are delayed before being sent.
     */
    private static final int SPECIAL_RESPONSE_DELAY_IN_MS = 5000;
    /**
     * Number of milliseconds after which we terminate a client due to inactivity.
     * This is currently equal to 5 hours.
     */
    private static final long TERMINATE_AFTER_INACTIVE_BUT_LOGGEDIN_IN_MS = 18000000;

    /**
     * Number of milliseconds after which we terminate a client due to inactivity.
     * This is currently equal to 5 hours.
     */
    private static final long TERMINATE_AFTER_INACTIVE_INITIAL_IN_MS = 600000;

    /** Constant for Offline users */
    private static final String OFFLINE = " /Offline";
    /**
     * Time at which we should send a response to the (private) messages we were
     * sent.
     */
    private Date sendResponses;

    /** Time at which the client should be terminated due to lack of activity. */
    private GregorianCalendar terminateInactivity;

    /** Queue of special Messages that we must send immediately. */
    private Queue<Message> immediateResponse;

    /** Queue of special Messages that we will need to send. */
    private Queue<Message> specialResponse;

    /** Socket over which the conversation with the single client occurs. */
    private final SocketChannel socket;

    /**
     * Utility class which we will use to receive communication from this client.
     */
    private ScanNetNB input;

    /** Utility class which we will use to send communication to this client. */
    private PrintNetNB output;

    /** Id for the user for whom we use this ClientRunnable to communicate. */
    private int userId;

    /** Name that the client used when connecting to the server. */
    private String name;

    /** IP of the client **/
    private String ip;

    private boolean isSubpoena;

    /**
     * Whether this client has been initialized, set its user name, and is ready to
     * receive messages.
     */
    private boolean initialized;

    /**
     * The future that is used to schedule the client for execution in the thread
     * pool.
     */
    private ScheduledFuture<ClientRunnable> runnableMe;

    /** Collection of messages queued up to be sent to this client. */
    private Queue<Message> waitingList;

    private UserServicePrattle userService;

    private GroupServicePrattle groupService;

    private User user;

    private MongoDatabase db;

    private SubpoenaServicePrattle subpoenaService;

    private static final Logger LOGGER = Logger.getLogger(Logger.class.getName());

    /**
     * Create a new thread with which we will communicate with this single client.
     * 
     * @param client
     *            SocketChannel over which we will communicate with this new client
     * @throws IOException
     *             Exception thrown if we have trouble completing this connection
     */
    public ClientRunnable(SocketChannel client) throws IOException {
        // initialize db
        db = MongoConnection.createConnection();

        userService = new UserServicePrattle(db);

        groupService = new GroupServicePrattle(db);

        subpoenaService = new SubpoenaServicePrattle(db);

        isSubpoena = false;
        // Set up the SocketChannel over which we will communicate.
        socket = client;
        socket.configureBlocking(false);
        // Create the class we will use to receive input
        input = new ScanNetNB(socket);
        // Create the class we will use to send output
        output = new PrintNetNB(socket);
        // Mark that we are not initialized
        initialized = false;
        // Create our queue of special messages
        specialResponse = new LinkedList<>();
        // Create the queue of messages to be sent
        waitingList = new ConcurrentLinkedQueue<>();
        // Create our queue of message we must respond to immediately
        immediateResponse = new LinkedList<>();
        // Mark that the client is active now and start the timer until we
        // terminate for inactivity.
        terminateInactivity = new GregorianCalendar();
        terminateInactivity
        .setTimeInMillis(terminateInactivity.getTimeInMillis() + TERMINATE_AFTER_INACTIVE_INITIAL_IN_MS);
    }

    /**
     * Determines if this is a special message which we handle differently. It will
     * handle the messages and return true if msg is "special." Otherwise, it
     * returns false.
     * 
     * @param msg
     *            Message in which we are interested.
     * @return True if msg is "special"; false otherwise.
     */
    private boolean broadcastMessageIsSpecial(Message msg) {
        boolean result = false;
        String text = msg.getText();
        if (text != null) {
            ArrayList<Message> responses = ServerConstants.getBroadcastResponses(text);
            if (responses != null) {
                for (Message current : responses) {
                    handleSpecial(current);
                }
                result = true;
            }
        }
        return result;
    }

    /**
     * Check to see for an initialization attempt and process the message sent.
     */
    private void checkForInitialization() {
        // Check if there are any input messages to read
        if (input.hasNextMessage()) {
            // If a message exists, try to use it to initialize the connection
            Message msg = input.nextMessage();
            if (setUserName(msg.getName())) {
                // Update the time until we terminate this client due to inactivity.
                terminateInactivity.setTimeInMillis(
                        new GregorianCalendar().getTimeInMillis() + TERMINATE_AFTER_INACTIVE_INITIAL_IN_MS);
                // Set that the client is initialized.
                initialized = true;
            } else {
                initialized = false;
            }
        }
    }

    /**
     * Process one of the special responses
     * 
     * @param msg
     *            Message to add to the list of special responses.
     */
    private void handleSpecial(Message msg) {
        if (specialResponse.isEmpty()) {
            sendResponses = new Date();
            sendResponses.setTime(sendResponses.getTime() + SPECIAL_RESPONSE_DELAY_IN_MS);
        }
        specialResponse.add(msg);
    }

    /**
     * Check if the message is properly formed. At the moment, this means checking
     * that the identifier is set properly.
     * 
     * @param msg
     *            Message to be checked
     * @return True if message is correct; false otherwise
     */
    private boolean messageChecks(Message msg) {
        // Check that the message name matches.
        return (msg.getName() != null) && (msg.getName().compareToIgnoreCase(getName()) == 0);
    }

    /**
     * Immediately send this message to the client. This returns if we were
     * successful or not in our attempt to send the message.
     * 
     * @param message
     *            Message to be sent immediately.
     * @return True if we sent the message successfully; false otherwise.
     */
    private boolean sendMessage(Message message) {
        String msg = message.toString();
        LOGGER.log(Level.INFO, msg);
        return output.print(message);
    }

    /**
     * Try allowing this user to set his/her user name to the given username.
     * 
     * @param userName
     *            The new value to which we will try to set userName.
     * @return True if the username is deemed acceptable; false otherwise
     */
    private boolean setUserName(String userName) {
        // Now make sure this name is legal.
        if (userName != null) {
            // Optimistically set this users ID number.
            setName(userName);
            userId = hashCode();
            return true;
        }
        // Clear this name; we cannot use it. *sigh*
        userId = -1;
        return false;
    }

    /**
     * Add the given message to this client to the queue of message to be sent to
     * the client.
     * 
     * @param message
     *            Complete message to be sent.
     */
    public void enqueueMessage(Message message) {
        waitingList.add(message);
    }

    /**
     * Get the name of the user for which this ClientRunnable was created.
     * 
     * @return Returns the name of this client.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the user for which this ClientRunnable was created.
     * 
     * @param name
     *            The name for which this ClientRunnable.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return true iff this client is a subpoena
     */
    public boolean isSubpoena() {
        return isSubpoena;
    }

    /**
     * Gets the name of the user for which this ClientRunnable was created.
     * 
     * @return Returns the current value of userName.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Return if this thread has completed the initialization process with its
     * client and is read to receive messages.
     * 
     * @return True if this thread's client should be considered; false otherwise.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Perform the periodic actions needed to work with this client.
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        boolean terminate = false;
        // The client must be initialized before we can do anything else
        if (!initialized) {
            checkForInitialization();
        } else {
            try {
                terminate = checkMessages(terminate);
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, e.toString());
                this.enqueueMessage(Message.makeFailMsg());
            } catch (NullPointerException ne) {
                LOGGER.log(Level.SEVERE, ne.toString());
                this.enqueueMessage(Message.makeFailMsg());
            } finally {
                // When it is appropriate, terminate the current client.
                if (terminate) {
                    terminateClient();
                }
            }
        }
        // Finally, check if this client have been inactive for too long and,
        // when they have, terminate
        // the client.
        if (!terminate && terminateInactivity.before(new GregorianCalendar())) {
            String msg = "Timing out or forcing off a user " + name;
            LOGGER.log(Level.WARNING, msg);
            terminateClient();
        }
    }
    /**
     * Check for messages in queue
     * @param terminate
     * @return
     * @throws JsonProcessingException
     */
    private boolean checkMessages(boolean terminate)
            throws JsonProcessingException {
        // Client has already been initialized, so we should first check
        // if there are any input
        // messages.
        if (input.hasNextMessage()) {
            // Get the next message
            Message msg = input.nextMessage();
            // Update the time until we terminate the client for
            // inactivity.
            terminateInactivity.setTimeInMillis(
                    new GregorianCalendar().getTimeInMillis() + TERMINATE_AFTER_INACTIVE_BUT_LOGGEDIN_IN_MS);
            if (msg.terminate()) {
                // Stop sending the poor client message.
                terminate = true;
                // Reply with a quit message.
                enqueueMessage(Message.makeQuitMessage(name));
            }
            else
                handleMsgs(msg); 
        }
        if (!immediateResponse.isEmpty()) {
            while (!immediateResponse.isEmpty()) {
                sendMessage(immediateResponse.remove());
            }
        }
        terminate = postCompletionTasks(terminate);
        return terminate;
    }

    /**
     * Handle messages in queue
     * @param msg
     * @throws JsonProcessingException
     */
    private void handleMsgs(Message msg) throws JsonProcessingException {
        // If it is create user message
        if (msg.isUserCreate()) {
            this.initialized = true;
            createUser(msg);
        }
        // If it is login user message
        else if (msg.isUserLogin()) {
            this.initialized = true;
            loginUser(msg);
        }
        // Subpoena Login
        else if (msg.isSubpoenaLogin()) {
            this.initialized = true;
            loginSubpoena(msg);
        } else if (name.equalsIgnoreCase("DUMMYUSER"))
            this.enqueueMessage(Message.makeFailMsg());
        // Handle Private Message
        else if (msg.isPrivateMessage()) {
            this.initialized = true;
            sendPrivateMsg(msg);
        }
        // Handle Group Message
        else if (msg.isGroupMessage()) {
            this.initialized = true;
            sendGroupMsg(msg);
        }
        // Handle MIME messages
        else if (msg.isMIME()) {
            this.initialized = true;
            sendMIME(msg);
        }
        // If it is create group message
        else if (msg.isCreateGroup()) {
            this.initialized = true;
            createGroup(msg);
        }
        // If it is Adding user to group message
        else if (msg.isAddToGroup()) {
            this.enqueueMessage(this.addUserToGroup(msg.getName()));
        }
        // If user is exiting a group
        else if (msg.isGroupExit()) {
            this.initialized = true;
            exitGroup(msg);
        }
        // If group is being deleted
        else if (msg.isGroupDelete()) {
            this.initialized = true;
            deleteGroup(msg);
        }
        // If user is being deleted
        else if (msg.isUserDelete()) {
            this.initialized = true;
            deleteUser(msg);
        }

        // If user is being updated
        else if (msg.isUserUpdate()) {
            this.initialized = true;
            updateUser(msg);
        }
        // Recall
        else if (msg.isRecallMessage()) {
            this.initialized = true;
            recallMsg(msg);
        } else
            handleOtherMsgs(msg);
    }

    /**
     * Handle search, parental, subpoena and display msgs
     * @param msg
     */
    private void handleOtherMsgs(Message msg) {
        // Search
        if (msg.isSearchMessage()) {
            this.initialized = true;
            searchMsg(msg);
        }
        // For Parental Control
        else if (msg.isParentalControl()) {
            this.initialized = true;
            toggleParentalControl(msg);
        }
        // Create Subpoena
        else if (msg.isUserSubpoena() || msg.isGroupSubpoena()) {
            this.initialized = true;
            createSubpoena(msg);
        }
        // If the message is a broadcast message, send it out
        else if (msg.isDisplayMessage()) {
            handleDisplayMsg(msg);
        }
    }

    private boolean postCompletionTasks(boolean terminate) {
        // Check to make sure we have a client to send to.
        boolean processSpecial = !specialResponse.isEmpty()
                && ((!initialized) || (!waitingList.isEmpty()) || sendResponses.before(new Date()));
        boolean keepAlive = !processSpecial;
        // Send the responses to any special messages we were asked.
        if (processSpecial) {
            // Send all of the messages and check that we get valid
            // responses.
            while (!specialResponse.isEmpty()) {
                keepAlive |= sendMessage(specialResponse.remove());
            }
        }
        if (!waitingList.isEmpty()) {
            if (!processSpecial) {
                keepAlive = false;
            }
            // Send out all of the message that have been added to the
            // queue.
            do {
                Message msg = waitingList.remove();
                boolean sentGood = sendMessage(msg);
                keepAlive |= sentGood;
            } while (!waitingList.isEmpty());
        }
        terminate |= !keepAlive;
        return terminate;
    }

    /** Handle display message
     * @param msg the message
     */
    private void handleDisplayMsg(Message msg) {
        // Check if the message is legal formatted
        if (messageChecks(msg)) {
            // Check for our "special messages"
            if ((msg.isBroadcastMessage()) && (!broadcastMessageIsSpecial(msg))) {
                // Check for our "special messages"
                if ((msg.getText() != null)
                        && (msg.getText().compareToIgnoreCase(ServerConstants.BOMB_TEXT) == 0)) {
                    initialized = false;
                    Prattle.broadcastMessage(Message.makeQuitMessage(name));
                } else {
                    Prattle.broadcastMessage(msg);
                }
            }
        } else {
            Message sendMsg;
            sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
                    "Last message was rejected because it specified an incorrect user name.");
            enqueueMessage(sendMsg);
        }
    }

    /** Create a subpoena
     * @param msg the message
     */
    private void createSubpoena(Message msg) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate fromDate = LocalDate.parse(msg.getMsgRecipient(), formatter);
        LocalDate toDate = LocalDate.parse(msg.getText(), formatter);
        Message ackMsg;
        Boolean valid = true;
        this.initialized = true;
        if (toDate.isBefore(fromDate)) {
            valid = false;
        }
        if (!user.getUsername().equalsIgnoreCase("admin")) {
            ackMsg = Message.makeCreateNoPrivilegeMessage();
        } else {
            if (msg.isUserSubpoena()) {
                ackMsg = createUserSubpoena(msg, fromDate, toDate, valid);
            } else {
                ackMsg = createGroupSubpoena(msg, fromDate, toDate, valid);
            }
            Prattle.createActiveSubpoenaMap();
        }
        this.enqueueMessage(ackMsg);
    }

    /** Create group subpoena
     * @param msg
     * @param fromDate
     * @param toDate
     * @param valid
     * @return
     */
    private Message createGroupSubpoena(Message msg, LocalDate fromDate,
            LocalDate toDate, Boolean valid) {
        Message ackMsg;
        Subpoena sb;
        if (groupService.findGroupByName(msg.getName()) == null) {
            valid = false;
        }
        if (valid) {
            sb = subpoenaService.createSubpoena("", "", msg.getName(), fromDate, toDate);
            ackMsg = Message.makeSubpoenaSuccess(sb.getId());
        } else {
            ackMsg = Message.makeFailMsg();
        }
        return ackMsg;
    }

    /**
     * Create user subpoena
     * @param msg
     * @param fromDate
     * @param toDate
     * @param valid
     * @return
     */
    private Message createUserSubpoena(Message msg, LocalDate fromDate,
            LocalDate toDate, Boolean valid) {
        Message ackMsg;
        Subpoena sb;
        String[] params = msg.getName().split("\\$\\%\\$");
        User toUser;
        User fromUser = userService.findUserByUsername(params[0]);
        if (fromUser == null) {
            valid = false;
        }
        if (!params[1].equals("all")) {
            toUser = userService.findUserByUsername(params[1]);
            if (toUser == null) {
                valid = false;
            }
        }
        if (valid) {
            sb = subpoenaService.createSubpoena(params[0], params[1], "", fromDate, toDate);
            ackMsg = Message.makeSubpoenaSuccess(sb.getId());
        } else {
            ackMsg = Message.makeFailMsg();
        }
        return ackMsg;
    }

    /**
     * toggle parental control
     * @param msg
     */
    private void toggleParentalControl(Message msg) {
        Boolean pc = msg.getName().equalsIgnoreCase("ON");
        if (user.getParentalControl() != null && pc == user.getParentalControl()) {
            this.enqueueMessage(Message.makeSuccessMsg());
        } else {
            this.user.setParentalControl(pc);
            if (userService.switchParentalControl(this.user.getUsername()))
                this.enqueueMessage(Message.makeSuccessMsg());
            else
                this.enqueueMessage(Message.makeFailMsg());
        }
    }

    /**
     * Search for message
     * @param msg
     */
    private void searchMsg(Message msg) {
        List<String> messages = userService.getMessages(msg.getText(), msg.getMsgRecipient(),
                msg.getName());
        for (String text : messages) {
            this.enqueueMessage(Message.makeHistoryMessage(text));
        }
        if (messages.isEmpty()) {
            this.enqueueMessage(Message.makeFailMsg());
        }
    }

    /**
     * Recall a message
     * @param msg
     */
    private void recallMsg(Message msg) {
        Message ackMsg = null;
        if (msg.getMsgRecipient().equalsIgnoreCase("user") || (msg.getMsgRecipient().equalsIgnoreCase("group"))) {
            userService.recallMessage(msg.getName(), msg.getMsgRecipient(), msg.getText());
            ackMsg = Message.makeSuccessMsg();
        } 
        else                             
            ackMsg = Message.makeFailMsg();
        this.enqueueMessage(ackMsg);
    }

    /**
     * Update a user
     * @param msg
     */
    private void updateUser(Message msg) {
        Message ackMsg;
        if (!UserServicePrattle.checkPassword(msg.getName(), user.getPassword()))
            ackMsg = Message.makeUserWrongPasswordMsg();
        else {
            if (userService.updateUser(user, msg.getText())) {
                user = userService.findUserByUsername(user.getUsername());
                ackMsg = Message.makeSuccessMsg();
            } else
                ackMsg = Message.makeFailMsg();
        }
        this.enqueueMessage(ackMsg);
    }

    /**
     * Delete a user
     * @param msg
     * @throws JsonProcessingException
     */
    private void deleteUser(Message msg) throws JsonProcessingException {
        Message ackMsg;
        if (!UserServicePrattle.checkPassword(msg.getName(), user.getPassword()))
            ackMsg = Message.makeUserWrongPasswordMsg();
        else {
            if (userService.deleteUser(user.getUsername()))
                ackMsg = Message.makeDeleteUserSuccessMsg();
            else
                ackMsg = Message.makeFailMsg();
        }
        this.enqueueMessage(ackMsg);
    }

    /**
     * Delete a group
     * @param msg
     */
    private void deleteGroup(Message msg) {
        Message ackMsg;
        Group group = groupService.findGroupByName(msg.getName());
        if (group == null)
            ackMsg = Message.makeGroupNotExist();
        else {
            if (groupService.deleteGroup(group.getName())) {
                user = userService.findUserByUsername(user.getUsername());
                ackMsg = Message.makeSuccessMsg();
            } else
                ackMsg = Message.makeFailMsg();
        }
        this.enqueueMessage(ackMsg);
    }

    /**
     * Exit a group
     * @param msg
     */
    private void exitGroup(Message msg) {
        Message ackMsg;
        Group group = groupService.findGroupByName(msg.getName());
        if (group == null) {
            ackMsg = Message.makeGroupNotExist();
        } else {
            if (groupService.exitGroup(user.getUsername(), group.getName())
                    && user.getListOfGroups().contains(group.getName())) {
                user = userService.findUserByUsername(user.getUsername());
                ackMsg = Message.makeSuccessMsg();
            } else {
                ackMsg = Message.makeFailMsg();
            }
        }
        this.enqueueMessage(ackMsg);
    }

    /**
     * Create a group
     * @param msg
     * @throws JsonProcessingException
     */
    private void createGroup(Message msg) throws JsonProcessingException {
        if (!groupService.isGroupnameTaken(msg.getName())) {
            Group group = groupService.createGroup(msg.getName());
            if (group == null) {
                this.enqueueMessage(Message.makeCreateGroupFail());
            } else {
                this.enqueueMessage(Message.makeCreateGroupSuccess());
                this.enqueueMessage(this.addUserToGroup(msg.getName()));
            }
        } else {
            this.enqueueMessage(Message.makeGroupExist());
        }
    }

    /**
     * Send MIME msg
     * @param msg
     */
    private void sendMIME(Message msg) {
        long timestamp = new java.sql.Timestamp(System.currentTimeMillis()).getTime();
        String m =  timestamp + " " +  getIP() + "File Sent To " + msg.getMsgRecipient();
        String mg = timestamp + " " +  getIP() + "File Received From " + user.getUsername();
        this.enqueueMessage(Message.makeUID("" + timestamp));
        Prattle.broadcastPrivateMessage(user, msg, msg.getMsgRecipient(), m, mg);
    }

    /**
     * Send a group msg
     * @param msg
     */
    private void sendGroupMsg(Message msg) {
        String groupName = msg.getMsgRecipient();
        Group group = groupService.findGroupByName(groupName);
        // group does not exist or user not part of group
        if (group == null || !group.getListOfUsers().contains(msg.getName())) {
            Message failMsg = Message.makeGroupNotExist();
            this.enqueueMessage(failMsg);
        } else {
            long timestamp = new java.sql.Timestamp(System.currentTimeMillis()).getTime();
            String m = timestamp + " " +  getIP() +  " GROUP " + msg.getMsgRecipient() + " " + msg.getText();
            String mg = timestamp + " " + getIP() + " [" + user.getUsername() + "@" + msg.getMsgRecipient() + "] " + msg.getText();
            this.enqueueMessage(Message.makeUID("" + timestamp));
            Prattle.broadcastGroupMessage(user, msg, group.getListOfUsers(), m, mg);
        }
    }

    /** 
     * Send a private msg
     * @param msg
     */
    private void sendPrivateMsg(Message msg) {
        if (userService.findUserByUsername(msg.getMsgRecipient()) != null) {
            String timestamp = "" + new java.sql.Timestamp(System.currentTimeMillis()).getTime();
            String m = timestamp + " " + getIP() + " PRIVATE " + msg.getMsgRecipient() + " " + msg.getText()
            + OFFLINE;
            String mg = timestamp + " " + getIP() + " [Private Msg] " + user.getUsername() + ": " + msg.getText()
            + OFFLINE;
            this.enqueueMessage(Message.makeUID(timestamp));
            Prattle.broadcastPrivateMessage(user, msg, msg.getMsgRecipient(), m, mg);
        } else
            this.enqueueMessage(Message.makeFailMsg());
    }

    /** 
     * Login into a subpoena
     * @param msg
     */
    private void loginSubpoena(Message msg) {
        Subpoena sb = subpoenaService.querySubpoenaById(msg.getName());
        if (sb != null) {
            this.enqueueMessage(Message.makeSubpoenaLoginSuccess());
            this.isSubpoena = true;
            this.name = msg.getName();
            List<String> messages = sb.getListOfMessages();
            if (messages != null) {
                for (String text : messages) {
                    this.enqueueMessage(Message.makeHistoryMessage(text));
                }
            }
            Prattle.addToActiveClients(name, this);
        } else
            this.enqueueMessage(Message.makeFailMsg());
    }

    /**
     * Login a user
     * @param msg
     */
    private void loginUser(Message msg) {
        user = userService.authenticateUser(msg.getName(), msg.getText());
        if (user == null)
            this.enqueueMessage(Message.makeLoginFail());
        else {
            name = user.getUsername();
            Prattle.addToActiveClients(name, this);
            this.enqueueMessage(Message.makeLoginSuccess(name));
            List<String> messages = user.getMyMessages();
            List<String> pendingMessages = user.getMyUnreadMessages();
            for (String text : messages) {
                this.enqueueMessage(Message.makeHistoryMessage(text));
            }
            if (!pendingMessages.isEmpty()) {
                this.enqueueMessage(Message.makePendingMsgNotif());
                for (String text : pendingMessages) {
                    updateReceiverIP(text);
                    updateSenderIP(text);
                    this.enqueueMessage(Message.makeHistoryMessage(text));
                }
            }
            userService.clearUnreadMessages(user);
        }
    }

    /**
     * Create a user
     * @param msg
     * @throws JsonProcessingException
     */
    private void createUser(Message msg) throws JsonProcessingException {
        Message ackMsg;
        if (!userService.isUsernameTaken(msg.getName())) {
            user = userService.createUser(msg.getName(), msg.getText());
            if (user == null) {
                ackMsg = Message.makeCreateUserFail();
            } else {
                name = user.getUsername();
                ackMsg = Message.makeCreateUserSuccess(name);
            }
        } else {
            ackMsg = Message.makeUserIdExist();
        }
        this.enqueueMessage(ackMsg);
    }

    // Update receiver's IP to his msg copy on receiver's side
    private void updateReceiverIP(String text) {
        String newMsg = text.substring(0, text.length() - 8);
        newMsg += this.getIP();
        userService.updateMessage(name, text, newMsg);
    }

    // Update receiver's IP to his msg copy on sender's side
    private void updateSenderIP(String text) {
        if(text.contains("[Private Msg]"))
            updatePrivateSender(text);
        else if (text.matches(".*\\[.*@.*\\].*"))  // group msg
            updateGroupSender(text);
    }

    /**
     * Handle group message sender IP
     * @param text
     */
    private void updateGroupSender(String text) {
        String sender = null;
        String msg = null;
        String newMsg = null;
        msg = text.substring(0, text.indexOf('['));
        msg += "GROUP " + text.substring(text.indexOf('@') + 1, text.indexOf(']')); // group name
        sender = text.substring(text.indexOf('[') + 1, text.indexOf('@'));
        msg += text.substring(text.indexOf(']') + 1, text.indexOf("->") + 2);  // text
        msg += " " + name + OFFLINE;
        newMsg = msg.substring(0, msg.length() - 8) + this.getIP();
        userService.updateMessage(sender, msg, newMsg);
    }

    /**
     * Handle private message sender IP
     * @param text
     */
    private void updatePrivateSender(String text) {
        String msg = text.substring(0, text.indexOf('['));
        msg += "PRIVATE " + name;
        String sender = text.substring(text.indexOf("] ") + 2, text.indexOf(':', text.indexOf(':') + 1));
        msg += text.substring(text.indexOf(':', text.indexOf(':') + 1) + 1);
        String newMsg = msg.substring(0, msg.length() - 8) + this.getIP();
        userService.updateMessage(sender, msg, newMsg);
    }


    /**
     * @return the IP of this client
     */
    public String getIP() { return ip; }

    /**
     * set the IP of this client
     * @param ip
     */
    public void setIP(String ip) { this.ip = ip; }

    /**
     * Store the object used by this client runnable to control when it is scheduled
     * for execution in the thread pool.
     * 
     * @param future
     *            Instance controlling when the runnable is executed from within the
     *            thread pool.
     */
    public void setFuture(ScheduledFuture<ClientRunnable> future) {
        runnableMe = future;
    }

    /**
     * Add user to group
     * @param groupName the name of group
     * @return message acknowledgement
     */
    private Message addUserToGroup(String groupName) {
        Message ackMsg;
        this.initialized = true;
        Group group = groupService.findGroupByName(groupName);
        if (group == null) {
            ackMsg = Message.makeGroupNotExist();
        } else {
            if (groupService.addUserToGroup(group, user) && !group.getListOfUsers().contains(user.getUsername())) {
                user = userService.findUserByUsername(user.getUsername());
                ackMsg = Message.makeGroupAddSuc();
            } else {
                ackMsg = Message.makeGroupAddFail();
            }
        }
        return ackMsg;
    }

    /**
     * Terminate a client that we wish to remove. This termination could happen at
     * the client's request or due to system need.
     */
    public void terminateClient() {
        try {
            // Once the communication is done, close this connection.
            input.close();
            socket.close();
        } catch (IOException e) {
            // If we have an IOException, ignore the problem
            LOGGER.log(Level.WARNING, e.toString());
        } finally {
            // Remove the client from our client listing.
            Prattle.removeClient(this);
            // And remove the client from our client pool.
            runnableMe.cancel(false);
        }
    }
}