package x590.argparser.option;

import java.util.Arrays;
import java.util.stream.Collectors;

import x590.argparser.ArgumentParseException;

public class EnumOption<E extends Enum<E>> extends Option<E> {
	
	private final E[] enumConstants;
	private int maxValueStringLength = 24;
	private String valueString;
	
	public EnumOption(Class<E> clazz, String name) {
		super(name);
		enumConstants = clazz.getEnumConstants();
	}
	
	public EnumOption(Class<E> clazz, String name1, String name2) {
		super(name1, name2);
		enumConstants = clazz.getEnumConstants();
	}
	
	public EnumOption(Class<E> clazz, String name1, String... names) {
		super(name1, names);
		enumConstants = clazz.getEnumConstants();
	}
	
	
	/** Максимальная длина строки с перечислением вариантов, которая будет напечатана в help-е.
	 * Если длина строки с вариантами больше этого значения, используется супер-реализация
	 * метода {@link #valueToString()}.<br>
	 * По умолчанию {@link #maxValueStringLength} равно 24. */
	public EnumOption<E> maxValueStringLength(int length) {
		this.maxValueStringLength = length;
		return this;
	}
	
	/** Устанавливает {@link #maxValueStringLength} в максимально возможное значение */
	public EnumOption<E> alwaysPrintVariants() {
		this.maxValueStringLength = Integer.MAX_VALUE;
		return this;
	}
	
	
	@Override
	protected E parseValue(String value) throws ArgumentParseException {
		for(E enumConstant : enumConstants) {
			if(enumConstant.name().equalsIgnoreCase(value))
				return enumConstant;
		}
		
		throw new ArgumentParseException("Enum constant '" + value + "' not found");
	}
	
	@Override
	protected String valueToString() {
		if(valueString == null) {
			valueString = " = " + Arrays.stream(enumConstants).map(enumConstant -> enumConstant.name().toLowerCase()).collect(Collectors.joining("|"));
			if(valueString.length() > maxValueStringLength)
				valueString = super.valueToString();
		}
		
		return valueString;
	}
}