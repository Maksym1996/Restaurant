package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Validator {

	private Validator() {
		throw new IllegalStateException("Utility class");
	}

	public static final String NUMBER_PATTERN = "[1-9]{1}[0-9]*";

	public static Map<String, String> mainPageValidator(String page, String productId, String value) {
		Map<String, String> params = new HashMap<>();

		if (page == null || !Pattern.matches(NUMBER_PATTERN, page)) {
			params.put("page", "1");
		} else {
			params.put("page", page);
		}
		if (productId == null || !Pattern.matches(NUMBER_PATTERN, productId)) {
			params.put("productId", "0");
		} else {
			params.put("productId", productId);
		}
		if (value == null) {
			params.put("sortValue", "id");
		} 
		return params;
	}

	public static boolean workZoneValidator(String status, String id) {
		return id != null && Pattern.matches(NUMBER_PATTERN, id) && status != null 
				&& Status.valueOf(status) != null;
	}

}
