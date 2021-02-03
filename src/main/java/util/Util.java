package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	
	public static String hash(String input) throws NoSuchAlgorithmException {
		int sizeByte = 256;
		int numSystem = 16;
		MessageDigest digest = MessageDigest.getInstance("MD5");

		digest.update(input.getBytes());

		byte[] hash = digest.digest();

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			int num = ((int) hash[i] < 0 ? sizeByte : 0) + (int) hash[i];
			String str = Integer.toString(num, numSystem);

			if (str.length() == 1) {
				str = "0" + str;
			}
			for (char ch : str.toCharArray()) {
				result.append(Character.toUpperCase(ch));
			}
		}

		return result.toString();

	}

}
