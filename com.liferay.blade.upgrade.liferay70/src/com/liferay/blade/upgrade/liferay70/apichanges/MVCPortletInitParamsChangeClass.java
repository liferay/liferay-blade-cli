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
		"problem.title=copy-request-parameters init-param default value change",
		"problem.summary=The copy-request-parameters init parameter's default value is now set to true in all portlets that extend MVCPortlet.",
		"problem.tickets=LPS-54798",
		"problem.section=#changed-the-default-value-of-the-copy"
	},
	service = FileMigrator.class
)
public class MVCPortletInitParamsChangeClass  extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findSuperClass("MVCPortlet");
	}
}
