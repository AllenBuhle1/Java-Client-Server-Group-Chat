/**
 * 
 */
package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author buhleallen
 *
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//Initialising variables
			Socket s = new Socket("localhost",2025);
			isLoggedIn = true;
			_bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			_br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			//Initialising connection to server
			initConnection();
			
			//Recieving Messages from other member
			recieveMassage();
			
			//Sending Message
			sendMessage(s);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Helpers
	private static void initConnection()
	{
		System.out.print("Enter Username: ");
		Scanner scUserName = new Scanner(System.in);
		try {
			_bw.write(scUserName.nextLine());
			_bw.newLine();
			_bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sendMessage(Socket s)
	{
		try {
			Scanner message = new Scanner(System.in);
			while(s.isConnected()) {
				_bw.write(message.nextLine());
				_bw.newLine();
				_bw.flush();
			}
			message.close();
			s.close();
		} catch (IOException e) {
			try {
				s.close();
				_br.close();
				_bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	private static void recieveMassage()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(isLoggedIn) {
						System.out.println(_br.readLine());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();;
	}
	//Varibles
	private static BufferedReader _br;
	private static BufferedWriter _bw;
	private static boolean isLoggedIn = false;
}
