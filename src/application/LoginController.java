package application;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import application.news.User;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import serverConection.ConnectionManager;

public class LoginController implements ServiceRegistryAware, ControllerEvents, NewsController {
	private LoginModel loginModel = new LoginModel();

	private ServiceRegistry serviceRegistry;

	private boolean usernameTouched = false;
	private boolean passwordTouched = false;

	@FXML
	private JFXPasswordField passwordField;

	@FXML
	private JFXTextField usernameField;

	@FXML
	private Label formErrorNote;

	@FXML
	private Label loginErrorNote;

	@FXML
	void initialize() {
        assert usernameField != null : "fx:id=\"usernameField\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Login.fxml'.";
        assert formErrorNote != null : "fx:id=\"formErrorNote\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginErrorNote != null : "fx:id=\"loginErrorNote\" was not injected: check your FXML file 'Login.fxml'.";

		this.usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
			usernameTouched = true;
			validateForm();
		});
		this.passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
			passwordTouched = true;
			validateForm();
		});
	}

	@Override
	public void onBeforeShow() {
		loginModel.setConnectionManager(serviceRegistry.get(ConnectionManager.class));
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

		User loggedInUser = this.loginModel.login(username, password);
		if (loggedInUser != null) {
			serviceRegistry.set(User.class, loggedInUser);

			closeModal();
		} else {
			loginErrorNote.setVisible(true);
		}
	}

	@FXML
	public void closeModal() {
		SceneManager.getInstance().closeModal(AppScenes.LOGIN);
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

	@Override
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}