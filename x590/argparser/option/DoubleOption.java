package x590.argparser.option;

import x590.argparser.ArgumentParseException;

public class DoubleOption extends NumberOption<Double> {
	
	public DoubleOption(String name) {
		super(name);
	}
	
	public DoubleOption(String name1, String name2) {
		super(name1, name2);
	}
	
	public DoubleOption(String name1, String... names) {
		super(name1, names);
	}
	
	@Override
	protected Double parseValue(String value) throws ArgumentParseException {
		return tryParseValue(value, Double::parseDouble);
	}
	
	public DoubleOption min(double threshold) {
		super.minThershold(value -> value >= threshold, Double.toString(threshold));
		return this;
	}
	
	public DoubleOption max(double threshold) {
		super.maxThershold(value -> value <= threshold, Double.toString(threshold));
		return this;
	}
	
	public DoubleOption minExclusive(double threshold) {
		super.minThershold(value -> value > threshold, Double.toString(threshold));
		return this;
	}
	
	public DoubleOption maxExclusive(double threshold) {
		super.maxThershold(value -> value < threshold, Double.toString(threshold));
		return this;
	}
}