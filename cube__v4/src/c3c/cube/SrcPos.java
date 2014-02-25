package c3c.cube;

public class SrcPos implements Comparable<SrcPos> {
	
	/**
	 * Encoded source location that includes line, and begin and end columns
	 * 
	 * line:	24bits --> 16M7 lines of code
	 * begin: 	20bits --> 1M columns
	 * end:		20bits --> 1M columns
	 * 
	 */
	/*int64*/ long pos;
	int offset;
	
//	/**
//	 * Encoded source location that includes line, and begin and end columns
//	 * 
//	 * line:	18bits --> 262K lines of code
//	 * begin: 	8bits --> 256 columns
//	 * end:		6bits --> 64 chars for maximum token track size
//	 * 
//	 */
//	/*int32*/ int pos;
	
	public SrcPos(int line, int offset, int begin, int end) {
		this.offset = offset;
		pos = ((long) (line & 0xFFFFFF)) << 40 | ((long)(begin & 0xFFFFF)) << 20 | (end	& 0xFFFFF);
	}

	public SrcPos(SrcPos location) {
		this.offset = location.offset;
		this.pos = location.pos;
	}

	public int line() {
		return (int) (pos >> 40) & 0xFFFFFF;
	}
	
	public int begin() {
		return (int) (pos >> 20) & 0xFFFFF;
	}
	
	public int end() {
		return (int) pos & 0xFFFFF;
	}
	
	public int offset() {
		return offset;
	}
	
	public void add(SrcPos location) {
		if (location.begin() < begin()) {
			// TODO remove begin
			pos |= ((long)(location.begin() & 0xFFFFF)) << 20;
		}
		if (location.end() > end()) {
			pos &= 0xFFFFFFFFFFF00000L;
			pos |= (location.end() & 0xFFFFF);
		}
	}

	@Override
	public int compareTo(SrcPos o) {
		return (int) (pos - o.pos);
	}

	public String toString() {
		return "line: " + line() + " [" + begin() + "-" + end() + "]";
	}
}
