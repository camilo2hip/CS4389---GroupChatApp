package GUI;

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
        GUIResources.currentUser.sendMessage(message);

    }
    

    @FXML
    public void updateUserMenu() {

    }

    public void updateMessageScreen(String message) 
    {
    	messageTextDisplayArea.appendText(message + "\n");
    }
    
    public void updateMessageScreen(String username, String message) 
    {
    	updateMessageScreen(username + ": " + message);
    }

    public static void listenForMessage() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String msgFromGroupChat;

				while(GUIResources.currentSocket.isConnected()) {
					try {
						msgFromGroupChat = GUIResources.currentUser.bufferedReader.readLine();
						
						String[] parts = msgFromGroupChat.split("--");

						String privateKey = parts[0];
						String encryptedAESKey = parts[1];
						String encryptedMessage = parts[2];

						//Decrypt the AES key encrypted with the client's public key using the client's private key
						String decryptedAesKey = RSAUtil.decrypt(encryptedAESKey, privateKey);

						//get the AES key
						SecretKey aesKey = AESUtil.convertStringToSecretKeyto(decryptedAesKey);

						//Decrypt the encrypted message using the decrypted AES key
						String decryptedMessage = AES.decrypt(encryptedMessage, aesKey);


						//String decryptedMessage = RSAUtil.decrypt(encryptedMessage, privateKey);

						//display the message
						System.out.println(decryptedMessage);
						//updateMessageScreen(decryptedMessage);
					} 
						catch (IOException e) {
						GUIResources.currentUser.closeEverything();
					} 
				catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.setDaemon(true);
		thread.start();
	}

}
