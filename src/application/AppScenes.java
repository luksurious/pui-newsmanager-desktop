/**
 * 
 */
package application;

/**
 * Contain all app scenes
 * 
 * @author AngelLucas
 *
 */
public enum AppScenes {
	LOGIN("Login.fxml"),
	READER("NewsReader.fxml"),
	NEWS_DETAILS("NewsDetails.fxml"),
	EDITOR("NewsEdit.fxml"),
	ADMIN("AdminNews.fxml"),
	/*IMAGE_PICKER("ImagePicker.fxml")*/
	IMAGE_PICKER("ImagePickerMaterialDesign.fxml");
	
	private String fxmlFile;

	private AppScenes(String file) {
		this.fxmlFile = file;
	}

	public String getFxmlFile() {
		return this.fxmlFile;
	}

}
