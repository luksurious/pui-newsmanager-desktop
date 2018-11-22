package application;

import application.news.Article;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

import java.io.IOException;

/**
 *
 * The delete controller is in charge of dealing with the dialog popup
 * It delete the current article from the database and the local application
 */
public class DeleteController implements ServiceRegistryAware, ControllerEvents, NewsController {
    protected ServiceRegistry serviceRegistry;

    @FXML
    private Button confirm;

    @FXML
    private Button cancel;

    private Article article;

    public DeleteController(){}

    /**
     * This event is handled when the user press the confirmation button from the delete dialog box
     */
    @FXML
    private void confirmAction() throws IOException {
        try {
            System.out.println(this.article);
        } catch (Exception e){
            System.out.println(e.getCause());
        }

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
     * This event is handled when the user press the cancel button from the delete dialog box
     */
    @FXML
    private void cancelAction(){
        SceneManager.getInstance().closeModal(AppScenes.DELETE);
    }

    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }


    public void setArticle(Article article){
        this.article = article;
    }

    @Override
    public void onBeforeShow() {

    }
}
