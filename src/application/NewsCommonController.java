package application;

import java.io.File;
import java.io.IOException;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import application.components.NewsHead;
import application.news.Article;
import application.news.User;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

/**
 * The aim of this abstract class is the define some common methods for controllers
 *
 * @author students
 */
public abstract class NewsCommonController implements ServiceRegistryAware, ControllerEvents, NewsController {
	/**
	 * The logged in user, or null
	 */
	protected User user;

	/**
	 * Service registry holding global services
	 */
	protected ServiceRegistry serviceRegistry;

	/**
	 * The root pane for the stage
	 */
	@FXML
	protected BorderPane rootPane;

	/**
	 * The header of the main views
	 */
	@FXML
	protected NewsHead newsHead;

	/**
	 * Adding the news head to the BorderPane at the top, so all the scenes have the same headnews
	 */
	@FXML
	public void initialize() {
		assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'NewsReader.fxml'.";

		// Adding <NewsHead> into the FXML files didn't work properly with SceneBuilder
		// although the application started fine, so the head part is added from the code
		newsHead = new NewsHead(this);
		rootPane.setTop(newsHead);
	}

    /**
     * Setup the Connection Manager and the User just before displaying the screen
     */
	@Override
	public void onBeforeShow() {
		getModel().setConnectionManager(serviceRegistry.get(ConnectionManager.class));

		setUser(serviceRegistry.get(User.class));
	}

    /**
     * Define the current user if connected and update the interface depending on the login status
     *
     * @param user
     */
	public void setUser(User user) {
		this.user = user;

		if (this.user instanceof User) {
			updateUiAfterLogin();
		} else {
			updateUiAfterLogout();
		}
	}

    /**
     * Display the login modal
     */
	@FXML
	public void openLogin() {
		SceneManager sceneManager = SceneManager.getInstance();

		try {
			sceneManager.showSceneInModal(AppScenes.LOGIN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// updated the UI after logging in
		onBeforeShow();
	}

    /**
     * Logout the user by redefining the user to null
     */
	@FXML
	public void logout() {
		getModel().logout();
		serviceRegistry.set(User.class, null);
		setUser(null);
	}

	/**
     * Open the editor for the current open article
	 */
	void openEditorForExistingArticle(Article article) {
		if (!openEditor()) {
			return;
		}

		SceneManager sceneManager = SceneManager.getInstance();
		NewsEditController controller = (NewsEditController) sceneManager.getController(AppScenes.EDITOR);
		controller.setArticle(article);
	}

    /**
     * Display the delete article modal
     *
     * @param article
     */
	void openDeleteDialog(Article article) {
		SceneManager sceneManager = SceneManager.getInstance();
		try {
            sceneManager.showSceneInModal(AppScenes.DELETE, false);
            DeleteController controller = (DeleteController) sceneManager.getController(AppScenes.DELETE);
            controller.setArticle(article);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

    /**
     * Load news data from JSON to the interface
     *
     * Open the select file popup
     * Reload the editor with the article values
     */
	@FXML
	public void loadNewsFile() {
		Stage stage = SceneManager.getInstance().getPrimaryStage();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open JSON Article");
		File file = fileChooser.showOpenDialog(stage);

		Article article = getModel().createArticleFromFile(file);
		if (article == null) {
			return;
		}

		openEditorForExistingArticle(article);
	}

    /**
     * Display the editor in order to create a new article
     *
     * @return
     */
	@FXML
	public boolean openEditor() {
		SceneManager sceneManager = SceneManager.getInstance();

		try { //shows the scene of editing as primary stage
			sceneManager.showSceneInPrimaryStage(AppScenes.EDITOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Setter for Service Registry
	 */
	@Override
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

    /**
     * Come back to the home screen
     *
     * @throws IOException
     */
	public void openMainView() throws IOException {
		SceneManager.getInstance().showSceneInPrimaryStage(AppScenes.READER, false);
	}

	/**
	 * Subclasses must implement this and retrieve its model which must be an instance of {@link NewsCommonModel}
	 * @return The model of the controller
	 */
	protected abstract NewsCommonModel getModel();

    /**
     * Redefine the UI after login
     * Allowing more features
     */
	protected void updateUiAfterLogin() {
		newsHead.updateUiAfterLogin(user);
	}

    /**
     * Redefine the UI after logout
     * Allowing fewer features
     */
	protected void updateUiAfterLogout() {
		newsHead.updateUiAfterLogout();
	}

    /**
     * Quick displaying of the dialog box
     * @param text
     */
	protected void showDialog(String text) {
		showDialog(new Label(text));
	}

    /**
     * Display error message
     * @param text
     */
	protected void showErrorDialog(String text) {
		ImageView errorImage = new ImageView(getClass().getResource("/error.png").toExternalForm());
		errorImage.setFitWidth(32);
		errorImage.setFitHeight(32);

		showDialog(new Label(text, errorImage));
	}

    /**
     * Display the dialog box
     * @param body
     */
	protected void showDialog(Node body) {
		JFXDialogLayout layout = new JFXDialogLayout();
		JFXButton okayButton = new JFXButton("OK");
		layout.setBody(body);
		layout.getActions().add(okayButton);

		JFXAlert<Void> alert = new JFXAlert<Void>((Stage) rootPane.getScene().getWindow());

		okayButton.setOnAction((event) -> alert.hide());

		alert.setOverlayClose(true);
		alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
		alert.setContent(layout);
		alert.initModality(Modality.NONE);
		alert.showAndWait();
	}
}
