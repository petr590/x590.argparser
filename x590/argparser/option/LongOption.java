package x590.argparser.option;

import x590.argparser.ArgumentParseException;

public class LongOption extends NumberOption<Long> {
	
	public LongOption(String name) {
		super(name);
	}
	
	public LongOption(String name1, String name2) {
		super(name1, name2);
	}
	
	public LongOption(String name1, String... names) {
		super(name1, names);
	}
	
	public LongOption min(long threshold) {
		super.minThershold(value -> value >= threshold, Long.toString(threshold));
		return this;
	}
	
	public LongOption max(long threshold) {
		super.maxThershold(value -> value <= threshold, Long.toString(threshold));
		return this;
	}
	
	public LongOption minExclusive(long threshold) {
		super.minThershold(value -> value > threshold, Long.toString(threshold));
		return this;
	}
	
	public LongOption maxExclusive(long threshold) {
		super.maxThershold(value -> value < threshold, Long.toString(threshold));
		return this;
	}
	
	@Override
	protected Long parseValue(String value) throws ArgumentParseException {
		return tryParseValue(value, Long::parseLong);
	}
}