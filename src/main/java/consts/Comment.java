package consts;

public class Comment {
	public static final String SQL_EXCEPTION = "SQLException: ";
	public static final String DB_EXCEPTION = "DBException: ";
	public static final String EXCEPTION = "Exception: ";
	public static final String ILLEGAL_STATE = "Utility class";

	public static final String COMMIT = "Commit";
	public static final String ROLLBACK = "Rollback!!!";
	public static final String RETURN = "Return: ";
	public static final String EXTRACTION = "Extraction model: ";
	public static final String BEGIN = "!Begin!";
	public static final String CLOSED = "Closed : ";

	private Comment() {
		throw new IllegalStateException(ILLEGAL_STATE);
	}
}
