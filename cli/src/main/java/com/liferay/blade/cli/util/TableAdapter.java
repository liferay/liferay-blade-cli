package com.liferay.blade.cli.util;

import java.util.function.Function;

public interface TableAdapter<T> extends Function<T, String> {
}