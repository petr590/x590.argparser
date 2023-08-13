package x590.argparser;

import static x590.argparser.Argument.PREFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import x590.util.annotation.Nullable;

/**
 * Стандартный парсер. Изначально содержит флаги --help
 * и --version, если указана версия программы.
 */
public class StandartArgParser implements ArgParser {
	
	public static final String INDENT = "  ";
	
	public final String programName;
	
	public static final Locale RU_LOCALE = Locale.forLanguageTag("ru-ru");
	private @Nullable Locale locale;
	
	private int lineWidth = 40;
	
	private final Map<String, Argument<?>>
			optionalArguments = new HashMap<>(),
			positionalArguments = new HashMap<>();
	
	private final Set<Argument<?>> options = new LinkedHashSet<>();
	
	private ErrorHandler errorHandler = ErrorHandlers.PRINT_AND_EXIT;
	
	
	public StandartArgParser(String programName) {
		this.programName = programName;
		
		Argument<?> helpFlag = new Flag("-h", "--help", "-?")
				.replaceable().hideFromShortHelp()
						.help("Show this help and exit")
						.help(RU_LOCALE, "Показать это сообщение")
				
				.action(namespace -> {
					
					System.out.println("Usage: " + programName + " " +
							options.stream().filter(argument -> !argument.hiddenFromShortHelp())
								.map(argument -> argument.toString()).collect(Collectors.joining(" ")));
					
					System.out.println("Arguments:\n" +
							options.stream().map(argument -> {
								String helpMessage = argument.getHelpMessage(locale);
								
								if(helpMessage == null)
									return "";
								
								String name = argument.toHelpString();
								
								if(helpMessage.isEmpty())
									return INDENT + name;
								
								return INDENT + name +
										(name.length() > lineWidth - 4 ? "\n" + " ".repeat(lineWidth) : " ".repeat(lineWidth - name.length()))
										+ helpMessage;
							
							}).collect(Collectors.joining("\n")));
					
					System.exit(0);
				}
		);
		
		optionalArguments.put("-h", helpFlag);
		optionalArguments.put("--help", helpFlag);
		optionalArguments.put("-?", helpFlag);
		
		options.add(helpFlag);
	}
	
	public StandartArgParser(String programName, String version) {
		this(programName);
		
		Argument<?> versionFlag = new Flag("-v", "--version")
				.replaceable().hideFromShortHelp()
					.help("Print version of the program and exit")
					.help(RU_LOCALE, "Вывести версию программы")
				
				.action(namespace -> {
					System.out.println(programName + " " + version);
					System.exit(0);
				});
		
		optionalArguments.put("-v", versionFlag);
		optionalArguments.put("--version", versionFlag);
		
		options.add(versionFlag);
	}
	
	
	private List<Entry<String, Argument<?>>> findEntries(Map<String, Argument<?>> options, Argument<?> argument) {
		return options.entrySet().stream().filter(
				entry -> argument.names.stream().anyMatch(
						name -> name.equals(entry.getKey()))).toList();
	}
	
	
	public StandartArgParser localize(Locale locale) {
		this.locale = locale;
		return this;
	}
	
