package application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import application.services.SceneManager;
import application.services.ServiceRegistry;
import javafx.application.Application;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

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
			
			/**
			 * Set up the service registry, and give it the connection manager.
			 * Afterwards, set up the SceneManager and use it to show the main view
			 * @author students
			 */
			ServiceRegistry services = new ServiceRegistry();
			services.set(ConnectionManager.class, createConnectionManager());

			SceneManager sceneManager = SceneManager.getInstance();
			sceneManager.setPrimaryStage(primaryStage);
			sceneManager.setMainStylessheet("application.css");
			sceneManager.setServiceRegistry(services);

			primaryStage.setResizable(false);

			sceneManager.showSceneInPrimaryStage(AppScenes.READER, false);
			// end code for main window reader
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ConnectionManager createConnectionManager() throws IOException {
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

	/**
	 * Read the configuration file from the resources folder
	 * 
	 * @author students
	 * @return the loaded configuration
	 * @throws IOException
	 */
	private Properties loadConfiguration() throws IOException {
		InputStream appConfigFile = getClass().getClassLoader().getResourceAsStream("config.properties");

		Properties appProps = new Properties();
		appProps.load(appConfigFile);
		appConfigFile.close();

		return appProps;
	}
}
