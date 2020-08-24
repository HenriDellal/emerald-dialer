package ru.henridellal.dialer;

public class RegexQueryResult implements Comparable<RegexQueryResult> {
	public final int position;
	public final int start;
	public final int end;
	public int numberStart;
	public int numberEnd;
	
	public RegexQueryResult(int position, int start, int end) {
		this.position = position;
		this.start = start;
		this.end = end;
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
