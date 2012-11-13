package nl.gjosse;

import java.io.File;
import java.net.InetAddress;

public class FileSystem {

	static File loc = new File(System.getProperty("user.home"), "ChatSettings.txt");
	public static String getPortFromFile() {
			if(loc.exists())
			{
				System.out.println("File exists!");
			}
			return "21212";
	}

	public static String getIpFromFile() {
		try{
			if(loc.exists())
			{
				System.out.println("File exists!");
			}
			return 	InetAddress.getLocalHost().getHostAddress();
		
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return "Bla";
	}

}
