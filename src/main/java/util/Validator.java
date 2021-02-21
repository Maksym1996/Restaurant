package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import consts.Param;

public class Validator {

	private Validator() {
		throw new IllegalStateException(Comment.ILLEGAL_STATE);
	}

	public static final String INTEGER_PATTERN = "[1-9][0-9]*";
	public static final String PHONE_NUMBER_PATTERN = "[0][1-9][0-9]{8}";
	public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{8,}";
	public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

	private static final Logger log = LogManager.getLogger(Validator.class);

	public static Map<String, String> mainPageValidator(String page, String productId, String sortValue) {
		log.info(Comment.BEGIN);
		Map<String, String> params = new HashMap<>();

		log.debug("page " + page);
		log.debug("productId " + productId);
		log.debug("sortValue " + sortValue);

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

		log.debug("After valid page " + page);
		log.debug("After valid productId " + productId);
		log.debug("After valid sortValue " + sortValue);

		log.info(Comment.RETURN + params);
		return params;
	}

	public static boolean workZoneValidator(String status, String id) {
		log.info(Comment.BEGIN);
		return id != null && Pattern.matches(INTEGER_PATTERN, id) && status != null && Status.valueOf(status) != null;
	}

	public static Map<String, String> productValidator(String name, String price, String description, String imageLink,
			String category) {
		log.info(Comment.BEGIN);
		Map<String, String> errors = new HashMap<>();

		log.debug("name " + name);
		log.debug("price " + price);
		log.debug("description " + description);
		log.debug("imageLink " + imageLink);
		log.debug("category " + category);

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

		log.info(Comment.RETURN + errors);
		return errors;
	}

	public static boolean intValidator(String num) {
		log.info(Comment.BEGIN);
		return num != null && !num.isEmpty() && Pattern.matches(INTEGER_PATTERN, num);
	}

	public static Map<String, String> authorizationValidator(String phoneNumber, String password) {
		log.info(Comment.BEGIN);
		Map<String, String> errors = new HashMap<>();
		log.debug("phoneNumber " + phoneNumber);
		log.debug("password " + password);

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
		log.info(Comment.RETURN + errors);
		return errors;
	}

	public static Map<String, String> registrationValidator(String firstName, String lastName, String email,
			String phoneNumber, String password, String confirmPassword) {
		log.info(Comment.BEGIN);
		Map<String, String> errors = new HashMap<>();

		log.debug("firstName " + firstName);
		log.debug("lastName " + lastName);
		log.debug("email " + email);
		log.debug("phoneNumber " + phoneNumber);
		log.debug("password " + password);
		log.debug("confirmPassword " + confirmPassword);

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
		log.info(Comment.RETURN + errors);
		return errors;

	}

	public static Map<String, Integer> cartValidator(String plusOrMinus, String id) {
		log.info(Comment.BEGIN);
		Map<String, Integer> params = new HashMap<>();
		log.debug("plusOrMinus " + plusOrMinus);
		log.debug("id " + id);
		if (Param.INC.equals(plusOrMinus)) {
			params.put(Param.CHANGE, 1);
		} else if (Param.DEC.equals(plusOrMinus)) {
			params.put(Param.CHANGE, -1);
		} else {
			params.put(Param.CHANGE, 0);
		}

		params.put(Param.ID, Validator.intValidator(id) ? Integer.parseInt(id) : 0);

		log.debug("After valid plusOrMinus " + plusOrMinus);
		log.debug("After valid id " + id);

		return params;

	}

	public static int intValidatorReturnInt(String id) {
		log.info(Comment.BEGIN);
		log.debug("id" + id);
		if (null == id || id.isEmpty() || !Pattern.matches(INTEGER_PATTERN, id)) {
			return 0;
		}
		log.info(Comment.RETURN + id);
		return Integer.parseInt(id);
	}

}
