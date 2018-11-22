package application.components;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import application.news.Article;
import application.news.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.scene.Cursor;
import javafx.scene.control.TitledPane;

/**
 * This is the controller / manager for a single article item inside the accordion view.
 * It is created as a separate FXML template so that it can more easily be generated for each article.
 * This controller sets up the basic elements of the view with the data of the article and adds event handlers.
 */
public class NewsAccordionItem extends TitledPane {

	@FXML
	private ImageView thumbnailImage;
	@FXML
	private WebView abstractHtml;
	@FXML
	private JFXButton btnEdit;
	@FXML
	private JFXButton btnDelete;
	@FXML
	private JFXButton btnShow;
	@FXML
	private HBox articlePreviewContainer;

	private Article article;
	private Runnable openDetailsCallback;
	private Runnable openEditCallback;
	private Runnable openDeleteCallback;

	public NewsAccordionItem(Article article, Runnable openDetailsCallback, Runnable openEditCallback, Runnable openDeleteCallback) {
		this.article = article;
		this.openDetailsCallback = openDetailsCallback;
		this.openEditCallback = openEditCallback;
		this.openDeleteCallback = openDeleteCallback;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsAccordionItem.fxml"));
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.setRoot(this);
        
        try {
        	loader.load();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
	}

	@FXML
	public void initialize() {
        assert articlePreviewContainer != null : "fx:id=\"articlePreviewContainer\" was not injected: check your FXML file 'NewsAccordionItem.fxml'.";
        assert thumbnailImage != null : "fx:id=\"thumbnailImage\" was not injected: check your FXML file 'NewsAccordionItem.fxml'.";
        assert abstractHtml != null : "fx:id=\"abstractHtml\" was not injected: check your FXML file 'NewsAccordionItem.fxml'.";
        assert btnEdit != null : "fx:id=\"btnEdit\" was not injected: check your FXML file 'NewsAccordionItem.fxml'.";
        assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'NewsAccordionItem.fxml'.";
        assert btnShow != null : "fx:id=\"btnShow\" was not injected: check your FXML file 'NewsAccordionItem.fxml'.";

		this.setText(article.getTitle());
		if (article.getImageData() == null) {
			this.thumbnailImage.setImage(new Image(getClass().getResourceAsStream("/noImage.jpg")));
		} else {
			this.thumbnailImage.setImage(article.getImageData());
		}
		
		String layout = "<div style=\"width:100%%;height:100%%;cursor:pointer;\">%s</div>";
		this.abstractHtml.getEngine().loadContent(String.format(layout, article.getAbstractText()));

		this.articlePreviewContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> openDetailsCallback.run());
		this.articlePreviewContainer.setCursor(Cursor.HAND);

		this.btnShow.setOnAction((action) -> openDetailsCallback.run());

		this.btnEdit.setOnAction((action) -> openEditCallback.run());
		
    	this.btnDelete.setOnAction((action) -> openDeleteCallback.run());
		
		updateForLoggedOut();
	}

	public void updateForLoggedIn(User user) {
		this.btnDelete.setVisible(true);
		this.btnEdit.setVisible(true);

		if (article.getIdUser() != user.getIdUser()) {
			this.btnDelete.setDisable(true);
			this.btnEdit.setDisable(true);
		}

	}

	public void updateForLoggedOut() {
		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(false);
	}
}
