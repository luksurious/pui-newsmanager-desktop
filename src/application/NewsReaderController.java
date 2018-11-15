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
import java.util.Locale.Category;
import java.util.concurrent.CompletableFuture;
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
import javafx.concurrent.Worker.State;
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
		this.categoriesList.setItems(this.categoryList);
		this.categoriesList.getSelectionModel().selectFirst();

		WebEngine webEngine = this.newsWebArea.getEngine();
		webEngine.loadContent("<h1>Loading...</h1>");

		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");

		this.headline.setText("These are the news for today, " + headFormat.format(new Date()));
		

		this.categoriesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categories>() {
			@Override
			public void changed(ObservableValue<? extends Categories> observable, Categories oldValue, Categories newValue) {
				if (newValue != null) {
					updateNewsContent();
				} else { // Nothing selected
					categoriesList.getSelectionModel().selectFirst();
				}
			}
		});
//		this.newsWebArea.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
//            @Override
//            public void changed(ObservableValue ov, State oldState, State newState) {
//                if (newState == Worker.State.SUCCEEDED) {
//                    EventListener listener = new EventListener() {
//                        @Override
//                        public void handleEvent(Event ev) {
//                            String domEventType = ev.getType();
//                            //System.err.println("EventType: " + domEventType);
//                            if (domEventType.equals(EVENT_TYPE_CLICK)) {
//                                String href = ((Element)ev.getTarget()).getAttribute("href");
//                                ////////////////////// 
//                                // here do what you want with that clicked event 
//                                // and the content of href 
//                                //////////////////////                               
//                            } 
//                        }
//                    };
// 
//                    Document doc = webView.getEngine().getDocument();
//                    NodeList nodeList = doc.getElementsByTagName("a");
//                    for (int i = 0; i < nodeList.getLength(); i++) {
//                        ((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, listener, false);
//                        //((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_MOUSEOVER, listener, false);
//                        //((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_MOUSEOVER, listener, false);
//                    }
//                }
//            }
//        });
	}

	public NewsReaderController() {
		// Uncomment next sentence to use data from server instead dummy data
		// newsReaderModel.setDummyDate(false);
		// Get text Label

		this.categoryList = FXCollections.observableArrayList();
		this.categoryList.addAll(Categories.values());
	}

	private void getData() {
		// The method newsReaderModel.retrieveData() can be used to retrieve data
//		CompletableFuture.runAsync(() -> {
			this.newsReaderModel.retrieveData();
			System.out.println("Loaded data");

			this.updateNewsContent();
//		});
	}
	
	private void updateNewsContent() {
		Categories category = this.categoriesList.getSelectionModel().getSelectedItem();
		
		String html = "";
		for (Article article : this.newsReaderModel.getArticles()) {
			System.out.println("generating for article...");
			if (category != null && !category.equals(Categories.ALL) && !article.getCategory().equals(category.toString())) {
				continue;
			}
			
			String imageHtml = "";
			if (article.getImageData() != null) {
				BufferedImage bImage = SwingFXUtils.fromFXImage(article.getImageData(), null);
				ByteArrayOutputStream s = new ByteArrayOutputStream();
				try {
					String base64Image = "";
					ImageIO.write(bImage, "png", s);
					byte[] res = s.toByteArray();
					s.close(); // especially if you are using a different output stream.
					base64Image = Base64.getEncoder().encodeToString(res);
					base64Image = "data:image/png;base64," + base64Image;
					imageHtml = String.format("<img style=\"width:400px;\" src=\"%s\">", base64Image);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			html += String.format("<div><a href=\"#news-details/%d\">%s<br><h2>%s</h2><h4>%s</h4><p>%s</p></a></div>", article.getIdArticle(),
					imageHtml, article.getTitle(), article.getSubtitle(), article.getAbstractText());
		}
		
		html = String.format("<div style=\"font-family:'Segoe UI', sans-serif;padding:20px;\">%s</div>", html);

		System.out.println("generated html");
		WebEngine webEngine = this.newsWebArea.getEngine();

		System.out.println("got engine, setting content");
		webEngine.loadContent(html);
		System.out.println("Updated news items");
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
		}

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.WINDOW_MODAL);

		stage.initOwner(parentScene.getWindow());
//		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
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
