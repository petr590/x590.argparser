package x590.argparser;

import java.util.function.Supplier;

/**
 * Обработчик ошибок при парсинге аргументов
 * @see ErrorHandlers
 */
@FunctionalInterface
public interface ErrorHandler {
	
	public void handle(String programName, String message, Supplier<ArgumentParseException> exceptionCreator);
	
	public default void handle(String programName, String message) {
		handle(programName, message, () -> new ArgumentParseException(message));
	}
}