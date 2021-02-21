package exception;
/**
 * The class for database exceptions
 *
 */
public class DBException extends Exception {

	private static final long serialVersionUID = -4858243871976691879L;

	public DBException(Throwable cause) {
		super(cause);
	}
	
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}
		
}
