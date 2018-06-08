package com.liferay.extensions.sample.command;

import com.liferay.blade.cli.command.BaseCommand;

public class Hello extends BaseCommand<HelloArgs> {

	@Override
	public void execute() throws Exception {
		HelloArgs helloArgs = getArgs();

		getBladeCLI().out("Hello " + helloArgs.getName());
	}

	@Override
	public Class<HelloArgs> getArgsClass() {
		return HelloArgs.class;
	}

}