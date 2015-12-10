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
		"problem.title=Custom AUI Validators Are No Longer Implicitly Required",
		"problem.section=#custom-aui-validators-are-no-longer-implicitly-required",
		"problem.summary=The AUI Validator tag no longer forces custom validators to be required",
		"problem.tickets=LPS-60995",
	},
	service = FileMigrator.class
)
public class CustomAUIValidatorTags extends JSPFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JSPFile jspFileChecker) {
		return jspFileChecker.findJSPTags("aui:validator",
				new String[] { "name" },new String[] { "custom" });
	}
}