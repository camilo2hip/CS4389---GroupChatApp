package soketGroupChat;

import AES.AES;
import AES.AESUtil;
import RSA.RSAKeyPairGenerator;
import RSA.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class ClientHandler implements Runnable{

	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	public static HashMap<String, RSAKeyPairGenerator> map =  new HashMap<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUsername;
	private RSAKeyPairGenerator keyPairGenerator;
	//private String AESkey = "d8VBc9D7mrA=";

	
	public ClientHandler(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientUsername = bufferedReader.readLine().split(":")[0];

			//Generate the user's public and private key and save it in an encoded file
			boolean succ = new File("." + File.separatorChar + clientUsername + File.separatorChar).mkdirs();
			if(succ) {
				System.out.println("Creating new folder for " + clientUsername + "...");
			}
			else {
				System.out.println("Found folder for " + clientUsername + ".");
			}
			
			this.keyPairGenerator = new RSAKeyPairGenerator();
			String publicPath = clientUsername + "/publicKey";
			String privatePath = clientUsername + "/privateKey";
			System.out.printf("publicPath: %s \n privatePath: %s \n", publicPath, privatePath);
			keyPairGenerator.writeToFile(publicPath, keyPairGenerator.getPublicKey().getEncoded());
			keyPairGenerator.writeToFile(privatePath, keyPairGenerator.getPrivateKey().getEncoded());
			clientHandlers.add(this);
			map.put(clientUsername, keyPairGenerator);

			broadcastMessage("SERVER: " + clientUsername + " entered the chat!");

		} catch(Exception e) {
			System.out.println("CLOSING3");
			e.printStackTrace();
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	@Override
	public void run() {
		//System.out.println("Entering client handler.");
		String messageFromClient;
		
		while(socket.isConnected()) {
			try {
				//System.out.println("preparing to read message.");
				messageFromClient = bufferedReader.readLine();
				//System.out.println("Received message from client: " + messageFromClient);
				broadcastMessage(messageFromClient);
			} catch(IOException e) {
				System.out.println("CLOSING4");
				e.printStackTrace();
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}
	
	
	public void broadcastMessage(String messageToSend) {
		System.out.println("Broadcasting message: " + messageToSend + " [END]");
		String publicKey;
		String privateKey;
		for(ClientHandler clientHandler: clientHandlers) {
			try {
				if(!clientHandler.clientUsername.equals(clientUsername)) {
					System.out.println("Encoding");
					//TODO: First time users have error here, works after logging in again
					publicKey =  Base64.getEncoder().encodeToString(map.get(clientUsername).getPublicKey().getEncoded());

					privateKey = Base64.getEncoder().encodeToString(map.get(clientUsername).getPrivateKey().getEncoded());

					//encrypt the message to be sent using RSA
					//byte[] encryptedMessage = RSAUtil.encrypt(messageToSend,publicKey);

					// byte[] to string
					// encode, convert byte[] to base64 encoded string
					//String s = Base64.getEncoder().encodeToString(encryptedMessage);


					AES aes = new AES();

					//get the AES secret key
					SecretKey aesKey = aes.init("secret", "salt");
					System.out.println("Encrypting");
					//encrypt the message to be sent using AES
					String encryptedMessage = aes.encrypt(messageToSend,aesKey);
					
					//convert AES key to string
					String encodedAesKey = AESUtil.convertSecretKeyToString(aesKey);

					//encrypt the encoded AES key using RSA
					byte[] encryptedAesKeyAsByte = RSAUtil.encrypt(encodedAesKey, publicKey);

					String encryptedAesKeyAsString = Base64.getEncoder().encodeToString(encryptedAesKeyAsByte);

					System.out.println("Preparing to broadcast");
					String valueToSend = privateKey + "--" + encryptedAesKeyAsString + "--" + encryptedMessage;
					System.out.println("Broadcasting message: " + valueToSend);
					clientHandler.bufferedWriter.write(valueToSend);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
			} catch(IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException e) {
				System.out.println("CLOSING5");
				closeEverything(socket, bufferedReader, bufferedWriter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessage("Server: " + clientUsername + " has left the chat!");
		
	}
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		removeClientHandler();
		try {
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

	
}
