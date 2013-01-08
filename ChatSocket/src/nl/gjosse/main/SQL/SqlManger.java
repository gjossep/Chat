package nl.gjosse.main.SQL;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;

public class SqlManger {


	private static String URL = "SQL09.FREEMYSQL.NET";
	private static String Username = "gjosse";
	private static String Password = "jozajoza";
	

	public static SyncSQL sql = new SyncSQL(URL, "chestweb", Username, Password);

	public static boolean startSQL() {
		return sql.initialise();
	}

	public static int registerNew(String username, String pass) {
		try{
		sql.refreshConnection();
		if(!sql.doesTableExist("users"))
		{
			System.out.println("Making Table");
			sql.standardQuery("CREATE TABLE users(id int NOT NULL AUTO_INCREMENT,username varchar(255),password varchar(255), PRIMARY KEY (id))");
			System.out.println("Table Made");
		} 
		
		if(!ifUserExists(username))
		{
		sql.standardQuery("INSERT INTO users(username, password) VALUES ('"+username+"', '"+md5(pass)+"')");
		System.out.println("User made!");
		} else {
			return 2;
		}
		return 1;
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	private static boolean ifUserExists(String username) {
		try{
			sql.refreshConnection();
			ResultSet rs = sql.sqlQuery("SELECT * FROM users WHERE username='"+username+"'");
			while(rs.next())
			{
				return true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	
	public static boolean checkUser(String username, String password) {
		sql.refreshConnection();
		try{
			ResultSet rs = sql.sqlQuery("SELECT * FROM users WHERE username='"+username+"'");
			while(rs.next())
			{
			String pass = rs.getString("password");
			if(md5(password).equalsIgnoreCase(pass))
			{
				return true;
			}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static String md5(String input) {
        
        String md5 = null;
         
        if(null == input) return null;
         
        try {
             
        //Create MessageDigest object for MD5
        MessageDigest digest = MessageDigest.getInstance("MD5");
         
        //Update input string in message digest
        digest.update(input.getBytes(), 0, input.length());
 
        //Converts message digest value in base 16 (hex) 
        md5 = new BigInteger(1, digest.digest()).toString(16);
 
        } catch (NoSuchAlgorithmException e) {
 
            e.printStackTrace();
        }
        return md5;
    }


	

}
