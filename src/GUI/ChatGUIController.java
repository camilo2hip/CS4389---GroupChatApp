package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import javax.crypto.SecretKey;

import AES.AES;
import AES.AESUtil;
import Client.Client;
import RSA.RSAUtil;

public class ChatGUIController 
{
	@FXML private Label userMenuLabel;
	@FXML private TextArea messageTextDisplayArea;
	@FXML private TextField messageTextField;
	@FXML private Button messageSendButton;
	public static String username = "DefaultName";
	public String message;
	public String decryptedMessage = "";
	public static boolean printerRunning = false;
	
	public void initialize() throws InterruptedException {
		if(printerRunning || GUIResources.currentUser != null) {
			return;
		}
		GUIResources.currentUser = new Client(GUIResources.currentSocket, Client.getUsername());
		System.out.println("user found... listening... \n");
		Thread.sleep(500);
        if(!printerRunning) {
        	System.out.println("Testing: " + (GUIResources.currentUser == null));
        	Thread thread = new Thread(new PrinterRunnable(GUIResources.currentUser, messageTextDisplayArea));
        	thread.setDaemon(true);
        	thread.start();
        }
	}
	
    @FXML
    public void onSendButtonClick() throws IOException 
    {
    	username = Client.getUsername();
        message = messageTextField.getText();
        messageTextField.clear();
        updateMessageScreen(Client.getUsername(), message);
        Client.body += username + ": " + message;
        
        if(message.charAt(message.length() - 1) != '\n') {
        	Client.body += "\n";
        }
        GUIResources.currentUser.sendMessage(message);

    }

    public void updateMessageScreen(String message) 
    {
    	Platform.runLater(() -> messageTextDisplayArea.appendText(message + "\n"));
    }
    
    public void updateMessageScreen(String username, String message) 
    {
    	
    	updateMessageScreen(username + ": " + message);
    }

}
