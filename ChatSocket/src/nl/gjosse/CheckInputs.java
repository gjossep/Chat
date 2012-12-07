package nl.gjosse;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class CheckInputs implements Runnable {
	User toCheck;
	Socket clientSocket;
	User personTo;
	
	public CheckInputs(User username,User person, Socket socket)
	{
		this.toCheck = username;
		this.clientSocket = socket;
		this.personTo = person;
	}

	@Override
	public void run() {
		try {
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		while(true)
			{
				if(toCheck.getTalkingTo()!=null)
				{
						String text = in.readLine();
						if(text!=null)
						{
							if(!ChatEncoder.decodeString(text).startsWith("/*/"))
							{
							String textToSend = toCheck.getUserName()+": "+ChatEncoder.decodeString(text);
							personTo.sendMessage(ChatEncoder.encodeString(textToSend));
								if(!toCheck.isSending())
								{
								System.out.println(toCheck.getUserName()+": "+ChatEncoder.decodeString(text));
								}
							} else {
								toCheck.checkCommand(ChatEncoder.decodeString(text));
							}
						}
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 
	}


}
