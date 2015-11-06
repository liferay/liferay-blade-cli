
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
		"file.extensions=java",
		"problem.title=Removed WikiUtil.getEntries Method",
		"problem.section=#removed-wikiutil",
		"problem.summary=Removed WikiUtil.getEntries Method",
		"problem.tickets=LPS-56242",
	},
	service = FileMigrator.class
)
public class WikiUtilGetEntriesInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodInvocations(null, "WikiUtil",
				"getEntries", null);
	}

}
