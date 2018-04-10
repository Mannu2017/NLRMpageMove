package com.mannu;

import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.DefaultComboBoxModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Login extends JFrame{
	
	JComboBox database;
	public Connection connection;
	private String[] args;
	private JTextField usname;
	private JPasswordField password;
	private JTextField ipaddress;
	
	public Login() {
		super();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				ipaddress.requestFocus();
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(Login.class.getResource("/com/img/icon.png")));
		setTitle("Login");
        setSize(350,226);
        setResizable(false);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "eRecord Login", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 322, 174);
		panel.setLayout(null);
		getContentPane().add(panel);
		
		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setBounds(32, 81, 68, 14);
		panel.add(lblUserName);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(167, 81, 68, 14);
		panel.add(lblPassword);
		
		usname = new JTextField();
		usname.setBounds(30, 101, 127, 20);
		panel.add(usname);
		usname.setColumns(10);
		
		password = new JPasswordField();
		password.setBounds(167, 101, 127, 20);
		panel.add(password);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(usname.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Enter username");
				} else if(password.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Enter Password");
				} else {
					if(database.getSelectedItem().equals("Select Database")) {
						JOptionPane.showMessageDialog(null, "Enter Server IP Address");
					} else {
						try {
							connection=DriverManager.getConnection("jdbc:postgresql://"+ipaddress.getText()+":5432/"+database.getSelectedItem(),"postgres","root");
							String data=usname.getText().toUpperCase()+password.getText();
							byte password[]=data.getBytes();
							String value =new String(password, "UTF-8");
							MessageDigest msg=MessageDigest.getInstance("MD5");
							byte[] digest=msg.digest(value.getBytes());
							String hase=new BigInteger(1, digest).toString(16);
							System.out.println(usname.getText().toUpperCase()+" "+hase);
							PreparedStatement pStatement=connection.prepareStatement("select distinct userid,upassword from tbusermaster where userid='"+usname.getText().toUpperCase()+"' and upassword='"+hase+"'");
							ResultSet rSet=pStatement.executeQuery();
							if (rSet.next()) {
								System.out.println("Login");
								dispose();
								MainForm mainForm=new MainForm(connection,usname.getText().toUpperCase());
								mainForm.start();
							} else {
									JOptionPane.showMessageDialog(null, "Wrong Username and Password !!");
							
							}
							
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, "Error: "+e2);
						}
						
						
					}
				}
			}
		});
		btnLogin.setBounds(64, 132, 89, 23);
		panel.add(btnLogin);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnClose.setBounds(166, 132, 89, 23);
		panel.add(btnClose);
		
		JLabel lblIpaddress = new JLabel("IpAddress:");
		lblIpaddress.setBounds(26, 21, 68, 14);
		panel.add(lblIpaddress);
		
		ipaddress = new JTextField();
		ipaddress.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				database.removeAllItems();
				if(ipaddress.getText().isEmpty()) 
				{
					System.out.println("Please enter ip address");
					database.setModel(new DefaultComboBoxModel(new String[] {"Select Database"}));
				} else {
					try {
						Class.forName("org.postgresql.Driver");
						connection=DriverManager.getConnection("jdbc:postgresql://"+ipaddress.getText()+":5432/?","postgres", "root");
						PreparedStatement pStatement=connection.prepareStatement("SELECT datname FROM pg_database WHERE datistemplate = false;");
						ResultSet rSet=pStatement.executeQuery();
						while (rSet.next()) {
							System.out.println(rSet.getString(1));
							
							if(!rSet.getString(1).equals("postgres")) {
								database.addItem(rSet.getString(1));
							}
						}
						pStatement.close();
						rSet.close();
						
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error: "+e);
					}
				}
			}
		});
		ipaddress.setColumns(10);
		ipaddress.setBounds(105, 18, 193, 20);
		panel.add(ipaddress);
		
		JLabel lblDatabase = new JLabel("Database:");
		lblDatabase.setBounds(29, 53, 68, 14);
		panel.add(lblDatabase);
		
		database = new JComboBox();
		database.setModel(new DefaultComboBoxModel(new String[] {"Select Database"}));
		database.setBounds(105, 49, 193, 20);
		panel.add(database);
	}

	public void setArgs(String[] args) {
		this.args=args;
	}

	public void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
}
