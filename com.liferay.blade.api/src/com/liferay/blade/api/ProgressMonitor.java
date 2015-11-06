package com.liferay.blade.api;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ProgressMonitor {

	public void beginTask(String taskName, int totalWork);

	public void done();

	public boolean isCanceled();

	public void setTaskName(String taskName);

	public void worked(int work);
}
