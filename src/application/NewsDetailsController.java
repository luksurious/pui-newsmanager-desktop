package application;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import application.news.Article;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Controller for the details view
 * 
 * @author students
 */
public class NewsDetailsController extends NewsCommonController {
	private NewsCommonModel newsDetailsModel = new NewsCommonModel();

	/**
	 * The article being shown
	 */
	private Article article;

	/**
	 * Button to edit the article
	 */
	@FXML
	private JFXButton btnEdit;
	
	/**
	 * Button to delete the article
	 */
	@FXML
	private JFXButton btnDelete;
	
	/**
	 * Button to switch between abstract and body
	 */
	@FXML
	private JFXButton btnAbstract;
	
	/**
	 * ImageView to show the image of the article
	 */
	@FXML
	private ImageView newsImage;
	
	/**
	 * Control showing the title of the article
	 */
	@FXML
	private Text title;

	/**
	 * Control showing the category of the article
	 */
	@FXML
	private Text category;

	/**
	 * Control showing the subtitle of the article
	 */
	@FXML
	private Text subtitle;

	/**
	 * Control showing information of who and when the article was updated
	 */
	@FXML
	private Text updated;
	
	/**
	 * Webview to show the body or abstract in HTML format
	 */
	@FXML
	private WebView body;

	@FXML
	@Override
	public void initialize() {
		super.initialize();

		assert newsImage != null : "fx:id=\"newsImage_id\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert title != null : "fx:id=\"title_id\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert category != null : "fx:id=\"category_id\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert subtitle != null : "fx:id=\"subtitle_id\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert body != null : "fx:id=\"body_id\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert updated != null : "fx:id=\"updated_id\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert btnEdit != null : "fx:id=\"btnEdit\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'NewsDetails.fxml'.";
		assert btnAbstract != null : "fx:id=\"btnAbstract\" was not injected: check your FXML file 'NewsDetails.fxml'.";
	}

	/**
	 * When a user logs in it will change the interface by appearing the buttons of
	 * Edit and Delete news. When the user is not the same as the one that created
	 * the news, then the buttons of editing will be disabled for him/her, since the
	 * user that created the new is the only one able to modify it.
	 */
	@Override
	protected void updateUiAfterLogin() {
		super.updateUiAfterLogin();

		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);

		if (article instanceof Article && user.getIdUser() != article.getIdUser()) {
			// disable edit and delete if it is not from the same user
			btnEdit.setDisable(true);
			btnDelete.setDisable(true);
		}
	}

	/**
	 * When a user logs out it will change the interface by hiding the buttons of
	 * Edit and Delete news.
	 */
	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();

		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
	}

	/**
	 * @param article the article to set Sets the article and shows the details of
	 *                the selected article in the details news page.
	 */
	public void setArticle(Article article) {
		this.article = article;

		this.title.setText(article.getTitle());
		this.subtitle.setText(article.getSubtitle());
		this.category.setText(article.getCategory());

		if (article.getImageData() == null) {
			// show a placeholder image if no image is given
			this.newsImage.setImage(new Image(getClass().getResourceAsStream("/noImage.jpg")));
		} else {
			this.newsImage.setImage(article.getImageData());
		}

		WebEngine webEngine = this.body.getEngine();
		webEngine.loadContent(article.getBodyText());

		if (article.getPublicationDate() != null) {
			this.updated.setText("Updated by user " + article.getIdUser() + " on " + article.getPublicationDate());
		} else {
			this.updated.setText("Updated by user " + article.getIdUser());
		}

		if (user != null && user.getIdUser() != article.getIdUser()) {
			// disable edit and delete if it is not from the same user
			btnEdit.setDisable(true);
			btnDelete.setDisable(true);
		}
	}

	/**
	 * When clicking the button of abstract/body, it will change the text displayed
	 * in the web engine and also the button text either to show the body or the
	 * abstract.
	 */
	@FXML
	public void changeAbstractBody(ActionEvent event) throws IOException {
		WebEngine webEngine = this.body.getEngine();
		if (btnAbstract.getText().equals("Show Abstract")) {
			webEngine.loadContent(article.getAbstractText());
			btnAbstract.setText("Show Body");
		} else if (btnAbstract.getText().equals("Show Body")) {
			webEngine.loadContent(article.getBodyText());
			btnAbstract.setText("Show Abstract");
		}
	}

	/**
	 * When clicking the button it will open the news editor.
	 */
	@FXML
	public void editNews() {
		openEditorForExistingArticle(article);
	}

	/**
	 * When clicking the button it will delete the selected news.
	 */
	@FXML
	public void deleteNews() {
		openDeleteDialog(article);
	}

	/**
	 * The controller gets the model.
	 */
	@Override
	protected NewsCommonModel getModel() {
		return newsDetailsModel;
	}
}
