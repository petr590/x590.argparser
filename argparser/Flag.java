package argparser;

public class Flag extends Argument<Boolean> {
	
	private static void checkName(String name) {
		if(!name.startsWith(PREFIX))
			throw new ArgumentFormatException("Flag name must start with a '" + PREFIX + "'");
	}
	
	{
		defaultValue = Boolean.FALSE;
	}
	
	public Flag(String name) {
		super(name, Times.ANY);
		checkName(name);
	}
	
	public Flag(String name1, String name2) {
		super(name1, name2, Times.ANY);
		checkName(name1);
		checkName(name2);
	}
	
	public Flag(String name1, String... names) {
		super(name1, names, Times.ANY);
		checkName(name1);
		
		for(String name : names)
			checkName(name);
	}
	
	
	@Override
	public boolean isPositional() {
		return false;
	}
	
	public Flag inversed() {
		defaultValue = Boolean.TRUE;
		return this;
	}
	
	@Override
	public Flag defaultValue(Boolean defaultValue) {
		throw new UnsupportedOperationException("Cannot set default value for flag");
	}
	
	@Override
	public Flag implicitValue(Boolean implicitValue) {
		throw new UnsupportedOperationException("Cannot set implicit value for flag");
	}
	
	@Override
	public Flag multiargs() {
		throw new UnsupportedOperationException("Cannot enable multiargs for flag");
	}
	
	@Override
	public Flag metavar(String metavar) {
		throw new UnsupportedOperationException("Cannot set metavar for flag");
	}
	
	@Override
	public Flag strong() {
		throw new UnsupportedOperationException("Flag is strong by default");
	}
	
	@Override
	public Flag weak() {
		throw new UnsupportedOperationException("Cannot make flag weak");
	}
	
	
	@Override
	public Boolean getImplicitValue() {
		return !defaultValue;
	}
	
	@Override
	public boolean isMultiargs() {
		return false;
	}
	
	@Override
	public String getMetavar() {
		return "";
	}
	
	@Override
	public boolean isValueRequired() {
		return false;
	}
	
	@Override
	protected Boolean parseValue(String value) throws ArgumentParseException {
		switch(value.toLowerCase()) {
			case "true", "yes", "y", "1": return Boolean.TRUE;
			case "false", "no", "n", "0": return Boolean.FALSE;
			default: throw new ArgumentParseException("Cannot parse boolean value '" + value + "'");
		}
	}
	
	@Override
	protected String valueToString() {
		return "";
	}
	
	@Override
	public String toString() {
		return "[" + name + "]";
	}
}