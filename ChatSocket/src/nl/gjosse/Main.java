package nl.gjosse;

import java.util.HashMap;
import java.util.Map;

public class Main {
	
	static MultiThreadedServer server;
	
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
	    }

	    public static User getUserByName(String name){
	        return userByNameMap.get(name);
	    }

		public static void removeUser(String userName) {
			userByNameMap.remove(userName);
		}
	
	

}
