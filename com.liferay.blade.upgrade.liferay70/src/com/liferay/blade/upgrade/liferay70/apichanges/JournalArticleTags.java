package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JSPFileMigrator;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Removed the liferay-ui:journal-article tag",
		"problem.section=#removed-the-liferay-uijournal-article-tag",
		"problem.summary=Removed the liferay-ui:journal-article Tag",
		"problem.tickets=LPS-56383",
	},
	service = FileMigrator.class
)
public class JournalArticleTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file,
			JSPFile jspFileChecker) {

		return jspFileChecker.findJSPTags("liferay-ui:journal-article", null , null);
	}
}