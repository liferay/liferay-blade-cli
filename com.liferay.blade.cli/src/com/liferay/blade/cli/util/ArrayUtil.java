package com.liferay.blade.cli.util;

import java.lang.reflect.Array;

public class ArrayUtil {

	@SuppressWarnings("unchecked")
	public static <T> T[] append(T[] array, T value) {
		Class<?> arrayClass = array.getClass();

		T[] newArray = (T[])Array.newInstance(
			arrayClass.getComponentType(), array.length + 1);

		System.arraycopy(array, 0, newArray, 0, array.length);

		newArray[array.length] = value;

		return newArray;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] append(T[] array1, T[] array2) {
		Class<?> array1Class = array1.getClass();

		T[] newArray = (T[])Array.newInstance(
			array1Class.getComponentType(), array1.length + array2.length);

		System.arraycopy(array1, 0, newArray, 0, array1.length);

		System.arraycopy(array2, 0, newArray, array1.length, array2.length);

		return newArray;
	}
	
	public static boolean isEmpty(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		return false;
	}
}
