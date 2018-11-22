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

public class NewsReaderController extends NewsCommonController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();

	private boolean loaded = false;
	private boolean loading = false;

	@FXML
	private Accordion newsList;
	@FXML
	private JFXListView<Label> categoryListView;
	@FXML
	private HBox loadingNotification;
	@FXML
	private HBox noItemsNote;
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
			getData();
		}
	}

	@Override
	protected void updateUiAfterLogin() {
		super.updateUiAfterLogin();

		newsList.getPanes().forEach((TitledPane titledPane) -> {
			((NewsAccordionItem) titledPane).updateForLoggedIn(user);
		});
	}

	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();

		newsList.getPanes().forEach((TitledPane titledPane) -> {
			((NewsAccordionItem) titledPane).updateForLoggedOut();
		});
	}

	private void initCategoriesList() {
		ObservableList<Categories> categoryDataList = this.newsReaderModel.getCategories();
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

		categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateNewsContent();
			} else { // Nothing selected
				categoryListView.getSelectionModel().selectFirst();
			}
		});
	}

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
		});
	}

	private void updateNewsContent() {
		newsList.getPanes().clear();
		Label category = categoryListView.getSelectionModel().getSelectedItem();

		for (Article article : newsReaderModel.getArticles()) {
			if (category != null && !category.getText().equals(Categories.ALL.toString())
					&& !article.getCategory().equals(category.getText())) {
				continue;
			}

			NewsAccordionItem item = new NewsAccordionItem(article, () -> openDetailsbyId(article),
					() -> openEditorForExistingArticle(article), () -> openDeleteDialog(article));

			if (user instanceof User) {
				item.updateForLoggedIn(user);
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
			newsScrollPane.setVisible(true);
			newsScrollPane.setManaged(true);
		}
	}

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
