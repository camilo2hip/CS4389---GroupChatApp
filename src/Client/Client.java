package Client;

import AES.AES;
import RSA.RSAUtil;
import AES.AESUtil;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;



public class Client {

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientName;
	
	
	static Connection connection = null;
	static String databaseName = "login";
	static String url = "jdbc:mysql://localhost:3306/" + databaseName;
	
	static String username = "root";
	static String password = "Friday123!";
	
	
	public Client(Socket socket, String clientName) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientName = clientName;
		} catch(IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void sendMessage() {
		try {
			bufferedWriter.write(clientName);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			Scanner scanner = new Scanner(System.in);
			while(socket.isConnected()) {
				String messageToSend = scanner.nextLine();
				bufferedWriter.write(clientName + ": " + messageToSend);
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

						String clientUsername = parts[0];
						String encryptedAESKey = parts[1];
						String encryptedMessage = parts[2];

						String path = clientUsername + "/privateKey";
						byte[] privateKeyFromFile = readFileAsString(path);

						//encode the privateKey gotten from the file with base64
						String privateKey = Base64.getEncoder().encodeToString(privateKeyFromFile);

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

	public static byte[] readFileAsString(String fileName)throws Exception
	{
		return Files.readAllBytes(Paths.get(fileName));

	}
	
	
	public static boolean validatePsswd(Connection connection, String clientName, String psswd) {
		String query = "SELECT * FROM login.users WHERE idusers=? and psswd=?";
		PreparedStatement stmt = null;
		boolean success = false;
		try {
			stmt = connection.prepareStatement(query);
			stmt.setString(1, clientName);
			stmt.setString(2, psswd);
			ResultSet rs = stmt.executeQuery();
			if(rs.next())
				success = true;
			rs.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				stmt.close();
				
			}catch (Exception e) {
				
			}
		}
		
		return success;
	}
	
	private static String encrypt(String psswd) throws NoSuchAlgorithmException {
		String salt = getSalt();
		String securePassword = get_SHA_256_SecurePassword(psswd, salt);
        //System.out.println(securePassword);
        return securePassword;
	}
	
	private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }
	
	private static String get_SHA_256_SecurePassword(String passwordToHash,
            String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

	
	public static void main(String[] args) throws UnknownHostException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException  {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		try (Connection connection = DriverManager.getConnection(url, username, password);) {
		 	System.out.println("Database Connected!");
		 	
		 	Scanner scanner = new Scanner(System.in);
		 	String clientName;
		 	String psswd;
		 	String encPsswd;
		 	
		 	while(true) {
				System.out.println("Enter your usernmae for the group chat: ");
				clientName = scanner.nextLine();
				System.out.println("Enter Password");
				psswd = scanner.nextLine();
				try {
					encPsswd = encrypt(psswd);
					if(validatePsswd(connection, clientName, encPsswd)) {
						System.out.println("Logging in...");
						break;
					}
					else {
						System.out.println("Username or Password incorrect, retry");
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				
				
		 	}
			
		 	Socket socket = new Socket("localhost", 1234);
			Client client = new Client(socket, clientName);
			System.out.println("You are connected!");
			client.listenForMessage();
			client.sendMessage();
	       }
	       // Handle any errors that may have occurred.
	       catch (SQLException e) {
	           e.printStackTrace();
	       }
		 
		
		
		
	
	}
}
