package application.components;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfoenix.controls.JFXButton;

import application.NewsCommonController;
import application.news.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * This is the controller / manager for the shared head element of the main
 * scenes. To prevent repetition it is extracted as a separate template with
 * controller. Unfortunately, SceneBuilder does not easily work with it when
 * used in another template.
 */
public class NewsHead extends HBox {
	@FXML
	private Label headline;
	@FXML
	private Label headlinePre;
	@FXML
	private SplitMenuButton btnAdd;
	@FXML
	private JFXButton btnLogin;
	@FXML
	private ImageView headImage;
	@FXML
	private MenuItem btnLoadNewsFile;
	@FXML
	private MenuButton btnUser;

	private NewsCommonController parentController;

	public NewsHead(NewsCommonController parentController) {
		this.parentController = parentController;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsHead.fxml"));
		loader.setController(this);
		loader.setClassLoader(getClass().getClassLoader());
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}

	@FXML
	public void initialize() {
		assertControls();

		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");
		headline.setText(headFormat.format(new Date()));

		btnUser.setManaged(false);
	}

	protected void assertControls() {
		assert headImage != null : "fx:id=\"headImage\" was not injected: check your FXML file 'NewsHead.fxml'.";
		assert headlinePre != null : "fx:id=\"headlinePre\" was not injected: check your FXML file 'NewsHead.fxml'.";
		assert headline != null : "fx:id=\"headline\" was not injected: check your FXML file 'NewsHead.fxml'.";
		assert btnAdd != null : "fx:id=\"btnAdd\" was not injected: check your FXML file 'NewsHead.fxml'.";
		assert btnLoadNewsFile != null : "fx:id=\"btnLoadNewsFile\" was not injected: check your FXML file 'NewsHead.fxml'.";
		assert btnUser != null : "fx:id=\"btnUser\" was not injected: check your FXML file 'NewsHead.fxml'.";
		assert btnLogin != null : "fx:id=\"btnLogin\" was not injected: check your FXML file 'NewsHead.fxml'.";

		assert parentController != null : "You must set a parent controller for this element to work!";
	}

	public void updateUiAfterLogin(User user) {
		btnLogin.setVisible(false);
		btnLogin.setManaged(false);

		btnUser.setText(user.getLogin());
		btnUser.setManaged(true);
		btnUser.setVisible(true);
	}

	public void updateUiAfterLogout() {
		btnLogin.setVisible(true);
		btnLogin.setManaged(true);

		btnUser.setText("logged out");
		btnUser.setVisible(false);
		btnUser.setManaged(false);
	}

	public void setCustomTitle(String title) {
		headline.setText(title);
		headlinePre.setVisible(false);
		headlinePre.setManaged(false);
	}

	@FXML
	public void loadNewsFile() {
		parentController.loadNewsFile();
	}

	@FXML
	public void openEditor() {
		parentController.openEditor();
	}

	@FXML
	public void logout() {
		parentController.logout();
	}

	@FXML
	public void openLogin() {
		parentController.openLogin();
	}

	@FXML
	public void openMainView() throws IOException {
		parentController.openMainView();
	}
}
