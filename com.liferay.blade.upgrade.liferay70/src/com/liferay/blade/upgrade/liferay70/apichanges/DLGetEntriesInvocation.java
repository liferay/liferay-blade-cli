package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=The getEntries method was no longer used, and contained hardcoded references to classes that will be moved into OSGi bundles.",
		"problem.tickets=LPS-56247",
		"problem.title=Removed Method getEntries from DL, DLImpl, and DLUtil Classes",
		"problem.section=#removed-method-getentries-from-dl-dlimpl-and-dlutil-classes"
	},
	service = FileMigrator.class
)
public class DLGetEntriesInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<SearchResult>();

		final String[] argTypes = new String[] { "Hits" };

		searchResults.addAll(javaFileChecker.findMethodInvocations("DL", null,
				"getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null, "DL",
				"getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DLImpl",
				null, "getEntries", null));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null,
				"DLImpl", "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations("DLUtil",
				null, "getEntries", argTypes));

		searchResults.addAll(javaFileChecker.findMethodInvocations(null,
				"DLUtil", "getEntries", argTypes));

		return searchResults;
	}

}