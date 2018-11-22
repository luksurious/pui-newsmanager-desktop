package application;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.services.SceneManager;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class NewsDetailsController extends NewsCommonController {
	private NewsCommonModel newsDetailsModel = new NewsCommonModel();

	private Article article;

	@FXML
	private JFXButton btnEdit;
	@FXML
	private JFXButton btnDelete;
	@FXML
	private JFXButton btnAbstract;
	@FXML
	private ImageView newsImage;
	@FXML
	private Text title;
	@FXML
	private Text category;
	@FXML
	private Text subtitle;
	@FXML
	private Text updated;
	@FXML
	private WebView body;
	
	@FXML @Override
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
	public void setArticle(Article article) {
		this.article = article;

		this.title.setText(article.getTitle());
		this.subtitle.setText(article.getSubtitle());
		this.category.setText(article.getCategory());
		this.newsImage.setImage(article.getImageData());
		WebEngine webEngine = this.body.getEngine();
		webEngine.loadContent(article.getBodyText());
		
		if (article.getPublicationDate() != null) {
			this.updated.setText("Updated by user " + article.getIdUser() + " on " + article.getPublicationDate());
		} else {
			this.updated.setText("Updated by user " + article.getIdUser());
		}

		if (user != null && user.getIdUser() != article.getIdUser()) {
			btnEdit.setDisable(true);
			btnDelete.setDisable(true);
		}
	}
	public Article getArticle() {
		return this.article;
	}

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
	
	@FXML
	public void editNews() {
		openEditorForExistingArticle(article);
	}

	@FXML
	public void deleteNews() {
		openDeleteDialog(article);

	}

	@Override
	protected NewsCommonModel getModel() {
		return newsDetailsModel;
	}
}
