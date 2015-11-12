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
		"problem.title=Changes to Indexer methods",
		"problem.summary=Method Indexer.addRelatedEntryFields(Document, Object) has been moved into RelatedEnt" +
			"ryIndexer. Indexer.reindexDDMStructures(List<Long>) has been moved into DDMStructureIndexer." +
			" Indexer.getQueryString(SearchContext, Query) has been removed, in favor of calling SearchEngineUtil." +
			"getQueryString(SearchContext, Query)",
		"problem.tickets=LPS-55928",
		"problem.section=#moved-indexer"
	},
	service = FileMigrator.class
)
public class IndexerThreeMethodsChange  extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> declarations = javaFileChecker.findMethodDeclaration(
				"addRelatedEntryFields", new String[] { "Document", "Object" });
		searchResults.addAll(declarations);

		declarations = javaFileChecker.findMethodDeclaration(
				"reindexDDMStructures", new String[] { "List<Long>" });
		searchResults.addAll(declarations);

		declarations = javaFileChecker.findMethodDeclaration("getQueryString",
				new String[] { "SearchContext", "Query" });
		searchResults.addAll(declarations);

		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
				"Indexer", null, "addRelatedEntryFields",
				new String[] { "Document", "Object" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("indexer", null,
				"addRelatedEntryFields", new String[] { "Document", "Object" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("Indexer", null,
				"reindexDDMStructures", new String[] { "List<Long>" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("indexer", null,
				"reindexDDMStructures", new String[] { "List<Long>" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("Indexer", null,
				"getQueryString", new String[] { "SearchContext", "Query" });
		searchResults.addAll(invocations);

		invocations = javaFileChecker.findMethodInvocations("indexer", null,
				"getQueryString", new String[] { "SearchContext", "Query" });
		searchResults.addAll(invocations);

		return searchResults;
	}
}
