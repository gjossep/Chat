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
	
	private User userByName;
	
	public User(String userName, String talkingTo, Socket clientSocket) {
		this.userName = userName;
		this.talkingTo = talkingTo;
		this.clientSocket = clientSocket;
		
		 Main.registerUserByName(userName, this);
		
			userByName = Main.getUserByName(talkingTo);
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
				userByName.sendMessage("Someone wants to talk to you!");
				userByName.sendMessage("His name is "+userName);
				userByName.setTalkingTo(userName);
				System.out.println("He is now talking to "+userByName.getTalkingTo());
				userByName.checkInputs();
			}
			
			checkInputs();
			
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
         OutputStream out = clientSocket.getOutputStream();
        
         text = text+"\n";
         out.write(text.getBytes(Charset.forName("UTF-8")));
         out.flush();

		} catch(Exception e)
		{
			e.printStackTrace();
		}
         
	}
	
	private void checkInputs() {
		 try {
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		while(true)
			{
				if(talkingTo!=null)
				{
					if(userByName!=null)
					{
						String text = in.readLine();
						if(text!=null)
						{
							userByName.sendMessage(userName+": "+text);
							System.out.println(userName+": "+text);
						}
					} else {
						userByName = Main.getUserByName(talkingTo);
					}
				} 
			}
		} catch (IOException e) {
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
	
	public void setTalkingTo(String userName2) {
		this.talkingTo = userName2;
	}
	

}
