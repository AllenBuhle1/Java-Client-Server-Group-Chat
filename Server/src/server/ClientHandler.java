/**
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author buhleallen
 *Method to handle client connection
 */
public class ClientHandler implements Runnable{
	
	/**
	 * Constructor method to initialize buffered streams and client socket
	 * Also tells other members that a new member has joined the chat
	 * and add member to the list of group members
	 * @param s client/user socket
	 */
	public ClientHandler(Socket s) {
		this._socket = s;
		try {
			this._br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this._bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
			_userName = _br.readLine();
			
			//Telling group members that new user has joined chat 
			sendMessage("*"+_userName+" has joined the chat*",true);
			System.out.println("*"+_userName+" has joined the chat*");
			clientList.add(this);
		} catch (IOException e) {
			close(_socket,_br,_bw);
		}
	}

	@Override
	public void run() {
		//Broadcasting user message while user is still connected to the server
		String message;
		while(_socket.isConnected()){
			try {
				//Checking if the buffered reader is not null
				if(_br!=null) {
					String brMessage = _br.readLine();
					//Checking if read meassage is not null
					if(!brMessage.equals(null)) {
						message = _userName +": "+ brMessage;
						sendMessage(message,false);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}catch(NullPointerException e) {
				System.err.println(_userName+ " left");
				sendMessage("*"+_userName+" left chat*",true);
				break;
			}
		}
	}
	
	//HELPER METHODS
	
	/**
	 * Method to send message to other group members
	 * @param message message from this client to other group members
	 * @param joiningORliving true if the message tells other members that this user left/joined group
	 */
	private void sendMessage(String message,boolean joiningORliving) {
		if(!clientList.isEmpty()) {
			//Looping through all group members
			for(ClientHandler h:clientList) {
				
				//Make sure you do not send message to myself
				if(!h._userName.equals(_userName)) {
					 try {
						 //Checking if this members buffered writer is not null
						 if(h._bw!=null) {
							 
							//Sending message
							 String finalMessage = joiningORliving+","+message;
							 h._bw.write(finalMessage);
							 h._bw.newLine();
							 h._bw.flush();
						 }
					} catch (IOException e) {
						close(h._socket,h._br,h._bw);
						clientList.remove(h);
					}
				}
			}
		}
	}
	
	/**
	 * Method to close the Buffered streams and socket
	 * @param s client/user socket 
	 * @param br BufferedReader
	 * @param bw BufferedWriter
	 */
	private void close(Socket s,BufferedReader br,BufferedWriter bw) {
		try {
			if(br!=null) {
				br.close();
			}
			
			if(bw!=null) {
				bw.close();
			}
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Variables
	private Socket _socket;
	private BufferedReader _br;
	private BufferedWriter _bw;
	private String _userName;
	private static ArrayList<ClientHandler> clientList = new ArrayList<>();
}
