package edu.northeastern.ccs.im;

import java.io.IOException;

import edu.northeastern.ccs.im.server.Prattle;

public class PrattleRunabale  implements Runnable {
	private  int PORT = 4545;
	private  String HOST= "127.0.0.1";
	private Prattle prattle;
	
	public void run() {
		String[] args = new String[2];
		args[0]=HOST;
		args[1]=PORT+"";
		try {
			prattle.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void terminate(){
		prattle.setDone(true);
	}

}
