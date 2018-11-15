package application;

import javafx.scene.Scene;

public class SceneManager {
        // static variable single_instance of type Singleton
    private static SceneManager single_instance = null;

    // variable of type String
    private Scene SceneReader;

    // private constructor restricted to this class itself
    private SceneManager()
    {
        SceneReader = null;
    }

    // static method to create instance of Singleton class
    public static SceneManager getInstance() {
        if (single_instance == null)
            single_instance = new SceneManager();

        return single_instance;
    }

    public void setSceneReader(Scene sceneReader) {
        SceneReader = sceneReader;
    }

    public Scene getSceneReader(){
        return this.SceneReader;
    }
}
