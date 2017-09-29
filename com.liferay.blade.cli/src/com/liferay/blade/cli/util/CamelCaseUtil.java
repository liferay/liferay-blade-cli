package com.liferay.blade.cli.util;

public class CamelCaseUtil {

	public static String fromCamelCase(String s, char delimiter) {
		StringBuilder sb = new StringBuilder();

		boolean upperCase = false;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((i > 0) && Character.isUpperCase(c)) {
				if (!upperCase ||
					((i < (s.length() - 1)) &&
					 !Character.isUpperCase(s.charAt(i + 1)))) {

					sb.append(delimiter);
				}

				c = Character.toLowerCase(c);

				upperCase = true;
			}
			else {
				upperCase = false;
			}

			sb.append(c);
		}

		return sb.toString();
	}

}
