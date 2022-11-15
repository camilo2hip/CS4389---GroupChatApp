package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Client.Client;

public class LoginController {
    @FXML private Label welcomeText;
    @FXML private TextField passwordTextField;
    @FXML private TextField usernameTextField;
    
    @FXML
    protected void onLoginButtonClick()
    {
        String password = passwordTextField.getText();
        String username = usernameTextField.getText();

        if(Client.validationTest(username, password)) {
			System.out.println("Logging in...");
			welcomeText.setText("Logging in...");
		}
        else {
        	welcomeText.setText("Incorrect username and/or password");
        }
    }
}