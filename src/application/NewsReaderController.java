/**
 * 
 */
package application;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Predicate;

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
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
	@FXML Label headline;
	@FXML Button btnAdd;
	@FXML Button btnLogin;
	@FXML ListView<Categories> categoriesList;
	@FXML WebView newsWebArea;
	@FXML ImageView headImage;

	// TODO add attributes and methods as needed
	

	@FXML
	void initialize() {
		
		// ObservableList category new ObservableList<Categories>()
		// this.categoriesList.setItems();
		
		WebEngine webEngine = this.newsWebArea.getEngine();
		webEngine.loadContent("<h1>Hello</h1>");
		
//		headImage.setImage(new Image(this.getClass().getResource("eit-logo.png").toString(), true));
	}

	public NewsReaderController() {
		// Uncomment next sentence to use data from server instead dummy data
		// newsReaderModel.setDummyDate(false);
		// Get text Label

	}

	private void getData() {
		// TODO retrieve data and update UI
		// The method newsReaderModel.retrieveData() can be used to retrieve data
		this.newsReaderModel.retrieveData();
		// this.newsList.setItems(this.newsReaderModel.getArticles());
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
