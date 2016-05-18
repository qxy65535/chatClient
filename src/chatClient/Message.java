package chatClient;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Message {
	
	private static DatagramSocket socket;
	private static Socket client;
	private static String host = "172.20.84.173";
	private static int port;
	private static ObjectInputStream input;
	private static ObjectOutputStream output;
	
	static{
		try{
			socket = new DatagramSocket();
			port = socket.getLocalPort();
		}catch (SocketException e){
			e.printStackTrace();
		}
	}
	
	private static void connectToServer(){
		//System.out.println("000");
		try{
			client = new Socket(host, 12344);
			//System.out.println("111");
			output = new ObjectOutputStream(client.getOutputStream());
			//System.out.println("222");
			input = new ObjectInputStream(client.getInputStream());
			//System.out.println("333");
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static Map<String, Object> login(Map<String, Object> userInfo){
		connectToServer();
		userInfo.put("port", port);
		
		return sendData(userInfo);
//		Map<String, Object> message = new HashMap<String, Object>();
//		
//		//System.out.println("123");
//		try {
//			userInfo.put("port", port);
//			sendData(userInfo);
//			//System.out.println("456");
//			message = (Map<String, Object>) input.readObject();
//			//System.out.println("789");
//			return message;
//		}catch (IOException e){
//			e.printStackTrace();
//			message.put("messageCode", Code.UNKNOW_ERROR);
//		}finally {
//			return message;
//		}
//		
	}
	
	public static void logout(){
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("type", "logout");
		
		try {
			
			output.writeObject(message);
			output.flush();

			output.close();
			input.close();
			client.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static Map<String, Object> addFriend(Map<String, Object> info){
		return sendData(info);
	}
	
	private static Map<String, Object> sendData(Object message){
		Map<String, Object> responseMessage = new HashMap<String, Object>();
		
		//System.out.println("123");
		try {
			
			output.writeObject(message);
			output.flush();
			System.out.println("456");
			responseMessage = (Map<String, Object>) input.readObject();
			System.out.println("789");
			return responseMessage;
		}catch (IOException e){
			e.printStackTrace();
			responseMessage.put("messageCode", Code.UNKNOW_ERROR);
		}finally {
			return responseMessage;
		}

	}
	
	public static void sendPacket(Map<String, Object> message){
		try{
			byte[] data = convertToByte(message);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(host), 54321);
			//System.out.println(socket.getPort());
			//System.out.println(sendPacket.getPort());
			socket.send(sendPacket);
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static Map<String, Object> receiveResponsePacket(){
		try {
			byte[] data = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			socket.receive(receivePacket);
			
			return convertToMap(receivePacket.getData());

		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Map<String, Object> receivePacket(){
		try {
			byte[] data = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			socket.receive(receivePacket);
			
			return convertToMap(receivePacket.getData());

		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	private static Map<String, Object> convertToMap(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
		    Map<String, Object> result = (Map<String, Object>) ois.readObject();
		    return result;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] convertToByte(Map<String, Object> map) {
	    try {
	    	ByteArrayOutputStream b = new ByteArrayOutputStream();
		    ObjectOutputStream ois = new ObjectOutputStream(b);
		    ois.writeObject(map);
		    byte[] temp = b.toByteArray();
		    return temp;
		      
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  return null;
	}
}
