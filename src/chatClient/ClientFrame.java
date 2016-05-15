package chatClient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ClientFrame extends JFrame implements Runnable{
	
	private String username;
	private int userID;
//	private JButton button1 = new JButton("qxy");	
//	private JButton button2 = new JButton("admin");	
	
	private JButton[] buttons;
	private ExecutorService chatRoom;
	private Map<String, Object> chatToList;
	private Map<String, Object> message;
	
	public ClientFrame(String username, Map<String, Object> message){
		super(username);
		this.username = username;
		this.userID = (int) message.get("userID");
		chatToList = new HashMap<String, Object>();
		
		if (message.get("clients") != null){
			ArrayList<String> clients = (ArrayList<String>) message.get("clients");
			buttons = new JButton[clients.size()];
			ButtonListener listener = new ButtonListener();
			
			for (int i = 0; i < clients.size(); ++i){
				chatToList.put(clients.get(i), new ChatLog(clients.get(i)));
				buttons[i] = new JButton(clients.get(i));
				buttons[i].addActionListener(listener);
				add(buttons[i]);
			}
		}
		
		//chatRoom = Executors.newCachedThreadPool();
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		setSize(300, 400);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		add(button1);
//		add(button2);
//		
//		button1.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent event){
//				
//				openChatFrame(event.getActionCommand());
////				ChatFrame chatFrame = new ChatFrame(username, userID, "qxy");
////				chatRoom.execute(chatFrame);
//				
////				Map<String, Object> message = new HashMap<String, Object>();
////				message.put("chatTo", "qxy");
////				message.put("username", username);
////				message.put("userID", "userID");
////				Message.sendPacket(message);
//				
//			}
//		});
//		
//		button2.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent event){
//				
//				ChatFrame chatFrame = new ChatFrame(username, userID, "admin");
//				chatRoom.execute(chatFrame);
//				
////				Map<String, Object> message = new HashMap<String, Object>();
////				message.put("chatTo", "qxy");
////				message.put("username", username);
////				message.put("userID", "userID");
////				Message.sendPacket(message);
//				
//			}
//		});
		
		new Thread(this).start();
		setVisible(true);
	}
	
	private class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			openChatFrame(event.getActionCommand());
		}
	}
	
	private void openChatFrame(String chatTo){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				((ChatLog) chatToList.get(chatTo)).openChatFrame(username, userID, chatTo);
			}
		});
	}
	
	public void run(){
		while (true){
			System.out.println("1.!#23213");
			message = Message.receivePacket();
			String chatTo = (String) message.get("from");
			System.out.println(chatTo);
			if (chatToList.get(chatTo) == null){
				System.out.println("0.!#23213");
				chatToList.put(chatTo, new ChatLog(chatTo));
			}
			
			ChatLog chatLog = (ChatLog) chatToList.get(chatTo);
			if (chatLog.isChatFrameOpen()){
				System.out.println("2.!#23213");
				chatLog.getChatFrame().displayMessage(message);
			}
			else {
				System.out.println("3.!#23213");
				chatLog.addUnreadMessage(message);
			}
		}
	}
	
	
}
