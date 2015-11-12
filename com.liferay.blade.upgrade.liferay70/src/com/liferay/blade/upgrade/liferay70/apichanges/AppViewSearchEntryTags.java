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
		"problem.title=Removed mbMessages and fileEntryTuples Attributes from app-view-search-entry Tag",
		"problem.section=#removed-mbmessages-and-fileentrytuples-attributes-from-app-view-search-entry-tag",
		"problem.summary=Removed mbMessages and fileEntryTuples Attributes from app-view-search-entry Tag",
		"problem.tickets=LPS-55886",
	},
	service = FileMigrator.class
)
public class AppViewSearchEntryTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JSPFile jspFileChecker) {

		return jspFileChecker.findJSPTags("liferay-ui:app-view-search-entry",
				new String[] { "mbMessages", "fileEntryTuples" }, null);
	}
}