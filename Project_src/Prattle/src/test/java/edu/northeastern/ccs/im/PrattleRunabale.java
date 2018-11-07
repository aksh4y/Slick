package edu.northeastern.ccs.im;

import java.io.IOException;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;

/**
 * Creates an instance of Prattle.main and runs it on a new thread
 * @author Nipun
 * @version 1.0
 */
public class PrattleRunabale extends Thread {
    private  int PORT = 4545;  // holds the port
    private  String HOST= "127.0.0.1"; // holds the host
    private Prattle prattle;   // the Prattle instance

    /**
     * Runs the thread
     */
    public void run() {
        String[] args = new String[2];
        try {
            prattle.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Terminates the thread
     */
    public void terminate(){
        prattle.setDone(true);
    }

    /**
     * Send a null broadcast msg
     */
    public void sendMsg() {
        prattle.broadcastMessage(null);
    }

    /**
     * 
     * @return true iff isDone is true
     */
    public boolean isDone() {
        return prattle.isDone();
    }

    /**
     * Remove the given client
     * @param client the client
     */
    public void removeClient(ClientRunnable client) {
        prattle.removeClient(client);
    }
}