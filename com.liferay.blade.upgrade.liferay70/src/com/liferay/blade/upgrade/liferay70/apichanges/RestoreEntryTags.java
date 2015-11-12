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
		"problem.title=Changed the Usage of the liferay-ui:restore-entry Tag",
		"problem.section=#changed-the-usage-of-the-liferay-uirestore-entry-tag",
		"problem.summary=Changed the Usage of the liferay-ui:restore-entry Tag",
		"problem.tickets=LPS-54106",
	},
	service = FileMigrator.class
)
public class RestoreEntryTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file,
			JSPFile jspFileChecker) {

		return jspFileChecker.findJSPTags("liferay-ui:restore-entry", null, null);
	}
}