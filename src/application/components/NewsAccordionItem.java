package application.components;

import java.io.IOException;

import application.news.Article;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;

public class NewsAccordionItem extends TitledPane {

	@FXML ImageView thumbnailImage;
	@FXML WebView abstractHtml;
	@FXML Button btnEdit;
	@FXML Button btnDelete;
	@FXML Button btnShow;

	public NewsAccordionItem(Article article, Runnable openDetailsCallback, Runnable openEditCallback) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsAccordionItem.fxml"));
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.setRoot(this);
        
        try {
        	loader.load();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    	
    	this.setText(article.getTitle());
    	this.thumbnailImage.setImage(article.getImageData());
    	this.abstractHtml.getEngine().loadContent(String.format("<style>.pseudolink{text-decoration:none;color:#444;font-family:sans-serif;}</style><a href=\"#\" class=\"pseudolink\">%s</a>", article.getAbstractText()));

    	this.abstractHtml.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> openDetailsCallback.run());
    	this.thumbnailImage.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> openDetailsCallback.run());

    	this.btnShow.setOnAction((action) -> openDetailsCallback.run());
    	
    	this.btnEdit.setOnAction((action) -> openEditCallback.run());
	}

	@FXML
	void initialize() {
		updateForLoggedOut();
	}
	
	public void updateForLoggedIn() {
		this.btnDelete.setVisible(true);
		this.btnEdit.setVisible(true);
	}
	
	public void updateForLoggedOut() {
		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(false);
	}
}
