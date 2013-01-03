package nl.gjosse.cl;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login {

	private JFrame frame;
	private JTextField textUser;
	private JLabel lblPassword;
	private JPasswordField textPass;
	private JLabel Status;
	private JButton btnLogin;
	
	private boolean logginIn = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 339, 506);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(46, 264, 77, 16);
		frame.getContentPane().add(lblUsername);
		
		textUser = new JTextField();
		textUser.setBounds(135, 258, 134, 28);
		frame.getContentPane().add(textUser);
		textUser.setColumns(10);
		
		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(46, 319, 77, 16);
		frame.getContentPane().add(lblPassword);
		
		textPass = new JPasswordField();
		textPass.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent key) {
				if(key.getKeyCode()==key.VK_ENTER)
				{
					if(!logginIn)
					{
					login();
					}
				}
			}
		});
		textPass.setBounds(135, 313, 134, 28);
		frame.getContentPane().add(textPass);
		textPass.setColumns(10);
		
		btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(!logginIn)
				{
				login();
				}
			}
		});
		btnLogin.setBounds(95, 399, 117, 29);
		frame.getContentPane().add(btnLogin);
		
		Status = new JLabel("");
		Status.setBounds(78, 347, 200, 34);
		frame.getContentPane().add(Status);
		
		JLabel logo = new JLabel("\n");
		logo.setIcon(new ImageIcon(Login.class.getResource("/nl/gjosse/cl/chat-icon.png")));
		logo.setBounds(29, 6, 291, 240);
		frame.getContentPane().add(logo);
	}

	protected void login() {
		if(!textUser.getText().equalsIgnoreCase(""))
		{
			if(!textPass.getText().equalsIgnoreCase(""))
				{
					logginIn = true;
					btnLogin.setEnabled(false);
					String ipAddress = getIpFromWeb();
					System.out.println(ipAddress);
					LoginSuccess(ipAddress);
				} else {
					Status.setForeground(Color.RED);
					Status.setText("Password field is blank!");
				}
		} else {
			Status.setForeground(Color.RED);
			Status.setText("Username field is blank!");
		}
	}



	private void LoginSuccess(final String ip) {
		Status.setForeground(Color.GREEN.darker());
		Status.setText("Success!! Loging in.");
		Timer time = new Timer(); 
		time.schedule(new TimerTask() {
			
			@Override
			public void run() {
				frame.setVisible(false);
				frame.removeAll();
				frame.dispose();
				
				Window.start(textUser.getText(), ip);
			}
		}, 2000L);
	}
	
	private String getIpFromWeb() {
		String returned = "000.000.000.000";
		try{
		URL url = new URL("http://gjosse.nl/Java/ip.html");
		URLConnection con = url.openConnection();
		Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
		Matcher m = p.matcher(con.getContentType());
		/* If Content-Type doesn't match this pre-conception, choose default and 
		 * hope for the best. */
		String charset = m.matches() ? m.group(1) : "ISO-8859-1";
		Reader r = new InputStreamReader(con.getInputStream(), charset);
		StringBuilder buf = new StringBuilder();
		while (true) {
		  int ch = r.read();
		  if (ch < 0)
		    break;
		  buf.append((char) ch);
		}
		 returned = buf.toString();
		
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(returned);
		returned = StringUtils.substringBetween(returned, "<ip>", "</ip>");
		
		return returned;
	}
}
