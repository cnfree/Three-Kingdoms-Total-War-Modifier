package org.sf.feeling.sanguo.patch.util;

public class PatternUtil {
	public static String escape(String original) {
		StringBuffer buffer = new StringBuffer(original.length());
		for (int i = 0; i < original.length(); i++) {
			char c = original.charAt(i);
			if (c == '\\' || c == '{' || c == '}') {
				buffer.append("\\").append(c);
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
}
