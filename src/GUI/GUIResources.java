package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

import Client.Client;

//https://stackoverflow.com/questions/25222811/access-restriction-the-type-application-is-not-api-restriction-on-required-l

//https://stackoverflow.com/questions/56307973/cannot-access-class-com-sun-javafx-util-utils-in-module-javafx-graphics-java
public class GUIResources extends Application {
	static Scene scene;
	static Socket currentSocket;
	static Client currentUser;

	@Override
	public void start(Stage stage) throws IOException {
		System.out.println("In the start method");
		currentSocket = new Socket("localhost", 5000);
		setLoginScene(stage);
	}

	public static void launchGUI() {
		launch();
	}

	public static void setLoginScene(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GUIResources.class.getResource("login-view.fxml"));
		scene = new Scene(fxmlLoader.load(), 320, 240);
		stage.setAlwaysOnTop(true);
		stage.setTitle("Login");
		stage.setScene(scene);

		stage.show();
	}

	public static void setChatRoomScene(Stage stage) throws IOException {
		stage.hide();

		FXMLLoader fxmlLoader = new FXMLLoader(GUIResources.class.getResource("chat-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 600, 400);
		stage.setAlwaysOnTop(true);
		stage.setTitle("Chat Room:: Logged in as " + Client.getUsername());
		stage.setScene(scene);
		stage.show();
	}

}