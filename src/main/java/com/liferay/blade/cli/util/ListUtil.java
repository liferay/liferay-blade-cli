package com.liferay.blade.cli.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtil {

	public static <E> List<E> fromArray(E[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		return new ArrayList<>(Arrays.asList(array));
	}

}
