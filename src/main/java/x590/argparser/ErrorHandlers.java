package x590.argparser;

/**
 * Объявляет различные обработчики, можно
 * также создать свой при необходимости
 */
public class ErrorHandlers {
	
	private static void printError(String programName, String message) {
		System.err.println(programName + ": error: " + message);
		System.err.println("Use '" + programName + " --help' for more information");
	}
	
	public static final ErrorHandler
			PRINT = (programName, message, exceptionCreator) -> {
				printError(programName, message);
			},
			
			EXIT = (programName, message, exceptionCreator) -> {
				System.exit(1);
			},
			
			PRINT_AND_EXIT = (programName, message, exceptionCreator) -> {
				printError(programName, message);
				System.exit(1);
			},
			
			THROW = (programName, message, exceptionCreator) -> {
				throw exceptionCreator.get();
			},
			
			PRINT_AND_THROW = (programName, message, exceptionCreator) -> {
				printError(programName, message);
				throw exceptionCreator.get();
			},
			
			IGNORE = (programName, message, exceptionCreator) -> {};
}