package exception;
/**
 * THhe class for provider exceptions
 *
 */
public class ProviderException extends RuntimeException {

	private static final long serialVersionUID = -4858243871976691879L;

	public ProviderException(Throwable cause) {
		super(cause);
	}
	
	public ProviderException(String message, Throwable cause) {
		super(message, cause);
	}
		
}
