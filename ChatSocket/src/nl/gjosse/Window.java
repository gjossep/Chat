package nl.gjosse;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class Window {

	private JFrame frame;
	private JTextField txtPort;
	private JTextField txtIp;
	static JTextArea txtpnConnections;
	JButton btnStart;
	JButton btnStop;
	public JScrollPane scrollPane_1;
	public static JTextArea txtpnInfo;
	
	boolean isServerOn = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					redirectSystemStreams();
					Window window = new Window();
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
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
			
				if(isServerOn)
				{
					Main.stop();
				} 
				
			}
		});
		frame.setBounds(100, 100, 600, 415);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("ChatWindow Server");
		lblTitle.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblTitle.setBounds(143, 6, 340, 34);
		frame.getContentPane().add(lblTitle);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(241, 64, 340, 300);
		frame.getContentPane().add(scrollPane);
		
		txtpnConnections = new JTextArea();
		txtpnConnections.setText("");
		scrollPane.setViewportView(txtpnConnections);
		
		JLabel lblConnections = new JLabel("Connections");
		lblConnections.setBounds(368, 45, 112, 16);
		frame.getContentPane().add(lblConnections);
		
		/*  Start of Buttons  */
		btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				isServerOn = true;
				Main.start(getPort(), getIp());
			}
		});
		btnStart.setBounds(56, 160, 117, 29);
		frame.getContentPane().add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				isServerOn = false;
				Main.stop();
			}
		});
		btnStop.setBounds(56, 197, 117, 29);
		frame.getContentPane().add(btnStop);
		
		/* End of buttons  */
		
		JLabel lblIp = new JLabel("IP:");
		lblIp.setBounds(37, 93, 27, 16);
		frame.getContentPane().add(lblIp);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(37, 121, 61, 16);
		frame.getContentPane().add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setText(FileSystem.getPortFromFile());
		txtPort.setBounds(76, 115, 134, 28);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		txtIp = new JTextField();
		txtIp.setText(FileSystem.getIpFromFile());
		txtIp.setBounds(76, 87, 134, 28);
		frame.getContentPane().add(txtIp);
		txtIp.setColumns(10);
		
		JLabel lblInfo = new JLabel("INFO:");
		lblInfo.setBounds(89, 250, 61, 16);
		frame.getContentPane().add(lblInfo);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 269, 220, 118);
		frame.getContentPane().add(scrollPane_1);
		
		txtpnInfo = new JTextArea();
		txtpnInfo.setText("");
		scrollPane_1.setViewportView(txtpnInfo);
		
		System.out.println("All Set-Up done!");
	}
	
	
	protected String getIp() {
		return txtIp.getText();
	}

	protected int getPort() {
		return Integer.parseInt(txtPort.getText());
	}

	static void redirectSystemStreams() {
		  OutputStream out = new OutputStream() {
		    @Override
		    public void write(int b) throws IOException {
		      updateTextArea(String.valueOf((char) b));
		    }
		 
		    @Override
		    public void write(byte[] b, int off, int len) throws IOException {
		     updateTextArea(new String(b, off, len));
		    }
		 
		    @Override
		    public void write(byte[] b) throws IOException {
		      write(b, 0, b.length);
		    }
		  };
		  
		  System.setOut(new PrintStream(out, true));
		  System.setErr(new PrintStream(out, true));
		}

	protected static void updateTextArea(final String text) {
		 SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		    	  txtpnConnections.append(text);
		      }
		    });
	}

	public static void changeList() {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				List<User> activeUsers = Main.activeUsers;
				txtpnInfo.setText("");
				String total = "";
				for(User user: activeUsers)
				{
					total = total+user.getUserName()+"\n";
				}
				System.out.println("Did list! List is "+total);
				txtpnInfo.setText(total);
			}
		});
	}
}
