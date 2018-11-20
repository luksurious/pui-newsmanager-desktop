/**
 * 
 */
package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
//import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.services.SceneManager;
import application.utils.JsonArticle;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;
import application.components.NewsHead;
import javafx.scene.layout.StackPane;

/**
 * @author AngelLucas
 *
 */
public class NewsEditController extends NewsCommonController {
	private NewsEditModel editingArticle;
	private boolean htmlMode;
	private boolean bodyMode;
	private String bodyText = "";
	private String abstractText = "";
	// TODO add attributes and methods as needed

	@FXML
	private ImageView imgPreview;

	@FXML
	private TextField title;

	@FXML
	private TextField subtitle;

	@FXML
	private ComboBox<Categories> category;

	@FXML
	private ObservableList<Categories> categoriesList;

	@FXML
	Button btnHome;

	@FXML
	Button sendAndBack;

	@FXML
	Button switchAttribute;

	@FXML
	Button switchFormat;

	@FXML
	HTMLEditor editorHtml;

	@FXML
	TextArea editorText;

	@FXML
	Label abstractLabel;

	@FXML
	Label bodyLabel;

	public NewsEditController() {
		this.categoriesList = FXCollections.observableArrayList();
		this.categoriesList.addAll(Categories.values());
		// remove option for category "ALL"
		this.categoriesList.remove(0);

		this.editingArticle = new NewsEditModel(null);
		this.htmlMode = true;
		this.bodyMode = false;
	}

	@FXML
	void initialize() {
		super.initialize();

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

		editingArticle.setUser(getUser());
		sendAndBack.setDisable(false);
	}

	@Override
	protected void updateUiAfterLogout() {
		super.updateUiAfterLogout();
		
		editingArticle.setUser(getUser());
		sendAndBack.setDisable(true);
	}
	

	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			try {
				SceneManager.getInstance().showSceneInModal(AppScenes.IMAGE_PICKER);
				ImagePickerController controller = (ImagePickerController) SceneManager.getInstance().getController(AppScenes.IMAGE_PICKER);
				Image image = controller.getImage();
				if (image != null) {
//					editingArticle.setImage(image);
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
	 * @throws ServerCommunicationError 
	 * @throws IOException 
	 */
	private boolean send() throws ServerCommunicationError, IOException {
		String titleText = this.getArticle().getTitle();
		String category = this.getArticle().getCategory();

		if (titleText == null || category == null || titleText.equals("")) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and category are mandatory!",
					ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		//this command will send the article to the server
		editingArticle.getConnectionManager().saveArticle(this.getArticle());
		SceneManager.getInstance().showSceneInPrimaryStage(AppScenes.READER, true);

		//super.openMainView();
		//super.initialize();
		return true;
	}

	/**
	 * This method is used to set the connection manager which is needed to save a
	 * news
	 * 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.editingArticle.setConnectionManager(connection);
		
		sendAndBack.setDisable(false);
	}

	Article getArticle() {
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
	void setArticle(Article article) {
		newsHead.setCustomTitle("Edit an article");
		this.editingArticle = (article != null) ? new NewsEditModel(user, article) : new NewsEditModel(user);
		// TODO update UI

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
			System.out.println(newValue);
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
		// TODO Consolidate all changes
		this.editingArticle.commit();
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
	public void saveToFile() {
		String fileName = write();
		
		if (fileName != null) {
			JFXDialogLayout layout = new JFXDialogLayout();
			VBox body = new VBox();
			Label label = new Label("The article was successfully saved to");
			JFXTextArea filenameField = new JFXTextArea(fileName);
			filenameField.setEditable(false);
			filenameField.setPrefHeight(50);
			body.getChildren().add(label);
			body.getChildren().add(filenameField);
	        layout.setBody(body);
	        JFXButton okayButton = new JFXButton("OK");
	        layout.getActions().add(okayButton);
	        
	        JFXAlert<Void> alert = new JFXAlert<Void>((Stage) rootPane.getScene().getWindow());
	        
	        okayButton.setOnAction((event) -> alert.hide());
	        
	        alert.setOverlayClose(true);
	        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
	        alert.setContent(layout);
	        alert.initModality(Modality.NONE);
	        alert.showAndWait();
		}
	}
	
	@FXML
	public void saveToServer() throws ServerCommunicationError, IOException {

		editingArticle.commit();
		
		if (send()) {
			JFXDialogLayout layout = new JFXDialogLayout();
			VBox body = new VBox();
			Label label = new Label("The article was successfully sent to the server");
			body.getChildren().add(label);
	        layout.setBody(body);
	        JFXButton okayButton = new JFXButton("OK");
	        layout.getActions().add(okayButton);
	        
	        JFXAlert<Void> alert = new JFXAlert<Void>((Stage) rootPane.getScene().getWindow());
	        
	        okayButton.setOnAction((event) -> alert.hide());
	        
	        alert.setOverlayClose(true);
	        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
	        alert.setContent(layout);
	        alert.initModality(Modality.NONE);
	        alert.showAndWait();
		}
		
	}
}
