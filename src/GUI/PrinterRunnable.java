package GUI;
import java.io.IOException;

import javax.crypto.SecretKey;

import AES.AES;
import AES.AESUtil;
import Client.Client;
import RSA.RSAUtil;

import Client.Client;

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
		
		text.appendText("Welcome to the chat room!\n");
		while(true) {
			System.out.println(Client.getUsername() + ": " + GUIResources.currentUser.messageQueue.size());
			if(GUIResources.currentUser == null || GUIResources.currentSocket.isClosed()) {
				text.appendText("Connection Error. \n");
				break;
			}
			if (GUIResources.currentUser.messageQueue.size() > 0) {
				try {
					text.appendText(GUIResources.currentUser.messageQueue.poll());
					Thread.sleep(250);
				} catch (InterruptedException e) {
					text.appendText("Thread Error. \n");
					break;
				}
				
			}
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					text.appendText("Thread Error. \n");
					break;
				}
			}

		}

		
	}
}