package consts;

public class Dao {
	
	public static final String PRODUCT = "productDao";
	public static final String USER = "userDao";
	public static final String ORDER_VIEW = "orderViewDao";
	
	
	private Dao() {
	    throw new IllegalStateException(Comment.ILLEGAL_STATE);
	  }

}
