/**
 * 
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
			ServerSocket ss = new ServerSocket(2025);
			System.out.println("Running on port: "+ss.getLocalPort());
			ServerGlobalVariables.clientList = new ArrayList<>();
			while(true) {
				acceptClientConnection(ss);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Helpers
	
	private static void acceptClientConnection(ServerSocket ss)
	{
		try {
			Socket socket = ss.accept();
			System.out.println("Connection Established...");
			Thread client = new Thread(new ClientHandler(socket));
			client.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
