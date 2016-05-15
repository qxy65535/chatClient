package chatClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;

import javax.swing.JOptionPane;

public class Message {
	
	private static DatagramSocket socket;
	
	static{
		try{
			socket = new DatagramSocket();
		}catch (SocketException e){
			e.printStackTrace();
		}
	}
	
	
	public static void sendPacket(Map<String, Object> message){
		try{
			byte[] data = convertToByte(message);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 12345);
			
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
