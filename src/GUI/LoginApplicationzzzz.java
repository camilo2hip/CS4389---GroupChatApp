package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;



public class LoginApplicationzzzz extends Application
{
	@Override
	public void start(Stage stage) throws Exception {
//	    primaryStage.setTitle("Hello world Application");
//	    primaryStage.setWidth(300);
//	    primaryStage.setHeight(200);
//	    
//	    VBox vbox = new VBox();
//	    
//	    
//	    Label helloWorldLabel = new Label("Hello world!");
//	    helloWorldLabel.setAlignment(Pos.CENTER);
//	    Scene primaryScene = new Scene(helloWorldLabel);
//	    primaryStage.setScene(primaryScene);
//	    primaryStage.show();
		
		FXMLLoader fxmlLoader = new FXMLLoader(GUIResources.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
		
		
	}


    public static void main(String[] args) {
        launch();
        
    }
}