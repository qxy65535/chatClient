package chatClient;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame{
	
	JLabel label_username;
	JTextField text_username;
	JLabel label_password;
	JPasswordField text_password;
	JButton login;
	

	
	public LoginFrame(){
		super("login");
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40));
		setSize(400, 250);
		

		initViews();
		setVisible(true);
	}
	
	private void initViews(){
		Font font = new Font("宋体", Font.PLAIN, 20);
		label_username = new JLabel("用户名");
		text_username = new JTextField(15);
		label_password = new JLabel("密码");
		text_password = new JPasswordField(15);
		login = new JButton("登录");
		label_username.setFont(font);
		text_username.setFont(font);
		label_password.setFont(font);
		text_password.setFont(font);
		login.setFont(font);
		
		Box wholeBox = Box.createVerticalBox();
		
		Box username = Box.createHorizontalBox();
		username.add(label_username);
		username.add(Box.createHorizontalStrut(10));
		username.add(text_username);
		
		Box password = Box.createHorizontalBox();
		password.add(label_password);
		password.add(Box.createHorizontalStrut(10));
		password.add(text_password);
		
		wholeBox.add(username);
		wholeBox.add(Box.createVerticalStrut(20));
		wholeBox.add(password);
		wholeBox.add(Box.createVerticalStrut(20));
		wholeBox.add(login);
		
		add(wholeBox);
		
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				switch (checkInput()){
				case Code.NO_USERNAME:
					JOptionPane.showMessageDialog(LoginFrame.this, "未输入用户名！");
					return;
				case Code.USERNAME_TOO_LONG:
					JOptionPane.showMessageDialog(LoginFrame.this, "用户名不能超过20个字符！");
					return;
				case Code.NO_PASSWORD:
					JOptionPane.showMessageDialog(LoginFrame.this, "未输入密码！");
					return;
				case Code.PASSWORD_TOO_LONG:
					JOptionPane.showMessageDialog(LoginFrame.this, "密码不能超过20个字符！");
					return;
				}
				
				Map<String, Object> userInfo = new HashMap<String, Object>();
				userInfo.put("username", text_username.getText());
				userInfo.put("password", new String(text_password.getPassword()));
				userInfo.put("type", "login");
				
				Message.sendPacket(userInfo);
				Map<String, Object> message = Message.receiveResponsePacket();
				switch((int) message.get("messageCode")){
				case Code.USER_INFO_ERROR:
					JOptionPane.showMessageDialog(LoginFrame.this, "用户名或密码错误！");
					return;
				case Code.SQL_EXCEPTION:
					JOptionPane.showMessageDialog(LoginFrame.this, "数据库错误！");
					return;
				case Code.SUCCESS:
					ClientFrame clientFrame = new ClientFrame(text_username.getText(), message);
					LoginFrame.this.dispose();
				}
			}
		});
	}
	
	private int checkInput(){
		if ("".equals(text_username.getText()))
			return Code.NO_USERNAME;
		if (text_username.getText().length() > 20)
			return Code.USERNAME_TOO_LONG;
		if ("".equals(text_password.getPassword()))
			return Code.NO_PASSWORD;
		if (text_password.getPassword().length > 20)
			return Code.PASSWORD_TOO_LONG;
		return 0;
	}
	
}
