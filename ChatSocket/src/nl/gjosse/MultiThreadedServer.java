package nl.gjosse;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MultiThreadedServer implements Runnable{

    protected int          serverPort   = 6666;
    protected String       ipAddress    = "";
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;

    public MultiThreadedServer(int port, String ip){
        this.serverPort = port;
        this.ipAddress = ip;
        System.out.println("Making server!");

    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                sendDiscription(clientSocket);
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            new Thread(
                new WorkerRunnable(clientSocket, "Multithreaded Server")).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private void sendDiscription(Socket clientSocket) {
    	try {
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			out.writeUTF("Gjosse's server!");
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+serverPort, e);
        }
    }

}