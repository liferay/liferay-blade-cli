package com.liferay.blade.api;

import java.io.File;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface AutoMigrator {

	public void correctProblems(File file, List<Problem> problems) throws AutoMigrateException;

}
