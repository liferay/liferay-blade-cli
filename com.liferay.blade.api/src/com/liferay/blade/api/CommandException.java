package com.liferay.blade.api;

public class CommandException extends Exception {

	private static final long serialVersionUID = -3880250268320611763L;

	public CommandException(String msg) {
		super(msg);
	}

	public CommandException(String msg, Exception e) {
		super(msg, e);
	}

}
