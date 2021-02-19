package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import consts.Param;

public class Validator {

	private Validator() {
		throw new IllegalStateException("Utility class");
	}

	public static final String INTEGER_PATTERN = "[1-9][0-9]*";
	public static final String PHONE_NUMBER_PATTERN = "[0][1-9][0-9]{8}";
	public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{8,}";
	public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

	public static Map<String, String> mainPageValidator(String page, String productId, String sortValue) {
		Map<String, String> params = new HashMap<>();

		if (page == null || !Pattern.matches(INTEGER_PATTERN, page)) {
			params.put(Param.PAGE, "1");
		} else {
			params.put(Param.PAGE, page);
		}
		if (productId == null || !Pattern.matches(INTEGER_PATTERN, productId)) {
			params.put(Param.PRODUCT_ID, "0");
		} else {
			params.put(Param.PRODUCT_ID, productId);
		}
		if (sortValue == null) {
			params.put(Param.SORT_VALUE, "id");
		} else {
			params.put(Param.SORT_VALUE, sortValue);
		}
		return params;
	}

	public static boolean workZoneValidator(String status, String id) {
		return id != null && Pattern.matches(INTEGER_PATTERN, id) && status != null && Status.valueOf(status) != null;
	}

	public static Map<String, String> productValidator(String name, String price, String description, String imageLink,
			String category) {
		Map<String, String> errors = new HashMap<>();

		if (name == null || name.isEmpty()) {
			errors.put(Param.NAME, "Enter product name");
		}

		if (price == null || price.isEmpty()) {
			errors.put(Param.PRICE, "Enter product price");
		} else if (!Pattern.matches(INTEGER_PATTERN, price)) {
			errors.put(Param.PRICE, "The price must be an integer and not start from zero");
		}

		if (description == null || description.isEmpty()) {
			errors.put(Param.DESCRIPTION, "Enter product description");
		}

		if (imageLink == null || imageLink.isEmpty()) {
			errors.put(Param.IMAGE_LINK, "Enter product imageLink");
		}

		if (category == null || category.isEmpty()) {
			errors.put(Param.CATEGORY, "Enter product category");
		} else if (Category.byTitle(category) == null) {
			errors.put(Param.CATEGORY, "The '" + category + "' is not a valid category");
		}

		return errors;
	}

	public static boolean intValidator(String num) {
		return num != null && !num.isEmpty() && Pattern.matches(INTEGER_PATTERN, num);
	}

	public static Map<String, String> authorizationValidator(String phoneNumber, String password) {
		Map<String, String> errors = new HashMap<>();

		if (phoneNumber == null) {
			errors.put(Param.PHONE_NUMBER, "Please, enter your phone number");
		} else if (!Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber)) {
			errors.put(Param.PHONE_NUMBER, "Entered phone number is not correct");
		}
		if (password == null) {
			errors.put(Param.PASSWORD, "Please, enter your password");
		}

//      For easier access
//		else if(!Pattern.matches(INTEGER_PATTERN, password)) {
//			errors.put(Param.PASSWORD, productId);
//		}

		return errors;
	}

	public static Map<String, String> registrationValidator(String firstName, String lastName, String email,
			String phoneNumber, String password, String confirmPassword) {
		Map<String, String> errors = new HashMap<>();

		if (firstName == null || firstName.isEmpty()) {
			errors.put(Param.FIRST_NAME, "Provide your first name");
		}

		if (lastName == null || lastName.isEmpty()) {
			errors.put(Param.LAST_NAME, "Provide your last name");
		}

		if (email == null || email.isEmpty()) {
			errors.put(Param.EMAIL, "Provide your email");
		} else if (!Pattern.matches(EMAIL_PATTERN, email)) {
			errors.put(Param.EMAIL, "The entered email is incorrect");
		}

		if (phoneNumber == null || phoneNumber.isEmpty()) {
			errors.put(Param.PHONE_NUMBER, "Provide your first name");
		} else if (!Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber)) {
			errors.put(Param.PHONE_NUMBER, "The entered phone number is incorrect");
		}

		if (password == null || password.isEmpty()) {
			errors.put(Param.PASSWORD, "Provide your password");
		} else if (confirmPassword == null || confirmPassword.isEmpty()) {
			errors.put(Param.CONFIRM_PASSWORD, "Confirm password");
		} else if (!password.equals(confirmPassword)) {
			errors.put(Param.CONFIRM_PASSWORD, "The passwords you entered are different");
		} else if (!Pattern.matches(PASSWORD_PATTERN, password)) {
			errors.put(Param.PASSWORD,
					"The password must consist of at least 8 characters, at least one digit, one uppercase and lowercase letters of the Latin alphabet and one special character");
		}

		return errors;

	}

	public static Map<String, Integer> cartValidator(String plusOrMinus, String id) {
		Map<String, Integer> params = new HashMap<>();
		if (Param.INC.equals(plusOrMinus)) {
			params.put(Param.CHANGE, 1);
		} else if (Param.DEC.equals(plusOrMinus)) {
			params.put(Param.CHANGE, -1);
		} else {
			params.put(Param.CHANGE, 0);
		}

		params.put(Param.ID, Validator.intValidator(id) ? Integer.parseInt(id) : 0);

		return params;

	}

	public static int intValidatorReturnInt(String id) {
		if (null == id || id.isEmpty() || Pattern.matches(INTEGER_PATTERN, id)) {
			return 0;
		}
		return Integer.parseInt(id);
	}

}
