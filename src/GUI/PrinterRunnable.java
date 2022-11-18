package GUI;
import java.io.IOException;

import javax.crypto.SecretKey;

import AES.AES;
import AES.AESUtil;
import Client.Client;
import RSA.RSAUtil;

import Client.Client;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class PrinterRunnable implements Runnable {
	private Client client;
	private TextArea text;
	public PrinterRunnable(Client client, TextArea text) {
		this.client = client;
		this.text = text;
		ChatGUIController.printerRunning = true;
	}

	@Override
	public void run() {
		
		//text.appendText("Welcome to the chat room!\n");
		while(true) {
			
			text.setText(Client.body);

//			if(GUIResources.currentUser == null || GUIResources.currentSocket.isClosed()) {
//				text.appendText("Connection Error. \n");
//				break;
//			}
//			if (GUIResources.currentUser.messageQueue.peek() != null) {
//				System.out.println("Message found, printing...");
//				String userSent = GUIResources.currentUser.messageQueue.poll();
//				final String guiSent = userSent.charAt(userSent.length() - 1) == '\n' ? userSent : userSent + "\n";
//				if(Platform.isFxApplicationThread()) {
//					text.appendText(guiSent);
//
//				}
//				else {
//					Platform.runLater(() -> text.appendText(guiSent));
//				}
//				
//			}
//			else {
//				try {
//					System.out.println("No messages found, waiting...");
//					Thread.sleep(15);
//				} catch (InterruptedException e) {
//					text.appendText("Thread Error. \n");
//					break;
//				}
//			}

		}
		
		//ChatGUIController.printerRunning = false;

		
	}
}