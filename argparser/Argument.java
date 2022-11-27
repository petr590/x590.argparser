package argparser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Описывает абстрактный аргумент.
 * @version 1.1
 */
public abstract class Argument<T> {
	
	public static final String PREFIX = "-";
	
	protected static final Pattern OPTION_NAME_PATTERN =
			Pattern.compile("-?[^ (){}\\[\\]\\\\$<>=\"'`;]+");
	
	private static final Consumer<ArgsNamespace> NO_ACTION = namespace -> {};
	
	/** Основное имя аргумента */
	protected String name;
	
	/** Хранит все имена (включая основное).
	 * Все имена аргумента должны быть уникальными и
	 * либо начинаться с {@value #PREFIX}, либо нет.
	 * Если у одного имени есть префикс, а у другого нет - будет ошибка. */
	protected final List<String> names;
	
	protected final boolean isPositional;
	
	/** Список условий для парсинга. Если одно из них не выполнится,
	 * будет брошен {@link ArgumentParseException} при парсинге */
	protected final List<Function<T, String>> conditions = new ArrayList<>();
	
	/** Значение по умолчанию. По умолчанию равно {@literal null} */
	protected T defaultValue;
	
	/** Сколько раз можно парсить аргумент (хранит минимальное и максимальное значения) */
	protected Times times;
	
	/** Сколько раз аргумент был распарсен */
	int parsedTimes; // = 0
	
	/** Сообщение, выводимое в help.
	 * Если оно равно {@literal null}, аргумент не выводится. */
	protected String helpMessage = "";
	
	/** Скрыт ли аргумент из короткого help */
	protected boolean hiddenFromShortHelp;
	
	protected boolean replaceable; // = false
	protected Consumer<ArgsNamespace> action = NO_ACTION;
	protected Function<String, T> parser = this::parseValue;
	
	private void addName(String name) {
		if(!OPTION_NAME_PATTERN.matcher(name).matches())
			throw new IllegalArgumentException("Option name '" + name + "' has illegal format");
		
		names.add(name);
	}
	
	private Argument(String name, Times times, int namesCount) {
		this.names = new ArrayList<>(namesCount);
		this.name = name;
		this.isPositional = !name.startsWith(PREFIX);
		this.times = times;
		addName(name);
	}
	
	protected Argument(String name, Times times) {
		this(name, times, 1);
	}
	
	protected Argument(String name1, String name2, Times times) {
		this(name1, times, 2);
		
		if(name1.startsWith(PREFIX) != name2.startsWith(PREFIX))
			throw new ArgumentFormatException("Both names must start or not start with a '" + PREFIX + "'");
		
		addName(name2);
	}
	
	protected Argument(String name1, String[] names, Times times) {
		this(name1, times, names.length + 1);
		
		boolean startsWithPrefix = name1.startsWith(PREFIX);
		
		for(String name : names) {
			if(name.startsWith(PREFIX) != startsWithPrefix)
				throw new ArgumentFormatException("All names must start or not start with a '" + PREFIX + "'");
			addName(name);
		}
	}
	
	
	/** {@literal true}, если аргумент позиционный, т.е. не начинается с {@value #PREFIX} */
	public boolean isPositional() {
		return isPositional;
	}
	
	/** Устанавливает значение по умолчанию */
	public Argument<T> defaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	
	/** Устанавливает значение, которое присваивается аргументу,
	 * когда он указан, но не указано значение */
	public abstract Argument<T> implicitValue(T implicitValue);
	
	/** Задаёт, сколько раз аргумент может быть задан.
	 * По умолчанию {@link Times#ONCE} для позиционных и
	 * {@link Times#ONE_OR_MORE} для непозиционных */
	public Argument<T> times(Times times) {
		this.times = times;
		return this;
	}
	
	
	public Argument<T> zeroOrOneTimes() {
		return this.times(Times.ZERO_OR_ONE);
	}
	
	public Argument<T> oneOrMoreTimes() {
		return this.times(Times.ONE_OR_MORE);
	}
	
