package chatClient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ClientFrame extends JFrame{
	
	private String username;
	private int userID;
	private JButton button1 = new JButton("qxy");	
	private JButton button2 = new JButton("admin");	
	private ExecutorService chatRoom;
	
	public ClientFrame(String username, int userID){
		super(username);
		this.username = username;
		this.userID = userID;
		chatRoom = Executors.newCachedThreadPool();
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		setSize(300, 400);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(button1);
		add(button2);
		
		button1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				
				ChatFrame chatFrame = new ChatFrame(username, userID, "qxy");
				chatRoom.execute(chatFrame);
				
//				Map<String, Object> message = new HashMap<String, Object>();
//				message.put("chatTo", "qxy");
//				message.put("username", username);
//				message.put("userID", "userID");
//				Message.sendPacket(message);
				
			}
		});
		
		button2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				
				ChatFrame chatFrame = new ChatFrame(username, userID, "admin");
				chatRoom.execute(chatFrame);
				
//				Map<String, Object> message = new HashMap<String, Object>();
//				message.put("chatTo", "qxy");
//				message.put("username", username);
//				message.put("userID", "userID");
//				Message.sendPacket(message);
				
			}
		});
		
		setVisible(true);
	}
	
	
}
