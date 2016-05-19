package chatClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
//
//public class Client {
//	private DatagramSocket socket;
//	private DatagramPacket receivePacket;
//	
//	public Client(){
//		try{
//			socket = new DatagramSocket();
//		}catch (SocketException e){
//			e.printStackTrace();
//		}
//	}
//	
//	public void receivePacket(){
//		try{
//			byte[] data = new byte[1024];
//			receivePacket = new DatagramPacket(data, data.length);
//			
//			socket.receive(receivePacket);
//			
//			System.out.println(">>> " + new String(receivePacket.getData(), 0, receivePacket.getLength()));
//			
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public void sendPacket(){
//		try{
//			Map<String, Object> userInfo = new HashMap<String, Object>();
//			String message;
//			BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
//			message = strin.readLine();
//			userInfo.put("userName", message);
//			message = strin.readLine();
//			userInfo.put("password", message);
//			
//			
//			byte[] data = convertMapToByteArray(userInfo);
//			DatagramPacket sendPacket = new DatagramPacket(data, data.length, 
//					InetAddress.getLocalHost(), 12345);
//			
//			socket.send(sendPacket);
//			
//			System.out.println("<<< " + message);
//			
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//	}
//
//	
//	public static byte[] convertMapToByteArray(Map<String, Object> map) {
//	    try {		      
//	    	ByteArrayOutputStream b = new ByteArrayOutputStream();
//		    ObjectOutputStream ois = new ObjectOutputStream(b);
//		    ois.writeObject(map);
//		    byte[] temp = b.toByteArray();
//		    return temp;
//		      
//		  } catch (Exception e) {
//			  e.printStackTrace();
//		  }
//		  return null;
//	}
//}
