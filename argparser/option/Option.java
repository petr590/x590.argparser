package argparser.option;

import argparser.Argument;
import argparser.Times;

public abstract class Option<T> extends Argument<T> {
	
	protected T implicitValue;
	protected boolean multiargs;
	protected String metavar;
	protected boolean requiredValue = true;
	
	private void initMetavar(String name) {
		metavar = isPositional ? "" : name.replaceFirst("^-+", "").replace('-', '_');
	}
	
	private static Times timesByName(String name) {
		return name.startsWith(PREFIX) ? Times.ZERO_OR_ONE : Times.ONCE;
	}
	
	public Option(String name) {
		super(name, timesByName(name));
		initMetavar(name);
	}
	
	public Option(String name1, String name2) {
		super(name1, name2, timesByName(name1));
		initMetavar(name2);
	}
	
	public Option(String name1, String... names) {
		super(name1, names, timesByName(name1));
		initMetavar(names[0]);
	}
	
	@Override
	public Option<T> implicitValue(T implicitValue) {
		this.implicitValue = implicitValue;
		return this;
	}
	
	@Override
	public Option<T> multiargs() {
		multiargs = true;
		times = new Times(times.minTimes, Times.MAX);
		return this;
	}
	
	@Override
	public Option<T> metavar(String metavar) {
		this.metavar = metavar;
		return this;
	}
	
	@Override
	public Option<T> strong() {
		this.requiredValue = true;
		return this;
	}
	
	@Override
	public Option<T> weak() {
		this.requiredValue = false;
		return this;
	}
	
	
	@Override
	public T getImplicitValue() {
		return implicitValue;
	}
	
	@Override
	public boolean isMultiargs() {
		return multiargs;
	}
	
	@Override
	public String getMetavar() {
		return metavar;
	}
	
	@Override
	public boolean isValueRequired() {
		return requiredValue;
	}
	
	
	@Override
	protected String valueToString() {
		return metavar == null || metavar.isEmpty() ? "" : " = <" + metavar + "> ";
	}
}