package util;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import db.entity.Product;

public class Util {
	
	
	private Util() {
	};

	public static int getMaxPages(long itemsCount, long pageSize) {
		int i = (int) (itemsCount / pageSize);
		return (double) itemsCount / pageSize != (double) i ? i + 1 : i;
	}

	public static List<Product> sortBy(List<Product> products, String value) {
		return products.stream().sorted(comparing(value)).collect(Collectors.toList());
	}

	private static Comparator<Product> comparing(String value) {
		switch (value.toLowerCase()) {
		case "name":
			return (o1, o2) -> o1.getName().compareTo(o2.getName());
		case "price":
			return (o1, o2) -> o1.getPrice().compareTo(o2.getPrice());
		case "category":
			return (o1, o2) -> o1.getCategory().compareTo(o2.getCategory());
		default:
			return null;
		}

	}

}
