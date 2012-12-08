package nl.gjosse;

import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

public class User {
	private String userName;
	private String talkingTo = "";
	private Socket clientSocket;
	
	private boolean sendFile = false;
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
				if(userByName.talkingTo.equals(""))
				{
				sendMessage("User Found!");
				userByName.sendMessage("Someone wants to talk to you!");
				userByName.sendMessage("His name is "+userName);
				userByName.setTalkingTo(userName);
				userByName.setUserbyName();
				System.out.println("He is now talking to "+userByName.getTalkingTo());
				
				CheckInputs otherUser = new CheckInputs(userByName, this, userByName.getSocket());
				Thread active = new Thread(otherUser);
				active.start();		
				Main.registerInputByName(talkingTo, active);
				} else {
					sendMessage("The person you are trying to talk to is busy at the moment!");
				}
			}
			
			if(!userByName.talkingTo.equals(""))
			{
			CheckInputs thisUser = new CheckInputs(this, userByName, getSocket());
			Thread active = new Thread(thisUser);
			active.start();
			
			Main.registerInputByName(userName, active);
			}
	}
	


	private void setUserbyName() {
		this.userByName = Main.getUserByName(talkingTo);
	}



	private Socket getSocket() {
		return clientSocket;
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



	public void checkCommand(String text) {
		System.out.println("Checking command...");
			text = text.replace("/*/", "");
			
			if(text.equalsIgnoreCase("stop"))
			{
				if(!talkingTo.equals(""))
				{
				Main.removeInput(talkingTo);
				userByName.talkingTo = "";
				}
				
				Main.removeInput(userName);
				this.talkingTo = "";
				
				sendMessage("[*Quit*]");
				
			}
			if(text.contains("quit:"))
			{
				String name = text.substring(5);
				System.out.println("Quiting list is "+name);
				
				if(!talkingTo.equals(""))
				{
				userByName.sendMessage("Your partner has disconected!");
				
				userByName.talkingTo = "";
				}
				
				Main.removeInput(userName);
				Main.removeUser(userName);

			}
			
			if(text.equalsIgnoreCase("dwlComp"))
			{
				sendFile = false;
				System.out.println("File Done!");
			}
			
			if(text.startsWith("file")) {

				String[] split = text.split(":");
				//File newFile = new File(System.getProperty("user.home"), "Downloads/"+split[1]);
				userByName.sendMessage("[*file:" + split[1] + ":" + split[2]+"*]");
			    try {
				    InputStream input = clientSocket.getInputStream();
					//FileOutputStream out = new FileOutputStream(newFile);
					byte[] buffer = new byte[1024 * 1024];
					int size = Integer.parseInt(split[2]);
					int bytesReceived = 0;
					OutputStream output = userByName.clientSocket.getOutputStream();

					while (size > 0 && (bytesReceived = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
			        {     
			            size -= bytesReceived;
			            System.out.println("Size: "+size);
			            System.out.println("Bytes R: "+bytesReceived);
			            output.write(buffer, 0, bytesReceived);
			            output.flush();
			        } 
					//out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("File downloaded");
				//sendFiletoUser(newFile);
				
			}
			
			
	}



	private void sendFiletoUser(File sending) {
		userByName.sendMessage("[*file:" + sending.getName() + ":" + sending.length()+"*]");
		try {
			OutputStream output = userByName.clientSocket.getOutputStream();
			FileInputStream fileInputStream = new FileInputStream(sending);
			int amountOfBytes = fileInputStream.available();
			System.out.println(amountOfBytes);
			byte[] buffer = new byte[1024 * 1024];
			int bytesRead = 0;

			while ((bytesRead = fileInputStream.read(buffer)) > 0 ) {
				output.write(buffer, 0, bytesRead);
				output.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean isSending() {
		return sendFile;
	}
	

}
