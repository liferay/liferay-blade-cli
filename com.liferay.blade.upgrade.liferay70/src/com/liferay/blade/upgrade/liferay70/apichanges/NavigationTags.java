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
		"problem.title=Removed the liferay-ui:navigation Tag and Replaced with liferay-site-navigation:navigation Tag",
		"problem.section=#removed-the-liferay-uinavigation-tag-and-replaced-with-liferay-site-navigationnavigation-tag",
		"problem.summary=Removed the liferay-ui:navigation Tag and Replaced with liferay-site-navigation:navigation Tag",
		"problem.tickets=LPS-60328",
	},
	service = FileMigrator.class
)
public class NavigationTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JSPFile jspFileChecker) {
		return jspFileChecker.findJSPTags("liferay-ui:navigation", null, null);
	}
}