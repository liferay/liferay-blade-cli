package com.liferay.blade.api;

public class AutoMigrateException extends Exception {

	private static final long serialVersionUID = 3776203562079756845L;

	public AutoMigrateException(String msg) {
		super(msg);
	}

	public AutoMigrateException(String msg, Exception e) {
		super(msg, e);
	}

}
