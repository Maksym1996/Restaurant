package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import db.entity.OrderView;
import db.entity.Product;
import db.entity.User;

public class Util {

	private Util() {
		throw new IllegalStateException("Utility class");
	};

	private static final Logger log = LogManager.getLogger(Util.class);

	public static int getMaxPages(long itemsCount, long pageSize) {
		log.info(Comment.BEGIN);
		log.debug("itemsCount " + itemsCount);
		log.debug("pageSize " + pageSize);
		int i = (int) (itemsCount / pageSize);
		log.debug("count/size= " + i);
		return (double) itemsCount / pageSize != (double) i ? i + 1 : i;
	}

	public static User createUser(String firstName, String lastName, String email, String phoneNumber,
			String password) {
		log.info(Comment.BEGIN);
		log.debug("firstName " + firstName);
		log.debug("lastName " + lastName);
		log.debug("email " + email);
		log.debug("phoneNumber " + phoneNumber);
		log.debug("password " + password);

		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhoneNumber(phoneNumber);
		user.setEmail(email);
		user.setPassword(password);
		user.setRegistered("true");

		log.info(Comment.RETURN + user);
		return user;

	}

	public static User createUser(String firstName, String phoneNumber) {
		log.info(Comment.BEGIN);
		log.debug("firstName " + firstName);
		log.debug("phoneNumber " + phoneNumber);
		User user = new User();
		user.setFirstName(firstName);
		user.setPhoneNumber(phoneNumber);
		user.setRegistered("false");

		log.info(Comment.RETURN + user);
		return user;

	}

	public static OrderView createOrder(Status status, String address, int userId, int sum) {
		log.info(Comment.BEGIN);
		log.debug("status " + status);
		log.debug("address " + address);
		log.debug("userId " + userId);
		log.debug("sum " + sum);
		OrderView order = new OrderView();

		order.setStatus(status);
		order.setAddress(address);
		order.setUserId(userId);
		order.setSum(String.valueOf(sum));

		log.info(Comment.RETURN + order);
		return order;

	}

	public static Product createProduct(String name, int price, String description, String imageLink, Category category,
			int id) {
		log.info(Comment.BEGIN);
		log.debug("name " + name);
		log.debug("price " + price);
		log.debug("description " + description);
		log.debug("imageLink " + imageLink);
		log.debug("category " + category);
		log.debug("id " + id);
		Product product = new Product();

		product.setName(name);
		product.setPrice(price);
		product.setDescription(description);
		product.setImageLink(imageLink);
		product.setCategory(category);
		product.setId(id);

		log.info(Comment.RETURN + product);
		return product;

	}

	private static final String SALT = "234jsdflakj";

	public static String stringToMD5(String password) {
		log.info(Comment.BEGIN);
		log.debug("password: " + password);
		if (password == null) {
			log.info(Comment.RETURN + null);
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
			log.error("NoSuchAlgorithmException: " + e.getMessage());
		}

		BigInteger bigInt = new BigInteger(1, digest);
		String md5Hex = bigInt.toString(16);

		while (md5Hex.length() < 32) {
			md5Hex = "0" + md5Hex;
		}
		log.info("Return MD5 code");
		return md5Hex;
	}

}
