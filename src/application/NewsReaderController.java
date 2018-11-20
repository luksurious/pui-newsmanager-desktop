/**
 * 
 */
package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale.Category;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;

import application.components.NewsAccordionItem;
import application.components.NewsHead;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.services.SceneManager;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;

/**
 * @author AngelLucas
 */
public class NewsReaderController extends NewsCommonController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();

	private ObservableList<Categories> categoryDataList;
	
	private boolean loaded = false;

	@FXML
	Accordion newsList;
	@FXML
	JFXListView<Label> categoryListView;
	@FXML
	HBox loadingNotification;
	@FXML
	HBox noItemsNote;

	public NewsReaderController() {
		this.categoryDataList = this.newsReaderModel.getCategories();
	}

	@FXML
	void initialize() {
		super.initialize();
		
		assert categoryListView != null : "fx:id=\"categoriesList\" was not injected: check your FXML file 'NewsReader.fxml'.";

		initCategoriesList();

		newsList.setManaged(false);
		newsList.setVisible(false);
		
		noItemsNote.setManaged(false);
		noItemsNote.setVisible(false);
	}

	private void initCategoriesList() {
		for (Categories category : categoryDataList) {
			Label categoryLabel = new Label(category.getName());
			
			ImageView categoryImage = new ImageView(new Image(getClass().getClassLoader().getResource(category.getImagePath()).toString()));
			categoryImage.setFitHeight(44);
			categoryImage.setFitWidth(44);
			categoryLabel.setGraphic(categoryImage);
			
			categoryListView.getItems().add(categoryLabel);
		}
		
		categoryListView.getSelectionModel().selectFirst();
		
		categoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				if (newValue != null) {
					updateNewsContent();
				} else { // Nothing selected
					categoryListView.getSelectionModel().selectFirst();
				}
			}
		});
	}

	@Override
	public void onShow() {
		super.onShow();
		
		if (!loaded) {
			getData();
		}
	}

	@Override
	void setUser(User user) {
		super.setUser(user);
		
		// Reload articles
		// TODO discuss if needed
//		getData();
	}
	

	@Override
	protected void updateUiAfterLogin() {
		super.updateUiAfterLogin();
		
		newsList.getPanes().forEach((TitledPane titledPane) -> {
			((NewsAccordionItem) titledPane).updateForLoggedIn();
		});
	}

	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();

		newsList.getPanes().forEach((TitledPane titledPane) -> {
			((NewsAccordionItem) titledPane).updateForLoggedOut();
		});
	}

	void getData() {
		noItemsNote.setManaged(false);
		noItemsNote.setVisible(false);
		newsList.setVisible(false);
		newsList.setManaged(false);
		loadingNotification.setVisible(true);
		loadingNotification.setManaged(true);
		
		CompletableFuture.runAsync(() -> {
			newsReaderModel.retrieveData();

			Platform.runLater(() -> updateNewsContent());

			loaded = true;
		});
	}
	
	private void updateNewsContent() {
		newsList.getPanes().clear();
		Label category = categoryListView.getSelectionModel().getSelectedItem();
		
		for (Article article : newsReaderModel.getArticles()) {
			if (category != null && !category.getText().equals(Categories.ALL.toString())
				&& !article.getCategory().equals(category.getText())
			) {
				continue;
			}
			
			NewsAccordionItem item = new NewsAccordionItem(
				article,
				() -> openDetailsbyId(article),
				() -> openEditorForExistingArticle(article)
			);

			if (user instanceof User) {
				item.updateForLoggedIn();
			}
			
			newsList.getPanes().add(item);
		}
		
		loadingNotification.setVisible(false);
		loadingNotification.setManaged(false);
		
		if (newsList.getPanes().size() == 0) {
			noItemsNote.setManaged(true);
			noItemsNote.setVisible(true);
		} else {
			noItemsNote.setManaged(false);
			noItemsNote.setVisible(false);
			newsList.setVisible(true);
			newsList.setManaged(true);
		}
	}
	
	void openDetailsbyId(Article article) {
		SceneManager sceneManager = SceneManager.getInstance();
		
		try {
			sceneManager.showSceneInPrimaryStage(AppScenes.NEWS_DETAILS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		NewsDetailsController controller = (NewsDetailsController) sceneManager.getController(AppScenes.NEWS_DETAILS);
		controller.setArticle(article);
		controller.setUser(user);
		controller.setConnectionManager(this.newsReaderModel.getConnectionManager());
	}

	@Override
	protected NewsCommonModel getModel() {
		return newsReaderModel;
	}

	// Auxiliary methods
	private interface InitUIData<T> {
		void initUIData(T loader);
		// TODO check if needed
	}
}
