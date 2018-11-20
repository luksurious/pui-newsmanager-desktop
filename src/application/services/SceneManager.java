package application.services;

import java.io.IOException;
import java.util.HashMap;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;

import application.AppScenes;
import application.ControllerEvents;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneManager {
	private static SceneManager single_instance = null;

	private Scene SceneReader;
	
	private Stage primaryStage;
	private Stage currentStage;
	private HashMap<AppScenes, Scene> loadedScenes = new HashMap<AppScenes, Scene>();
	private HashMap<AppScenes, Object> sceneControllers = new HashMap<AppScenes, Object>();
	
	private String mainStylessheet = "";

	private ServiceRegistry serviceRegistry;

	private SceneManager() {}

	public static SceneManager getInstance() {
		if (single_instance == null) {			
			single_instance = new SceneManager();
		}

		return single_instance;
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setMainStylessheet(String mainStylessheet) {
		this.mainStylessheet = mainStylessheet;
	}

	public void setSceneReader(Scene sceneReader) {
		SceneReader = sceneReader;
	}

	public Scene getSceneReader() {
		return this.SceneReader;
	}

	public Stage getCurrentStage() {
		return currentStage;
	}
	
	public void showSceneInPrimaryStage(AppScenes sceneConfig) throws IOException {
		showSceneInPrimaryStage(sceneConfig, true);
	}

	public void showSceneInPrimaryStage(AppScenes sceneConfig, boolean overwrite) throws IOException {
		Scene scene = loadScene(sceneConfig, overwrite);
		
		fireOnShowEvent(sceneConfig);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		currentStage = primaryStage;
	}
	
	public void showSceneInModal(AppScenes sceneConfig) throws IOException {
		showSceneInModal(sceneConfig, StageStyle.UTILITY, true);
	}
	
	public void showSceneInModal(AppScenes sceneConfig, StageStyle style, boolean blocking) throws IOException {
		Scene scene = loadScene(sceneConfig, true);

        JFXAlert<Void> alert = new JFXAlert<Void>(currentStage);
        scene.setUserData(alert);
                
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(scene.getRoot());
        alert.initModality(Modality.NONE);

		fireOnShowEvent(sceneConfig);
		
		if (blocking) {
	        alert.showAndWait();
		} else {
	        alert.show();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void closeModal(AppScenes sceneConfig) {
		if (!loadedScenes.containsKey(sceneConfig)) {
			return;
		}
		
		Scene scene = loadedScenes.get(sceneConfig);
		((JFXAlert<Void>) scene.getUserData()).hide();
	}

	private void fireOnShowEvent(AppScenes sceneConfig) {
		Object controller = getController(sceneConfig);
		if (controller instanceof ControllerEvents) {
			((ControllerEvents) controller).onShow();
		}
	}
	
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
			
			Object controller = loader.getController();
			if (controller instanceof ServiceRegistryAware) {
				((ServiceRegistryAware) controller).setServiceRegistry(serviceRegistry);
			}
			
			loadedScenes.put(sceneConfig, scene);
			sceneControllers.put(sceneConfig, controller);
		}
		return scene;
	}

	public Object getController(AppScenes sceneConfig) throws RuntimeException {
		if (sceneControllers.containsKey(sceneConfig)) {
			return sceneControllers.get(sceneConfig);
		}
		
		throw new RuntimeException("Controller for scene " + sceneConfig.toString() + " is not loaded!");
	}

	public void setServiceRegistry(ServiceRegistry services) {
		this.serviceRegistry = services;		
	}
}
