package x590.argparser;

public class ExtraArgumentException extends RuntimeException {
	
	private static final long serialVersionUID = 4601003067927888676L;
	
	public ExtraArgumentException() {
		super();
	}
	
	public ExtraArgumentException(String message) {
		super(message);
	}
	
	public ExtraArgumentException(Throwable cause) {
		super(cause);
	}
	
	public ExtraArgumentException(String message, Throwable cause) {
		super(message, cause);
	}
}