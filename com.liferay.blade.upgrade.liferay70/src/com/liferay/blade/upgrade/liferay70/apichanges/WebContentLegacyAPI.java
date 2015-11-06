package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=All Web Content APIs previously exposed as Liferay Portal API in 6.2 have been move out from portal-service into separate OSGi modules",
		"problem.tickets=LPS-54838",
		"problem.title=Web Content APIs migrated to OSGi module",
		"problem.section=#legacy"
	},
	service = FileMigrator.class
)
public class WebContentLegacyAPI extends JavaFileMigrator {

	private static final String[] SERVICE_API_PREFIXES =  {
		"com.liferay.portlet.journal.service.JournalArticle",
		"com.liferay.portlet.journal.service.JournalArticleImage",
		"com.liferay.portlet.journal.service.JournalArticleResource",
		"com.liferay.portlet.journal.service.JournalContentSearch,",
		"com.liferay.portlet.journal.service.JournalFeed",
		"com.liferay.portlet.journal.service.JournalFolder",
		"com.liferay.portlet.journal.service.JournalStructure",
		"com.liferay.portlet.journal.service.JournalStructure",
		"com.liferay.portlet.journal.service.JournalTemplate",
	};

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findServiceAPIs(SERVICE_API_PREFIXES);
	}
}