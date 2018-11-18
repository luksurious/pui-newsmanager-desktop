/**
 * 
 */
package application;

import java.io.IOException;
//import java.util.Date;
import java.util.List;

import application.news.Article;
import application.news.Categories;
import application.utils.exceptions.ErrorMalFormedNews;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * This class provides the services needed for NewsReader controller
 * 
 * @author AngelLucas
 *
 */
class NewsReaderModel extends NewsCommonModel {
	/**
	 * Observable list with all articles, needed for a ListView control
	 */
	private ObservableList<Article> articles;
	
	/**
	 * Observable list with all categories, needed for a ComboBox control
	 */
	private ObservableList<Categories> categories;
	
	/**
	 * if dummyData is true all data for the model are generated by itself. In other
	 * case this class will connect with a server in order to retrieve all available
	 * articles
	 */
	private boolean dummyData = true;

	NewsReaderModel() {
		articles = FXCollections.observableArrayList();
		categories = FXCollections.observableArrayList();
		loadCategories();
	}

	/**
	 * 
	 * @return an observable list with the articles
	 */
	ObservableList<Article> getArticles() {
		return articles;
	}
	
	Article findArticleById(int id) {
		ObservableList<Article> candidates = getArticles().filtered((Article articleX) -> articleX.getIdArticle() == id);
		
		if (candidates.size() == 1) {
			return candidates.get(0);
		}
		
		return null;
	}

	/**
	 * 
	 * @return an observable list with the categories
	 */
	ObservableList<Categories> getCategories() {
		return categories;
	}

	/**
	 * Add a new article
	 * 
	 * @param article is added to the observable list, so if there is a ListView
	 *                using this observable list, the article will appear in this
	 *                control instantly
	 */
	void addArticle(Article article) {
		articles.add(article);
	}

	/**
	 * Remove an article from the list and from the server
	 * 
	 * @param article to be removed
	 */
	void deleteArticle(Article article) {
		if (articles.remove(article) && this.connectionManager != null) {
			// It was deleted from list
			// So deleted from BD
			try {
				connectionManager.deleteArticle(article.getIdArticle());
			} catch (ServerCommunicationError e) {
				e.printStackTrace();
			}
		}
	}

	void setDummyData(boolean dummy) {
		this.dummyData = dummy;
	}

	void setConnectionManager(ConnectionManager connectionManager) {
		this.setDummyData(false); // System is connected so dummy data are not needed
		
		super.setConnectionManager(connectionManager);
	}

	/**
	 * This method retrieve all articles and categories. If dummyData is true all
	 * needed data will be generated. In other case the data will be retrieved from
	 * server
	 */
	void retrieveData() {
		articles.clear();
		if (!this.dummyData) {
			this.dataFromServer();
			return;
		}
		// Dummy data
		String[] titles = { "Article1", "Article2", "Article 3 with a long name", "Article4" };
		String[] abstracts = { "Abstract <STRONG>for article 1</STRONG>",
				"Abstract <font color=\"red\">for article 2</font>",
				"Abstract <font color=\"blue\">for article 3</font>",
				"Abstract <font color=\"green\">for article 4</font></br>"
						+ "<img src=\"https://www.w3schools.com/html/pic_mountain.jpg\" alt=\"Mountain View\" style=\"width:304px;height:228px;\">", };
		String[] bodies = {
				// ----------Body 1
				"<h1>Body text for the article <STRONG>1!!!</STRONG></h1>"
						+ "<p><span style=\"color: #339966;\"><img src=\"http://www.eitdigital.eu/fileadmin/system2013/g/facebook_standard.png\" alt=\"EIT digital Logo\" width=\"200\" height=\"200\" /></span></p>"
						+ "<p><span style=\"color: #339966;\">This text is green!!!!</span></p>"
						+ "<p><span style=\"color: #993366;\">Now a link:<a href=\"https://masterschool.eitdigital.eu/programmes/hcid/\">ETI Digital hcid</a></span></p>",
				// ----------Body 2
				"<h1>Body text for the article <STRONG>2!!!</STRONG></h1>"
						+ "<p><span style=\"color: #339966;\"><img src=\"http://www.eitdigital.eu/fileadmin/system2013/g/facebook_standard.png\" alt=\"EIT digital Logo\" width=\"200\" height=\"200\" /></span></p>"
						+ "<p><span style=\"color: #339966;\">This text is green!!!!</span></p>"
						+ "<p><span style=\"color: #993366;\">Now a link:<a href=\"https://masterschool.eitdigital.eu/programmes/hcid/\">ETI Digital hcid</a></span></p>",
				// ----------Body 3
				"<h1>Body text for the article <STRONG>3!!!</STRONG></h1>"
						+ "<p><span style=\"color: #339966;\"><img src=\"http://www.eitdigital.eu/fileadmin/system2013/g/facebook_standard.png\" alt=\"EIT digital Logo\" width=\"200\" height=\"200\" /></span></p>"
						+ "<p><span style=\"color: #339966;\">This text is green!!!!</span></p>"
						+ "<p><span style=\"color: #993366;\">Now a link:<a href=\"https://masterschool.eitdigital.eu/programmes/hcid/\">ETI Digital hcid</a></span></p>",
				// ----------Body 4
				"<h1>Body text for the article <STRONG>4!!!</STRONG></h1>"
						+ "<p><span style=\"color: #339966;\"><img src=\"http://www.eitdigital.eu/fileadmin/system2013/g/facebook_standard.png\" alt=\"EIT digital Logo\" width=\"200\" height=\"200\" /></span></p>"
						+ "<p><span style=\"color: #339966;\">This text is green!!!!</span></p>"
						+ "<p><span style=\"color: #993366;\">Now a link:<a href=\"https://masterschool.eitdigital.eu/programmes/hcid/\">ETI Digital hcid</a></span></p>" };
		String[] abstractImage = { "images/abstract1.jpg", "images/abstract2.jpg",
				"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9zhpNZ-Rz0pv_dO1fQejnGFQlRaTRyMOmZHxSTXeJ-a-1P9x5",
				null };
		Categories[] category = { Categories.SPORTS, Categories.ECONOMY, Categories.INTERNATIONAL, Categories.ECONOMY };

		for (int i = 0; i < titles.length; i++) {
			// idUser == 2 correspond to Reader1 in dummy data test
			Article article = new Article(titles[i], 2, category[i].toString(), abstracts[i], abstractImage[i]);
			article.setBodyText(bodies[i]);
			this.articles.add(article);
		}
	}

	private void dataFromServer() {
		List<Article> articlesList;
		try {
			articlesList = connectionManager.getArticles();
			for (Article article : articlesList) {// FOR
				this.articles.add(article);
			} // For
		} catch (ServerCommunicationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}

	/**
	 * This method load the available categories
	 */
	private void loadCategories() {
		categories.clear();
		for (Categories cat : Categories.values()) {
			this.categories.add(cat);
		}
	}
}
