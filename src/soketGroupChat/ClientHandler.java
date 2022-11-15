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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
			this.clientUsername = bufferedReader.readLine();


			//Generate the user's public and private key and save it in an encoded file
			this.keyPairGenerator = new RSAKeyPairGenerator();
			String publicPath = clientUsername + "/publicKey";
			String privatePath = clientUsername + "/privateKey";
			keyPairGenerator.writeToFile(publicPath, keyPairGenerator.getPublicKey().getEncoded());
			keyPairGenerator.writeToFile(privatePath, keyPairGenerator.getPrivateKey().getEncoded());
			clientHandlers.add(this);
			map.put(clientUsername, keyPairGenerator);

			broadcastMessage("SERVER: " + clientUsername + " entered the chat!");

		} catch(IOException | NoSuchAlgorithmException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	@Override
	public void run() {

		String messageFromClient;
		
		while(socket.isConnected()) {
			try {
				messageFromClient = bufferedReader.readLine();

				broadcastMessage(messageFromClient);
			} catch(IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}
	
	
	public void broadcastMessage(String messageToSend) {
		String publicKey;
		String privateKey;
		for(ClientHandler clientHandler: clientHandlers) {
			try {
				if(!clientHandler.clientUsername.equals(clientUsername)) {

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

					//encrypt the message to be sent using AES
					String encryptedMessage = aes.encrypt(messageToSend,aesKey);

					//convert AES key to string
					String encodedAesKey = AESUtil.convertSecretKeyToString(aesKey);

					//encrypt the encoded AES key using RSA
					byte[] encryptedAesKeyAsByte = RSAUtil.encrypt(encodedAesKey, publicKey);

					String encryptedAesKeyAsString = Base64.getEncoder().encodeToString(encryptedAesKeyAsByte);


					String valueToSend = privateKey + "--" + encryptedAesKeyAsString + "--" + encryptedMessage;
					clientHandler.bufferedWriter.write(valueToSend);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
			} catch(IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void removeClientHandler() {
		clientHandlers.remove(this);
		broadcastMessage("Server " + clientUsername + " has left the chat!");
		
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
