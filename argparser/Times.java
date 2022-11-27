package argparser;

/**
 * Указывает, сколько раз может встречаться аргумент при парсинге.
 * Хранит минимальное и максимальное значения.
 */
public class Times {
	
	public static final int MAX = Integer.MAX_VALUE;
	
	public static final Times
			ONCE = new Times(1),
			ZERO_OR_ONE = new Times(0, 1),
			ONE_OR_MORE = new Times(1, MAX),
			ANY = new Times(0, MAX);
	
	
	public final int minTimes, maxTimes;
	
	public Times(int minTimes, int maxTimes) {
		this.minTimes = minTimes;
		this.maxTimes = maxTimes;
	}
	
	public Times(int times) {
		this(times, times);
	}
	
	public boolean test(int times) {
		return times >= minTimes && times <= maxTimes;
	}
	
	public boolean isRequired() {
		return minTimes > 0;
	}
}