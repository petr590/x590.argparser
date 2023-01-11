package x590.argparser;

public class ArgumentFormatException extends RuntimeException {
	
	private static final long serialVersionUID = -3456961029090523847L;
	
	public ArgumentFormatException() {
		super();
	}
	
	public ArgumentFormatException(String message) {
		super(message);
	}
	
	public ArgumentFormatException(Throwable cause) {
		super(cause);
	}
	
	public ArgumentFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}