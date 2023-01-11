package x590.argparser;

public interface ArgParser {

	public ArgParser add(Argument<?> option);

	public ArgsNamespace parse(String... args) throws ArgumentParseException;
}