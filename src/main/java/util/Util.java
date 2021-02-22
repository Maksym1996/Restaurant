package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CommentConst;
import db.entity.OrderView;
import db.entity.Product;
import db.entity.User;

/**
 * The class for implementing specific methods
 */
public class Util {

	private Util() {
		//nothing
	}

	private static final Logger LOG = LogManager.getLogger(Util.class);

	public static int getMaxPages(long itemsCount, long pageSize) {
		LOG.info(CommentConst.BEGIN);
		LOG.debug("itemsCount " + itemsCount);
		LOG.debug("pageSize " + pageSize);
		int i = (int) (itemsCount / pageSize);
		LOG.debug("count/size= " + i);
		return (double) itemsCount / pageSize != (double) i ? i + 1 : i;
	}

	public static User createUser(String firstName, String lastName, String email, String phoneNumber,
			String password) {
		LOG.info(CommentConst.BEGIN);
		LOG.debug("firstName " + firstName);
		LOG.debug("lastName " + lastName);
		LOG.debug("email " + email);
		LOG.debug("phoneNumber " + phoneNumber);
		LOG.debug("password " + password);

		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhoneNumber(phoneNumber);
		user.setEmail(email);
		user.setPassword(password);
		user.setRegistered("true");

		LOG.info(CommentConst.RETURN + user);
		return user;

	}

	public static User createUser(String firstName, String phoneNumber) {
		LOG.info(CommentConst.BEGIN);
		LOG.debug("firstName " + firstName);
		LOG.debug("phoneNumber " + phoneNumber);
		User user = new User();
		user.setFirstName(firstName);
		user.setPhoneNumber(phoneNumber);
		user.setRegistered("false");

		LOG.info(CommentConst.RETURN + user);
		return user;

	}

	public static OrderView createOrder(Status status, String address, int userId, int sum) {
		LOG.info(CommentConst.BEGIN);
		LOG.debug("status " + status);
		LOG.debug("address " + address);
		LOG.debug("userId " + userId);
		LOG.debug("sum " + sum);
		OrderView order = new OrderView();

		order.setStatus(status);
		order.setAddress(address);
		order.setUserId(userId);
		order.setSum(String.valueOf(sum));

		LOG.info(CommentConst.RETURN + order);
		return order;

	}

	public static Product createProduct(String name, int price, String description, String imageLink, Category category,
			int id) {
		LOG.info(CommentConst.BEGIN);
		LOG.debug("name " + name);
		LOG.debug("price " + price);
		LOG.debug("description " + description);
		LOG.debug("imageLink " + imageLink);
		LOG.debug("category " + category);
		LOG.debug("id " + id);
		Product product = new Product();

		product.setName(name);
		product.setPrice(price);
		product.setDescription(description);
		product.setImageLink(imageLink);
		product.setCategory(category);
		product.setId(id);

		LOG.info(CommentConst.RETURN + product);
		return product;

	}

	private static final String SALT = "234jsdflakj";

	public static String stringToMD5(String password) {
		LOG.info(CommentConst.BEGIN);
		LOG.debug("password: " + password);
		if (password == null) {
			LOG.info(CommentConst.RETURN + null);
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
			LOG.error("NoSuchAlgorithmException: " + e.getMessage());
		}

		BigInteger bigInt = new BigInteger(1, digest);
		String md5Hex = bigInt.toString(16);

		while (md5Hex.length() < 32) {
			md5Hex = "0" + md5Hex;
		}
		LOG.info("Return MD5 code");
		return md5Hex;
	}

}
