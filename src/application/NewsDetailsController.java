package application;

import java.io.IOException;

import application.news.Article;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author AngelLucas
 *
 */
public class NewsDetailsController extends NewsCommonController {
	private NewsCommonModel newsDetailsModel = new NewsCommonModel();

	private Article article;

	@FXML
	Button btnBack;
	@FXML
	Button btnEdit;
	@FXML
	Button btnDelete;
	@FXML
	Button btnAbstract;
	@FXML
	ImageView newsImage_id;
	@FXML
	Text title_id;
	@FXML
	Text category_id;
	@FXML
	Text subtitle_id;
	@FXML
	Text updated_id;
	@FXML
	WebView body_id;

	@Override
	protected void updateUiAfterLogin() {
		super.updateUiAfterLogin();

		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);
		
		if (article instanceof Article && user.getIdUser() != article.getIdUser()) {
			btnEdit.setDisable(true);
			btnDelete.setDisable(true);
		}
	}

	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();

		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;

		this.title_id.setText(article.getTitle());
		this.subtitle_id.setText(article.getSubtitle());
		this.category_id.setText(article.getCategory());
		this.newsImage_id.setImage(article.getImageData());
		WebEngine webEngine = this.body_id.getEngine();
		webEngine.loadContent(article.getBodyText());
		
		if (article.getPublicationDate() != null) {
			this.updated_id.setText("Updated by user " + article.getIdUser() + " on " + article.getPublicationDate());
		} else {
			this.updated_id.setText("Updated by user " + article.getIdUser());
		}

		if (user != null && user.getIdUser() != article.getIdUser()) {
			btnEdit.setDisable(true);
			btnDelete.setDisable(true);
		}
	}

	@FXML
	public void changeAbstractBody(ActionEvent event) throws IOException {
		WebEngine webEngine = this.body_id.getEngine();
		if (btnAbstract.getText().equals("Show Abstract")) {
			webEngine.loadContent(article.getAbstractText());
			btnAbstract.setText("Show Body");
		} else if (btnAbstract.getText().equals("Show Body")) {
			webEngine.loadContent(article.getBodyText());
			btnAbstract.setText("Show Abstract");
		}
	}
	
	@FXML
	public void editNews() {
		openEditorForExistingArticle(article);
	}

	@FXML
	public void deleteNews(ActionEvent event) throws IOException {

	}

	@Override
	protected NewsCommonModel getModel() {
		return newsDetailsModel;
	}
}
