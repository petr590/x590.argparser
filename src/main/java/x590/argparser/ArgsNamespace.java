package x590.argparser;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

public class ArgsNamespace {

	final Map<String, Entry<Argument<?>, List<?>>> map = new HashMap<>();


	private void addToMap(Map<String, Argument<?>> arguments) {
		for(Argument<?> argument : arguments.values()) {
			Map.Entry<Argument<?>, List<?>> entry = Map.entry(argument, new ArrayList<>());
			for(String name : argument.names)
				map.put(name, entry);
		}
	}


	public ArgsNamespace(Map<String, Argument<?>> optionalArguments, Map<String, Argument<?>> positionalArguments) {
		addToMap(optionalArguments);
		addToMap(positionalArguments);
	}


	protected <T> Entry<Argument<T>, List<T>> find(String name) {

		@SuppressWarnings("unchecked")
		var entry = (Entry<Argument<T>, List<T>>)(Entry<?, ?>)map.get(name);

		if(entry == null)
			throw new IllegalArgumentException("Argument '" + name + "' not found");

		return entry;
	}


	public <T> void set(String name, T value) {
		List<T> values = this.<T>find(name).getValue();
		values.clear();
		values.add(value);
	}

	public <T> void add(String name, T value) {
		this.<T>find(name).getValue().add(value);
	}


	public <T> T get(String name) {
		var entry = this.<T>find(name);
		List<T> values = entry.getValue();
		return values.isEmpty() ? entry.getKey().getDefaultValue() : values.get(0);
	}


	public Boolean getBoolean(String name) {
		return get(name);
	}

	public int getInt(String name) {
		return get(name);
	}

	public Integer getInteger(String name) {
		return get(name);
	}

	public Long getLong(String name) {
		return get(name);
	}

	public Float getFloat(String name) {
		return get(name);
	}

	public Double getDouble(String name) {
		return get(name);
	}


	public String getString(String name) {
		return get(name);
	}


	@SuppressWarnings("unchecked")
	public <T> List<T> getAll(String name) {
		return (List<T>)map.get(name).getValue();
	}
}