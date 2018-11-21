package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import application.news.Article;
import application.news.Categories;
import application.services.SceneManager;
import application.utils.JsonArticle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

public class NewsEditController extends NewsCommonController {
	private NewsEditModel editingArticle;
	private boolean htmlMode = true;
	private boolean bodyMode = false;

	private ObservableList<Categories> categoriesList;

	@FXML
	private ImageView imgPreview;

	@FXML
	private JFXTextField title;

	@FXML
	private JFXTextField subtitle;

	@FXML
	private JFXComboBox<Categories> category;

	@FXML
	private JFXButton sendAndBack;

	@FXML
	private HTMLEditor editorHtml;

	@FXML
	private JFXTextArea editorText;

	@FXML
	private Label abstractLabel;

	@FXML
	private Label bodyLabel;

	public NewsEditController() {
		this.categoriesList = FXCollections.observableArrayList();
		this.categoriesList.addAll(Categories.values());
		// remove option for category "ALL"
		this.categoriesList.remove(0);

		this.editingArticle = new NewsEditModel(null);
	}

	@FXML @Override
	public void initialize() {
		super.initialize();
		
        assert title != null : "fx:id=\"title\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert subtitle != null : "fx:id=\"subtitle\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert category != null : "fx:id=\"category\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert imgPreview != null : "fx:id=\"imgPreview\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert abstractLabel != null : "fx:id=\"abstractLabel\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert bodyLabel != null : "fx:id=\"bodyLabel\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert editorHtml != null : "fx:id=\"editorHtml\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert editorText != null : "fx:id=\"editorText\" was not injected: check your FXML file 'NewsEdit.fxml'.";
        assert sendAndBack != null : "fx:id=\"sendAndBack\" was not injected: check your FXML file 'NewsEdit.fxml'.";

		this.category.setItems(this.categoriesList);
		editorText.setManaged(false);
		editorText.setVisible(false);
		bodyLabel.setManaged(false);
		bodyLabel.setVisible(false);
		
		newsHead.setCustomTitle("Create a new article");
		
		sendAndBack.setDisable(true);
		
		editorText.textProperty().addListener((observable, oldValue, newValue) -> {
			editorHtml.setHtmlText(newValue);
		});
		
		setupFieldBindings();
	}

	@Override
	protected void updateUiAfterLogin() {
		super.updateUiAfterLogin();

		editingArticle.setUser(user);
		sendAndBack.setDisable(false);
	}

	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();
		
