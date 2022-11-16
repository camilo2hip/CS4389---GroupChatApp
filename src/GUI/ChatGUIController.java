package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import Client.Client;

public class ChatGUIController 
{
	@FXML private Label userMenuLabel;
	@FXML private TextArea messageTextDisplayArea;
	@FXML private TextField messageTextField;
	@FXML private Button messageSendButton;
	public static String username = "DefaultName";

    @FXML
    public void onSendButtonClick() throws IOException 
    {
        String message = messageTextField.getText();
        messageTextField.clear();
        messageTextDisplayArea.appendText(Client.getUsername() + ": " + message + "\n");
    }

    @FXML
    public void updateUserMenu() {

    }

    @FXML
    public void updateMessageScreen(String origin) 
    {
    	
    }


}
