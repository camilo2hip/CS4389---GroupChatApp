package Client;

import AES.AES;
import RSA.RSAUtil;
import javafx.stage.Stage;
import soketGroupChat.ClientRunnable;
import AES.AESUtil;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;
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

//GUI
import javafx.application.Application;
import GUI.GUIResources;
import GUI.LoginController;
import GUI.ChatGUIController;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Client{

	public Socket socket;
	public BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	private static String clientName;
	
	
	static Connection connection = null;
	static String databaseName = "login";
	static String url = "jdbc:mysql://localhost:3306/" + databaseName;
	
	static String dbUsername = "root";
	static String dbPassword = "admin";//"Friday123!";
	
	public Queue<String> messageQueue;
	
	
	public Client(Socket socket, String clientName) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientName = clientName;
			System.out.println("Testing 1");
			messageQueue = new LinkedList<String>();
			System.out.println("Testing 2");
		} catch(IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}		
		System.out.println("Testing 3");
		listenForMessage();
	}
	
	public static String getUsername() {
		return clientName;
	}

	
	public void sendMessage(String message) {
		try {
//			bufferedWriter.write(clientName);
//			bufferedWriter.newLine();
//			bufferedWriter.flush();
			bufferedWriter.write(clientName + ": " + message);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			
			
//			Scanner scanner = new Scanner(System.in);
//			while(socket.isConnected()) {
//				String messageToSend = scanner.nextLine();
//				bufferedWriter.write(clientName + ": " + messageToSend);
//				bufferedWriter.newLine();
//				bufferedWriter.flush();
//			}
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void listenForMessage() {
//		Thread thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				String msgFromGroupChat;
//
//				while(socket.isConnected()) {
//					try {
//						msgFromGroupChat = bufferedReader.readLine();
//
//						String[] parts = msgFromGroupChat.split("--");
//
//						String privateKey = parts[0];
//						String encryptedAESKey = parts[1];
//						String encryptedMessage = parts[2];
//
//						//Decrypt the AES key encrypted with the client's public key using the client's private key
//						String decryptedAesKey = RSAUtil.decrypt(encryptedAESKey, privateKey);
//
//						//get the AES key
//						SecretKey aesKey = AESUtil.convertStringToSecretKeyto(decryptedAesKey);
//
//						//Decrypt the encrypted message using the decrypted AES key
//						String decryptedMessage = AES.decrypt(encryptedMessage, aesKey);
//
//
//						//String decryptedMessage = RSAUtil.decrypt(encryptedMessage, privateKey);
//
//						//display the message
//						messageQueue.add(decryptedMessage);
//						System.out.println(decryptedMessage);
//
//					} catch (IOException e) {
//						closeEverything(socket, bufferedReader, bufferedWriter);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
		Thread thread = new Thread(new ClientRunnable(this));
		thread.setDaemon(true);
		thread.start();
	}
	
	public void closeEverything() {
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
	

	public static boolean validateUser(String user, String password) {
		Connection connection;
		try {
			connection = DriverManager.getConnection(url, dbUsername, dbPassword);
			String query = "SELECT * FROM login.users WHERE idusers=? and psswd=?";
			PreparedStatement stmt = null;
			boolean success = false;
			try {
				stmt = connection.prepareStatement(query);
				stmt.setString(1, user);
				stmt.setString(2, password);
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
			
			Client.loggedIn = success;
			if(success) {
				Client.clientName = user;
			}
			else {
				Client.clientName = "";
			}
			
			return success;
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error connecting to database.");
			Client.clientName = "";
			Client.loggedIn = false;
			return false;
		}
		
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
	
	static boolean loggedIn = false;
	//static String clientName = "";

	public static void main(String[] args) throws UnknownHostException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException  {
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);) {
		 	System.out.println("Database Connected!");
		 	
		 	GUIResources.launchGUI();
	       }
	       // Handle any errors that may have occurred.
	       catch (SQLException e) {
	           e.printStackTrace();
	       }
	}
}



