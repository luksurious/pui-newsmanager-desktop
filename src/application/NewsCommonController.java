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
 * This class is to gather together all the functions and methods that are common to 
 * several controllers, so that they can be override instead of being implemented
 * multiple times
 */
public abstract class NewsCommonController implements ServiceRegistryAware, ControllerEvents, NewsController {
	protected User user;

	protected ServiceRegistry serviceRegistry;

	@FXML
	protected BorderPane rootPane;

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
	 * Sets the connection manager and the user using serviceRegistry
	 */
	@Override
	public void onBeforeShow() {
		getModel().setConnectionManager(serviceRegistry.get(ConnectionManager.class));

		setUser(serviceRegistry.get(User.class));
	}

	/**
	 * Sets the user depending on if it is logged in or not
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
	 * Opens the modal window for the user to log in and then calls onBeforeShow()
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

		onBeforeShow();
	}

	/**
	 * Opens the model to log out and then sets the user to null
	 */
	@FXML
	public void logout() {
		getModel().logout();
		serviceRegistry.set(User.class, null);
		setUser(null);
	}

	/**
	 * Opens the editor for editing news and sets the article to it
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
	 * Opens the modal window for the user to log in and then calls onBeforeShow()
	 */
	void openDeleteDialog(Article article){
		SceneManager sceneManager = SceneManager.getInstance();
		try {
            sceneManager.showSceneInModal(AppScenes.DELETE, false);
            DeleteController controller = (DeleteController) sceneManager.getController(AppScenes.DELETE);
            controller.setArticle(article);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Opens a modal for loading a JSON file and then creates the article based on the JSON file
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
	 * Opens the scene for editing an article
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
	 * Opens the reader of news as main scene
	 */
	public void openMainView() throws IOException {
		SceneManager.getInstance().showSceneInPrimaryStage(AppScenes.READER, false);
	}

	protected abstract NewsCommonModel getModel();

	protected void updateUiAfterLogin() {
		newsHead.updateUiAfterLogin(user);
	}

	protected void updateUiAfterLogout() {
		newsHead.updateUiAfterLogout();
	}

	protected void showDialog(String text) {
		showDialog(new Label(text));
	}

	/**
	 * If there is an error, it will display this image and call the function to show a dialog with the error
	 */
	protected void showErrorDialog(String text) {
		ImageView errorImage = new ImageView(getClass().getResource("/error.png").toExternalForm());
		errorImage.setFitWidth(32);
		errorImage.setFitHeight(32);

		showDialog(new Label(text, errorImage));
	}
	/**
	 * It will show an informative dialog (modal window)
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
