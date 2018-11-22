package application.services;

import java.io.IOException;
import java.util.HashMap;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;

import application.AppScenes;
import application.ControllerEvents;
import application.NewsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The SceneManager class takes care of showing scenes. It is an abstraction so that the controllers
 * don't need to worry about loading FXML or remembering the state of a scene.
 * 
 * It is implemented as a statically available singleton for ease of use.
 * 
 * @author students
 */
public class SceneManager {
	/**
	 * The single instance for the scene manager
	 */
	private static SceneManager singleInstance = null;
	
	/**
	 * The primary stage for this application. Inside this stage all main views will be shown.
	 */
	private Stage primaryStage;
	/**
	 * References to the previously loaded scenes, so we can save their state and show the content as it was before when going back.
	 */
	private HashMap<AppScenes, Scene> loadedScenes = new HashMap<>();
	/**
	 * References to the controllers for all scenes. Controllers must implement the {@link NewsController} interface.
	 */
	private HashMap<AppScenes, NewsController> sceneControllers = new HashMap<>();
	
	/**
	 * The main CSS stylesheet which will be added to all views automatically
	 */
	private String mainStylessheet = "";

	/**
	 * The service registry to get access to global services
	 */
	private ServiceRegistry serviceRegistry;

	/**
	 * Make the constructor private so it can only be created from getInstance
	 */
	private SceneManager() {}

	/**
	 * @return the active scene manager instance
	 */
	public static SceneManager getInstance() {
		if (singleInstance == null) {			
			singleInstance = new SceneManager();
		}

		return singleInstance;
	}
	
	/**
	 * @param primaryStage The main stage of this application. Should only be called when initializing the application.
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * @return Get the main stage of this application so it can be used in other components
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * @param mainStylessheet Set the main stylesheet for the application. Should be set when initializing the application.
	 */
	public void setMainStylessheet(String mainStylessheet) {
		this.mainStylessheet = mainStylessheet;
	}
	
	/**
	 * Show a scene in the main window, replacing the previous version
	 * 
	 * @param sceneConfig The scene to show
	 * @throws IOException
	 */
	public void showSceneInPrimaryStage(AppScenes sceneConfig) throws IOException {
		showSceneInPrimaryStage(sceneConfig, true);
	}

	/**
	 * Show a scene in the main window. If you provide false to overwrite it will take the state of this scene as it was before.
	 * 
	 * @param sceneConfig The scene to show
	 * @param overwrite Whether or not to overwrite the previous state of the scene
	 * @throws IOException
	 */
	public void showSceneInPrimaryStage(AppScenes sceneConfig, boolean overwrite) throws IOException {
		Scene scene = loadScene(sceneConfig, overwrite);
		
		fireOnShowEvent(sceneConfig);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Show a scene as a modal window on top of the main window. Uses blocking mode, i.e. will not return until it is closed.
	 * 
	 * @param sceneConfig The scene to show in a modal
	 * @throws IOException
	 */
	public void showSceneInModal(AppScenes sceneConfig) throws IOException {
		showSceneInModal(sceneConfig, true);
	}
	
	/**
	 * Show a scene as a modal window on top of the main window.
	 * 
	 * @param sceneConfig The scene to show in a modal
	 * @param blocking Whether to wait for the modal to close before returning to the caller
	 * @throws IOException
	 */
	public void showSceneInModal(AppScenes sceneConfig, boolean blocking) throws IOException {
		Scene scene = loadScene(sceneConfig, true);

		// Use JFXAlert class so that we don't need a StackPane
        JFXAlert<Void> alert = new JFXAlert<>(primaryStage);
        // Save the current alert in the scene so we can close it easily later
        scene.setUserData(alert);

        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(scene.getRoot());

		fireOnShowEvent(sceneConfig);
		
		if (blocking) {
	        alert.showAndWait();
		} else {
	        alert.show();
		}
	}
	
	/**
	 * Closes the scene if it was loaded as a modal
	 *
	 * @param sceneConfig The scene to close
	 */
	@SuppressWarnings("unchecked")
	public void closeModal(AppScenes sceneConfig) {
		if (!loadedScenes.containsKey(sceneConfig)) {
			return;
		}
		
		Scene scene = loadedScenes.get(sceneConfig);
		((JFXAlert<Void>) scene.getUserData()).hide();
	}

	/**
	 * Fires the onBeforeShow event of controllers that implement ControllerEvents
	 * @param sceneConfig The scene to fire the event for
	 */
	private void fireOnShowEvent(AppScenes sceneConfig) {
		Object controller = getController(sceneConfig);
		if (controller instanceof ControllerEvents) {
			((ControllerEvents) controller).onBeforeShow();
		}
	}
	
	/**
	 * Loads a scene from an FXML file, and adds the default stylesheet to it.
	 * 
	 * @param sceneConfig The scene to load
	 * @param overwrite Whether to overwrite if the scene was loaded previously. If false, it will reuse an already loaded scene.
	 * @return The loaded scene
	 * @throws IOException
	 */
	private Scene loadScene(AppScenes sceneConfig, boolean overwrite) throws IOException {
		Scene scene;
		if (loadedScenes.containsKey(sceneConfig) && !overwrite) {
			scene = loadedScenes.get(sceneConfig);
		} else {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../" + sceneConfig.getFxmlFile()));
			Pane root = loader.load();

			scene = new Scene(root);
			if (!mainStylessheet.isEmpty()) {			
				scene.getStylesheets().add(getClass().getResource("../" + mainStylessheet).toExternalForm());
			}
			
			NewsController controller = loader.getController();
			if (controller instanceof ServiceRegistryAware) {
				// inject the service registry into the controller
				((ServiceRegistryAware) controller).setServiceRegistry(serviceRegistry);
			}
			
			loadedScenes.put(sceneConfig, scene);
			sceneControllers.put(sceneConfig, controller);
		}
		return scene;
	}

	/**
	 * Retrieves the controller of a given scene. Only possible if this scene was loaded before.
	 * 
	 * @param sceneConfig The scene whose controller to retrieve
	 * @return The controller of the scene
	 * @throws RuntimeException
	 */
	public NewsController getController(AppScenes sceneConfig) throws RuntimeException {
		if (sceneControllers.containsKey(sceneConfig)) {
			return sceneControllers.get(sceneConfig);
		}
		
		throw new RuntimeException("Controller for scene " + sceneConfig.toString() + " is not loaded!");
	}

	/**
	 * Set the service registry so it can be injected to the controllers
	 * @param services The service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) {
		this.serviceRegistry = services;		
	}
}
