package argparser.option;

import java.util.function.Function;
import java.util.function.Predicate;

import argparser.ArgumentParseException;

public abstract class NumberOption<T extends Number> extends Option<T> {
	
	public NumberOption(String name) {
		super(name);
	}
	
	public NumberOption(String name1, String name2) {
		super(name1, name2);
	}
	
	public NumberOption(String name1, String... names) {
		super(name1, names);
	}
	
	protected void minThershold(Predicate<T> predicate, String threshold) {
		addCondition(value -> predicate.test(value) ? null : "Value " + value + " is less than minimum threshold " + threshold);
	}
	
	protected void maxThershold(Predicate<T> predicate, String threshold) {
		addCondition(value -> predicate.test(value) ? null : "Value " + value + " is greater than maximum threshold " + threshold);
	}
	
	protected T tryParseValue(String value, Function<String, T> parser) throws ArgumentParseException {
		try {
			return parser.apply(value);
		} catch(NumberFormatException ex) {
			throw new ArgumentParseException(ex);
		}
	}
}