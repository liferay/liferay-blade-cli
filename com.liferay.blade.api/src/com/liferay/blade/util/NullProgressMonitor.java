package com.liferay.blade.util;

import com.liferay.blade.api.ProgressMonitor;

public class NullProgressMonitor implements ProgressMonitor {

	@Override
	public void beginTask(String taskName, int totalWork) {
	}

	@Override
	public void done() {
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void setTaskName(String taskName) {
	}

	@Override
	public void worked(int work) {
	}

}
