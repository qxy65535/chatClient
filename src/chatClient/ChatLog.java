package chatClient;

import java.awt.BorderLayout;
import java.awt.Font;
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
	
	public void openChatFrame(){
		chatFrame = new ChatFrame();
		isChatFrameOpen = true;
		
		for (int i = 0, size = chatLog.size(); i < size; ++i){
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
		
		public ChatFrame(){
			super(chatToName);
			setSize(500, 450);
			//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());
			
			Font font = new Font("宋体", Font.PLAIN, 20);
			chatLogger = new JTextArea();
			chatLogger.setEditable(false);
			chatLogger.setLineWrap(true);
			chatLogger.setFont(font);
			inputArea = new JTextField();
			inputArea.setFont(font);
			send = new JButton("send");	
			send.setFont(font);
			
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
			
			setLocationRelativeTo(null);
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
			
			displayMessage("<<< Me " + getTime() + "\n  " + message.get("message") + "\n");
			Message.sendPacket(message);
			
			
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
			final String m = ">>> " + message.get("fromName") + " " + message.get("time") + "\n  " + message.get("message") + "\n";
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
