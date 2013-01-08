package nl.gjosse.cl.socket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import nl.gjosse.cl.Window;

public class Downloader implements Runnable {

	public static boolean done = false;
	File storage;
	int size;
	ServerSocket socket;
	int oldBytes;
	int newBytes;
	
	SocketAddress sa; 
	
	public Downloader(File storage, int size)
	{
		this.storage = storage;
		this.size = size;
	}
	
	@Override
	public void run() {
		
		
		Socket accepted = null;
		try {
			accepted = socket.accept();
			socket = new ServerSocket(13131);
			Client.sendText("[*Ip:"+socket.getInetAddress()+"*]");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final dataKeeper dk = new dataKeeper();
		Timer timer = new Timer();

		TimerTask getNewBytes = new TimerTask() {

			@Override
			public void run() {
				int newBytes = dk.totalDownloaded;
			}
		};

		try {
			InputStream input = accepted.getInputStream();
			FileOutputStream out = new FileOutputStream(storage);
			byte[] buffer = new byte[1024 * 1024];
			int bytesReceived = 0;

			Window.setUpBar(size, 0, 0);
			Window.setBarVisable(true);

			timer.schedule(getNewBytes, 1000L);
			while ((bytesReceived = input.read(buffer, 0,(int) Math.min(buffer.length, size))) != -1) {
				out.write(buffer, 0, bytesReceived);
				size -= bytesReceived;
				dk.totalDownloaded += bytesReceived;

				oldBytes = dk.totalDownloaded;

				Window.incressBar(dk.totalDownloaded);
				if (bytesReceived == 0 && size == 0) {
					break;
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		double DoneSize = (dk.totalDownloaded/1000000);
		System.out.println("Download Complete!, Total Size: " + DoneSize+" mb.");
		Client.sendText("/*/dwlComp");
		Window.setBarVisable(false);
		done = true;
		
		try {
			accepted.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public int calculateDwlSpeed()
	{
		return newBytes - oldBytes;
	}
	
	static class dataKeeper
    {
    	public int totalDownloaded = 0;
    }

	public boolean isDone() {
		return done;
	}

}
