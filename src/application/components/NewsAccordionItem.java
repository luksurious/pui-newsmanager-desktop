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
 * This is the controller / manager for a single article item inside the
 * accordion view. It is created as a separate FXML template so that it can more
 * easily be generated for each article. This controller sets up the basic
 * elements of the view with the data of the article and adds event handlers.
 * 
 * @author students
 */
public class NewsAccordionItem extends TitledPane {

	/**
	 * Control for showing the thumbnail preview of the article image
	 */
	@FXML
	private ImageView thumbnailImage;

	/**
	 * The WebView component showing the abstract text, as an HTML
	 */
	@FXML
	private WebView abstractHtml;

	/**
	 * The button to edit the article
	 */
	@FXML
	private JFXButton btnEdit;

	/**
	 * The button to delete the article
	 */
	@FXML
	private JFXButton btnDelete;

	/**
	 * The button to show the details of the article
	 */
	@FXML
	private JFXButton btnShow;

	/**
	 * The container for the image and the abstract
	 */
	@FXML
	private HBox articlePreviewContainer;

	/**
	 * The current active article in this accordion item
	 */
	private Article article;

	/**
	 * The callback to the main controller for opening the details view
	 */
	private Runnable openDetailsCallback;

	/**
	 * The callback to the main controller for opening the edit view
	 */
	private Runnable openEditCallback;

	/**
	 * The callback to the main controller for showing the delete modal
	 */
	private Runnable openDeleteCallback;

	public NewsAccordionItem(Article article, Runnable openDetailsCallback, Runnable openEditCallback,
			Runnable openDeleteCallback) {
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

		// set the content of the elements from the given article
		this.setText(article.getTitle());
		if (article.getImageData() == null) {
			// show a default placeholder image if no image is given
			this.thumbnailImage.setImage(new Image(getClass().getResourceAsStream("/noImage.jpg")));
		} else {
			this.thumbnailImage.setImage(article.getImageData());
		}

		// the webview does not show a cursor for the whole control as defined in the
		// FXML
		// so we add a dummy container where we change the pointer to have it consistent
		// with the whole HBox container
		String layout = "<div style=\"width:100%%;height:100%%;cursor:pointer;\">%s</div>";
		this.abstractHtml.getEngine().loadContent(String.format(layout, article.getAbstractText()));

		this.articlePreviewContainer.setCursor(Cursor.HAND);

		// add actions to the elements, referencing the callbacks
		this.articlePreviewContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> openDetailsCallback.run());

		this.btnShow.setOnAction((action) -> openDetailsCallback.run());
		this.btnEdit.setOnAction((action) -> openEditCallback.run());
		this.btnDelete.setOnAction((action) -> openDeleteCallback.run());

		// by default we initialize the component for a logged out user
		updateForLoggedOut();
	}

	/**
	 * Update the view if a user is logged in. Shows the buttons to edit and delete
	 * 
	 * @param user The logged in user
	 */
	public void updateForLoggedIn(User user) {
		this.btnDelete.setVisible(true);
		this.btnEdit.setVisible(true);

		if (article.getIdUser() != user.getIdUser()) {
			// disable editing and deleting if the article was created by a different user
			this.btnDelete.setDisable(true);
			this.btnEdit.setDisable(true);
		}
	}

	/**
	 * Update the view if no user is logged in. Hides the buttons to edit and delete
	 */
	public void updateForLoggedOut() {
		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(false);
	}
}
