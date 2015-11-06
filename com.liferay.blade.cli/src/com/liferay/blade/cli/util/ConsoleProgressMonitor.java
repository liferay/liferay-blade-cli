package com.liferay.blade.cli.util;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.ProgressMonitor;

@SuppressWarnings("restriction")
@Component
public class ConsoleProgressMonitor implements ProgressMonitor {

	@Override
	public void beginTask(String taskName, int totalWork) {
		System.out.print(taskName + "\r");
	}

	@Override
	public void done() {
		System.out.println();
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void setTaskName(String taskName) {
		System.out.print("\033[K");
		System.out.print(taskName + "\r");
	}

	@Override
	public void worked(int work) {
	}

}
