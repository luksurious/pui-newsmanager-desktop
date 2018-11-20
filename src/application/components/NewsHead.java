package application.components;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfoenix.controls.JFXButton;

import application.NewsCommonController;
import application.news.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class NewsHead extends HBox {
	@FXML
	Label headline;
	@FXML
	Label headlinePre;
	@FXML
	SplitMenuButton btnAdd;
	@FXML
	JFXButton btnLogin;
	@FXML
	ImageView headImage;
	@FXML
	MenuItem btnLoadNewsFile;
	@FXML
	MenuButton btnUser;
	
	NewsCommonController parentController;

	public NewsHead() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsHead.fxml"));
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.setRoot(this);
        
        try {
        	loader.load();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
	}

	public void setParentController(NewsCommonController parentController) {
		this.parentController = parentController;
	}

	@FXML
	void initialize() {
        assertControls();
		
		SimpleDateFormat headFormat = new SimpleDateFormat("EEE, dd. MMMMM YYYY");
		headline.setText(headFormat.format(new Date()));
		
		btnUser.setManaged(false);
	}

	protected void assertControls() {
		assert headImage != null : "fx:id=\"headImage\" was not injected: check your FXML file 'NewsReader.fxml'.";
        assert headline != null : "fx:id=\"headline\" was not injected: check your FXML file 'NewsReader.fxml'.";
        assert btnAdd != null : "fx:id=\"btnAdd\" was not injected: check your FXML file 'NewsReader.fxml'.";
        assert btnLoadNewsFile != null : "fx:id=\"btnLoadNewsFile\" was not injected: check your FXML file 'NewsReader.fxml'.";
        assert btnLogin != null : "fx:id=\"btnLogin\" was not injected: check your FXML file 'NewsReader.fxml'.";
        
        assert parentController != null : "You must set a parent controller for this element to work!";
	}

	public void updateUiAfterLogin(User user) {
		btnLogin.setVisible(false);
		btnLogin.setManaged(false);
		
		btnUser.setText(user.getLogin());
		btnUser.setManaged(true);
		btnUser.setVisible(true);
	}

	public void updateUiAfterLogout() {
		btnLogin.setVisible(true);
		btnLogin.setManaged(true);
		
		btnUser.setText("logged out");
		btnUser.setVisible(false);
		btnUser.setManaged(false);
	}
	
	public void setCustomTitle(String title) {
		headline.setText(title);
		headlinePre.setVisible(false);
		headlinePre.setManaged(false);
	}

	@FXML
	public void loadNewsFile() {
		parentController.loadNewsFile();
	}
	
	@FXML
	public void openEditor() {
		parentController.openEditor();
	}
	
	@FXML
	void logout() {
		parentController.logout();
	}
	
	@FXML
	void openLogin() {
		parentController.openLogin();
	}
	
	@FXML
	void openMainView() {
		parentController.openMainView();
	}
}
