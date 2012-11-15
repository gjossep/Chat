package nl.gjosse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
	
	static MultiThreadedServer server;
	
	public static List<User> activeUsers = new ArrayList<User>();
	
	public static void start(int port, String ip) {
		server = new MultiThreadedServer(port, ip);
		new Thread(server).start();
	}
	
	public static void stop()
	{
		System.out.println("Stoping server...");
		server.stop();
	}
	
	 	private static Map<String, User> userByNameMap = new HashMap<String, User>();

	    public static void registerUserByName(String name, User user){
	        userByNameMap.put(name, user);
	        activeUsers.add(user);
	        Window.changeList();
	    }

	    public static User getUserByName(String name){
	        return userByNameMap.get(name);
	    }

		public static void removeUser(String userName) {
			userByNameMap.remove(userName);
	        activeUsers.remove(userName);
	        
	        Window.changeList();

		}
	
		
		private static Map<String, Thread> inputsByNameMap = new HashMap<String, Thread>();

	    public static void registerInputByName(String name, Thread bob){
	    	inputsByNameMap.put(name, bob);
	    }

	    public static Thread getInputByName(String name){
	        return inputsByNameMap.get(name);
	    }

		public static void removeInput(String userName) {
			Thread thread = inputsByNameMap.get(userName);
			thread.stop();
			
			inputsByNameMap.remove(userName);
		}

		public static String getRandomUser() {
			Random ran = new Random();
			int stopAt = ran.nextInt(activeUsers.size());
			for(int i=0; i<=activeUsers.size();i++)
			{
				if(i == stopAt)
				{
					return activeUsers.get(i).getUserName();
				}
			}
			return "Bob";
		}
	
	

}
