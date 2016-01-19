package com.liferay.blade.cli;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Gregory Amerson
 */
public class bladenofail extends blade {

	public bladenofail() throws UnsupportedEncodingException {
	}

	public bladenofail(PrintStream out) throws UnsupportedEncodingException {
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

	private PrintStream _out;

}