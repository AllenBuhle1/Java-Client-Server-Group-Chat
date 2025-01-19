/**
 * 
 */


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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author buhleallen
 *Class to handle Graphics and client logic
 */
public class AnchorPaneChat extends AnchorPane{
	/**
	 * Constructor, Display welcome window of the group chat and change it
	 * to chatting window when the user has successfully joined the chat
	 */
	public AnchorPaneChat() {
		this.setPrefSize(_WIDTH, _HEIGHT);
		
		//Username Text field
		final TextField tfUserName = new TextField();
		tfUserName.setPrefSize(240, 30);
		tfUserName.setLayoutX(30);
		tfUserName.setLayoutY(155);
		
		//ERROR MESSAGE label
		final Label lbError = new Label();
		lbError.setTextFill(Color.RED);
		lbError.setPrefWidth(240);
		lbError.setLayoutX(30);
		lbError.setLayoutY(140);
		lbError.setFont(new Font(10));
		
		
		//Join Group Chat Button
		Button btJoin = new Button("JOIN");
		btJoin.setPrefSize(100, 30);
		btJoin.setLayoutX(100);
		btJoin.setLayoutY(215);
		btJoin.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				//Checking if the username field is not empty/null to avoid Null pointer exception
				if(!(tfUserName.getText()==null || tfUserName.getText().trim().isEmpty())) {
					
					//Only allow users with username length <19 to join
					if(tfUserName.getText().length()<19) {
						String userName = tfUserName.getText();
						joinChat(userName);
					}else {
						lbError.setText("Username must be <19 characters!");
					}
					
				}else {
					lbError.setText("Username can't be empty!");
				}
			}
		});
		
		//Adding welcome window components
		this.getChildren().add(lbError);
		this.getChildren().add(tfUserName);
		this.getChildren().add(btJoin);
	}
	
	//HELPER METHODS
	    
	    /**
	     * Method to handle events for all components in chat window
	     * @param s client socket
	     * @param br BufferedReader
	     * @param bw BufferedWriter
	     */
	    private void handleAllEvents(final Socket s,final BufferedReader br,final BufferedWriter bw) {
	    	/*
	    	 * VERTICAL BOX EVENT HANDLER
	    	 * Listen to the change in height of the  box
	    	 */
				_vbTextWrapper.heightProperty().addListener(new ChangeListener<Number>() {
		
					public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
						//Scrolling the scroll pane to the bottom
						_spTextWrapper.setVvalue((Double) newValue);
					}
				});
				
				/*
				 * SEND BUTTON EVENT HANDLER
				 * Sending message when send button is pressed
				 */
				_btSend.setOnAction(new EventHandler<ActionEvent>() {

					public void handle(ActionEvent event) {
						//Checking if the message text field is not empty
						if(!(_tfMessage.getText()==null || _tfMessage.getText().trim().isEmpty())) {
							String message = _tfMessage.getText();
							sendMessage(s,br,bw,message);
						}
					}
				});
	     }
	    
	    

	    /**
	     * Method To create gui for chat window
	     * @param userName username of the new member
	     */
	    private void chatGui(String userName) {
	    	//Initializing components
			_btSend = new Button("SEND");
			_tfMessage = new TextField();
			_spTextWrapper = new ScrollPane();
			_vbTextWrapper = new VBox();
			
			//Vertical Box Wrapping Texts
			_vbTextWrapper.setPrefSize(260, 310);
			
			//Label to Identify Phone
			Label lbPhoneIdentifier = new Label(userName+" Phone");
			lbPhoneIdentifier.setLayoutX(15);
			lbPhoneIdentifier.setLayoutY(10);
			
			//ScrollPane Wrapping Texts
			_spTextWrapper.setPrefSize(270, 315);
			_spTextWrapper.setLayoutX(15);
			_spTextWrapper.setLayoutY(25);
			_spTextWrapper.setFitToWidth(true);
			_spTextWrapper.setContent(_vbTextWrapper);
			
			
			//Send Button
			_btSend.setPrefSize(60, 30);
			_btSend.setLayoutX(225);
			_btSend.setLayoutY(355);
			
			//Message to send text field
			_tfMessage.setPrefSize(200, 30);	
			_tfMessage.setLayoutX(15);
			_tfMessage.setLayoutY(355);
				
			//Add Components to Anchor Pane
			this.getChildren().add(lbPhoneIdentifier);
			this.getChildren().add(_spTextWrapper);
			this.getChildren().add(_tfMessage);
			this.getChildren().add(_btSend);
	    }
	    

	     /**
	      * Method to Join Group Chat
	     * @param userName username of the new member
	     */
	    private void joinChat(String userName)
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
					
					//Sending client username to server
					bw.write(userName);
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Creating chat GUI
				chatGui(userName);
				
				//Recieving Messages from other member
				recieveMassage(s,br,bw);
				
				//Handling All events in the chat screen
				handleAllEvents(s,br,bw);
			} catch (UnknownHostException e) {
				close(s,br,bw);
			}
	     }
	     
	     
		/**
		 * Method to initialize connection
		 * @param sAddr Server system address and port
		 * @param s client/user socket
		 */
		private void initConnection(SocketAddress sAddr,Socket s)
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
		 * @param message Message to send to group
		 */
		private void sendMessage(Socket s,BufferedReader br,BufferedWriter bw,String message)
		{
			try {
				//Sending message
				bw.write(message);
				bw.newLine();
				bw.flush();
				
				//Displaying Message to me/sender
					//Horizontal Box Message wrapper to allow proper alignment
					HBox hbTextWrapper = new HBox();
					hbTextWrapper.setAlignment(Pos.CENTER_RIGHT);
					Insets hbPadding = new Insets(5,5,5,40);
					hbTextWrapper.setPadding(hbPadding);
					
					//Text message and text floe to allow text wrap
					Text txMessage = new Text(message);
					TextFlow tfMessage = new TextFlow(txMessage);
					tfMessage.setStyle("-fx-color:rgb(255,0,0); "+
									   "-fx-background-color:rgb(42,82,190); "+
									   "-fx-background-radius:"+_TEXT_FLOW_RADIUS+"px; ");
					Insets tfMessagePadding = new Insets(5,10,5,10);
					tfMessage.setPadding(tfMessagePadding);
					txMessage.setFill(Color.color(0.934,0.945,0.996));
					
					//Adding message to Horizontal Box Message wrapper
					hbTextWrapper.getChildren().add(tfMessage);
					
					//Adding the new message to the screen
					_vbTextWrapper.getChildren().add(hbTextWrapper);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Method to recieve messages from other group members
		 * @param s client/user socket
		 * @param br BufferedReader
		 * @param bw BufferedWriter
		 */
		private void recieveMassage(final Socket s,final BufferedReader br,final BufferedWriter bw)
		{
			//Making a new thread so that a client can listen and send and not code 
			//stop execution while listen for new messages
			new Thread(new Runnable() {
				public void run() {
					//Listen for new messages while user still connected to server
					while(s.isConnected()) {
						try {
							//Checking if the buffered reader is not null
							if(br!=null) {
								//Reading message from buffered reader 
								String message = br.readLine();
								
								//Checking if the message is not null 
								if(message!=null)
								{
									//Seperating the boolean joining or living Chat from rest of the message
									String joiningORliving = message.substring(0,message.indexOf(","));
									final String messageWithUserName = message.substring(message.indexOf(",")+1,message.length());
									
									//Checking if the member is joining or living Chat
									if(joiningORliving.equals("false")) {
										//Seperating username from message
										String userName = messageWithUserName.substring(0,messageWithUserName.indexOf(":"));
										String userMessage = messageWithUserName.substring(messageWithUserName.indexOf(":")+1,messageWithUserName.length());
										
										//adding message gui component to screen
										recievedMessageTag(userName,userMessage);
									}else if(joiningORliving.equals("true")) {
										/*
										 * Telling other members that a member has joined/left
										 * Making sure this run on the application thread
										 */
										Platform.runLater(new Runnable() {

											public void run() {
												// Creating component for new user that joined chat
												HBox hbJoinChatWrapper = new HBox();
												hbJoinChatWrapper.setAlignment(Pos.CENTER);
												Insets hbJoinChatWrapperPadding = new Insets(5,5,5,5);
												hbJoinChatWrapper.setPadding(hbJoinChatWrapperPadding);
												Label lbMessage = new Label(messageWithUserName);
												lbMessage.setFont(new Font(10));
												
												hbJoinChatWrapper.getChildren().add(lbMessage);
												_vbTextWrapper.getChildren().add(hbJoinChatWrapper);
											}
										});
									}
								}
							}
						}catch (IOException e) {
							close(s,br,bw);
							break;
						}
					}
					
				}
				
			}).start();;
		}
		
		/**
		 * Method to add the recieved message to the screen
		 * @param userName senders username
		 * @param message message recieved
		 */
		private void recievedMessageTag(String userName,String message) {
			//Horizontal Box Message wrapper to allow proper alignment
			final HBox hbTextWrapper = new HBox();
			hbTextWrapper.setAlignment(Pos.CENTER_LEFT);
			Insets hbTextWrapperPadding = new Insets(5,40,5,5);
			hbTextWrapper.setPadding(hbTextWrapperPadding);
			
			//Recieved Message text and text flow to allow text wrap
			Text txMessage = new Text(message);
			TextFlow tfMessage = new TextFlow(txMessage);
			tfMessage.setStyle("-fx-color:rgb(0,0,0); "+
					   "-fx-background-color:rgb(211,211,211); "+
					   "-fx-background-radius:"+_TEXT_FLOW_RADIUS+"px; ");
			Insets tfMessagePadding = new Insets(5,10,5,10);
			tfMessage.setPadding(tfMessagePadding);
			
			//Recieved message sender's username
			Label lbUserName = new Label(userName);
			lbUserName.setFont(new Font(10));
			
			//Adding message to screen
			VBox tempWrapper = new VBox();
			tempWrapper.getChildren().add(lbUserName);
			tempWrapper.getChildren().add(tfMessage);
			
			//Adding the message to the Horizontal Box Message wrapper
			hbTextWrapper.getChildren().add(tempWrapper);
			
			//Making sure it runs from application thread and not recieve message thread
			Platform.runLater(new Runnable() {

				public void run() {
					//Adding message to chat screen
					_vbTextWrapper.getChildren().add(hbTextWrapper);
				}
				
			});
		}
		
		/**
		 * Method to close the Buffered streams and socket
		 * @param s client/user socket 
		 * @param br BufferedReader
		 * @param bw BufferedWriter
		 */
		public void close(Socket s,BufferedReader br,BufferedWriter bw) {
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
	
	//VARIABLES
		//GUI Components
		Button _btSend;
		TextField _tfMessage;
		ScrollPane _spTextWrapper;
		VBox _vbTextWrapper;
		
		//Window dimensions
		private final double _HEIGHT = 350;
		private final double _WIDTH = 300;
		
		//Styling
		private final double _TEXT_FLOW_RADIUS = 5;
}
