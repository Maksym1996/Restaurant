package util;

public enum Category {
	PIZZA("Pizza"), BURGER("Burger"), DRINKS("Drinks");

	private String title;

	Category(String title) {
		this.title = title;
	}

	public static Category byTitle(String title) {
		for (Category category : values()) {
			if (category.title.equals(title)) {
				return category;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return title;
	}
	
	
}
