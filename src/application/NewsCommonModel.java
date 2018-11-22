package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import application.news.Article;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedNews;
import serverConection.ConnectionManager;

class NewsCommonModel {
	protected ConnectionManager connectionManager;

	ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * Create an article instance based on a File
	 *
	 * @param file
	 * @return
	 */
	Article createArticleFromFile(File file) {
		if (file == null) {
			return null;
		}

		JsonReader jsonReader;
		try {
			jsonReader = Json.createReader(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		JsonObject object = jsonReader.readObject();
		jsonReader.close();

		Article article;
		try {
			article = JsonArticle.jsonToArticle(object);
		} catch (ErrorMalFormedNews e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		return article;
	}

	void logout() {
		this.connectionManager.logout();
	}
}