	public Argument<T> anyTimes() {
		return this.times(Times.ANY);
	}
	
	
	/** Дайт возможность указывать несколько значений аргумента */
	public abstract Argument<T> multiargs();
	
	
	/** Описание агрумента, которое показывается в help-е */
	public Argument<T> help(String helpMessage) {
		this.helpMessage = helpMessage;
		return this;
	}
	
	/** Скрывает аргумент из help-а */
	public Argument<T> hideFromHelp() {
		this.helpMessage = null;
		return this;
	}
	
	/** Скрывает аргумент из короткого help-а */
	public Argument<T> hideFromShortHelp() {
		this.hiddenFromShortHelp = true;
		return this;
	}
	
	/** Скрывает аргумент из всех help-ов */
	public Argument<T> hideFromAll() {
		return this.hideFromHelp().hideFromShortHelp();
	}
	
	/** Название, которое будет отображаться в help-е */
	public abstract Argument<T> metavar(String metavar);
	
	/** Делает обязательным указание значения для опции */
	public abstract Argument<T> strong();
	
	/** Даёт возможность не указывать значение для опции,
	 * в таком случае используется {@link #implicitValue} */
	public abstract Argument<T> weak();
	
	Argument<T> replaceable() {
		this.replaceable = true; 
		return this;
	}
	
	public Argument<T> action(Consumer<ArgsNamespace> action) {
		this.action = action;
		return this;
	}
	
	/**
	 * Функция <b>{@code condition}</b> возвращает строку - сообщение об ошибке.
	 * Если строка равна {@literal null}, считается, что функция выполнена успешно,
	 * иначе выбрасывается исключение с сообщением.<br>
	 * Просто неохота в каждой функции писать выброс исключения.
	 */
	public Argument<T> addCondition(Function<T, String> condition) {
		conditions.add(condition);
		return this;
	}
	
	/**
	 * Устанавливает кастомный парсер. При неправильных входных данных
	 * он должен выбрасывать {@link ArgumentParseException}
	 */
	public Argument<T> parser(Function<String, T> parser) {
		this.parser = parser;
		return this;
	}
	
	
	/** Значение аргумента по умолчанию */
	public T getDefaultValue() {
		return defaultValue;
	}
	
	/** Значение, которое присваивается аргументу, когда оно не указано явно */
	public abstract T getImplicitValue();
	
	/** Может ли аргумент содержать несколько значений после себя или только одно */
	public abstract boolean isMultiargs();
	
	public Times getTimes() {
		return times;
	}
	
	public String getHelpMessage() {
		return helpMessage;
	}
	
	public boolean hiddenFromShortHelp() {
		return hiddenFromShortHelp;
	}
	
	public abstract String getMetavar();
	
	public boolean isReplaceable() {
		return replaceable;
	}
	
	void performAction(ArgsNamespace arguments) {
		action.accept(arguments);
	}
	
	
	public abstract boolean isValueRequired();
	
	
	public T parse(String value) throws ArgumentParseException {
		T parsedValue = parser.apply(value);
		
		for(Function<T, String> condition : conditions) {
			String result = condition.apply(parsedValue);
			if(result != null) {
				throw new ArgumentParseException(result);
			}
		}
		
		return parsedValue;
	}
	
	protected abstract T parseValue(String value) throws ArgumentParseException;
	
	public boolean canParse() {
		return parsedTimes < times.maxTimes;
	}
	
	public boolean parsed() {
		return parsedTimes >= times.minTimes;
	}
	
	protected abstract String valueToString();
	
	protected String toHelpString() {
		return names.stream().collect(Collectors.joining(", ")) + valueToString();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(name);
		
		String metavar = getMetavar();
		if(!metavar.isEmpty()) {
			if(isValueRequired())
				str.append('=').append(metavar);
			else
				str.append("[=").append(metavar).append(']');
		}
		
		return times.isRequired() ? this.isPositional() ? "<" + str + ">" : str.toString() : "[" + str + "]";
	}
}