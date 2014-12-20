package bigdata;

public class Pair<T1 extends Comparable<T1>, T2> implements Comparable<Pair<T1, T2>>
{
	private T1 key;
	private T2 value;
	
	public Pair(T1 key, T2 value)
	{
		this.key = key;
		this.value = value;
	}
	
	public T1 getKey()
	{
		return key;
	}
	
	public T2 getValue()
	{
		return value;
	}
	
	public int compareTo(Pair<T1, T2> o)
	{
		return key.compareTo(o.key);
	}
}
