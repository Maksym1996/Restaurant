package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import db.entity.OrderView;
import db.entity.Product;
import db.entity.User;

public class Util {

	private Util() {
		throw new IllegalStateException("Utility class");
	};

	public static int getMaxPages(long itemsCount, long pageSize) {
		int i = (int) (itemsCount / pageSize);
		return (double) itemsCount / pageSize != (double) i ? i + 1 : i;
	}

	public static User createUser(String firstName, String lastName, String email, String phoneNumber,
			String password) {
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhoneNumber(phoneNumber);
		user.setEmail(email);
		user.setPassword(password);
		user.setRegistered("true");
		return user;

	}

	public static User createUser(String firstName, String phoneNumber) {
		User user = new User();
		user.setFirstName(firstName);
		user.setPhoneNumber(phoneNumber);
		user.setRegistered("false");
		return user;

	}

	public static OrderView createOrder(Status status, String address, int userId, int sum) {
		OrderView order = new OrderView();

		order.setStatus(status);
		order.setAddress(address);
		order.setUserId(userId);
		order.setSum(String.valueOf(sum));

		return order;

	}

	public static Product createProduct(String name, int price, String description, String imageLink,
			Category category, int id) {
		Product product = new Product();

		product.setName(name);
		product.setPrice(price);
		product.setDescription(description);
		product.setImageLink(imageLink);
		product.setCategory(category);
		product.setId(id);

		return product;

	}
	
	private static final String SALT = "234jsdflakj";
	
	public static String stringToMD5(String password) {
		if(password == null) {
			return null;
		}
		String saltedPassword = password.concat(SALT);
	    MessageDigest messageDigest = null;
	    byte[] digest = new byte[0];

	    try {
	        messageDigest = MessageDigest.getInstance("MD5");
	        messageDigest.reset();
	        messageDigest.update(saltedPassword.getBytes());
	        digest = messageDigest.digest();
	    } catch (NoSuchAlgorithmException e) {
	        System.err.println(e);
	        
	    }

	    BigInteger bigInt = new BigInteger(1, digest);
	    String md5Hex = bigInt.toString(16);

	    while( md5Hex.length() < 32 ){
	        md5Hex = "0" + md5Hex;
	    }

	    return md5Hex;
	}

}
