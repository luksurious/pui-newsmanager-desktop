package application;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import com.jfoenix.controls.JFXListView;

import application.components.NewsAccordionItem;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.services.SceneManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;

/**
 * Manage the main screen displaying articles and categories
 *
 * @author AngelLucas
 * @author students
 */
public class NewsReaderController extends NewsCommonController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();

	/**
	 * Whether or not data was already loaded
	 */
	private boolean loaded = false;
	/**
	 * Whether or not loading data from the server is currently in progress
	 */
	private boolean loading = false;

	/**
	 * The news list displaying
	 */
	@FXML
	private Accordion newsList;

	/**
	 * The category list
	 */
	@FXML
	private JFXListView<Label> categoryListView;

	/**
	 * The loader while data is fetched from the server
	 */
	@FXML
	private HBox loadingNotification;

	/**
	 * No item message
	 */
	@FXML
	private HBox noItemsNote;

	/**
	 * The container for the accordion to allow scrolling
	 */
	@FXML
	private ScrollPane newsScrollPane;

	@FXML
	@Override
	public void initialize() {
		super.initialize();

		assert categoryListView != null : "fx:id=\"categoryListView\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert newsScrollPane != null : "fx:id=\"newsScrollPane\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert newsList != null : "fx:id=\"newsList\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert loadingNotification != null : "fx:id=\"loadingNotification\" was not injected: check your FXML file 'NewsReader.fxml'.";
		assert noItemsNote != null : "fx:id=\"noItemsNote\" was not injected: check your FXML file 'NewsReader.fxml'.";

		initCategoriesList();

		// hide elements if data is not yet loaded
		noItemsNote.setManaged(false);
		noItemsNote.setVisible(false);

		newsScrollPane.setVisible(false);
		newsScrollPane.setManaged(false);

		categoryListView.setDisable(true);
	}

	@Override
	public void onBeforeShow() {
		super.onBeforeShow();

		if (!loaded) {
			// start to load data if the view is going to be shown next
			// only if it was not loaded before
			getData();
		}
	}

	/**
	 * Redefine the UI after login by updating each accordion item: enable the edit and delete button
	 */
	@Override
	protected void updateUiAfterLogin() {
		super.updateUiAfterLogin();

		newsList.getPanes().forEach((TitledPane titledPane) -> {
			((NewsAccordionItem) titledPane).updateForLoggedIn(user);
		});
	}

	/**
	 * Redefine the UI after login by updating each accordion item: disable the edit and delete button
	 */
	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();

		newsList.getPanes().forEach((TitledPane titledPane) -> {
			((NewsAccordionItem) titledPane).updateForLoggedOut();
		});
	}

	/**
	 * Load the category list with a dedicated icon
	 */
	private void initCategoriesList() {
		ObservableList<Categories> categoryDataList = this.newsReaderModel.getCategories();
		
		// Because the JFXListView control does not support icons with custom elements, we need to use a Label with a Graphic set
		for (Categories category : categoryDataList) {
			Label categoryLabel = new Label(category.getName());

			InputStream imageResource = getClass().getClassLoader().getResourceAsStream(category.getImagePath());
			ImageView categoryImage = new ImageView(new Image(imageResource));
			categoryImage.setFitHeight(44);
			categoryImage.setFitWidth(44);
			categoryLabel.setGraphic(categoryImage);

			categoryListView.getItems().add(categoryLabel);
		}

		categoryListView.getSelectionModel().selectFirst();

		// react to changes on the category selection: filter the news elements
		categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateNewsContent();
			} else { // Nothing selected
				categoryListView.getSelectionModel().selectFirst();
			}
		});
	}

	/**
	 * Load data from the server
	 */
	private void getData() {
		if (loading) {
			return;
		}
		loading = true;
		
		noItemsNote.setManaged(false);
		noItemsNote.setVisible(false);
		newsScrollPane.setVisible(false);
		newsScrollPane.setManaged(false);
		loadingNotification.setVisible(true);
		loadingNotification.setManaged(true);

		// load data in a separate thread so that the main UI is not blocked and is
		// shown directly
		CompletableFuture.runAsync(() -> {
			newsReaderModel.retrieveData();

			// run code inside the JavaFX thread to update the UI
			Platform.runLater(() -> {
				updateNewsContent();
				categoryListView.setDisable(false);
			});

			loaded = true;
			loading = false;
		}).whenComplete((t, ex) -> {
			if (ex != null) {
				ex.printStackTrace();
			}
		});
	}

	/**
	 * Load the UI based on the data
	 */
	private void updateNewsContent() {
		// remove all news elements
		newsList.getPanes().clear();
		
		Label category = categoryListView.getSelectionModel().getSelectedItem();
		for (Article article : newsReaderModel.getArticles()) {
			// skip articles which do not fit the selected category
			if (category != null && !category.getText().equals(Categories.ALL.toString())
					&& !article.getCategory().equals(category.getText())) {
				continue;
			}

			// create a new accordion item for each element, passing the callbacks to the methods in this controller
			NewsAccordionItem item = new NewsAccordionItem(
				article,
				() -> openDetailsbyId(article),
				() -> openEditorForExistingArticle(article),
				() -> openDeleteDialog(article)
			);

			if (user instanceof User) {
				// show buttons to edit/delete
				item.updateForLoggedIn(user);
			}

			newsList.getPanes().add(item);
		}

		loadingNotification.setVisible(false);
		loadingNotification.setManaged(false);

		if (newsList.getPanes().size() == 0) {
			// show hint about no news items being available
			noItemsNote.setManaged(true);
			noItemsNote.setVisible(true);
		} else {
			// show the news list
			noItemsNote.setManaged(false);
			noItemsNote.setVisible(false);
			newsScrollPane.setVisible(true);
			newsScrollPane.setManaged(true);
		}
	}

	/**
	 * Open the detail screen
	 *
	 * @param article
	 */
	private void openDetailsbyId(Article article) {
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
	}

	@Override
	protected NewsCommonModel getModel() {
		return newsReaderModel;
	}
}
