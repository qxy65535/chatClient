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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 247267039795100225L;
	private String username;
	private int userID;
//	private JButton button1 = new JButton("qxy");	
//	private JButton button2 = new JButton("admin");	
	
	private JButton[] buttons;
//	private ExecutorService chatRoom;
	private Map<Integer, Object> chatToList;
	private Map<String, Integer> nameToID;
	private Map<String, Object> message;
	
	public ClientFrame(String username, Map<String, Object> message){
		super(username);
		this.username = username;
		this.userID = (Integer) message.get("userID");
		ChatLog.initUser(username, userID);
		
		chatToList = new HashMap<Integer, Object>();
		nameToID = new HashMap<String, Integer>();
		
		if (message.get("friendID") != null){
			ArrayList<Integer> friendID = (ArrayList<Integer>) message.get("friendID");
			System.out.println(message.get("friendID"));
			ArrayList<String> friendName = (ArrayList<String>) message.get("friendName");
			buttons = new JButton[friendID.size()];
			ButtonListener listener = new ButtonListener();
			
			for (int i = 0; i < friendID.size(); ++i){
				chatToList.put(friendID.get(i), new ChatLog(friendID.get(i), friendName.get(i)));
				nameToID.put(friendName.get(i), friendID.get(i));
				
				buttons[i] = new JButton(friendName.get(i));
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
	
	private void openChatFrame(final String chatToName){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				int id = nameToID.get(chatToName);
				((ChatLog) chatToList.get(id)).openChatFrame();
			}
		});
	}
	
	public void run(){
		while (true){
			System.out.println("1.!#23213");
			message = Message.receivePacket();
			int chatToID = (Integer) message.get("fromID");
			String chatToUsername = (String) message.get("fromUsername");
			System.out.println(chatToID);
			if (chatToList.get(chatToID) == null){
				System.out.println("0.!#23213");
				chatToList.put(chatToID, new ChatLog(chatToID, chatToUsername));
			}
			
			ChatLog chatLog = (ChatLog) chatToList.get(chatToID);
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
