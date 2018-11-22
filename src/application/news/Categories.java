package application.news;

public enum Categories {
	ALL("All", "all.png"),
	ECONOMY("Economy", "economy.png"),
	INTERNATIONAL("International", "international.png"),
	NATIONAL("National", "national.png"),
	SPORTS("Sports", "sports.png"),
	TECHNOLOGY("Technology", "technology.png");
	
	private String name;
	/**
	 * The path to the image for this category. Should be inside the resource folder.
	 * @author students
	 */
	private String imagePath = "";

	private Categories(String name, String imagePath) {
		this.name = name;
		this.imagePath = imagePath;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @author students
	 * @return The path to the image for this category
	 */
	public String getImagePath() {
		return imagePath;
	}

	public String toString() {
		return name;
	}
}
