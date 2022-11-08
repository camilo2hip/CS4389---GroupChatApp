package Client;

import AES.AES;
import RSA.RSAUtil;
import AES.AESUtil;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;


	//add public and private key
	
	public Client(Socket socket, String username) throws IOException {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.username = username;


		} catch(IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendMessage() {
		try {
			bufferedWriter.write(username);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner scanner = new Scanner(System.in);
			while(socket.isConnected()) {
				String messageToSend = scanner.nextLine();
				bufferedWriter.write(username + ": " + messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String msgFromGroupChat;

				while(socket.isConnected()) {
					try {
						msgFromGroupChat = bufferedReader.readLine();

						String[] parts = msgFromGroupChat.split("--");
						String encryptedAESKey = parts[0];
						String privateKey = parts[1];
						String encryptedMessage = parts[2];


						//Decrypt the AES key encrypted with the client's public key using the client's private key
						String decryptedAesKey = RSAUtil.decrypt(encryptedAESKey, privateKey);

						//get the AES key
						SecretKey aesKey = AESUtil.convertStringToSecretKeyto(decryptedAesKey);


						//Decrypt the encrypted message using the decrypted AES key
						String decryptedMessage = AES.decrypt(encryptedMessage, aesKey);

						//display the message
						System.out.println(decryptedMessage);
					} catch (IOException e) {
						closeEverything(socket, bufferedReader, bufferedWriter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		try{
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if(socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your username for the group chat: ");

		String username = scanner.nextLine();
		
		Socket socket = new Socket("localhost", 1234);
		Client client = new Client(socket, username);
		client.listenForMessage();
		client.sendMessage();
	}
}
