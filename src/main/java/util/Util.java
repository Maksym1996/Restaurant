package util;

import db.entity.User;

public class Util {
	
	
	private Util() {
	};

	public static int getMaxPages(long itemsCount, long pageSize) {
		int i = (int) (itemsCount / pageSize);
		return (double) itemsCount / pageSize != (double) i ? i + 1 : i;
	}

	public static User createUser(String firstName, String lastName, String email, String phoneNumber, String password,
			String street, String house, String apartment, String porch, String registred) {
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhoneNumber(phoneNumber);
		user.setEmail(email);
		user.setPassword(password);
		user.setStreet(street);
		user.setHouse(house);
		user.setApartment(apartment);
		user.setPorch(porch);
		user.setRegistred(registred);
		return user;

	}

}
