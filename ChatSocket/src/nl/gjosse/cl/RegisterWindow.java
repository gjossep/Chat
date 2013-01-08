package nl.gjosse.cl;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSeparator;

import nl.gjosse.main.SQL.SqlManger;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPasswordField;

public class RegisterWindow {

	private JFrame frame;
	private JTextField username;
	private JPasswordField password;
	private JPasswordField password2;
	JLabel status;

	/**
	 * Launch the application.
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterWindow window = new RegisterWindow();
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
	public RegisterWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 340, 257);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(20, 35, 76, 16);
		frame.getContentPane().add(lblUsername);

		username = new JTextField();
		username.setBounds(94, 29, 204, 28);
		frame.getContentPane().add(username);
		username.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(20, 102, 76, 16);
		frame.getContentPane().add(lblPassword);

		password = new JPasswordField();
		password.setBounds(94, 96, 204, 28);
		frame.getContentPane().add(password);
		password.setColumns(10);

		JLabel lblRepeat = new JLabel("Repeat:");
		lblRepeat.setBounds(20, 143, 61, 16);
		frame.getContentPane().add(lblRepeat);

		password2 = new JPasswordField();
		password2.setBounds(94, 137, 204, 28);
		frame.getContentPane().add(password2);
		password2.setColumns(10);

		final JButton btnRegister = new JButton("Register");
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (btnRegister.getText().equalsIgnoreCase("Register")) {
					if (password.getText().equals(password2.getText())) {
						status.setForeground(Color.CYAN);
						status.setText("Making...");
						int worked = SqlManger.registerNew(username.getText(), password.getText());
						if(worked == 2)
						{
							status.setForeground(Color.RED.darker());
							status.setText("Account already exists!");
							btnRegister.setText("Ok");
						} else if (worked == 1) {
							status.setForeground(Color.GREEN.darker());
							status.setText("Registered!");
							btnRegister.setText("Ok");
						} else if(worked == 0) {
							status.setForeground(Color.RED.darker());
							status.setText("Failed!");
							btnRegister.setText("Ok");

						}
					} else {
						status.setForeground(Color.RED.darker());
						status.setText("Passwords do not match!");
					}
				} else if(btnRegister.getText().equalsIgnoreCase("OK"))
				{
					frame.dispose();
				}
			}
		});
		btnRegister.setBounds(122, 172, 117, 29);
		frame.getContentPane().add(btnRegister);

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(20, 63, 279, 6);
		frame.getContentPane().add(separator);

		status = new JLabel("");
		status.setForeground(Color.RED);
		status.setBounds(111, 213, 187, 16);
		frame.getContentPane().add(status);
	}
}
