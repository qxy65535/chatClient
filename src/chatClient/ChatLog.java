package chatClient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatLog {
	
	private static String username;
	private static int userID;
	private int chatToID;
	private String chatToName;
	private LinkedBlockingQueue<Map<String, Object>> chatLog;
	private boolean isChatFrameOpen;
	private ChatFrame chatFrame;
	
	public ChatLog(int chatToID, String chatToName){
		this.chatToID = chatToID;
		this.chatToName = chatToName;
		chatLog = new LinkedBlockingQueue<Map<String, Object>>();
		isChatFrameOpen = false;
	}
	
	public void addUnreadMessage(Map<String, Object> message){
		try{
			//System.out.println("6.!#23213");
			chatLog.add(message);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean isChatFrameOpen(){
		return isChatFrameOpen;
	}
	
	public ChatFrame getChatFrame(){
		return chatFrame;
	}
	
	public void openChatFrame(/*String username, int userID, String chatTo*/){
		chatFrame = new ChatFrame(/*username, userID, chatTo*/);
		isChatFrameOpen = true;
		
		//System.out.println("4.!#23213    " + chatLog.size());
		for (int i = 0, size = chatLog.size(); i < size; ++i){
			//System.out.println("5.!#23213    " + chatLog.size());
			chatFrame.displayMessage(chatLog.poll());
		}
	}
	
	public static void initUser(String username_, int userID_){
		username = username_;
		userID = userID_;
	}
	
	public class ChatFrame extends JFrame{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -5410005616099472821L;
		private JTextArea chatLogger;
		private JTextField inputArea;
		private JButton send;
		
//		private String username;
//		private int userID;
//		private String chatTo;
		
		public ChatFrame(/*String username, int userID, String chatTo*/){
			super(chatToName);
//			
//			this.username = username;
//			this.userID = userID;
//			this.chatTo = chatTo;
			
			setSize(500, 450);
			//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());
			
			chatLogger = new JTextArea();
			chatLogger.setEditable(false);
			chatLogger.setLineWrap(true);
			inputArea = new JTextField();
			send = new JButton("send");	
			
			JPanel logPanel = new JPanel();
			logPanel.setLayout(new BorderLayout());
			logPanel.add(new JScrollPane(chatLogger), BorderLayout.CENTER);
			
			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new BorderLayout());
			inputPanel.add(inputArea, BorderLayout.CENTER);
			inputPanel.add(send, BorderLayout.EAST);
			
			add(logPanel, BorderLayout.CENTER);
			add(inputPanel, BorderLayout.SOUTH);
			
			send.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage();
				}
			});
			
			inputArea.addKeyListener(new KeyAdapter(){
				 public void keyPressed(KeyEvent event){ 
					 if (event.getKeyCode() == KeyEvent.VK_ENTER){ 
						 send.doClick();
					 }
				 }
			});
			
			setVisible(true);
		}
		
		private void sendMessage(){
			Map<String, Object> message = new HashMap<String, Object>();
			System.out.println(username + userID);
			
			
			message.put("username", username);
			message.put("userID", userID);
			message.put("chatToName", chatToName);
			message.put("chatToID", chatToID);
			message.put("type", "message");
			message.put("message", inputArea.getText());
			
			Message.sendPacket(message);
			displayMessage("<<< Me " + getTime() + "\n  " + message.get("message") + "\n");
			
			inputArea.setText("");
		}
		
		public void displayMessage(final String message){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					chatLogger.append(message);
				}
			});
		}
		
		public void displayMessage(Map<String, Object> message){
			final String m = ">>> " + chatToName + " " + message.get("time") + "\n  " + message.get("message") + "\n";
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
		
//		public void run(){
//			while (true){
//				Map<String, Object> message = Message.receivePacket();
//				
//				displayMessage(">>> " + chatTo + " " + message.get("time") + "\n  " + message.get("message") + "\n");
//			}
//		}
	    protected void processWindowEvent(WindowEvent event) {  
	        if (event.getID() == WindowEvent.WINDOW_CLOSING){
	        	System.out.println("click close");
				isChatFrameOpen = false;
				this.dispose();
	        	return; //直接返回，阻止默认动作，阻止窗口关闭  
	        }
	        super.processWindowEvent(event); //该语句会执行窗口事件的默认动作(如：隐藏)  
	    }  
		
	}

}
