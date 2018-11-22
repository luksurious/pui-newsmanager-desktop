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
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

public abstract class NewsCommonController implements ServiceRegistryAware, ControllerEvents, NewsController {
	protected User user;

	protected ServiceRegistry serviceRegistry;

	@FXML
	protected BorderPane rootPane;

	@FXML
	protected NewsHead newsHead;

	@FXML
	public void initialize() {
		assert rootPane != null : "fx:id=\"rootPane\" was not injected: check your FXML file 'NewsReader.fxml'.";
        
		// Adding <NewsHead> into the FXML files didn't work properly with SceneBuilder
		// although the application started fine, so the head part is added from the code
		newsHead = new NewsHead(this);
		rootPane.setTop(newsHead);
	}

	@Override
	public void onBeforeShow() {
		getModel().setConnectionManager(serviceRegistry.get(ConnectionManager.class));

		setUser(serviceRegistry.get(User.class));
	}

	public void setUser(User user) {
		this.user = user;

		if (this.user instanceof User) {
			updateUiAfterLogin();
		} else {
			updateUiAfterLogout();
		}
	}

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

	@FXML
	public void logout() {
		getModel().logout();
		serviceRegistry.set(User.class, null);
		setUser(null);
	}

	void openEditorForExistingArticle(Article article) {
		if (!openEditor()) {
			return;
		}

		SceneManager sceneManager = SceneManager.getInstance();
		NewsEditController controller = (NewsEditController) sceneManager.getController(AppScenes.EDITOR);
		controller.setArticle(article);
	}

	void openDeleteDialog(Article article){
		SceneManager sceneManager = SceneManager.getInstance();
		try {
            sceneManager.showSceneInModal(AppScenes.DELETE, null, false);
            DeleteController controller = (DeleteController) sceneManager.getController(AppScenes.DELETE);
            controller.setArticle(article);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	@FXML
	public void loadNewsFile() {
		Stage stage = SceneManager.getInstance().getCurrentStage();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open JSON Article");
		File file = fileChooser.showOpenDialog(stage);

		Article article = getModel().createArticleFromFile(file);
		if (article == null) {
			return;
		}

		openEditorForExistingArticle(article);
	}

	@FXML
	public boolean openEditor() {
		SceneManager sceneManager = SceneManager.getInstance();

		try {
			sceneManager.showSceneInPrimaryStage(AppScenes.EDITOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}


	@Override
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

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

	protected void showErrorDialog(String text) {
		ImageView errorImage = new ImageView(getClass().getResource("/error.png").toExternalForm());
		errorImage.setFitWidth(32);
		errorImage.setFitHeight(32);

		showDialog(new Label(text, errorImage));
	}

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
