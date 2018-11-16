/**
 * 
 */
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
public class NewsDetailsController {
	// TODO add attributes and methods as needed
	private NewsDetailsModel newsDetailsModel = new NewsDetailsModel();
	private User usr;
	private Article article;
	
	@FXML
	Label headline;
	@FXML
	Button btnBack;
	@FXML
	Button btnEdit;	
	@FXML
	Button btnDelete;
	@FXML
	Button btnLogin;
	@FXML
	Button btnAbstract;
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
	Text updated_id;
	@FXML
	WebView body_id;
	@FXML
	ListView<Article> newsList;
	@FXML
	SplitMenuButton btnAdd;
	@FXML 
	MenuItem btnLoadNewsFile;
	@FXML 
	MenuButton btnUser;
	
	@FXML
	void initialize() {
		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");
		this.headline.setText("These are the news for today, " + headFormat.format(new Date()));
		this.btnUser.setManaged(false);
	}
	
	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			this.btnLogin.setVisible(true);
			this.btnLogin.setManaged(true);
			
			this.btnUser.setText("logged out");
			this.btnUser.setVisible(false);
			this.btnUser.setManaged(false);
			
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
		} else {
			// logged in
			this.btnLogin.setVisible(false);
			this.btnLogin.setManaged(false);
			
			this.btnUser.setText(this.usr.getLogin());
			this.btnUser.setManaged(true);
			this.btnUser.setVisible(true);
			
			this.btnEdit.setVisible(true);
			this.btnDelete.setVisible(true);
		}
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;

		this.title_id.setText(article.getTitle());
		this.subtitle_id.setText(article.getSubtitle());
		this.category_id.setText(article.getCategory());
		//this.abstract_id.setText(article.getAbstractText());
		this.newsImage_id.setImage(article.getImageData());
		WebEngine webEngine = this.body_id.getEngine();
		webEngine.loadContent(article.getBodyText());
		System.out.println(article.getPublicationDate());
		if(article.getPublicationDate() != null) {
			this.updated_id.setText("Updated by " + article.getIdUser() + "(user_id) on " + article.getPublicationDate());
		}
		else {
			this.updated_id.setText("Updated by " + article.getIdUser() + "(user_id)");
		}
		// TODO complete this method	
	}
	
	void setConnectionManager(ConnectionManager connection) {
		this.newsDetailsModel.setConnectionManager(connection);
	}
	
	@FXML
	public void openLogin(ActionEvent event) {
		Pane root = null;
		Scene parentScene = ((Node) event.getSource()).getScene(); 

		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
		
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		LoginController controller = loader.<LoginController>getController();
		controller.setConnectionManager(this.newsDetailsModel.getConnectionManager());

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.WINDOW_MODAL);

		stage.initOwner(parentScene.getWindow());
//		stage.initStyle(StageStyle.UNDECORATED);
		stage.showAndWait();
		
		if (controller.getLoggedUsr() != null) {
			setUsr(controller.getLoggedUsr());
		}
	}
	
	@FXML
	void logout() {
		this.newsDetailsModel.getConnectionManager().logout();
		setUsr(null);
	}
	
	@FXML
	public void changeAbstractBody(ActionEvent event) throws IOException {
		WebEngine webEngine = this.body_id.getEngine();
		if(btnAbstract.getText().equals("Show Abstract")) {
			webEngine.loadContent(article.getAbstractText());
			btnAbstract.setText("Show Body");
		}
		else if(btnAbstract.getText().equals("Show Body")) {
			webEngine.loadContent(article.getBodyText());
			btnAbstract.setText("Show Abstract");
		}
	}
	
	@FXML
	public void getBack(ActionEvent event) throws IOException {

		Stage stage;
		stage = (Stage) btnBack.getScene().getWindow();
		Scene scene = SceneManager.getInstance().getSceneReader();
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	public void loadNewsFile(ActionEvent event) {
		
		Stage stage;
		Parent root = null;

		stage = (Stage)btnAdd.getScene().getWindow();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(stage);
		if (file == null) {
			return;
		}
		
		JsonReader jsonReader;
		try {
			jsonReader = Json.createReader(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		JsonObject object = jsonReader.readObject();
		jsonReader.close();

		Article article;
		try {
			article = JsonArticle.jsonToArticle(object);
		} catch (ErrorMalFormedNews e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		SceneManager.getInstance().setSceneReader(stage.getScene());

		FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
		
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		NewsEditController controller = loader.<NewsEditController>getController();
		controller.setArticle(article);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	public void openEditor(ActionEvent event) throws IOException {

		Stage stage;
		Parent root;

		stage = (Stage) btnAdd.getScene().getWindow();

		root = FXMLLoader.load(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	public void editNews(ActionEvent event) throws IOException {
		Stage stage;
		Parent root;

		stage = (Stage) btnEdit.getScene().getWindow();

		root = FXMLLoader.load(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	public void deleteNews(ActionEvent event) throws IOException {
		
	}
	
	
}
