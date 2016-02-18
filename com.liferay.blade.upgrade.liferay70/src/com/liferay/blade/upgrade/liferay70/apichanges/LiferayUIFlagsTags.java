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
		"problem.title=Deprecated the liferay-ui:flags Tag and Replaced with liferay-flags:flags",
		"problem.section=#deprecated-the-liferay-uiflags-tag-and-replaced-with-liferay-flagsflags",
		"problem.summary=Deprecated the liferay-ui:flags Tag and Replaced with liferay-flags:flags",
		"problem.tickets=LPS-60967",
	},
	service = FileMigrator.class
)
public class LiferayUIFlagsTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file,
			JSPFile jspFileChecker) {

		return jspFileChecker.findJSPTags("liferay-ui:flags", null, null);
	}
}