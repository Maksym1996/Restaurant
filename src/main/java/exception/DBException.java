package exception;

public class DBException extends RuntimeException {

	private static final long serialVersionUID = -4858243871976691879L;

	public DBException(Throwable cause) {
		super(cause);
	}
	
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}
		
}
