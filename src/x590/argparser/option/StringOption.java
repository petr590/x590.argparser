package x590.argparser.option;

public class StringOption extends Option<String> {
	
	public StringOption(String name) {
		super(name);
	}
	
	public StringOption(String name1, String name2) {
		super(name1, name2);
	}
	
	public StringOption(String name1, String... names) {
		super(name1, names);
	}
	
	@Override
	protected String parseValue(String value) {
		return value;
	}
}