	public StandartArgParser localize() {
		return localize(Locale.getDefault());
	}
	
	
	public StandartArgParser lineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}
	
	@Override
	public StandartArgParser add(Argument<?> argument) {
		final Map<String, Argument<?>> arguments = argument.isPositional() ? positionalArguments : optionalArguments;
		
		List<Entry<String, Argument<?>>> foundEntries = findEntries(arguments, argument);
		
		for(var foundEntry : foundEntries) {
			Argument<?> foundArg = foundEntry.getValue();
			if(foundArg.isReplaceable()) {
				final List<String>
						argumentNames = argument.names,
						foundArgNames = foundArg.names;
				
				foundArgNames.removeAll(argumentNames);
				
				for(String name : argumentNames)
					arguments.remove(name);
				
				if(!foundArgNames.isEmpty() && argumentNames.contains(foundArg.name)) {
					foundArg.name = foundArgNames.get(0);
				}
			
			} else {
				throw new IllegalArgumentException("Argument '" + argument.toString() + "' already added");
			}
		}
		
		for(String name : argument.names) {
			arguments.put(name, argument);
		}
		
		options.add(argument);
		
		return this;
	}
	
	public StandartArgParser errorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
		return this;
	}
	
	@Override
	public ArgsNamespace parse(String... args) throws ArgumentParseException {
		return parseImpl(args);
	}
	
	// T - подставной тип, нужен для того, чтобы компилятор проверял правильность кода.
	// А на самом деле каждый аргумент имеет свой тип, просто мы работаем со всеми типами как с одним.
	private <T> ArgsNamespace parseImpl(String... args) throws ArgumentParseException {
		
		ArgsNamespace namespace = new ArgsNamespace(optionalArguments, positionalArguments);
		
		@SuppressWarnings("unchecked")
		var namespaceMap = (Map<String, Entry<Argument<T>, List<T>>>)(Map<String, ?>)namespace.map;
		
		@SuppressWarnings("unchecked")
		Map<String, Argument<T>>
				positionalArguments = (Map<String, Argument<T>>)(Map<String, ?>)this.positionalArguments,
				optionalArguments = (Map<String, Argument<T>>)(Map<String, ?>)this.optionalArguments;
		
		Iterator<Argument<T>> positionalIter = positionalArguments.values().iterator();
		Argument<T> positionalArgument = positionalIter.hasNext() ? positionalIter.next() : null;
		
		boolean skipOption = false;
		
		for(int i = 0, length = args.length; i < length; i++) {
			String arg = args[i];
			
			if(!skipOption && arg.startsWith(PREFIX)) {
				
				if(arg.equals(PREFIX + PREFIX)) {
					skipOption = true;
					continue;
				}
				
				String[] nameAndValue = arg.split("=", 2);
				String name  = nameAndValue[0];
				String value = nameAndValue.length >= 2 ? nameAndValue[1] : null;
				
				Optional<Entry<String, Argument<T>>> foundArg = optionalArguments.entrySet()
						.stream().filter(entry -> entry.getKey().equals(name)).findAny();
				
				if(foundArg.isPresent()) {
					Argument<T> argument = foundArg.get().getValue();
					
					if(!argument.canParse()) {
						errorHandler.handle(programName, "Cannot parse argument '" + name + "'");
						continue;
					}
					
					T parsedValue;
					
					if(value == null) {
						if(argument.isValueRequired()) {
							if(++i >= length) {
								errorHandler.handle(programName, "Value for option '" + name + "' not specified");
								continue;
							}
							
							parsedValue = tryParseValue(argument, args[i]);
						
						} else {
							parsedValue = argument.getImplicitValue();
						}
						
						argument.parsedTimes++;
						
						if(argument.isMultiargs()) {
							positionalIter = new OptionalArgumentIterator<>(positionalIter, argument);
							positionalArgument = argument;
						}
					
					} else {
						parsedValue = tryParseValue(argument, value);
					}
					
					namespaceMap.get(name).getValue().add(parsedValue);
					argument.performAction(namespace, parsedValue);
				
				} else {
					errorHandler.handle(programName, "Unknown option '" + arg + "'", () -> new UnknownOptionException(arg));
					continue;
				}
				
				continue;
				
			} else {
				if(positionalArgument == null || !positionalArgument.canParse()) {
					if(!positionalIter.hasNext())
						errorHandler.handle(programName, "Unrecognized argument '" + arg + "'", () -> new UnrecognizedArgumentException(arg));
					
					positionalArgument = positionalIter.next();
				}
				
				var values = namespaceMap.get(positionalArgument.name).getValue();
				values.add(positionalArgument.parse(arg));
				
				positionalArgument.parsedTimes++;
				
				skipOption = false;
			}
		}
		
		List<Argument<T>> requiredArguments = new ArrayList<>();
		
		for(Entry<Argument<T>, List<T>> entry : namespaceMap.values().stream().collect(Collectors.toSet())) {
			
			Argument<T> argument = entry.getKey();
			
			if(!argument.parsed()) {
				requiredArguments.add(argument);
				continue;
			}
			
			if(argument.times.isRequired()) {
				List<T> values = entry.getValue();
				if(values.isEmpty()) {
					values.add(argument.getDefaultValue());
				}
			}
		}
		
		if(!requiredArguments.isEmpty()) {
			String message = requiredArguments.size() == 1 ?
					"Required argument '" + requiredArguments.get(0).name + "'" :
					"Required arguments " + requiredArguments.stream().map(argument -> "'" + argument.name + "'").collect(Collectors.joining(", "));
			errorHandler.handle(programName, message, () -> new ArgumentParseException(message));
		}
		
		return namespace;
	}
	
	
	private <T> T tryParseValue(Argument<T> argument, String value) {
		try {
			return argument.parse(value);
		} catch(ArgumentParseException ex) {
			errorHandler.handle(programName, ex.getLocalizedMessage(), () -> ex);
			return argument.getDefaultValue();
		}
	}
	
	
	
	private static class OptionalArgumentIterator<T> implements Iterator<Argument<T>> {
		
		private final Iterator<Argument<T>> iterator;
		private Argument<T> argument;
		
		public OptionalArgumentIterator(Iterator<Argument<T>> iterator, Argument<T> argument) {
			this.iterator = iterator instanceof OptionalArgumentIterator<T> multiargIterator ? multiargIterator.iterator : iterator;
			this.argument = argument;
		}
		
		@Override
		public boolean hasNext() {
			if(argument == null)
				return iterator.hasNext();
			
			return true;
		}
		
		@Override
		public Argument<T> next() {
			if(argument == null)
				return iterator.next();
			
			var localArgument = argument;
			argument = null;
			return localArgument;
		}
	}
}
