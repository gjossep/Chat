package nl.gjosse.cl;

import java.awt.EventQueue;
import java.beans.Encoder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

public class Client implements Runnable {
	static Socket socket;
	static boolean stop = false;
	
	String userName;
	String person;
	String ip;
	int port;
	
	static boolean isServerOn = false;


	public Client(String username, String person, String ip, String port) {
		this.userName = username;
		this.person = person;
		this.ip = ip;
		this.port = Integer.parseInt(port);

	}


	@Override
	public void run() {
		
		System.out.println("Connecting");
		try {
			socket = new Socket(ip, port);
			isServerOn = true;
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			Scanner scan = new Scanner(System.in);
			System.out.println("Made a connection with "+in.readUTF());

			if(!person.equals(""))
			{
				out.writeUTF(userName+":"+person);
			} else
			{
				out.writeUTF(userName);
			}
			
			String responseLine;

			while (!stop) {
				try{
			        responseLine = in.readLine();
			        responseLine = ChatEncoder.decodeString(responseLine);
			        if(responseLine!=null && !responseLine.startsWith("[*"))
			        {
			        	System.out.println(responseLine);
			        }
			        if(responseLine.startsWith("[*"))
			        {
			        	checkCommand(responseLine);
			        }
			   }catch(Exception e)
			   {
				   //do ntohing
			   }
			}
			
			
		} catch (IOException e) {
			System.out.println("Can't connect to the server, it might be down.");
		}
		
	}

	private static void checkCommand(String command) {
			command = command.replace("[*", "");
			command = command.replace("*]", "");
			
			if(command.equalsIgnoreCase("ServerQuiting"))
			{
				isServerOn = false;
				System.out.println("The server turned off...");
			}
	}

	private static void shutDown() {
		stop = true;
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}


	public void stopClient() {
		sendText("/quit:"+userName);
		isServerOn = false;
		try {
			stop = true;
			TimerTask stopSocket = new TimerTask() {
				
				@Override
				public void run() {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
			};
			new Timer().schedule(stopSocket, 1000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Closed!");
	}


	public void sendText(String text) {
		if(!text.startsWith("/"))
		{
		System.out.println("You: "+text);
		}
		if(isServerOn)
		{
		try{
	         OutputStream out = socket.getOutputStream();
	         text = ChatEncoder.encodeString(text);
	         text = text+"\n";
	         out.write(text.getBytes(Charset.forName("UTF-8")));
	         out.flush();

			} catch(Exception e)
			{
				e.printStackTrace();
			}
		} else {
			System.out.println("There is no connection with the server...");
		}
	}


}
