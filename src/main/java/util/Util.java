package util;

import db.entity.Order;
import db.entity.User;

public class Util {

	private Util() {
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

	public static Order createOrder(String status, String address, int userId) {
		Order order = new Order();

		order.setStatus(status);
		order.setAddress(address);
		order.setUserId(userId);

		return order;

	}

}
