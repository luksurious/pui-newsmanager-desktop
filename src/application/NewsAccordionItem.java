package application;

import java.io.IOException;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import application.news.Article;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
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

	public NewsAccordionItem(Article article, Runnable callback) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsAccordionItem.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
        	loader.load();
        	
        	this.setText(article.getTitle());
        	this.thumbnailImage.setImage(article.getImageData());
        	this.abstractHtml.getEngine().loadContent(String.format("<style>.pseudolink{text-decoration:none;color:#444;font-family:sans-serif;}</style><a href=\"#\" class=\"pseudolink\">%s</a>", article.getAbstractText()));

        	this.abstractHtml.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> callback.run());
        	this.thumbnailImage.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> callback.run());
        	
        	this.btnShow.setOnAction((action) -> callback.run());
        	
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
	}

	@FXML
	void initialize() {
		updateForLoggedOut();
	}
	
	void updateForLoggedIn() {
		this.btnDelete.setVisible(true);
		this.btnEdit.setVisible(true);
	}
	
	void updateForLoggedOut() {
		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(false);
	}
}
