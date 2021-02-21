package consts;
/**
 * The class for storing parametr consts
 * @author yzhym
 *
 */
public class Param {
	// MainPageServlet
	public static final String PAGE = "page";
	public static final String CATEGORIES = "categories";
	public static final String SORT_VALUE = "sortValue";
	public static final String ASC = "asc";
	public static final String MAX_PAGES = "maxPages";
	public static final String CURRENT_PAGE = "currentPage";

	// CartServlet
	public static final String CHANGE = "change";
	public static final String INC = "inc";
	public static final String DEC = "dec";
	public static final String DELETE_ID = "deleteId";

	// LoginPageServlet
	public static final String LOG_OUT = "logout";

	// product const
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String PRICE = "price";
	public static final String DESCRIPTION = "description";
	public static final String IMAGE_LINK = "imageLink";
	public static final String CATEGORY = "category";

	public static final String PRODUCT_ID = "productId";

	// user const
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String CONFIRM_PASSWORD = "confirmPassword";
	public static final String PHONE_NUMBER = "phoneNumber";

	public static final String ROLE = "role";

	// orderView const
	public static final String STATUS = "status";
	public static final String ADDRESS = "address";
	public static final String SUMM = "sum";
	public static final String ORDER_VIEW_LIST = "orderViewList";
	public static final String ORDERS = "orders";
	public static final String ORDER_SUMM = "orderSumm";
	public static final String CART = "cart";
	public static final String COUNT = "count";
	
	//LanguageServlet
	public static final String LANG = "lang";
	public static final String REFERER = "referer";

	// other
	public static final String PRODUCTS_LIST = "productsList";
	public static final String USER_LIST = "userList";
	public static final String USER = "user";
	public static final String NO_USER = "noUser";
	public static final String PRODUCT = "product";
	public static final String ERRORS = "errors";

	private Param() {
		throw new IllegalStateException(Comment.ILLEGAL_STATE);
	}

}
