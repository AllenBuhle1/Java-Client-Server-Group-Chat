/**
 * 
 */


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author buhleallen
 *Client/Group member class, every user connects to the server through this class to chat with other people in the group chat
 */
public class Client extends Application {

	public static void main(String[]args) {
		launch(args);
	}
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(new AnchorPaneChat(),300,400));
		primaryStage.setResizable(false);
		primaryStage.setTitle("BUHLE ALLEN GROUP CHAT");
		primaryStage.show();
	}
	
}
