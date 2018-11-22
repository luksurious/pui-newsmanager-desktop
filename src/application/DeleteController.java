package application;

import application.news.Article;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;


public class DeleteController implements ServiceRegistryAware, ControllerEvents, NewsController {
    protected ServiceRegistry serviceRegistry;

    @FXML
    private Button confirm;

    @FXML
    private Button cancel;

    private Article article;

    public DeleteController(){}

    @FXML
    private void confirmAction(){
        try {
            System.out.println(this.article);
        } catch (Exception e){
            System.out.println(e.getCause());
        }

        ConnectionManager connectionManager = serviceRegistry.get(ConnectionManager.class);

        try {
            // this command will send the article to the server
            connectionManager.deleteArticle(472);
        } catch (ServerCommunicationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        SceneManager.getInstance().closeModal(AppScenes.DELETE);
    }

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