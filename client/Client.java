/**
 * 
 */
package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author buhleallen
 *Client/Group member class, every user connects to the server through this class to chat with other people in the group chat
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Requesting New user's username and sending it to other members
		String userName = null;
		System.out.print("Enter Username: ");
		Scanner scUserName = new Scanner(System.in);
		userName = scUserName.nextLine();
		
		//Making sure code does not crash if client close window without joining group
		if(!userName.equals(null))
		{
			//Initialising objects
			Socket s = new Socket();
			BufferedReader br=null;
			BufferedWriter bw=null;
			InetAddress inetAddr;
			
			try {
				inetAddr = InetAddress.getByName("localhost");
				SocketAddress sAddr = new InetSocketAddress(inetAddr,2025);
				
				//Initialising connection to server
				initConnection(sAddr,s);
				
				try {
					bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					
					//Sending client username
					bw.write(userName);
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//Recieving Messages from other member
				recieveMassage(s,br,bw);
				
				//Sending Message
				sendMessage(s,br,bw);
			} catch (UnknownHostException e) {
				close(s,br,bw);
			}
		}
		scUserName.close();
	}
	
	//HELPER METHODS
	
	/**
	 * Method to 
	 * @param sAddr Server system address
	 * @param s client/user socket
	 */
	private static void initConnection(SocketAddress sAddr,Socket s)
	{
		try {
			s.connect(sAddr);
		} catch (IOException e) {
			System.err.println("Server failed to connect client");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to send messages to other group members
	 * @param s client/user socket
	 * @param br BufferedReader
	 * @param bw BufferedWriter
	 */
	private static void sendMessage(Socket s,BufferedReader br,BufferedWriter bw)
	{
		if(s.isConnected()) {
			try {
				Scanner message = new Scanner(System.in);
				while(s.isConnected()) {
					bw.write(message.nextLine());
					bw.newLine();
					bw.flush();
				}
				message.close();
				s.close();
			} catch (IOException e) {
				close(s,br,bw);
			}
		}
	}
	
	/**
	 * Method to recieve messages from other group members
	 * @param s client/user socket
	 * @param br BufferedReader
	 * @param bw BufferedWriter
	 */
	private static void recieveMassage(Socket s,BufferedReader br,BufferedWriter bw)
	{
		//Making a new thread so that a client can listen and send and not code excetution to listen for new messages
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(s.isConnected()) {
						if(br!=null) {
							System.out.println(br.readLine());
						}
					}
				} catch (IOException e) {
					close(s,br,bw);
				}
			}
			
		}).start();;
	}
	
	/**
	 * Method to close the Buffered streams and socket
	 * @param s client/user socket 
	 * @param br BufferedReader
	 * @param bw BufferedWriter
	 */
	public static void close(Socket s,BufferedReader br,BufferedWriter bw) {
		try {
			s.close();
			if(br!=null) {
				br.close();
			}
			if(bw!=null) {
				bw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
