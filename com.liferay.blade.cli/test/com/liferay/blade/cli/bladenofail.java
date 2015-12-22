package com.liferay.blade.cli;

import java.io.UnsupportedEncodingException;

public class bladenofail extends blade {

	public bladenofail() throws UnsupportedEncodingException {
		super();
	}

	@Override
	public boolean check(String... pattern) {
		return true;
	}
}