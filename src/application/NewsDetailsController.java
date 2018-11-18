package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.services.SceneManager;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

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
		// this.abstract_id.setText(article.getAbstractText());
		this.newsImage_id.setImage(article.getImageData());
		WebEngine webEngine = this.body_id.getEngine();
		webEngine.loadContent(article.getBodyText());
		System.out.println(article.getPublicationDate());
		if (article.getPublicationDate() != null) {
			this.updated_id.setText("Updated by user " + article.getIdUser() + " on " + article.getPublicationDate());
		} else {
			this.updated_id.setText("Updated by user " + article.getIdUser());
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
