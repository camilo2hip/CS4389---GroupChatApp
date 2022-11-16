package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import Client.Client;

import GUI.GUIResources;



public class LoginController {
    @FXML private Label welcomeText;
    @FXML private TextField passwordTextField;
    @FXML private TextField usernameTextField;
    @FXML private Button loginButton;
    
    @FXML
    public void onLoginButtonClick() throws IOException
    {
        String password = passwordTextField.getText();
        String username = usernameTextField.getText();
        
        if(Client.validationTest(username, password)) {
			System.out.println("Logging in...");
			welcomeText.setText("Logging in...");
			Stage stage = (Stage) loginButton.getScene().getWindow();
        	stage.close();
        	GUIResources.setChatRoomScene(stage);
			
		}
        else {
        	welcomeText.setText("Incorrect username and/or password");
//        	Stage stage = (Stage) loginButton.getScene().getWindow();
//        	stage.close();
//        	GUIResources.setChatRoomScene(stage);
        }
    }
}