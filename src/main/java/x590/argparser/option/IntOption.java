package x590.argparser.option;

import x590.argparser.ArgumentParseException;

public class IntOption extends NumberOption<Integer> {
	
	public IntOption(String name) {
		super(name);
	}
	
	public IntOption(String name1, String name2) {
		super(name1, name2);
	}
	
	public IntOption(String name1, String... names) {
		super(name1, names);
	}
	
	@Override
	protected Integer parseValue(String value) throws ArgumentParseException {
		return tryParseValue(value, Integer::parseInt);
	}
	
	public IntOption min(int threshold) {
		super.minThershold(value -> value >= threshold, Integer.toString(threshold));
		return this;
	}
	
	public IntOption max(int threshold) {
		super.maxThershold(value -> value <= threshold, Integer.toString(threshold));
		return this;
	}
	
	public IntOption minExclusive(int threshold) {
		super.minThershold(value -> value > threshold, Integer.toString(threshold));
		return this;
	}
	
	public IntOption maxExclusive(int threshold) {
		super.maxThershold(value -> value < threshold, Integer.toString(threshold));
		return this;
	}
}