package nl.gjosse;

import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

public class User {
	private String userName;
	private String talkingTo;
	private Socket clientSocket;

	public User(String userName, String talkingTo, Socket clientSocket) {
		this.userName = userName;
		this.talkingTo = talkingTo;
		this.clientSocket = clientSocket;
		
		Main.registerUserByName(userName, this);
		
			User userByName = Main.getUserByName(talkingTo);
			System.out.println("User to talk to is "+talkingTo);
			if(userByName==null)
			{
			sendMessage("User not found...");
			sendMessage("Logging you off.");
			sendMessage("[*Quit*]");
			shutdown();
			}
			else
			{
				sendMessage("User Found!");
			}
	}
	
	public User(String userName, Socket clientSocket)
	{
		this.userName = userName;
		this.clientSocket = clientSocket;
		
		Main.registerUserByName(userName, this);

		
		sendMessage("Hello there!");


	}

	public void sendMessage(String text) {
		try{
		 InputStream in  = clientSocket.getInputStream();
         OutputStream out = clientSocket.getOutputStream();
        
         text = text+"\n";
         out.write(text.getBytes(Charset.forName("UTF-8")));
         out.flush();

		} catch(Exception e)
		{
			e.printStackTrace();
		}
         
	}

	public void shutdown() {

			TimerTask afterDelay = new TimerTask() {
				
				@Override
				public void run() {
					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			new Timer().schedule(afterDelay, 3000);
			Main.removeUser(userName);		
	}

	public String getUserName() {
		return userName;
	}

	public String getTalkingTo() {
		return talkingTo;
	}
	
	

}
