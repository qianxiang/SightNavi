package org.jint.util;

public class strUtil {

	public static boolean isBlank(String str) {
		if (null == str) {
			return true;
		} else if (str.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

}
