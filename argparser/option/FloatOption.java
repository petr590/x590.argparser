package argparser.option;

import argparser.ArgumentParseException;

public class FloatOption extends NumberOption<Float> {
	
	public FloatOption(String name) {
		super(name);
	}
	
	public FloatOption(String name1, String name2) {
		super(name1, name2);
	}
	
	public FloatOption(String name1, String... names) {
		super(name1, names);
	}
	
	public FloatOption min(float threshold) {
		super.minThershold(value -> value >= threshold, Double.toString(threshold));
		return this;
	}
	
	public FloatOption max(float threshold) {
		super.maxThershold(value -> value <= threshold, Double.toString(threshold));
		return this;
	}
	
	public FloatOption minExclusive(float threshold) {
		super.minThershold(value -> value > threshold, Double.toString(threshold));
		return this;
	}
	
	public FloatOption maxExclusive(float threshold) {
		super.maxThershold(value -> value < threshold, Double.toString(threshold));
		return this;
	}
	
	@Override
	protected Float parseValue(String value) throws ArgumentParseException {
		return tryParseValue(value, Float::parseFloat);
	}
}