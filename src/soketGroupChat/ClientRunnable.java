package soketGroupChat;

import java.io.IOException;

import javax.crypto.SecretKey;

import AES.AES;
import AES.AESUtil;
import Client.Client;
import RSA.RSAUtil;

import Client.Client;

public class ClientRunnable implements Runnable {
	private Client client;
	
	public ClientRunnable(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String msgFromGroupChat;

		while(client.socket.isConnected()) {
			try {
				System.out.println("Reading buffer...");
				msgFromGroupChat = client.bufferedReader.readLine();

				if(msgFromGroupChat != null && !msgFromGroupChat.isEmpty()) {
					System.out.println("Message found!");
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
					System.out.println("Adding to queue...");
					client.messageQueue.add(decryptedMessage);
					System.out.println(decryptedMessage);
				}

			} catch (IOException e) {
				client.closeEverything();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}