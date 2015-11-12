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
		"problem.summary=Replaced Method getFacetQuery with getFacetBooleanFilter in Indexer",
		"problem.tickets=LPS-56064",
		"problem.title=Indexer API Changes",
		"problem.section=#replaced-method-getpermissionquery-with-getpermissionfilter-in-searchpermissionchecker-and-getfacetquery-with-getfacetbooleanfilter-in-indexer"
	},
	service = FileMigrator.class
)
public class IndexerGetFacetQuery extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		final List<SearchResult> declaration = javaFileChecker.findMethodDeclaration("getFacetQuery",
				new String[] { "String", "SearchContextPortletURL" });

		searchResults.addAll(declaration);

		final List<SearchResult> invocations = javaFileChecker.findMethodInvocations("Indexer", null, "getFacetQuery",
				null);

		searchResults.addAll(invocations);

		return searchResults;
	}
}