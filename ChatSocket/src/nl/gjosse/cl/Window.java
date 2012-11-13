package nl.gjosse.cl;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import nl.gjosse.MultiThreadedServer;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Window {

	private JFrame frame;
	private JTextField txtUsername;
	private JTextField txtPerson;
	private JTextField txtIp;
	private JTextField txtPort;
	private JTextField textField;
	private static JTextArea textArea;
	
	Client client;
	
	Thread active;

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
		frame.setBounds(100, 100, 599, 415);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("ChatWindow Client");
		lblTitle.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblTitle.setBounds(125, 6, 323, 69);
		frame.getContentPane().add(lblTitle);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				start();
			}
		});
		btnConnect.setBounds(43, 301, 117, 29);
		frame.getContentPane().add(btnConnect);
		
		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				stop();
			}
		});
		btnDisconnect.setBounds(43, 342, 117, 29);
		frame.getContentPane().add(btnDisconnect);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(239, 67, 341, 271);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(20, 96, 88, 16);
		frame.getContentPane().add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(93, 90, 134, 28);
		frame.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		JLabel lblPerson = new JLabel("Person:");
		lblPerson.setBounds(47, 136, 61, 16);
		frame.getContentPane().add(lblPerson);
		
		txtPerson = new JTextField();
		txtPerson.setBounds(93, 130, 134, 28);
		frame.getContentPane().add(txtPerson);
		txtPerson.setColumns(10);
		
		JLabel lblIp = new JLabel("IP:");
		lblIp.setBounds(77, 195, 31, 16);
		frame.getContentPane().add(lblIp);
		
		txtIp = new JTextField("192.168.178.12");
		txtIp.setBounds(93, 189, 134, 28);
		frame.getContentPane().add(txtIp);
		txtIp.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(65, 229, 36, 16);
		frame.getContentPane().add(lblPort);
		
		txtPort = new JTextField("21212");
		txtPort.setBounds(93, 223, 134, 28);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(34, 165, 193, 18);
		frame.getContentPane().add(separator);
		
		textField = new JTextField();
		textField.setBounds(239, 341, 268, 28);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				client.sendText(textField.getText());
				textField.setText("");
			}
		});
		btnSend.setBounds(498, 342, 81, 29);
		frame.getContentPane().add(btnSend);
	}
	

	public void start()
	{
		client = new Client(txtUsername.getText(), txtPerson.getText(), txtIp.getText(), txtPort.getText());
		active = new Thread(client);
		active.start();
	}
	
	public void stop()
	{
		client.stopClient();
		active.stop();
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
		    	  textArea.append(text);
		      }
		    });
	}
}
