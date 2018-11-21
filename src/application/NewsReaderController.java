/**
 * 
 */
package application;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.jfoenix.controls.JFXListView;

import application.components.NewsAccordionItem;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.services.SceneManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
		
		categoryListView.setDisable(true);
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

	void getData() {
		noItemsNote.setManaged(false);
		noItemsNote.setVisible(false);
		newsList.setVisible(false);
		newsList.setManaged(false);
		loadingNotification.setVisible(true);
		loadingNotification.setManaged(true);
		
		CompletableFuture.runAsync(() -> {
			newsReaderModel.retrieveData();

			Platform.runLater(() -> {
				updateNewsContent();
				categoryListView.setDisable(false);
			});

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
	}

	@Override
	protected NewsCommonModel getModel() {
		return newsReaderModel;
	}
}
