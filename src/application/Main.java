package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.omg.CORBA.portable.InputStream;

import application.news.Article;
import application.news.Categories;
import application.services.SceneManager;
import application.services.ServiceRegistry;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// This start method allow us to load a Scene (only one).
			// Uncomment the desire to load scene and comment the other ones
			/*
			 * We use an instance of Pane, so we don't worry about what kind of pane is used
			 * in the FXML file. Pane is the father of all container (BorderPane, FlowPane,
			 * AnchorPane ...
			 */
			// Code for reader main window
			ServiceRegistry services = new ServiceRegistry();
			services.set("connection", createConnectionManager());
			
			SceneManager sceneManager = SceneManager.getInstance();
			sceneManager.setPrimaryStage(primaryStage);
			sceneManager.setMainStylessheet("application.css");
			sceneManager.setServiceRegistry(services);
			
		    primaryStage.setResizable(false);
			
			sceneManager.showSceneInPrimaryStage(AppScenes.READER, false);
			// end code for main window reader
		} catch (AuthenticationError e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in login process");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ConnectionManager createConnectionManager() throws AuthenticationError, IOException {
		// Create properties for server connection
		Properties prop = buildServerProperties();
		ConnectionManager connection = new ConnectionManager(prop);

		Properties config = loadConfiguration();
		// Connecting as public (anonymous) for your group
		connection.setAnonymousAPIKey(config.getProperty("apiKey"));
		return connection;
	}

	public static void main(String[] args) {
		launch(args);
	}

	final static Properties buildServerProperties() {
		Properties prop = new Properties();
		prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
		prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");

		/*
		 * For http & https proxy prop.setProperty(ConnectionManager.ATTR_PROXY_HOST,
		 * "http://proxy.fi.upm.es");
		 * prop.setProperty(ConnectionManager.ATTR_PROXY_PORT, "80");
		 */
		/*
		 * For proxy or apache password auth
		 * prop.setProperty(ConnectionManager.ATTR_PROXY_USER, "...");
		 * prop.setProperty(ConnectionManager.ATTR_PROXY_PASS, "...");
		 */
		return prop;
	}

	private Properties loadConfiguration() throws IOException {
		// TODO: test with space in path
		InputStream appConfigFile = getClass().getClassLoader().getResourceAsStream("config.properties");

		Properties appProps = new Properties();
		appProps.load(appConfigFile);
		appConfigFile.close();

		return appProps;
	}
}
