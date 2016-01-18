package com.liferay.blade.cli;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class bladenofail extends blade {

	private PrintStream _out;

	public bladenofail() throws UnsupportedEncodingException {
		super();
	}

	public bladenofail(PrintStream out) throws UnsupportedEncodingException {
		super();
		_out = out;
	}

	@Override
	public boolean check(String... pattern) {
		return true;
	}

	@Override
	public PrintStream out() {
		return _out;
	}
}