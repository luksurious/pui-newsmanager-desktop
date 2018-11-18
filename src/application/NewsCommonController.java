package application;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.components.NewsHead;
import application.news.Article;
import application.news.User;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;

public abstract class NewsCommonController implements ServiceRegistryAware, ControllerEvents {
	protected User user;
	
	private ServiceRegistry serviceRegistry;

	@FXML
	BorderPane rootPane;
	
	@FXML
	NewsHead newsHead;

	@FXML
	void initialize() {
		newsHead = new NewsHead();
		rootPane.setTop(newsHead);
		
        newsHead.setParentController(this);
	}
	
	protected abstract NewsCommonModel getModel();
	
	@Override
	public void onShow() {
		getModel().setConnectionManager((ConnectionManager) serviceRegistry.get("connection"));
		
		if (serviceRegistry.has("user")) {
			setUser((User) serviceRegistry.get("user"));
		} else {			
			setUser(null);
		}
	}
	
	void setUser(User user) {
		this.user = user;
		
		if (this.user instanceof User) {
			updateUiAfterLogin();
		} else {
			updateUiAfterLogout();
		}
	}

	User getUser() {
		return user;
	}

	protected void updateUiAfterLogin() {
		newsHead.updateUiAfterLogin(user);
	}

	protected void updateUiAfterLogout() {
		newsHead.updateUiAfterLogout();
	}

	void setConnectionManager(ConnectionManager connection) {
		getModel().setConnectionManager(connection);
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
		
		onShow();
	}
	
	@FXML
	public void logout() {
		getModel().logout();
		serviceRegistry.set("user", null);
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

	public void openMainView() {
		SceneManager sceneManager = SceneManager.getInstance();
		
		try {
			sceneManager.showSceneInPrimaryStage(AppScenes.READER, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
