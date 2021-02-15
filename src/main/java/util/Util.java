package util;

import db.entity.OrderView;
import db.entity.Product;
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

	public static OrderView createOrder(String status, String address, int userId, String sum) {
		OrderView order = new OrderView();

		order.setStatus(status);
		order.setAddress(address);
		order.setUserId(userId);
		order.setSum(sum);

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

}
