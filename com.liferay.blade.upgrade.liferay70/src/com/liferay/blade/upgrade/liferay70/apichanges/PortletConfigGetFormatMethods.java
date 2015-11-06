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
		"file.extensions=java,jsp,jspf",
		"problem.summary=Removed get and format Methods that Used PortletConfig Parameters",
		"problem.tickets=LPS-44342",
		"problem.title=PortletConfig get/format methods",
		"problem.section=#removed-get-and-format-methods-that-used-portletconfig-parameters"
	},
	service = FileMigrator.class
)
public class PortletConfigGetFormatMethods extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		// get methods
		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(null, "LanguageUtil", "get",
				new String[] { "PortletConfig", "Locale", "String" });

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(null, "LanguageUtil", "get",
				new String[] { "PortletConfig", "Locale", "String", "String" });

		searchResults.addAll(invocations);

		//format methods
		invocations = javaFileChecker.findMethodInvocations(null, "LanguageUtil", "format",
				new String[] { "PortletConfig", "Locale", "String", "Object" });

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(null, "LanguageUtil", "format",
				new String[] { "PortletConfig", "Locale", "String", "Object", "boolean" });

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(null, "LanguageUtil", "format",
				new String[] { "PortletConfig", "Locale", "String", "Object[]" });

		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations(null, "LanguageUtil", "format",
				new String[] { "PortletConfig", "Locale", "String", "Object[]", "boolean" });

		searchResults.addAll(invocations);

		return searchResults;
	}
}