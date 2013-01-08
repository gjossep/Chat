package nl.gjosse.cl.socket;

import java.awt.EventQueue;
import java.beans.Encoder;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

import nl.gjosse.cl.Window;


public class Client implements Runnable {
	static Socket socket;
	public static boolean stop = false;

	String userName;
	String person;
	String ip;
	int port;

	static Downloader dwl;
	static boolean sendingFile = false;

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
			System.out.println("Made a connection with " + in.readUTF());

			if (!person.equals("")) {
				out.writeUTF(userName + ":" + person);
			} else {
				out.writeUTF(userName);
			}

			String responseLine;

			while (!stop) {
				try {
					responseLine = in.readLine();
					responseLine = ChatEncoder.decodeString(responseLine);
					if (responseLine != null && !responseLine.startsWith("[*")) {
						System.out.println(responseLine);
					}
					if (responseLine.startsWith("[*")) {
						checkCommand(responseLine);
					}
				} catch (Exception e) {
					// do ntohing
				}
			}

		} catch (IOException e) {
			System.out
					.println("Can't connect to the server, it might be down.");
		}

	}

	private static void checkCommand(String command) {
		command = command.replace("[*", "");
		command = command.replace("*]", "");
		System.out.println("Checking Command! Command is: " + command);
		if (command.equalsIgnoreCase("ServerQuiting")) {
			isServerOn = false;
			System.out.println("The server turned off...");
		}
		if (command.startsWith("file")) {
			getFile(command);
		}
		if (command.equalsIgnoreCase("dwlComp")) {
			Timer time = new Timer();
			TimerTask stopSending = new TimerTask() {
				
				@Override
				public void run() {
					sendingFile = false;					
				}
			};
			time.schedule(stopSending, 1000L);
		}
//		if(command.startsWith("makeSocket"))
//		{
//			String[] split = command.split(":");
//			getFile(split[1], Integer.parseInt(split[2]));
//		}
	}


	private static void getFile(String stuff) {
		System.out.println("Getting file...");
		String[] split = stuff.split(":");
		File file = new File(System.getProperty("user.home")+"/Downloads", split[1]);
		int size = Integer.parseInt(split[2]);
		downloadFile(file, size);
		//dwl = new Downloader(file, size);
		//Thread th = new Thread(dwl);
		//th.start();
	}

	private static void downloadFile(File file, int size) {
		final dataKeeper dk = new dataKeeper();
		Long oldTime = 0L;
		Long newTime = 0L;
		
		try {
			InputStream input = socket.getInputStream();
			FileOutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[1024 * 1024];
			int bytesReceived = 0;

			Window.setUpBar(size, 0, 0);
			Window.setBarVisable(true);

			oldTime = System.nanoTime();
			while ((bytesReceived = input.read(buffer, 0,(int) Math.min(buffer.length, size))) != -1) {
				out.write(buffer, 0, bytesReceived);
				size -= bytesReceived;
				dk.totalDownloaded += bytesReceived;

				Window.incressBar(dk.totalDownloaded);
				if (bytesReceived == 0 && size == 0) {
					break;
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		newTime = System.nanoTime();
		double timeTaken = (oldTime - newTime) / 1000000;
		double DoneSize = (dk.totalDownloaded/1000000);
		System.out.println("Download Complete!, Total Size: " + DoneSize+" mb, Taken time: "+timeTaken);
		Client.sendText("/*/dwlComp");
		Window.setBarVisable(false);

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
		sendText("/*/quit:" + userName);
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

	public static void sendText(String text) {
		if (!text.startsWith("/*/")) {
			System.out.println("You: " + text);
		}
		if (isServerOn) {
			if (!text.startsWith("FILE:")) {
				try {
					OutputStream out = socket.getOutputStream();
					text = ChatEncoder.encodeString(text);
					text = text + "\n";
					out.write(text.getBytes(Charset.forName("UTF-8")));
					out.flush();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Sending File...");
				sendFile();
			}
		} else {
			System.out.println("There is no connection with the server...");
		}
	}

	private static void sendFile() {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				File sending = Window.fileToSend;
				sendText("/*/file:"+sending.getName()+":"+sending.length());
				double DoneSize = sending.length() / 1000000;
				int trueUploaded = 0;
				Window.setUpBar((int)sending.length(), 0, 0);
				Window.setBarVisable(true);

				try {
					OutputStream output = socket.getOutputStream();
					Client.sendingFile = true;
					
					FileInputStream fileInputStream = new FileInputStream(sending);
					int amountOfBytes = fileInputStream.available();
					System.out.println(amountOfBytes);
					byte[] buffer = new byte[1024 * 1024];
					int bytesRead = 0;
					while ((bytesRead = fileInputStream.read(buffer)) > 0|| !(Client.sendingFile)) {
						output.write(buffer, 0, bytesRead);
						output.flush();
						trueUploaded += bytesRead;
						Window.incressBar(trueUploaded);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Upload Complele! , Total Size: "+DoneSize+" mb. Bytes Sent: "+trueUploaded);
				Window.setBarVisable(false);				
			}
		};
		
		Thread th = new Thread(run);
		th.start();
		
//		File sending = Window.fileToSend;
//		sendText("/*/file:"+sending.getName()+":"+sending.length());
//		double DoneSize = sending.length() / 1000000;
//		
//		Window.setUpBar((int)sending.length(), 0, 0);
//		Window.setBarVisable(true);
//
//		try {
//			OutputStream output = socket.getOutputStream();
//			Client.sendingFile = true;
//			
//			FileInputStream fileInputStream = new FileInputStream(sending);
//			int amountOfBytes = fileInputStream.available();
//			System.out.println(amountOfBytes);
//			byte[] buffer = new byte[1024 * 1024];
//			int bytesRead = 0;
//			int trueUploaded = 0;
//			while ((bytesRead = fileInputStream.read(buffer)) > 0|| !(Client.sendingFile)) {
//				output.write(buffer, 0, bytesRead);
//				output.flush();
//				trueUploaded += bytesRead;
//				Window.incressBar(trueUploaded);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("Upload Complele! , Total Size: "+DoneSize+" mb.");
//		Window.setBarVisable(false);
		
//		Uploader up = new Uploader(sending, socket);
//		Thread th = new Thread(up);
//		th.start();
	}
	
	static class dataKeeper
    {
    	public int totalDownloaded = 0;
    }
}

