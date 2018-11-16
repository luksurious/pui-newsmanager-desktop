/**
 * 
 */
package application;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

import com.jfoenix.controls.JFXButton;
//import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import application.news.Article;
import application.news.Categories;
import application.news.User;
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

/**
 * @author AngelLucas
 *
 */
public class NewsEditController {
	private ConnectionManager connection;
	private NewsEditModel editingArticle;
	private User usr;
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
    private ChoiceBox<Categories> category;

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

    @FXML
    void initialize(){
        this.category.setItems(this.categoriesList);
        editorText.setManaged(false);
    }

	public NewsEditController(){
        this.categoriesList = FXCollections.observableArrayList();
        this.categoriesList.addAll(Categories.values());

        this.editingArticle = new NewsEditModel(null);
        this.htmlMode = true;
        this.bodyMode = false;
	}

	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				// Scene scene = new Scene(root, 570, 420);
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					editingArticle.setImage(image);
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
		String titleText = null; // TODO Get article title
		Categories category = null; // TODO Get article cateory
		if (titleText == null || category == null || titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory",
					ButtonType.OK);
			alert.showAndWait();
			return false;
		}
//TODO prepare and send using connection.saveArticle( ...)

		return true;
	}

	/**
	 * This method is used to set the connection manager which is needed to save a
	 * news
	 * 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		// TODO enable save and send button
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		// TODO Update UI and controls

	}

	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}


    Article getPreparedArticle() {
	    this.synchroniseText();
	    this.editingArticle.setTitle(title.getText());
	    this.editingArticle.setSubtitle(subtitle.getText());
	    this.editingArticle.setImage(imgPreview.getImage());
	    this.editingArticle.setBody(bodyText);
        this.editingArticle.setAbstract(abstractText);
        this.editingArticle.setCategory(category.getValue());
        return this.editingArticle.getArticleOriginal();
    }

	/**
	 * PRE: User must be set
	 * 
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.editingArticle = (article != null) ? new NewsEditModel(usr, article) : new NewsEditModel(usr);
		// TODO update UI
	}

	/**
	 * Save an article to a file in a json format Article must have a title
	 */
	private void write() {
		// TODO Consolidate all changes
		this.editingArticle.commit();
		// Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?", "");
		String fileName = "saveNews//" + name + ".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(data.toString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@FXML
	public void openHome(ActionEvent event) throws IOException {
		Stage stage;

		stage = (Stage) btnHome.getScene().getWindow();

		Scene scene = SceneManager.getInstance().getSceneReader();
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	public void switchMode(){
        this.synchroniseFormat();

        editorHtml.setManaged(!this.htmlMode);
        editorHtml.setVisible(!this.htmlMode);
        editorText.setManaged(this.htmlMode);
        editorText.setVisible(this.htmlMode);

        this.htmlMode = !this.htmlMode;
    }

    private void synchroniseFormat(){
        if(this.htmlMode)
            editorText.setText(editorHtml.getHtmlText());
        else
            editorHtml.setHtmlText(editorText.getText());
    }

    private void synchroniseText(){
        if(this.bodyMode){
            this.bodyText = this.editorHtml.getHtmlText();
            this.editorHtml.setHtmlText(this.abstractText);
            this.editorText.setText(this.abstractText);
        } else {
            this.abstractText = this.editorHtml.getHtmlText();
            this.editorHtml.setHtmlText(this.bodyText);
            this.editorText.setText(this.bodyText);
        }
    }

    @FXML
    public void switchAttribute(){
        this.synchroniseFormat();
	    this.synchroniseText();

        bodyLabel.setManaged(!this.bodyMode);
        bodyLabel.setVisible(!this.bodyMode);
        abstractLabel.setManaged(this.bodyMode);
        abstractLabel.setVisible(this.bodyMode);

        this.bodyMode = !this.bodyMode;
    }
}
