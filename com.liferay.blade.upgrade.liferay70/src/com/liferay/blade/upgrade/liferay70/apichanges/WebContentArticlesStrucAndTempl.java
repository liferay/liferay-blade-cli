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
		"problem.title=Web Content Articles Now Require a Structure and Template",
		"problem.summary=Web content is now required to use a structure and template. " +
			"A default structure and template named Basic Web Content was " +
			"added to the global scope, and can be modified or deleted.",
		"problem.tickets=LPS-45107",
		"problem.section=#web-content-articles-now-require-a-structure-and-template"
	},
	service = FileMigrator.class
)
public class WebContentArticlesStrucAndTempl extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		// Journal API to create web content without a structure
		// or template are affected
		final List<SearchResult> searchResults = new ArrayList<>();

		List<SearchResult> journalArticleUtil = javaFileChecker
				.findMethodInvocations(null, "JournalArticleLocalServiceUtil",
						"addArticle", null);

		searchResults.addAll(journalArticleUtil);

		journalArticleUtil = javaFileChecker.findMethodInvocations(null,
				"JournalArticleServiceUtil", "addArticle", null);

		searchResults.addAll(journalArticleUtil);

		return searchResults;
	}

}
