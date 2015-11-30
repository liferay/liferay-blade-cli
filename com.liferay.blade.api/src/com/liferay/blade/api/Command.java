package com.liferay.blade.api;

import java.util.Map;

public interface Command {

	public Object execute(Map<String, ?> parameters) throws CommandException;

	public Object execute(String... args) throws CommandException;

}
