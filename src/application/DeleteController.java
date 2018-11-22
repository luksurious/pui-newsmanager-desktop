package application;

import application.news.Article;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.fxml.FXML;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

/**
 * The delete controller is in charge of dealing with the dialog popup It delete
 * the current article from the database and the local application
 * 
 * @author students
 */
public class DeleteController implements ServiceRegistryAware, NewsController {
	/**
	 * The service registry
	 */
	protected ServiceRegistry serviceRegistry;

	/**
	 * Button to confirm the deletion
	 */
	@FXML
	private JFXButton confirm;

	/**
	 * Button to cancel the deletion
	 */
	@FXML
	private JFXButton cancel;

	/**
	 * The article to delete
	 */
	private Article article;

	/**
	 * This event is handled when the user press the confirmation button from the
	 * delete dialog box
	 */
	@FXML
	private void confirmAction() throws IOException {
		ConnectionManager connectionManager = serviceRegistry.get(ConnectionManager.class);

		try {
			connectionManager.deleteArticle(article.getIdArticle());
		} catch (ServerCommunicationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		SceneManager.getInstance().closeModal(AppScenes.DELETE);
		SceneManager.getInstance().showSceneInPrimaryStage(AppScenes.READER, true);
	}

	/**
	 * This event is handled when the user press the cancel button from the delete
	 * dialog box
	 */
	@FXML
	private void cancelAction() {
		SceneManager.getInstance().closeModal(AppScenes.DELETE);
	}

	@Override
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setArticle(Article article) {
		this.article = article;
	}
}
