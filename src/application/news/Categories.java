package application.news;

public enum Categories {
	ALL("All", "all.png"),
	ECONOMY("Economy", "economy.png"),
	INTERNATIONAL("International", "international.png"),
	NATIONAL("National", "national.png"),
	SPORTS("Sports", "sports.png"),
	TECHNOLOGY("Technology", "technology.png");
	
	private String name;
	private String imagePath = "";

	private Categories(String name, String imagePath) {
		this.name = name;
		this.imagePath = imagePath;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public String toString() {
		return name;
	}
}
