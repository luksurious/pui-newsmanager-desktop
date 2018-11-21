package application;

import application.news.Article;
import application.services.ServiceRegistry;
import application.services.ServiceRegistryAware;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class DeleteController implements ServiceRegistryAware, ControllerEvents {
    protected ServiceRegistry serviceRegistry;

    @FXML
    private Button confirm;

    @FXML
    private Button cancel;

    private Article article;

    public DeleteController(){}

    @FXML
    private void confirmAction(){
        System.out.println(this.article.getTitle());
    }

    @FXML
    private void cancelAction(){}

    @Override
    public void onShow() {

    }

    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }


    public void setArticle(Article article){
        this.article = article;
    }
}