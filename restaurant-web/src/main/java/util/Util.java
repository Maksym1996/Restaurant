package util;

import java.util.List;

public class Util {
	private Util() {};
	
	public static int getMaxPages(List<?> list, long pageSize) {
		int i = (int) (list.size()/pageSize);
		return (double)list.size()/pageSize != (double)i ? i + 1 :i;
	}
}
