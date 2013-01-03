package nl.gjosse.cl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Uploader implements Runnable {
	File sending;
	Socket socket;
	Socket newSocket;
	
	boolean gotIp = false;
	String ip = "";
	public Uploader(File sending, Socket socket)
	{
		this.sending = sending;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		Client.sendText("[*makeSocket:"+sending.getName()+":"+sending.length()+"*]");
		try {
			socket = new Socket(InetAddress.getByName(ip), 31313);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		double DoneSize = sending.length() / 1000000;
		
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
			int trueUploaded = 0;
			while ((bytesRead = fileInputStream.read(buffer)) > 0|| !(Client.sendingFile)) {
				output.write(buffer, 0, bytesRead);
				output.flush();
				trueUploaded += bytesRead;
				Window.incressBar(trueUploaded);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Upload Complele! , Total Size: "+DoneSize+" mb.");
		Window.setBarVisable(false);
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
