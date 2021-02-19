package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import consts.Param;

public class Validator {

	private Validator() {
		throw new IllegalStateException("Utility class");
	}

	public static final String NUMBER_PATTERN = "[1-9]{1}[0-9]*";

	public static Map<String, String> mainPageValidator(String page, String productId, String sortValue) {
		Map<String, String> params = new HashMap<>();

		if (page == null || !Pattern.matches(NUMBER_PATTERN, page)) {
			params.put(Param.PAGE, "1");
		} else {
			params.put(Param.PAGE, page);
		}
		if (productId == null || !Pattern.matches(NUMBER_PATTERN, productId)) {
			params.put(Param.PRODUCT_ID, "0");
		} else {
			params.put(Param.PRODUCT_ID, productId);
		}
		if (sortValue == null) {
			params.put(Param.SORT_VALUE, "id");
		}else {
			params.put(Param.SORT_VALUE, sortValue);
		}
		return params;
	}

	public static boolean workZoneValidator(String status, String id) {
		return id != null && Pattern.matches(NUMBER_PATTERN, id) && status != null && Status.valueOf(status) != null;
	}

	public static Map<String, String> productValidator(String name, String price, String description, String imageLink,
			String category) {
		Map<String, String> errors = new HashMap<>();

		if (name == null || name.isEmpty()) {
			errors.put(Param.NAME, "Enter product name");
		}

		if (price == null || price.isEmpty()) {
			errors.put(Param.PRICE, "Enter product price");
		} else if (!Pattern.matches(NUMBER_PATTERN, price)) {
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
		return num != null && !num.isEmpty() && Pattern.matches(NUMBER_PATTERN, num);
	}

}
