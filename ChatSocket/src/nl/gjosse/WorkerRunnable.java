package nl.gjosse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
        	boolean stop = false;
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
           
            DataInputStream in = new DataInputStream(input);
            DataOutputStream out = new DataOutputStream(output);
            
            
            String line = null;
            Scanner scan = new Scanner(System.in);
            
            String utf = in.readUTF();
            System.out.println("UTF is "+utf);
   
            if(utf.contains(":"))
            {
            String[] splited = utf.split(":");
            String userName = splited[0];
            String talkTo = splited[1];
            	if(talkTo.equalsIgnoreCase("Random"))
            	{
            		talkTo = Main.getRandomUser();
            	}
            System.out.println("Talking to: "+talkTo);
            User newUser = new User(userName, talkTo, clientSocket);
            } else
            {
            	User newUser = new User(utf, clientSocket);
            }
           
   
 
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
