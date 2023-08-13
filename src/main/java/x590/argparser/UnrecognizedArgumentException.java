package x590.argparser;

public class UnrecognizedArgumentException extends ArgumentParseException {
	
	private static final long serialVersionUID = 668468095346868126L;
	
	
	public UnrecognizedArgumentException() {
		super();
	}
	
	public UnrecognizedArgumentException(String message) {
		super(message);
	}
	
	public UnrecognizedArgumentException(Throwable cause) {
		super(cause);
	}
	
	public UnrecognizedArgumentException(String message, Throwable cause) {
		super(message, cause);
	}
}