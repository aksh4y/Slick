package edu.northeastern.ccs.im;

public class ServerSingleton {
    static boolean running = false;
    static PrattleRunabale server;
    
    public static void runServer() {
        if(!running)
            runPrattle();
    }
    
    public static void runPrattle() {
        running = true;
        server = new PrattleRunabale();
        try {
        server.start();
        }
        catch(Exception e) {e.printStackTrace(); }
    }

    public static void terminate() {
        running = false;
        try {
        server.terminate();
        }
        catch(Exception e) {e.printStackTrace();}
    }
}
