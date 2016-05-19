package chatClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ClientFrame extends JFrame implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 247267039795100225L;
	private String username;
	private int userID;
//	private JButton button1 = new JButton("qxy");	
//	private JButton button2 = new JButton("admin");	
	
	//private JButton[] buttons;
	private JPanel friendListArea;
	private JPanel addFriendArea;
	private JTextField addFriendText;
	private JButton addButton;
	private TreeMouseListener listener;
	//private ButtonListener listener;
	
//	private ExecutorService chatRoom;
	private Map<Integer, Object> chatToList;
	private JTree friendTree; 
	private DefaultTreeModel friendTreeModel;
	private DefaultMutableTreeNode root; 
	private DefaultMutableTreeNode notice;
	private DefaultMutableTreeNode friends; 
	private DefaultMutableTreeNode stranger; 
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
		initViews(message);

		
		new Thread(this).start();
		setVisible(true);
	}
	
	private void initViews(Map<String, Object> message){
		Font font = new Font("宋体", Font.PLAIN, 20);
		
		friendListArea = new JPanel();
		friendListArea.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
		friendListArea.setBackground(Color.white);

		root = new DefaultMutableTreeNode("通讯录"); 
	    createTree(root, message); 
	    friendTree = new JTree(root);
	    friendTreeModel = (DefaultTreeModel) friendTree.getModel();
	    friendTree.setFont(font);
	    friendListArea.add(friendTree);
		
		addFriendArea = new JPanel();
		addFriendArea.setLayout(new BorderLayout());
		addFriendArea.setFont(font);
		
		addFriendText = new JTextField();
		addButton = new JButton("add");
		addButton.setFont(font);
		
		addFriendArea.add(addFriendText, BorderLayout.CENTER);
		addFriendArea.add(addButton, BorderLayout.EAST);
		add(new JScrollPane(friendListArea), BorderLayout.CENTER);
		add(addFriendArea, BorderLayout.SOUTH);
		
		//listener = new ButtonListener();
		listener = new TreeMouseListener();

		friendTree.addMouseListener(listener);
		
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
				case Code.DUP_USERNAME:
					JOptionPane.showMessageDialog(ClientFrame.this, "该用户已是您的好友！");
					return;
				case Code.SQL_EXCEPTION:
					JOptionPane.showMessageDialog(ClientFrame.this, "数据库错误！");
					return;
				case Code.FIAL_TO_CONNECT:
					JOptionPane.showMessageDialog(ClientFrame.this, "与服务器连接失败！");
					return;
				case Code.SUCCESS:
					JOptionPane.showMessageDialog(ClientFrame.this, "添加好友成功！");
					addFriendText.setText("");
					
					insertNewChat(friends, friendName, (Integer) responseMessage.get("userID"));
					return;
				}
			}
		});
		
	}
	
	private void createTree (DefaultMutableTreeNode root, Map<String, Object> message) { 

	   DefaultMutableTreeNode frinode; 
	   DefaultMutableTreeNode strangernode; 
	   
	   notice = new DefaultMutableTreeNode("新消息");
	   friends = new DefaultMutableTreeNode("我的好友"); 
	   stranger = new DefaultMutableTreeNode("陌生人"); 
	   
	   root.add(notice);
	   root.add(friends); 
	   root.add(stranger); 

		if (message.get("friendID") != null){
			ArrayList<Integer> friendID = (ArrayList<Integer>) message.get("friendID");
			System.out.println(message.get("friendID"));
			ArrayList<String> friendName = (ArrayList<String>) message.get("friendName");

			for (int i = 0; i < friendID.size(); ++i){
				chatToList.put(friendID.get(i), new ChatLog(friendID.get(i), friendName.get(i)));
				nameToID.put(friendName.get(i), friendID.get(i));
				
			    frinode = new DefaultMutableTreeNode(friendName.get(i));
			    friends.add(frinode);

			}
		}
	}
	
	private class TreeMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) friendTree.getLastSelectedPathComponent();
			try{
				String name = node.toString();
				if (name != null && nameToID.get(name) != null){
					openChatFrame(name);
					deleteNotice(name);
				}
			}catch (NullPointerException e1){
				//e1.printStackTrace();
			}
			friendTree.clearSelection();
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	private void openChatFrame(final String chatToName){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				int id = nameToID.get(chatToName);
				if (!((ChatLog) chatToList.get(id)).isChatFrameOpen())
					((ChatLog) chatToList.get(id)).openChatFrame();
			}
		});
	}
	
	private void insertNewChat(DefaultMutableTreeNode node, String username, int id){

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(username);
		System.out.println(username);
		friendTreeModel.insertNodeInto(newNode, node, node.getChildCount());
		chatToList.put(id, new ChatLog(id, username));
		nameToID.put(username, id);

	}
	
	private void addNewNotice(String name){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Enumeration e = notice.depthFirstEnumeration();
				
				while(e.hasMoreElements()){
				    DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
				    if (n.toString().equals(name)){
				    	return;
				    }
				}
				
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
				friendTreeModel.insertNodeInto(newNode, notice, notice.getChildCount());
			}
		});
	}
	
	private void deleteNotice(String name){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Enumeration e = notice.depthFirstEnumeration();
				
				while(e.hasMoreElements()){
				    DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
				    if (n.toString().equals(name)){
				    	friendTreeModel.removeNodeFromParent(n);
				    	break;
				    }
				}
			}
		});
	}
	
	public void run(){
		while (true){
			System.out.println("1.!#23213");
			message = Message.receivePacket();
			int chatToID = (Integer) message.get("fromID");
			String chatToUsername = (String) message.get("fromName");
			System.out.println(chatToID);
			System.out.println(chatToUsername);
			if (chatToList.get(chatToID) == null){
				System.out.println("0.!#23213");
				
				insertNewChat(stranger, chatToUsername, chatToID);
				
				System.out.println(chatToList);				

			}
			
			ChatLog chatLog = (ChatLog) chatToList.get(chatToID);
			if (chatLog.isChatFrameOpen()){
				System.out.println("2.!#23213");
				chatLog.getChatFrame().displayMessage(message);
				chatLog.getChatFrame().toFront();
			}
			else {
				addNewNotice(chatToUsername);
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
