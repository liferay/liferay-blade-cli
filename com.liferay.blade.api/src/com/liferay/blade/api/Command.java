package com.liferay.blade.api;

import java.util.Map;

public interface Command {

	public Object execute(Map<String, ?> parameters) throws CommandException;

}
