package application;

import application.news.User;

import serverConection.ConnectionManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class LoginController {
//TODO Add all attribute and methods as needed 
	private LoginModel loginModel = new LoginModel();

	private User loggedUsr = null;

	@FXML PasswordField passwordField;

	@FXML TextField usernameField;

	@FXML Label formErrorNote;

	private boolean usernameTouched = false;
	private boolean passwordTouched = false;

	@FXML Label loginErrorNote;

	public LoginController() {
	}


    @FXML
    void initialize() {
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Login.fxml'.";
        assert usernameField != null : "fx:id=\"usernameField\" was not injected: check your FXML file 'Login.fxml'.";
        assert formErrorNote != null : "fx:id=\"formErrorNote\" was not injected: check your FXML file 'Login.fxml'.";

		this.usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
			usernameTouched = true;
			validateForm();
		});
		this.passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
			passwordTouched = true;
			validateForm();
		});
    }

	User getLoggedUsr() {
		return loggedUsr;

	}

	void setConnectionManager(ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}

	@FXML
	public void submitLogin() {
		loginErrorNote.setVisible(false);
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		if (username.equals("") || password.equals("")) {
			formErrorNote.setVisible(true);
			return;
		}
		
		loggedUsr = this.loginModel.login(username, password);
		if (loggedUsr != null) {
			closeModal();
		} else {
			loginErrorNote.setVisible(true);
		}
	}

	@FXML
	public void closeModal() {
		usernameField.getScene().getWindow().hide();
	}


	private void validateForm() {
		loginErrorNote.setVisible(false);
		
		String username = usernameField.getText();
		String password = passwordField.getText();
		if ((username.equals("") && usernameTouched) || (password.equals("") && passwordTouched)) {
			formErrorNote.setVisible(true);
			return;
		}
		
		formErrorNote.setVisible(false);
	}


	@FXML
	public void submitIfEnter() {
		submitLogin();
	}
}