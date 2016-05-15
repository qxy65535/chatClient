package chatClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatFrame extends JFrame implements Runnable{
	
	private JTextArea chatLogger;
	private JTextField inputArea;
	private JButton send;
	
	private String username;
	private int userID;
	private String chatTo;
	
	public ChatFrame(String username, int userID, String chatTo){
		super(chatTo);
		
		this.username = username;
		this.userID = userID;
		this.chatTo = chatTo;
		
		setSize(500, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		chatLogger = new JTextArea();
		chatLogger.setEditable(false);
		inputArea = new JTextField();
		send = new JButton("send");	
		
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BorderLayout());
		logPanel.add(chatLogger, BorderLayout.CENTER);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(inputArea, BorderLayout.CENTER);
		inputPanel.add(send, BorderLayout.EAST);
		
		add(logPanel, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				Map<String, Object> message = new HashMap<String, Object>();
				message.put("username", username);
				message.put("userID", userID);
				message.put("chatTo", chatTo);
				message.put("type", "message");
				message.put("message", inputArea.getText());
				
				Message.sendPacket(message);
				displayMessage("<<< Me " + getTime() + "\n  " + message.get("message") + "\n");
				
				inputArea.setText("");
			}
		});
		
		setVisible(true);
	}
	
	public void displayMessage(String message){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatLogger.append(message);
			}
		});
	}
	
	public void displayMessage(Map<String, Object> message){
		String m = ">>> " + chatTo + " " + message.get("time") + "\n  " + message.get("message") + "\n";
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatLogger.append(m);
			}
		});
	}
	
	public String getTime(){
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public void run(){
		while (true){
			Map<String, Object> message = Message.receivePacket();
			
			displayMessage(">>> " + chatTo + " " + message.get("time") + "\n  " + message.get("message") + "\n");
		}
	}
}
