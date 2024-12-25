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

/**
 * @author buhleallen
 *
 */
public class ClientHandler implements Runnable{
	
	public ClientHandler(Socket s) {
		this._socket = s;
		try {
			this._br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this._bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			_userName = _br.readLine();
			
			sendMessage("*"+_userName+" has joined the chat*");
			System.out.println("*"+_userName+" has joined the chat*");
			ServerGlobalVariables.clientList.add(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(_socket.isConnected()){
			String message;
			try {
				message = _userName +": "+ _br.readLine();
				sendMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//Helpers
	private void sendMessage(String message) {
		
		for(ClientHandler h:ServerGlobalVariables.clientList) {
			if(!h._userName.equals(_userName)) {
				 try {
					 h._bw.write(message);
						h._bw.newLine();
						h._bw.flush();
					System.out.println(message);
				} catch (IOException e) {
					close(h);
					ServerGlobalVariables.clientList.remove(h);
					e.printStackTrace();
				}
			}
		}
		
	}
	private void close(ClientHandler h) {
		try {
			h._socket.close();
			h._br.close();
			h._bw.close();
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
}
