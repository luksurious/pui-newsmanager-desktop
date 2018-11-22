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
 * 
 * @author students
 */
public class NewsHead extends HBox {
	/**
	 * The logo of the application
	 */
	@FXML
	private ImageView headImage;
	
	/**
	 * The label showing the main headline
	 */
	@FXML
	private Label headline;
	
	/**
	 * The label showing a short text above the headline (e.g. "These are the news...")
	 */
	@FXML
	private Label headlinePre;
	
	/**
	 * The button group to create a new article, offering a menu item to load from file
	 */
	@FXML
	private SplitMenuButton btnAdd;
	
	/**
	 * The Menu item inside the creation button group to load a news file
	 */
	@FXML
	private MenuItem btnLoadNewsFile;
	
	/**
	 * The button to open the login
	 */
	@FXML
	private JFXButton btnLogin;
	
	/**
	 * The button if the user is logged in, showing an item to logout
	 */
	@FXML
	private MenuButton btnUser;

	/**
	 * Reference to the main controller this head component belongs to
	 */
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

		// show the current date in the headline
		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");
		headline.setText(headFormat.format(new Date()));

		// remove the user for logged in users from the layout
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

	/**
	 * Update the view if a user is logged in.
	 * Removes the button to login, and shows the button with the current user with the option to logout
	 * 
	 * @param user The logged in user
	 */
	public void updateUiAfterLogin(User user) {
		btnLogin.setVisible(false);
		btnLogin.setManaged(false);

		btnUser.setText(user.getLogin());
		btnUser.setManaged(true);
		btnUser.setVisible(true);
	}

	/**
	 * Update the view if a user is logged out
	 * Shows the button to login, and hides the button with the user and logout
	 */
	public void updateUiAfterLogout() {
		btnLogin.setVisible(true);
		btnLogin.setManaged(true);

		btnUser.setText("logged out");
		btnUser.setVisible(false);
		btnUser.setManaged(false);
	}

	/**
	 * Show a different headline than the "These are the news for today". Removes the pre text in that case.
	 * 
	 * @param title The new headline of the head area
	 */
	public void setCustomTitle(String title) {
		headline.setText(title);
		headlinePre.setVisible(false);
		headlinePre.setManaged(false);
	}

	/**
	 * Proxy method for loading a news item from a file
	 */
	@FXML
	public void loadNewsFile() {
		parentController.loadNewsFile();
	}

	/**
	 * Proxy method for showing the editor for a new article
	 */
	@FXML
	public void openEditor() {
		parentController.openEditor();
	}

	/**
	 * Proxy method to logout
	 */
	@FXML
	public void logout() {
		parentController.logout();
	}

	/**
	 * Proxy method to show the login
	 */
	@FXML
	public void openLogin() {
		parentController.openLogin();
	}

	/**
	 * Proxy method to go back to the main view
	 * @throws IOException
	 */
	@FXML
	public void openMainView() throws IOException {
		parentController.openMainView();
	}
}
