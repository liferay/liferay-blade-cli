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
		"problem.title=The aui:column taglib has been removed and replaced with aui:col taglib",
		"problem.section=#the-auicolumn-taglib-has-been-removed-and-replaced-with-auicol-taglib",
		"problem.summary=The aui:column taglib has been removed and replaced with aui:col taglib",
		"problem.tickets=LPS-62208",
	},
	service = FileMigrator.class
)
public class AUIColumnTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file,
			JSPFile jspFileChecker) {

		return jspFileChecker.findJSPTags("aui:column", null, null);
	}
}