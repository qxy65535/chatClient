package chatClient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
	private JPanel friendListArea;
	private JPanel addFriendArea;
	private JTextField addFriendText;
	private JButton addButton;
	private ButtonListener listener;
	
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
		
		setLayout(new BorderLayout());
		setSize(300, 400);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initViews();
		
		if (message.get("friendID") != null){
			ArrayList<Integer> friendID = (ArrayList<Integer>) message.get("friendID");
			System.out.println(message.get("friendID"));
			ArrayList<String> friendName = (ArrayList<String>) message.get("friendName");
			buttons = new JButton[friendID.size()];
			
			for (int i = 0; i < friendID.size(); ++i){
				chatToList.put(friendID.get(i), new ChatLog(friendID.get(i), friendName.get(i)));
				nameToID.put(friendName.get(i), friendID.get(i));
				
				buttons[i] = new JButton(friendName.get(i));
				buttons[i].addActionListener(listener);
				friendListArea.add(buttons[i]);
			}
		}
		
		//chatRoom = Executors.newCachedThreadPool();
		

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
	
	private void initViews(){
		friendListArea = new JPanel();
		friendListArea.setLayout(new FlowLayout());
		add(friendListArea, BorderLayout.CENTER);
		
		addFriendArea = new JPanel();
		addFriendArea.setLayout(new BorderLayout());
		
		addFriendText = new JTextField();
		addButton = new JButton("add");
		
		addFriendArea.add(addFriendText, BorderLayout.CENTER);
		addFriendArea.add(addButton, BorderLayout.EAST);
		add(addFriendArea, BorderLayout.SOUTH);
		
		listener = new ButtonListener();
		
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String friendName = addFriendText.getText();
				if ("".equals(friendName))
					return;
				Map<String, Object> message = new HashMap<String, Object>();
				
				message.put("username", username);
				message.put("userID", userID);
				message.put("friendName", friendName);
				message.put("type", "addFriend");
				
				//Message.sendPacket(message);
				//Map<String, Object> respenseMessage = Message.receiveResponsePacket();
				Map<String, Object> responseMessage = Message.addFriend(message);
				switch((Integer) responseMessage.get("messageCode")){
				case Code.USER_INFO_ERROR:
					JOptionPane.showMessageDialog(ClientFrame.this, "用户不存在！");
					return;
				case Code.SQL_EXCEPTION:
					JOptionPane.showMessageDialog(ClientFrame.this, "数据库错误！");
					return;
				case Code.SUCCESS:
					JOptionPane.showMessageDialog(ClientFrame.this, "添加好友成功！");
					addFriendText.setText("");
					JButton button = new JButton(friendName);
					String name = (String)responseMessage.get("username");
					int id = (Integer) responseMessage.get("userID");
					chatToList.put(id, new ChatLog(id, name));
					nameToID.put(name, id);
					button.addActionListener(listener);
					friendListArea.add(button);
					friendListArea.updateUI();
					return;
				}
			}
		});
		
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
	
    protected void processWindowEvent(WindowEvent event) {  
        if (event.getID() == WindowEvent.WINDOW_CLOSING){
        	System.out.println("exit");
        	Message.logout();//直接返回，阻止默认动作，阻止窗口关闭  
        }
        super.processWindowEvent(event); //该语句会执行窗口事件的默认动作(如：隐藏)  
    }  
	
}
