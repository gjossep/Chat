package nl.gjosse.cl;

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

public class Client implements Runnable {
	static Socket socket;
	static boolean stop = false;

	String userName;
	String person;
	String ip;
	int port;

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
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());

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
			sendingFile = false;
		}
	}

	private static void getFile(String text) {
		System.out.println("Getting file...");
		String[] split = text.split(":");
		File newFile = new File(System.getProperty("user.home"), "Downloads/"
				+ split[1]);
		int totalSize = 0;

		try {
			InputStream input = socket.getInputStream();
			FileOutputStream out = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024 * 1024];
			int size = Integer.parseInt(split[2]);
			int bytesReceived = 0;

			while ((bytesReceived = input.read(buffer, 0,
					(int) Math.min(buffer.length, size))) != -1) {
				out.write(buffer, 0, bytesReceived);
				size -= bytesReceived;
				System.out.println("Size: " + size);
				System.out.println("Bytes R: " + bytesReceived);
				totalSize += bytesReceived;
				if (bytesReceived == 0 && size == 0) {
					break;
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("File downloaded, Total Size: " + totalSize);
		sendText("/*/dwlComp");
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
		File sending = Window.fileToSend;
		sendText("/*/file:" + sending.getName() + ":" + sending.length());
		try {
			OutputStream output = socket.getOutputStream();
			sendingFile = true;
			FileInputStream fileInputStream = new FileInputStream(sending);
			int amountOfBytes = fileInputStream.available();
			System.out.println(amountOfBytes);
			byte[] buffer = new byte[1024 * 1024];
			int bytesRead = 0;

			while ((bytesRead = fileInputStream.read(buffer)) > 0 || !(sendingFile)) {
				output.write(buffer, 0, bytesRead);
				output.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
