package com.liferay.blade.api;

import java.io.OutputStream;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface Reporter {

	public void beginReporting(int format, OutputStream output);

	public void endReporting();

	public void report(Problem problem);

}