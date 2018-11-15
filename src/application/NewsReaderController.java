/**
 * 
 */
package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import serverConection.ConnectionManager;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

/**
 * @author AngelLucas
 *
 */
public class NewsReaderController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;

	@FXML
	ListView<Article> newsList;
	@FXML
	Label headline;
	@FXML
	Button btnAdd;
	@FXML
	Button btnLogin;
	@FXML
	ListView<Categories> categoriesList;
	@FXML
	WebView newsWebArea;
	@FXML
	ImageView headImage;

	// TODO add attributes and methods as needed
	ObservableList<Categories> categoryList;

	@FXML
	void initialize() {

		// ObservableList category new ObservableList<Categories>()
		this.categoriesList.setItems(this.categoryList);

		WebEngine webEngine = this.newsWebArea.getEngine();
		webEngine.loadContent("<h1>Loading...</h1>");

		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");

		this.headline.setText("These are the news for today, " + headFormat.format(new Date()));
	}

	public NewsReaderController() {
		// Uncomment next sentence to use data from server instead dummy data
		// newsReaderModel.setDummyDate(false);
		// Get text Label

		this.categoryList = FXCollections.observableArrayList();
		this.categoryList.addAll(Categories.values());
	}

	private void getData() {
		// TODO retrieve data and update UI
		// The method newsReaderModel.retrieveData() can be used to retrieve data
		this.newsReaderModel.retrieveData();

		String html = "";
		for (Article article : this.newsReaderModel.getArticles()) {
			BufferedImage bImage = SwingFXUtils.fromFXImage(article.getImageData(), null);
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			String base64Image = "";
			try {
				ImageIO.write(bImage, "png", s);
				byte[] res = s.toByteArray();
				s.close(); // especially if you are using a different output stream.
				base64Image = Base64.getEncoder().encodeToString(res);
				base64Image = "data:image/png;base64," + base64Image;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			html += String.format("<div><img style=\"width:400px;\" src=\"%s\"><br><h2>%s</h2><h4>%s</h4><p>%s</p></div>", base64Image,
					article.getTitle(), article.getSubtitle(), article.getAbstractText());
		}
		WebEngine webEngine = this.newsWebArea.getEngine();
		
		html = String.format("<div style=\"font-family:'Segoe UI', sans-serif;padding:20px;\">%s</div>", html);
		webEngine.loadContent(html);
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

	@FXML
	public void openEditor(ActionEvent event) throws IOException {

		Stage stage;
		Parent root;

		stage = (Stage) btnAdd.getScene().getWindow();

		SceneManager.getInstance().setSceneReader(stage.getScene());

		root = FXMLLoader.load(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager(ConnectionManager connection) {
		this.newsReaderModel.setDummyData(false); // System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {

		this.usr = usr;
		// Reload articles
		this.getData();
		// TODO Update UI
	}

	// Auxiliary methods
	private interface InitUIData<T> {
		void initUIData(T loader);
	}
}
