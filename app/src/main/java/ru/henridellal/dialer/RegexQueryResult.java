package ru.henridellal.dialer;

public class RegexQueryResult implements Comparable<RegexQueryResult> {
	public final int position;
	public final int start;
	public final int end;

	public final int id;
	public final String lookupKey;
	public final String name;
	public final String number;
	public int numberStart;
	public int numberEnd;
	
	public RegexQueryResult(int position, int start, int end, int id, String lookupKey, String name, String number) {
		this.position = position;
		this.start = start;
		this.end = end;
		this.id = id;
		this.lookupKey = lookupKey;
		this.name = name;
		this.number = number;
	}
	
	public void setNumberPlace(int numberStart, int numberEnd) {
		this.numberStart = numberStart;
		this.numberEnd = numberEnd;
	}
	
	@Override
	public int compareTo(RegexQueryResult obj) throws NullPointerException, ClassCastException {
		if (null == obj) {
			throw new NullPointerException();
		}
		int result = Integer.compare(this.start, obj.start);
		return (result != 0) ? result : Integer.compare(this.position, obj.position);
	}
}
