package exception;
/**
 * The class for context exceptions
 *
 */
public class ContextInitException extends RuntimeException {

	private static final long serialVersionUID = -6446221261883562454L;

	public ContextInitException(Throwable cause) {
		super(cause);
	}
	
	public ContextInitException(String message, Throwable cause) {
		super(message, cause);
	}
		
}
