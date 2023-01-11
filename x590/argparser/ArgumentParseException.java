package x590.argparser;

public class ArgumentParseException extends RuntimeException {
	
	private static final long serialVersionUID = -1718112116700749742L;
	
	public ArgumentParseException() {
		super();
	}
	
	public ArgumentParseException(String message) {
		super(message);
	}
	
	public ArgumentParseException(Throwable cause) {
		super(cause);
	}
	
	public ArgumentParseException(String message, Throwable cause) {
		super(message, cause);
	}
}