		editingArticle.setUser(user);
		sendAndBack.setDisable(true);
	}
	

	@FXML
	public void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			try {
				SceneManager.getInstance().showSceneInModal(AppScenes.IMAGE_PICKER);
				ImagePickerController controller = (ImagePickerController) SceneManager.getInstance().getController(AppScenes.IMAGE_PICKER);
				Image image = controller.getImage();
				if (image != null) {
					imgPreview.setImage(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send and article to server, Title and category must be defined and category
	 * must be different to ALL
	 * 
	 * @return true if the article has been saved
	 */
	private boolean send() {
		if (!validateArticle()) {
			return false;
		}
		ConnectionManager connectionManager = serviceRegistry.get(ConnectionManager.class);
		try {
			// this command will send the article to the server
			connectionManager.saveArticle(this.getArticle());
		} catch (ServerCommunicationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean validateArticle() {
		Article article = getArticle();
		if (article == null) {
			showErrorDialog("You cannot save an empty article.");
			return false;
		}
		
		String titleText = article.getTitle();
		String category = article.getCategory();

		if (titleText == null || category == null || titleText.equals("") || category.equals("")) {
			showErrorDialog("Imposible to save the article! Title and category are mandatory!");
			return false;
		}
		
		return true;
	}

	private Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article the article to set
	 */
	public void setArticle(Article article) {
		newsHead.setCustomTitle("Edit an article");
		this.editingArticle = (article != null) ? new NewsEditModel(user, article) : new NewsEditModel(user);
		// call onShow to handle model injections
		onBeforeShow();

		setupFieldBindings();
		
		category.getSelectionModel().select(Categories.valueOf(article.getCategory().toUpperCase()));
		
		if (article.getImageData() != null) {			
			imgPreview.setImage(article.getImageData());
		}
	}

	private void setupFieldBindings() {
		// TODO unbind listeners
		title.textProperty().bindBidirectional(editingArticle.titleProperty());
		subtitle.textProperty().bindBidirectional(editingArticle.subtitleProperty());
		category.getSelectionModel().selectedItemProperty().addListener((observer, oldValue, newValue) -> {
			editingArticle.setCategory(newValue);
		});
		imgPreview.imageProperty().addListener((observer, oldValue, newValue) -> {
			editingArticle.setImage(newValue);
		});
		bindTextEditors();
	}

	/**
	 * Save an article to a file in a json format Article must have a title
	 */
	private String write() {
		// Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?", "");
		String fileName = "saveNews//" + name + ".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		FileWriter writer = null;
		try {
			File file = new File(fileName);
            file.setWritable(true);
            file.setReadable(true);
			writer = new FileWriter(file);
			writer.write(data.toString());
			writer.flush();
			writer.close();
			
			return file.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			if (writer != null) {				
				try {
					writer.close();
				} catch (IOException e1) {
				}
			}
		}
		return null;
	}

	@FXML
	public void switchMode() {
		if (htmlMode) {
			editorText.setText(editorHtml.getHtmlText());
		}
		
		this.htmlMode = !this.htmlMode;
		
		editorHtml.setManaged(this.htmlMode);
		editorHtml.setVisible(this.htmlMode);
		editorText.setManaged(!this.htmlMode);
		editorText.setVisible(!this.htmlMode);
	}
	
	private void bindTextEditors() {
		if (this.bodyMode) {
			editorText.setText(this.editorHtml.getHtmlText());

			editorText.textProperty().unbindBidirectional(editingArticle.abstractTextProperty());
			editorText.textProperty().bindBidirectional(editingArticle.bodyTextProperty());
		} else {
			editorText.setText(this.editorHtml.getHtmlText());

			editorText.textProperty().unbindBidirectional(editingArticle.bodyTextProperty());
			editorText.textProperty().bindBidirectional(editingArticle.abstractTextProperty());
		}
	}
	
	@FXML
	public void switchAttribute() {
		this.bodyMode = !this.bodyMode;
		
		bodyLabel.setManaged(this.bodyMode);
		bodyLabel.setVisible(this.bodyMode);
		abstractLabel.setManaged(!this.bodyMode);
		abstractLabel.setVisible(!this.bodyMode);
		
		bindTextEditors();
	}

	@Override
	protected NewsCommonModel getModel() {
		return editingArticle;
	}

	@FXML
	public void backAndDiscard() throws IOException {
		editingArticle.discardChanges();
		openMainView();
	}
	
	@FXML
	public void saveToFile() {
		this.editingArticle.commit();
		if (!validateArticle()) {
			return;
		}
		
		String fileName = write();
		
		if (fileName != null) {
			VBox body = new VBox();
			Label label = new Label("The article was successfully saved to");
			JFXTextArea filenameField = new JFXTextArea(fileName);
			filenameField.setEditable(false);
			filenameField.setPrefHeight(50);
			body.getChildren().add(label);
			body.getChildren().add(filenameField);
	        showDialog(body);
		}
	}
	
	@FXML
	public void saveToServer() throws IOException {

		editingArticle.commit();
		
		if (send()) {
			showDialog("The article was successfully sent to the server");
	        
			SceneManager.getInstance().showSceneInPrimaryStage(AppScenes.READER, true);
		} else {
			showErrorDialog("There was an error saving the article");
		}
	}
}
