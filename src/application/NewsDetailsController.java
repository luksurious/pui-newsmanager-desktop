/**
 * 
 */
package application;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

/**
 * @author AngelLucas
 *
 */
public class NewsDetailsController {
	// TODO add attributes and methods as needed
	private User usr;
	private Article article;
	
	@FXML
	Label headline;
	@FXML
	Button btnAdd;
	@FXML
	Button btnLogin;
	@FXML
	ImageView headImage;
	@FXML
	ImageView newsImage_id;
	@FXML
	Text title_id;
	@FXML
	Text category_id;
	@FXML
	Text subtitle_id;
	@FXML
	Text abstract_id;
	@FXML
	Text updated_id;
	@FXML
	WebView body_id;
	
	@FXML
	void initialize() {
		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");
		this.headline.setText("These are the news for today, " + headFormat.format(new Date()));
	}
	
	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			return; // Not logged user
		}
		// TODO Update UI information
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;

		this.title_id.setText(article.getTitle());
		this.subtitle_id.setText(article.getSubtitle());
		this.category_id.setText(article.getCategory());
		this.abstract_id.setText(article.getAbstractText());
		this.newsImage_id.setImage(article.getImageData());
		WebEngine webEngine = this.body_id.getEngine();
		webEngine.loadContent(article.getBodyText());

		if(article.getPublicationDate() != null) {
			this.updated_id.setText("Updated by " + article.getIdUser() + "(user_id) on ");
		}
		else {
			this.updated_id.setText("Updated by " + article.getIdUser() + "(user_id)");
		}
		// TODO complete this method
		
	}
	
	@FXML
	public void openLogin(ActionEvent event) {
		Pane root = null;
    		Scene parentScene = ((Node) event.getSource()).getScene();
    		//Load, open and pass information to SecondWindow.fxml 

		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		//user response is required before continuing with the program
		stage.initModality(Modality.WINDOW_MODAL);
		stage.show();
		//Show scene (form) and wait
		//When SecondWindow is closed, retrieve the data and update de contact
	}
	
	
}
