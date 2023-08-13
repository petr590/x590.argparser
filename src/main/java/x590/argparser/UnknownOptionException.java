package x590.argparser;

public class UnknownOptionException extends ArgumentParseException {
	
	private static final long serialVersionUID = 1L;
	
	public UnknownOptionException() {
		super();
	}
	
	public UnknownOptionException(String name) {
		super("Unknown option '" + name + "'");
	}
}