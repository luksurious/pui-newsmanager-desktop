package application;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.news.User;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;

/**
 * This class provide the services needed to verify and grant users access to
 * the news manager
 * 
 * @author agonzalez
 *
 */
class LoginModel {
	/**
	 * Map for authorized users. "User:pass" is the key for this map This map is
	 * only used with dummy data
	 */
	private HashMap<String, User> users = new HashMap<String, User>();
	/**
	 * If is true dummyData for users will be used
	 */
	private boolean dummyData = true;
	/**
	 * The ModelManager is used to communicate with articles server
	 */
	private ConnectionManager connectionManager;

	LoginModel() {
		// Dummy data for test
		String[] login = { "Reader1", "Reader2", "Admin1" };
		String[] passwd = { "reader1", "reader2", "admin1" };
		for (int i = 1; i <= login.length; i++) {
			users.put(login[i - 1] + ":" + passwd[i - 1], new User(login[i - 1], i + 1));
		}
		users.get("Admin1:admin1").setAdmin(true);
	}

	boolean usingDummyData() {
		return this.dummyData;
	}

	void setDummyData(boolean dummy) {
		this.dummyData = dummy;
	}

	/**
	 * Sect the connection manager. Connection manager is needed for using real data
	 * 
	 * @param connection
	 */
	void setConnectionManager(ConnectionManager connection) {
		this.connectionManager = connection;
		this.setDummyData(false);
	}
	
	User login(String username, String password) {
		try {
			this.connectionManager.login(username, password);
		} catch (AuthenticationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		User loggedInUser = new User(username, new Integer(this.connectionManager.getIdUser()));
		loggedInUser.setAdmin(this.connectionManager.isAdministrator());
		return loggedInUser;
	}

	/**
	 * Method for user validation. Override is not allowed
	 * 
	 * @param login  user id
	 * @param passwd password for the user
	 * @return return the user if user id and password are correct. In other case
	 *         return null
	 */
	final User validateUser(String login, String passwd) {
		User usr = null;
		if (this.dummyData) {
			usr = users.get(login + ":" + passwd);
		} else {
			try {
				connectionManager.login(login, passwd);
				usr = new User(login, Integer.parseInt(connectionManager.getIdUser()));
				usr.setAdmin(connectionManager.isAdministrator());
			} catch (AuthenticationError e) {
				Logger.getGlobal().log(Level.INFO, "Login error! incorrect user or password!!");
			}

		}

		return usr;
	}
}
