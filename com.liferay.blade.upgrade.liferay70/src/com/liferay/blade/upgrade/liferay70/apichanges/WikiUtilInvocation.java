package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=Removed the getPageOrderByComparator Method from WikiUtil",
		"problem.section=#removed-the-getpageorderbycomparator-method-from-wikiutil",
		"problem.summary=Removed the getPageOrderByComparator Method from WikiUtil",
		"problem.tickets=LPS-60843",
	},
	service = FileMigrator.class
)
public class WikiUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodInvocations(null, "WikiUtil",
				"getPageOrderByComparator", null);
	}
}
