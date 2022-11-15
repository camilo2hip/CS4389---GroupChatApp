package Client;

import AES.AES;
import RSA.RSAUtil;
import javafx.stage.Stage;
import AES.AESUtil;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private static String clientName;
	
	
	static Connection connection = null;
	static String databaseName = "login";
	static String url = "jdbc:mysql://localhost:3306/" + databaseName;
	
	static String dbUsername = "root";
	static String dbPassword = "Friday123!";
	
	
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

	public static boolean validationTest(String user, String password) {
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

	static boolean loggedIn = false;
	//static String clientName = "";
	public static void main(String[] args) throws UnknownHostException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException  {
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);) {
		 	System.out.println("Database Connected!");
		 	GUIResources.launchGUI();
		 	
		 	Scanner scanner = new Scanner(System.in);
		 	
		 	while(!loggedIn) {
		 		
//				System.out.println("Enter your usernmae for the group chat: ");
//				clientName = scanner.nextLine();
//				System.out.println("Enter Password");
//				String psswd = scanner.nextLine();
//				if(validatePsswd(connection, clientName, psswd)) {
//					System.out.println("Logging in...");
//					break;
//				}
//				
//				else {
//					System.out.println("Username or Password incorrect, retry");
//				}
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

