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

    /**
     * Runs the thread
     */
    public  void run() {
        String[] args = new String[2];
        args[0] = HOST;
        args[1] = "4545";
        try {
            //if(!Prattle.isDone())
                Prattle.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Terminates the thread
     */
    public void terminate(){
        Prattle.setDone(true);
    }

    /**
     * Send a null broadcast msg
     */
    public static void sendMsg() {
        Prattle.broadcastMessage(null);
    }

    /**
     * 
     * @return true iff isDone is true
     */
    public static boolean isDone() {
        return Prattle.isDone();
    }

    /**
     * Remove the given client
     * @param client the client
     */
    public static void removeClient(ClientRunnable client) {
        Prattle.removeClient(client);
    }
}
