package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java",
		"problem.title=StorageAdapter API Changes",
		"problem.summary=Removed Operations That Used the Fields Class from the StorageAdapter Interface",
		"problem.tickets=LPS-53021",
		"problem.type=java",
		"problem.section=#removed-operations-that-used-the-fields-class-from-the-storageadapter-interface"
	},
	service = FileMigrator.class
)
public class StorageAdapterCreateUpdateMethods extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
				null, "StorageEngineUtil", "create",
				new String[] { "long", "long", "Fields", "ServiceContext" });

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(null,
				"StorageEngineUtil", "update",
				new String[] { "long", "Fields", "boolean", "ServiceContext" });

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(null,
				"StorageEngineUtil", "update",
				new String[] { "long", "Fields", "ServiceContext" });

		searchResults.addAll(invocations);

		return searchResults;
	}
}