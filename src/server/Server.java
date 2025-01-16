/**
 * 
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author buhleallen
 *
 */
public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ss = new ServerSocket(2025);
			System.out.println("Running on port: "+ss.getLocalPort());
			while(true) {
				acceptClientConnection();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Helpers
	
	/**
	 * Method to accept client connection
	 * @param ss ServerSocket
	 */
	private static void acceptClientConnection()
	{
		try {
			Socket socket = ss.accept();
			Thread client = new Thread(new ClientHandler(socket));
			client.start();
		} catch (IOException e) {
			close();
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to close server socket
	 */
	private static void close()
	{
		try {
			if(ss!=null) {
				ss.close();
			}
		}catch(IOException e){
			System.err.println("Failed Closing server Socket");
			e.printStackTrace();
		}
	}
	
	//Variables
	private static ServerSocket ss;

}
